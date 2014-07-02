package prediction.features.messages;

import snml.dataimport.MessageData;
import snml.dataimport.ThreadData;
import snml.rule.ObjectSetFeatureRule;
import snml.rule.basicfeature.IBasicFeatureRule;
import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

/**
 * Extract the numeric vector of title word ids that occurred in the message
 */
public class MessageTitleWordIdSetRule extends ObjectSetFeatureRule implements IBasicFeatureRule {
	
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
				return new MessageTitleWordIdSetRule(featureName);
			}
		};
	}
	
	/**
	 * Create an binary feature rule for extracting title words
	 * 
	 * @param destFeatureName
	 *            name for extracted feature
	 * @param totalRecipientNum
	 *            largest address id
	 */
	public MessageTitleWordIdSetRule(String featureName) {
		super(featureName);
	}

	/**
	 * Extract the set of words in the title
	 * 
	 * @param aThread
	 *            the source thread data
	 * @return double array of whether words are in the title
	 * @throws Exception
	 *             when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {
		MessageData msg = aThread.getKthEarlest(0);
		String[] words = (String[]) msg.getAttribute(MessageDataConfig.TITLE_WORDS);

		return words;
	}

}
