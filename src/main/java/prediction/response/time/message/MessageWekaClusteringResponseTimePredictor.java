package prediction.response.time.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.mail.MessagingException;

import prediction.features.messages.MessageIntermediateDataSetExtractor;
import prediction.features.messages.MessageLivenessRule;
import prediction.features.messages.ThreadSetProperties;
import snml.dataconvert.IntermediateData;
import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.WekaDataInitializer;
import snml.dataconvert.WekaDataSet;
import snml.rule.basicfeature.IBasicFeatureRule;
import snml.rule.superfeature.model.weka.WekaClusterModelRule;
import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

public class MessageWekaClusteringResponseTimePredictor<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
		implements MessageResponseTimePredictor<Collaborator, Message, ThreadType> {

	private String title;
	
	protected MessageIntermediateDataSetExtractor<Collaborator, Message, ThreadType> extractor = null;
	protected IntermediateDataSet currTrainDataSet;
	
	protected List<ThreadType> pastThreads = new ArrayList<>();
	protected IBasicFeatureRule[] featureRules;
	protected WekaClusterModelRule snmlModel;
	protected ThreadSetProperties<Collaborator,Message,ThreadType> threadsProperties;
	
	public static <Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
			MessageResponseTimePredictorFactory<Collaborator, Message, ThreadType>
			factory(final String title,
					final WekaClusterModelRule snmlModel,
					Class<Collaborator> collaboratorClass, Class<Message> messageClass,
					Class<ThreadType> threadClass) {

		return new MessageResponseTimePredictorFactory<Collaborator, Message, ThreadType>() {

			@Override
			public MessageResponseTimePredictor<Collaborator, Message, ThreadType> create(
					Collection<IBasicFeatureRule> features,
					ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties) {
				
				IBasicFeatureRule[] featureArray = features.toArray(new IBasicFeatureRule[0]);
				return new MessageWekaClusteringResponseTimePredictor<>(title, snmlModel, featureArray, threadsProperties);
			}
		};
	}
	
	public MessageWekaClusteringResponseTimePredictor(String title,
			WekaClusterModelRule snmlModel,
			IBasicFeatureRule[] featureRules,
			ThreadSetProperties<Collaborator,Message,ThreadType> threadsProperties) {
		this.title = title;
		this.snmlModel = snmlModel;
		this.featureRules = featureRules;
		this.threadsProperties = threadsProperties;
	}
	
	/**
	 * Gets the title of the predictor
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Adds a thread to train the model
	 * @param thread
	 * 			The past thread to be used for training
	 */
	@Override
	public void addPastThread(ThreadType thread) {
		try {
			Double timeToResponse = thread.getTimeToResponse();
			if (timeToResponse != null && !timeToResponse.isInfinite()) {
				pastThreads.add(thread);
				extractor = null;	
			}
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Trains the underlying model of the predictor
	 * @throws Exception 
	 */
	public void train() throws Exception {
		if (extractor == null) {
			extractor = new MessageIntermediateDataSetExtractor<>(threadsProperties);
			IBasicFeatureRule[] predictableRules = new IBasicFeatureRule[0];
			currTrainDataSet = extractor.extractAllIntermediateData(pastThreads, "liveness",
					featureRules, predictableRules, new WekaDataInitializer());
			snmlModel.train(currTrainDataSet, null);;
		}
	}
	
	/**
	 * Retrieves the cluster predicted by the weka clusterer
	 * @param thread The thread for which to predict the cluster
	 * @return The cluster id
	 * @throws Exception
	 */
	public Integer getCluster(ThreadType thread) throws Exception {
		train();
		
		IBasicFeatureRule[] predictableRules = new IBasicFeatureRule[0];
		IntermediateData instance = extractor.extractSingleItem(thread, "liveness_test_item", featureRules, predictableRules, new WekaDataInitializer());
		
		Object result =  snmlModel.extract(instance);
		Integer cluster = null;
		if (result != null) {
			if (result instanceof String) {
				cluster = Integer.parseInt((String) result);
			} else {
				cluster = (int) result;
			}
		}
		return cluster;
	}

	
	/**
	 * Retrieves response time range associated with a cluster
	 * @param thread The thread for which to predict the cluster
	 * @return The cluster id
	 * @throws Exception
	 */
	public ResponseTimeRange getClusterRange(Integer cluster) throws Exception {
		if (cluster == null) {
			return null;
		}
		int[] trainingDataAssignments = snmlModel.getAssignments();
		
		Double minTime = null;
		Double maxTime = null;
		for (int i = 0; i < trainingDataAssignments.length; i++) {
			if (trainingDataAssignments[i] == cluster) {
				ThreadType thread = pastThreads.get(i);
				Double responseTime = thread.getTimeToResponse();
				if (responseTime !=null) {
					if (minTime == null || minTime > responseTime) {
						minTime = responseTime;
					}
					if (maxTime == null || maxTime < responseTime) {
						maxTime = responseTime;
					}
				}
			}
		}
		
		return new ResponseTimeRange(minTime, maxTime);
	}
	
	/* (non-Javadoc)
	 * @see prediction.response.time.message.MessageResponseTimePredictor#predictResponseTime(data.representation.actionbased.messages.MessageThread)
	 */
	@Override
	public ResponseTimeRange predictResponseTime(ThreadType thread) throws Exception {
		train();
		Integer cluster = getCluster(thread);
		return getClusterRange(cluster);
	}
	
	
	@Override
	public void evaluate(Collection<ThreadType> testThreads) throws Exception {
		train();
		IBasicFeatureRule[] predictableRules = new IBasicFeatureRule[1];
		predictableRules[0] = new MessageLivenessRule("responseTime");
		WekaDataSet trainData = (WekaDataSet) extractor.extractAllIntermediateData(pastThreads, "trainData", featureRules, predictableRules, new WekaDataInitializer());
		trainData.setTargetIndex();
		WekaDataSet testData = (WekaDataSet) extractor.extractAllIntermediateData(testThreads, "testData", featureRules, predictableRules, new WekaDataInitializer());
		testData.setTargetIndex();
		snmlModel.evaluate(trainData, testData);
	}
	
	/* (non-Javadoc)
	 * @see prediction.response.liveness.message.MessageLivenessPredictor#getModelInfo()
	 */
	@Override
	public String getModelInfo() throws Exception {
		train();
		String info = snmlModel.getClusterer().toString();
		info += "\n\n";
		info += "Cluster to range\n====================\n";
		for (int i=0; i<snmlModel.getClusterer().numberOfClusters(); i++) {
			info += i + " -> " + getClusterRange(i) + "\n";
		}
		return info;
	}

}
