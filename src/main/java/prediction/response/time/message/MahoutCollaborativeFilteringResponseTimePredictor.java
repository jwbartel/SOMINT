package prediction.response.time.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.mail.MessagingException;

import prediction.features.messages.MessageIntermediateDataSetExtractor;
import prediction.features.messages.MessageIntermediateRecommendationDataSetExtractor;
import prediction.features.messages.SecondsToFirstResponseRule;
import prediction.features.messages.ThreadSetProperties;
import prediction.response.time.ResponseTimeRange;
import snml.dataconvert.IntermediateData;
import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.mahout.MahoutDataInitializer;
import snml.dataconvert.mahout.MahoutDataSet;
import snml.rule.basicfeature.IBasicFeatureRule;
import snml.rule.superfeature.model.mahout.MahoutCollaborativeFiteringModelRule;
import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

public abstract class MahoutCollaborativeFilteringResponseTimePredictor<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
		implements MessageResponseTimePredictor<Collaborator, Message, ThreadType> {

	private String title;
	
	protected MessageIntermediateDataSetExtractor<Collaborator, Message, ThreadType> extractor = null;
	protected Collection<ThreadType> pastThreads = new ArrayList<>();
	protected IBasicFeatureRule userFeatureRule;
	protected IBasicFeatureRule itemFeatureRule;
	protected MahoutCollaborativeFiteringModelRule snmlModel;
	protected ThreadSetProperties<Collaborator,Message,ThreadType> threadsProperties;
	
	public static abstract class MahoutCollaborativeFilteringPredictorFactory<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>> 
	implements MessageResponseTimePredictorFactory<Collaborator, Message, ThreadType>{
		
		
		public abstract MahoutCollaborativeFilteringResponseTimePredictor<Collaborator, Message, ThreadType>
				createCollaborativeFilteringPredictor(IBasicFeatureRule userFeature,
						IBasicFeatureRule itemFeature,
						ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties);
		
		@Override
		public MessageResponseTimePredictor<Collaborator, Message, ThreadType> create(
				List<IBasicFeatureRule> features,
				ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties)
				throws Exception {

			if (features.size() != 2) {
				throw new Exception(
						"Must have exactly two features. One for the user and one for the item");
			}

			return createCollaborativeFilteringPredictor(features.get(0),
					features.get(1), threadsProperties);
		}
	}
	
	public MahoutCollaborativeFilteringResponseTimePredictor(String title,
			IBasicFeatureRule userFeatureRule,
			IBasicFeatureRule itemFeatureRule,
			ThreadSetProperties<Collaborator,Message,ThreadType> threadsProperties) {
		this.title = title;
		this.userFeatureRule = userFeatureRule;
		this.itemFeatureRule = itemFeatureRule;
		this.threadsProperties = threadsProperties;
	}
	
	/**
	 * Gets the title of the predictor
	 * @return the title
	 */
	public String getTitle() {
		return title + "_" + userFeatureRule.getDestFeatureName() + "_"
				+ itemFeatureRule.getDestFeatureName();
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
			extractor = new MessageIntermediateRecommendationDataSetExtractor<>(threadsProperties);
			
			IBasicFeatureRule[] predictorRules = {userFeatureRule, itemFeatureRule};
			
			IBasicFeatureRule[] predictableRules = new IBasicFeatureRule[1];
			predictableRules[0] = new SecondsToFirstResponseRule("responseTime");
			
			IntermediateDataSet dataSet = extractor.extractAllIntermediateData(pastThreads, "liveness",
					predictorRules, predictableRules, new MahoutDataInitializer());
			dataSet.setTargetIndex();
			snmlModel.train(dataSet, null);
		}
	}
	
	@Override
	public ResponseTimeRange predictResponseTime(ThreadType thread) throws Exception {
		train();
		
		IBasicFeatureRule[] predictorRules = {userFeatureRule, itemFeatureRule};
		
		IBasicFeatureRule[] predictableRules = new IBasicFeatureRule[1];
		predictableRules[0] = new SecondsToFirstResponseRule("responseTime");
		
		IntermediateData instance = extractor.extractSingleItem(thread, "liveness_test_item", predictorRules, predictableRules, new MahoutDataInitializer());
		Object result =  snmlModel.extract(instance);
		Double prediction = null;
		if (result instanceof String) {
			prediction = Double.parseDouble((String) result);
		} else if (result != null) {
			prediction = (double) (float) result;
		}
		
		return new ResponseTimeRange(prediction, null);
	}
	
	
	@Override
	public void evaluate(Collection<ThreadType> testThreads) throws Exception {
		train();
		
		IBasicFeatureRule[] predictorRules = {userFeatureRule, itemFeatureRule};
		
		IBasicFeatureRule[] predictableRules = new IBasicFeatureRule[1];
		predictableRules[0] = new SecondsToFirstResponseRule("responseTime");
		
		MahoutDataSet trainData = (MahoutDataSet) extractor.extractAllIntermediateData(pastThreads, "trainData", predictorRules, predictableRules, new MahoutDataInitializer());
		trainData.setTargetIndex();
		MahoutDataSet testData = (MahoutDataSet) extractor.extractAllIntermediateData(testThreads, "testData", predictorRules, predictableRules, new MahoutDataInitializer());
		testData.setTargetIndex();
		
		//TODO: implement evaluate(trainData, testData) for Mahout SNML model
	}

}
