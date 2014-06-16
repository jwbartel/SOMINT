package snml.rule.basicfeature;

import snml.dataimport.MessageData;
import snml.dataimport.ThreadData;
import snml.dataimport.email.EmailDataConfig;
import snml.rule.NumericFeatureRule;

/**
 * Extract the number of recipients of the starting message 
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class EmailRecipientNumRule  extends NumericFeatureRule implements IBasicFeatureRule{

	/**
	   * Create an EmailRecipientNumRule
	   * 
	   * @param destFeatureName name for extracted feature
	   */
	public EmailRecipientNumRule(String destFeatureName) {
		super(destFeatureName);
	}

	/**
	 * Extract the number of recipients of the starting message 
	 * from given thread
	 * 
	 * @param aThread the source thread data
	 * @return number of recipients of the starting message 
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {
		
		MessageData msg = aThread.getKthEarlest(0);
		int[] recipients = (int[]) msg.getAttribute(EmailDataConfig.RECIPIENTS);
		return recipients.length;
	}

}
