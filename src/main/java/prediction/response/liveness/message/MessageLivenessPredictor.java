package prediction.response.liveness.message;

import java.util.Collection;

import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

public interface MessageLivenessPredictor<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>> {

	/**
	 * Gets the title of the predictor
	 * @return the title
	 */
	public String getTitle();
	
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
	 * @throws Exception 
	 */
	public Boolean predictLiveness(ThreadType thread) throws Exception;
	
	/**
	 * Trains the underlying model of the predictor
	 * @throws Exception 
	 */
	public void train() throws Exception;
	
	/**
	 * Runs the evaluation according to weka
	 * @param testThreads Threads to evaluate with
	 * @throws Exception
	 */
	public void evaluate(Collection<ThreadType> testThreads) throws Exception;
	
	/**
	 * Prints a description of the resultant model used to make predictions
	 * @return A human-readable String description of the model
	 * @throws Exception
	 */
	public String getModelInfo() throws Exception;
	
}
