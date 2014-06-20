package prediction.response.liveness.message;

import java.util.ArrayList;
import java.util.Collection;

import prediction.features.messages.MessageIntermediateDataSetExtractor;
import prediction.features.messages.MessageLivenessRule;
import prediction.features.messages.ThreadSetProperties;
import snml.dataconvert.IntermediateData;
import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.WekaDataInitializer;
import snml.dataconvert.WekaDataSet;
import snml.rule.basicfeature.ContainsFollowMessageRule;
import snml.rule.basicfeature.IBasicFeatureRule;
import snml.rule.superfeature.model.weka.WekaClassifyModelRule;
import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

public class WekaMessageLivenessPredictor<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
		implements MessageLivenessPredictor<Collaborator, Message, ThreadType> {

	private String title;
	
	private MessageIntermediateDataSetExtractor<Collaborator, Message, ThreadType> extractor = null;
	private Collection<ThreadType> pastThreads = new ArrayList<>();
	private IBasicFeatureRule[] featureRules;
	private WekaClassifyModelRule snmlModel;
	private ThreadSetProperties<Collaborator,Message,ThreadType> threadsProperties;
	
	public static <Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
			MessageLivenessPredictorFactory<Collaborator, Message, ThreadType>
			factory(final String title,
					final WekaClassifyModelRule snmlModel,
					Class<Collaborator> collaboratorClass, Class<Message> messageClass,
					Class<ThreadType> threadClass) {

		return new MessageLivenessPredictorFactory<Collaborator, Message, ThreadType>() {

			@Override
			public MessageLivenessPredictor<Collaborator, Message, ThreadType> create(
					Collection<IBasicFeatureRule> features,
					ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties) {
				
				IBasicFeatureRule[] featureArray = features.toArray(new IBasicFeatureRule[0]);
				return new WekaMessageLivenessPredictor<>(title, snmlModel, featureArray, threadsProperties);
			}
		};
	}
	
	public WekaMessageLivenessPredictor(String title,
			WekaClassifyModelRule snmlModel,
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
		pastThreads.add(thread);
		extractor = null;
	}
	
	/**
	 * Trains the underlying model of the predictor
	 * @throws Exception 
	 */
	public void train() throws Exception {
		if (extractor == null) {
			extractor = new MessageIntermediateDataSetExtractor<>(threadsProperties);
			IBasicFeatureRule[] predictableRules = new IBasicFeatureRule[1];
			predictableRules[0] = new ContainsFollowMessageRule("hasResponse");
			IntermediateDataSet dataSet = extractor.extractAllIntermediateData(pastThreads, "liveness",
					featureRules, predictableRules, new WekaDataInitializer());
			dataSet.setTargetIndex();
			snmlModel.train(dataSet, null);;
		}
	}
	
	/**
	 * Predicts whether a thread is alive or will have more responses
	 * @param thread
	 * 			The thread to predict liveness for
	 * @return Boolean of whether more responses will occur or not. null if
	 * 			it is not possible to recommend liveness.
	 * @throws Exception 
	 */
	@Override
	public Boolean predictLiveness(ThreadType thread) throws Exception {
		train();
		IBasicFeatureRule[] predictableRules = new IBasicFeatureRule[1];
		predictableRules[0] = new MessageLivenessRule("hasResponse");
		IntermediateData instance = extractor.extractSingleItem(thread, "liveness_test_item", featureRules, predictableRules, new WekaDataInitializer());
		Object result =  snmlModel.extract(instance);
		Double prediction;
		if (result instanceof String) {
			prediction = Double.parseDouble((String) result);
		} else {
			prediction = (double) result;
		}
		
		return (prediction) > 0.5;
	}
	
	
	@Override
	public void evaluate(Collection<ThreadType> testThreads) throws Exception {
		train();
		IBasicFeatureRule[] predictableRules = new IBasicFeatureRule[1];
		predictableRules[0] = new MessageLivenessRule("hasResponse");
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
