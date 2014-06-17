package prediction.response.liveness.message;

import java.util.ArrayList;
import java.util.Collection;

import snml.rule.superfeature.model.weka.WekaLinearRegressionModelRule;
import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

public class MessageRegressionLivenessPredictor<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
		implements MessageLivenessPredictor<Collaborator, Message, ThreadType> {

	private Collection<ThreadType> pastThreads = new ArrayList<>();
	private WekaLinearRegressionModelRule snmlModel;
	
	public MessageRegressionLivenessPredictor(WekaLinearRegressionModelRule snmlModel) {
		this.snmlModel = snmlModel;
	}
	
	
	/**
	 * Adds a thread to train the model
	 * @param thread
	 * 			The past thread to be used for training
	 */
	@Override
	public void addPastThread(ThreadType thread) {
		pastThreads.add(thread);
	}
	
	/**
	 * Trains the underlying model of the predictor
	 */
	public void train() {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Predicts whether a thread is alive or will have more responses
	 * @param thread
	 * 			The thread to predict liveness for
	 * @return Boolean of whether more responses will occur or not. null if
	 * 			it is not possible to recommend liveness.
	 */
	@Override
	public Boolean predictLiveness(ThreadType thread) {
		// TODO Auto-generated method stub
		return null;
	}

}
