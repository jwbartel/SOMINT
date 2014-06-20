package prediction.response.time.message;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import prediction.features.messages.ThreadSetProperties;
import snml.dataconvert.IntermediateData;
import snml.dataconvert.WekaData;
import snml.dataconvert.WekaDataInitializer;
import snml.rule.basicfeature.IBasicFeatureRule;
import snml.rule.superfeature.model.weka.WekaKmeansModelRule;
import weka.clusterers.SimpleKMeans;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

public class SigmoidWeightedKmeansMessageResponseTimePredictor<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
		extends
		WekaClusteringMessageResponseTimePredictor<Collaborator, Message, ThreadType> {
	
	public static <Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
			MessageResponseTimePredictorFactory<Collaborator, Message, ThreadType>
			factory(final String title,
					final WekaKmeansModelRule snmlModel,
					Class<Collaborator> collaboratorClass, Class<Message> messageClass,
					Class<ThreadType> threadClass) {

		return new MessageResponseTimePredictorFactory<Collaborator, Message, ThreadType>() {

			@Override
			public MessageResponseTimePredictor<Collaborator, Message, ThreadType> create(
					Collection<IBasicFeatureRule> features,
					ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties) {
				
				IBasicFeatureRule[] featureArray = features.toArray(new IBasicFeatureRule[0]);
				return new SigmoidWeightedKmeansMessageResponseTimePredictor<>(title, snmlModel, featureArray, threadsProperties);
			}
		};
	}
	

	public SigmoidWeightedKmeansMessageResponseTimePredictor(
			String title,
			WekaKmeansModelRule snmlModel,
			IBasicFeatureRule[] featureRules,
			ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties) {
		super(title, snmlModel, featureRules, threadsProperties);
	}
	
	public SimpleKMeans getKmeansModel() {
		return (SimpleKMeans) snmlModel.getClusterer();
	}
	
	private Map<Integer,Double> getCentroidDistances(ThreadType thread) throws Exception {
		
		DistanceFunction distanceFunction = getKmeansModel().getDistanceFunction();
		IntermediateData threadData = extractor.extractSingleItem(thread, "testItem", featureRules, new IBasicFeatureRule[0], new WekaDataInitializer());
		Instances centroids = getKmeansModel().getClusterCentroids();
		
		Map<Integer, Double> distances = new TreeMap<>();
		for (int i = 0; i < getKmeansModel().getNumClusters(); i++) {
			Instance centroid = centroids.get(i);
			double distance = distanceFunction.distance(centroid, ((WekaData) threadData).getInstValue());
			distances.put(i, distance);
		}
		
		return distances;
		
	}
	
	private double weight(double distance, double medianDistance) {
		return 1 / (1 + Math.exp(distance - medianDistance));
	}
	
	private ResponseTimeRange getWeightedMeanResponseTimeRange(ThreadType thread) throws Exception {
		Map<Integer,Double> distances = getCentroidDistances(thread);
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for (Double distance : distances.values()) {
			stats.addValue(distance);
		}

		double medianDistance = stats.getPercentile(50);
		double sumWeights = 0;
		double sumMinTimes = 0;
		double sumMaxTimes = 0;
		
		for (Integer cluster : distances.keySet()) {
			
			double distance = distances.get(cluster);
			double w = weight(distance, medianDistance);
			sumWeights += w;
			
			ResponseTimeRange range = getClusterRange(cluster);
			sumMinTimes += w*range.minResponseTime;
			sumMaxTimes += w*range.maxResponseTime;
		}
		
		Double avgMinTime = sumMinTimes/sumWeights;
		Double avgMaxTime = sumMaxTimes/sumWeights;
		return new ResponseTimeRange(avgMinTime, avgMaxTime);
	}
	
	/* (non-Javadoc)
	 * @see prediction.response.time.message.MessageResponseTimePredictor#predictResponseTime(data.representation.actionbased.messages.MessageThread)
	 */
	@Override
	public ResponseTimeRange predictResponseTime(ThreadType thread) throws Exception {
		train();
		return getWeightedMeanResponseTimeRange(thread);
	}

}
