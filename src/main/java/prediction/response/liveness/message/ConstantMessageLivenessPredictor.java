package prediction.response.liveness.message;

import java.util.ArrayList;
import java.util.Collection;

import prediction.features.messages.ThreadSetProperties;
import snml.rule.basicfeature.IBasicFeatureRule;
import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

public class ConstantMessageLivenessPredictor<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
		implements MessageLivenessPredictor<Collaborator, Message, ThreadType> {
	
	public static enum LivenessPrediction {
		Live,
		Dead
	}

	private LivenessPrediction prediction;
	private String title;
	private Collection<ThreadType> pastThreads = new ArrayList<>();
	
	public static <Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
			MessageLivenessPredictorFactory<Collaborator, Message, ThreadType>
			factory(final LivenessPrediction prediction,
					Class<Collaborator> collaboratorClass,
					Class<Message> messageClass,
					Class<ThreadType> threadClass) {

		return new MessageLivenessPredictorFactory<Collaborator, Message, ThreadType>() {

			@Override
			public MessageLivenessPredictor<Collaborator, Message, ThreadType> create(
					Collection<IBasicFeatureRule> features,
					ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties) {
				
				return new ConstantMessageLivenessPredictor<Collaborator,Message,ThreadType>(prediction);
			}
		};
	}
	
	public ConstantMessageLivenessPredictor(LivenessPrediction prediction) {
		this.title = "Constant prediction of "+prediction;
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
		if (prediction == LivenessPrediction.Live) {
			return true;
		} else if (prediction == LivenessPrediction.Dead) {
			return false;
		} else {
			return null;
		}
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
		return "Always predict "+prediction;
	}

}
