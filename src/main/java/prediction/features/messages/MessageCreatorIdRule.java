package prediction.features.messages;

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
