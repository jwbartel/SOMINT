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
import snml.rule.basicfeature.IBasicFeatureRule;
import snml.rule.superfeature.model.weka.WekaRegressionModelRule;
import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

public class MessageRegressionLivenessPredictor<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
		implements MessageLivenessPredictor<Collaborator, Message, ThreadType> {

	private MessageIntermediateDataSetExtractor<Collaborator, Message, ThreadType> extractor = null;
	private Collection<ThreadType> pastThreads = new ArrayList<>();
	private IBasicFeatureRule[] featureRules;
	private WekaRegressionModelRule snmlModel;
	private ThreadSetProperties<Collaborator,Message,ThreadType> threadsProperties;
	
	public static <Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
			MessageLivenessPredictorFactory<Collaborator, Message, ThreadType>
			factory(final WekaRegressionModelRule snmlModel,
					Class<Collaborator> collaboratorClass, Class<Message> messageClass,
					Class<ThreadType> threadClass) {

		return new MessageLivenessPredictorFactory<Collaborator, Message, ThreadType>() {

			@Override
			public MessageLivenessPredictor<Collaborator, Message, ThreadType> create(
					Collection<IBasicFeatureRule> features,
					ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties) {
				
				IBasicFeatureRule[] featureArray = features.toArray(new IBasicFeatureRule[0]);
				return new MessageRegressionLivenessPredictor<>(snmlModel, featureArray, threadsProperties);
			}
		};
	}
	
	public MessageRegressionLivenessPredictor(WekaRegressionModelRule snmlModel,
			IBasicFeatureRule[] featureRules,
			ThreadSetProperties<Collaborator,Message,ThreadType> threadsProperties) {
		this.snmlModel = snmlModel;
		this.featureRules = featureRules;
		this.threadsProperties = threadsProperties;
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
			predictableRules[0] = new MessageLivenessRule("hasResponse");
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
		return ((double) snmlModel.extract(instance)) > 0.5;
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

}
