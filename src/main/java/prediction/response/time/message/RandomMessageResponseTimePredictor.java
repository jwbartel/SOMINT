package prediction.response.time.message;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import prediction.features.messages.ThreadSetProperties;
import prediction.response.time.ResponseTimeRange;
import snml.rule.basicfeature.IBasicFeatureRule;
import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

public class RandomMessageResponseTimePredictor<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
		implements MessageResponseTimePredictor<Collaborator, Message, ThreadType> {

	private Double prediction;
	private String title;
	private Collection<ThreadType> pastThreads = new ArrayList<>();
	
	public static <Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
			MessageResponseTimePredictorFactory<Collaborator, Message, ThreadType>
			factory(Class<Collaborator> collaboratorClass,
					Class<Message> messageClass,
					Class<ThreadType> threadClass,
					final String label,
					final Double prediction) {

		return new MessageResponseTimePredictorFactory<Collaborator, Message, ThreadType>() {

			@Override
			public MessageResponseTimePredictor<Collaborator, Message, ThreadType> create(
					List<IBasicFeatureRule> features,
					ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties) {
				
				return new RandomMessageResponseTimePredictor<Collaborator,Message,ThreadType>(prediction, label);
			}
		};
	}
	
	public RandomMessageResponseTimePredictor(Double prediction, String label) {
		this.title = "Constant prediction of "+label;
		this.prediction = prediction;
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
	 * In this case, this is a no-op
	 * @param thread
	 * 			The past thread to be used for training
	 */
	@Override
	public void addPastThread(ThreadType thread) {
		pastThreads.add(thread);
	}
	
	/**
	 * Trains the underlying model of the predictor.
	 * In this case, this is a no-op
	 * @throws Exception 
	 */
	public void train() throws Exception {
	}

	@Override
	public void validate(Collection<ThreadType> validationSet) throws Exception {
		train();
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
	public ResponseTimeRange predictResponseTime(ThreadType thread) throws Exception {
		return new ResponseTimeRange(prediction, null);
	}
	
	
	/* (non-Javadoc)
	 * @see prediction.response.liveness.message.MessageLivenessPredictor#evaluate(java.util.Collection)
	 */
	@Override
	public void evaluate(Collection<ThreadType> testThreads) throws Exception {
	}
	
	/* (non-Javadoc)
	 * @see prediction.response.liveness.message.MessageLivenessPredictor#getModelInfo()
	 */
	@Override
	public String getModelInfo() throws Exception {
		return getTitle();
	}

}
