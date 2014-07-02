package prediction.features.messages;

import java.util.Collection;

import snml.dataconvert.IntermediateDataInitializer;
import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.IntermediateRecommendationDataInitializer;
import snml.dataconvert.RecommendationFeatureExtractor;
import snml.dataimport.ThreadDataSet;
import snml.rule.basicfeature.IBasicFeatureRule;
import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

public class MessageIntermediateRecommendationDataSetExtractor<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
		extends
		MessageIntermediateDataSetExtractor<Collaborator, Message, ThreadType> {

	public MessageIntermediateRecommendationDataSetExtractor(
			ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties) {
		super(threadsProperties);
	}
	public IntermediateDataSet extractAllIntermediateData(Collection<ThreadType> threads,
			String dataSetName,
			IBasicFeatureRule[] featureRules,
			IBasicFeatureRule[] predictedFeatureRules,
			IntermediateDataInitializer initializer) throws Exception {

		IBasicFeatureRule[] combinedRules = new IBasicFeatureRule[featureRules.length + predictedFeatureRules.length];
		for (int i=0; i<featureRules.length; i++) {
			combinedRules[i] = featureRules[i];
		}
		for (int i=0; i<predictedFeatureRules.length; i++) {
			combinedRules[i+featureRules.length] = predictedFeatureRules[i];
		}
		
		return extractFeatureData(threads, dataSetName + "-predictors",
				combinedRules, initializer);
	}

	@Override
	public IntermediateDataSet extractFeatureData(Collection<ThreadType> threads,
			String dataSetName,
			IBasicFeatureRule[] featureRules,
			IntermediateDataInitializer initializer) throws Exception {
		if (featureRules.length != 3) {
			throw new Exception("Must contain 3 features for the recommendation parts: user, item, and preference");
		}
		if (!(initializer instanceof IntermediateRecommendationDataInitializer)) {
			throw new Exception("Initializer must be of type " + IntermediateRecommendationDataInitializer.class);
		}
		
		ThreadDataSet threadDataSet = extractThreadData(threads);
		RecommendationFeatureExtractor extractor = new RecommendationFeatureExtractor((IntermediateRecommendationDataInitializer)initializer);
		return extractor.extract(threadDataSet, dataSetName, featureRules[0], featureRules[1], featureRules[2]);
	}
}
