package prediction.response.time.message;

import java.util.Collection;

import prediction.response.time.ResponseTimeRange;
import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

public interface MessageResponseTimePredictor<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>> {

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
	 * Predicts when a thread will have a response
	 * @param thread
	 * 			The thread to predict response time for
	 * @return A Double object representing the time in seconds until a reply is expected. Null if no prediction cannot be made  
	 * @throws Exception 
	 */
	public ResponseTimeRange predictResponseTime(ThreadType thread) throws Exception;
	
	/**
	 * Trains the underlying model of the predictor
	 * @throws Exception 
	 */
	public void train() throws Exception;
	
	/**
	 * Validates any necessary weights for the predictor
	 * @param validationSet The set of threads to use for validation
	 * @throws Exception 
	 */
	public void validate(Collection<ThreadType> validationSet) throws Exception;
	
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
