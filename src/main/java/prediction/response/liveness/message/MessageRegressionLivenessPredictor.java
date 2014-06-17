package prediction.response.liveness.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import prediction.features.messages.MessageIntermediateDataSetExtractor;
import snml.dataconvert.IntermediateData;
import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.WekaDataInitializer;
import snml.rule.basicfeature.ContainsFollowMessageRule;
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
	private Set<String> stopWords;
	
	public MessageRegressionLivenessPredictor(WekaRegressionModelRule snmlModel,
			IBasicFeatureRule[] featureRules,
			Set<String> stopWords) {
		this.snmlModel = snmlModel;
		this.featureRules = featureRules;
		this.stopWords = stopWords;
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
			extractor = new MessageIntermediateDataSetExtractor<>(pastThreads, stopWords);
			IBasicFeatureRule[] predictableRules = new IBasicFeatureRule[1];
			predictableRules[0] = new ContainsFollowMessageRule("hasResponse");
			IntermediateDataSet dataSet = extractor.extractAllIntermediateData(pastThreads, "liveness",
					featureRules, predictableRules, new WekaDataInitializer());
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
		predictableRules[0] = new ContainsFollowMessageRule("hasResponse");
		IntermediateData instance = extractor.extractSingleItem(thread, "liveness_test_item", featureRules, predictableRules, new WekaDataInitializer());
		return ((double) snmlModel.extract(instance)) > 0.5;
	}

}
