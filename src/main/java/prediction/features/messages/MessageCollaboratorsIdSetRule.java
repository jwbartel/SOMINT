package prediction.features.messages;

import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;
import snml.dataimport.MessageData;
import snml.dataimport.ThreadData;
import snml.rule.NumericVectorFeatureRule;
import snml.rule.basicfeature.IBasicFeatureRule;

/**
 * Extract the numeric vector of collaborator ids that occurred in the message
 */
public class MessageCollaboratorsIdSetRule extends NumericVectorFeatureRule implements
		IBasicFeatureRule {
	
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
				return new MessageCollaboratorsIdSetRule(featureName,
						threadsProperties.getCollaborators().size());
			}
		};
	}

	/**
	 * Create an binary feature rule for extracting recipient id
	 * 
	 * @param destFeatureName
	 *            name for extracted feature
	 * @param totalRecipientNum
	 *            largest address id
	 */
	public MessageCollaboratorsIdSetRule(String featureName, int totalRecipientNum) {
		super(featureName, totalRecipientNum);
	}

	/**
	 * Extract the numeric vector of whether an address is a collaborator of a
	 * given thread
	 * 
	 * @param aThread
	 *            the source thread data
	 * @return double array of whether addresses are collaborators
	 * @throws Exception
	 *             when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {

		MessageData msg = aThread.getKthEarlest(0);
		int[] collaborators = (int[]) msg.getAttribute(MessageDataConfig.COLLABORATORS);
		return collaborators;
	}

}
