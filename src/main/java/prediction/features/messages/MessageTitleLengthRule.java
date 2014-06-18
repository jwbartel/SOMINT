package prediction.features.messages;

import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;
import snml.dataimport.MessageData;
import snml.dataimport.ThreadData;
import snml.rule.NumericFeatureRule;
import snml.rule.basicfeature.IBasicFeatureRule;

/**
 * Extract the char-length of starting email's subject
 */
public class MessageTitleLengthRule extends NumericFeatureRule implements IBasicFeatureRule{
	
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
				return new MessageTitleLengthRule(featureName);
			}
		};
	}

	/**
	   * Create an EmailSubjectLengthRule
	   * 
	   * @param destFeatureName name for extracted feature
	   */
	public MessageTitleLengthRule(String destFeatureName) {
		super(destFeatureName);
	}

	/**
	 * Extract the char-length of starting email's subject
	 * from given thread
	 * 
	 * @param aThread the source thread data
	 * @return the char-length of starting message's subject 
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {
		
		MessageData msg = aThread.getKthEarlest(0);
		String subj = (String)msg.getAttribute(MessageDataConfig.TITLE);
		if(subj==null) return 0;
		
		double length = subj.length();
		
		return length;
	}

}
