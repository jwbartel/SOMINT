package prediction.response.liveness.message;

import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

public interface MessageLivenessPredictor<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>> {

	/**
	 * Adds a thread to train the model
	 * @param thread
	 * 			The past thread to be used for training
	 */
	public void addPastThread(ThreadType thread);
	
	/**
	 * Predicts whether a thread is alive or will have more responses
	 * @param thread
	 * 			The thread to predict liveness for
	 * @return Boolean of whether more responses will occur or not. null if
	 * 			it is not possible to recommend liveness.
	 */
	public Boolean predictLiveness(ThreadType thread);
	
	/**
	 * Trains the underlying model of the predictor
	 */
	public void train();
	
}
