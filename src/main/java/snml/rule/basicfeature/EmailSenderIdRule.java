package snml.rule.basicfeature;

import snml.dataimport.MessageData;
import snml.dataimport.ThreadData;
import snml.dataimport.email.EmailDataConfig;
import snml.rule.NumericVectorFeatureRule;

/**
 * Extract the numeric vector of whether an address appeared as the sender 
 * of the starting message 
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class EmailSenderIdRule extends NumericVectorFeatureRule implements IBasicFeatureRule{

	/**
	   * Create an binary feature rule for extracting sender id
	   * 
	   * @param destFeatureName name for extracted feature
	   * @param totalRecipientNum largest address id
	   */
	public EmailSenderIdRule(String featureName, int largestSenderId) {
		super(featureName, largestSenderId);
	}

	/**
	 * Extract the numeric vector of whether an address appeared as 
	 * the sender of the starting message of given thread
	 * 
	 * @param aThread the source thread data
	 * @return double array of whether addresses are sender
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {
		double[] ids = new double[length];
		MessageData msg = aThread.getKthEarlest(0);
		int senderId = (int) msg.getAttribute(EmailDataConfig.FROM);
		ids[senderId-1]=1;
		
		return ids;
	}

}
