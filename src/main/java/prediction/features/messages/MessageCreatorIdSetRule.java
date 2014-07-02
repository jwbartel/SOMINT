package prediction.features.messages;

import snml.dataimport.MessageData;
import snml.dataimport.ThreadData;
import snml.rule.ObjectSetFeatureRule;
import snml.rule.basicfeature.IBasicFeatureRule;
import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

/**
 * Extract the numeric vector of the ids of creators of the thread
 */
public class MessageCreatorIdSetRule extends ObjectSetFeatureRule implements IBasicFeatureRule{
	
	/**
	 * Create a factory to generate instances of the feature
	 * @param featureName
	 * 			The name of the feature
	 * @return The factory for the feature
	 */
	public static <Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
		FeatureRuleFactory<Collaborator, Message, ThreadType> factory(
				Class<Collaborator> collaboratorClass,
				Class<Message> messageClass,
				Class<ThreadType> threadClass,
				final String featureName) {
		
		return new FeatureRuleFactory<Collaborator, Message, ThreadType>() {

			@Override
			public IBasicFeatureRule create(
					ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties) {
				return new MessageCreatorIdSetRule(featureName);
			}
		};
	}

	/**
	   * Create an binary feature rule for extracting sender id
	   * 
	   * @param destFeatureName name for extracted feature
	   */
	public MessageCreatorIdSetRule(String featureName) {
		super(featureName);
	}

	/**
	 * Extract the numeric vector of whether an address appeared as 
	 * the sender of the starting message of given thread
	 * 
	 * @param aThread the source thread data
	 * @return double array of whether addresses are a creator
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {
		MessageData msg = aThread.getKthEarlest(0);
		int[] creatorIds = (int[]) msg.getAttribute(MessageDataConfig.CREATORS);
		return creatorIds;
	}

}
