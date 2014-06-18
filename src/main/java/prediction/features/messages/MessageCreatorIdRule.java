package prediction.features.messages;

import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;
import snml.dataimport.MessageData;
import snml.dataimport.ThreadData;
import snml.rule.NumericVectorFeatureRule;
import snml.rule.basicfeature.IBasicFeatureRule;

/**
 * Extract the numeric vector of whether an address appeared as the sender 
 * of the starting message of a thread
 */
public class MessageCreatorIdRule extends NumericVectorFeatureRule implements IBasicFeatureRule{
	
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
				return new MessageCreatorIdRule(featureName, threadsProperties.getCreators().size());
			}
		};
	}

	/**
	   * Create an binary feature rule for extracting sender id
	   * 
	   * @param destFeatureName name for extracted feature
	   * @param totalRecipientNum largest address id
	   */
	public MessageCreatorIdRule(String featureName, int largestSenderId) {
		super(featureName, largestSenderId);
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
		double[] ids = new double[length];
		MessageData msg = aThread.getKthEarlest(0);
		int[] creatorIds = (int[]) msg.getAttribute(MessageDataConfig.CREATORS);
		for (int creatorId : creatorIds) {
			ids[creatorId-1]=1;
		}
		
		return ids;
	}

}
