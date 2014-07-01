package prediction.response.time.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.mail.MessagingException;

import prediction.features.messages.MessageIntermediateDataSetExtractor;
import prediction.features.messages.SecondsToFirstResponseRule;
import prediction.features.messages.ThreadSetProperties;
import prediction.response.time.ResponseTimeRange;
import snml.dataconvert.IntermediateData;
import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.weka.WekaDataInitializer;
import snml.dataconvert.weka.WekaDataSet;
import snml.rule.basicfeature.IBasicFeatureRule;
import snml.rule.superfeature.model.weka.WekaRegressionModelRule;
import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

public class WekaRegressionMessageResponseTimePredictor<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
		implements MessageResponseTimePredictor<Collaborator, Message, ThreadType> {

	private String title;
	
	private MessageIntermediateDataSetExtractor<Collaborator, Message, ThreadType> extractor = null;
	private Collection<ThreadType> pastThreads = new ArrayList<>();
	private IBasicFeatureRule[] featureRules;
	private WekaRegressionModelRule snmlModel;
	private ThreadSetProperties<Collaborator,Message,ThreadType> threadsProperties;
	
	public static <Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
			MessageResponseTimePredictorFactory<Collaborator, Message, ThreadType>
			factory(final String title,
					final WekaRegressionModelRule snmlModel,
					Class<Collaborator> collaboratorClass, Class<Message> messageClass,
					Class<ThreadType> threadClass) {

		return new MessageResponseTimePredictorFactory<Collaborator, Message, ThreadType>() {

			@Override
			public MessageResponseTimePredictor<Collaborator, Message, ThreadType> create(
					List<IBasicFeatureRule> features,
					ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties) {
				
				IBasicFeatureRule[] featureArray = features.toArray(new IBasicFeatureRule[0]);
				return new WekaRegressionMessageResponseTimePredictor<>(title, snmlModel, featureArray, threadsProperties);
			}
		};
	}
	
	public WekaRegressionMessageResponseTimePredictor(String title,
			WekaRegressionModelRule snmlModel,
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
			IBasicFeatureRule[] predictableRules = new IBasicFeatureRule[1];
			predictableRules[0] = new SecondsToFirstResponseRule("responseTime");
			IntermediateDataSet dataSet = extractor.extractAllIntermediateData(pastThreads, "responseTime",
					featureRules, predictableRules, new WekaDataInitializer());
			dataSet.setTargetIndex();
			snmlModel.train(dataSet, null);;
		}
	}

	@Override
	public void validate(Collection<ThreadType> validationSet) throws Exception {
		train();
	}
	
	@Override
	public ResponseTimeRange predictResponseTime(ThreadType thread) throws Exception {
		train();
		IBasicFeatureRule[] predictableRules = new IBasicFeatureRule[1];
		predictableRules[0] = new SecondsToFirstResponseRule("responseTime");
		IntermediateData instance = extractor.extractSingleItem(thread, "liveness_test_item", featureRules, predictableRules, new WekaDataInitializer());
		Object result =  snmlModel.extract(instance);
		Double prediction;
		if (result instanceof String) {
			prediction = Double.parseDouble((String) result);
		} else {
			prediction = (double) result;
		}
		
		return new ResponseTimeRange(prediction, null);
	}
	
	
	@Override
	public void evaluate(Collection<ThreadType> testThreads) throws Exception {
		train();
		IBasicFeatureRule[] predictableRules = new IBasicFeatureRule[1];
		predictableRules[0] = new SecondsToFirstResponseRule("responseTime");
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
		return snmlModel.getClassifier().toString();
	}

}
