package snml.rule.basicfeature;

import snml.dataimport.MessageData;
import snml.dataimport.ThreadData;
import snml.dataimport.email.EmailDataConfig;
import snml.rule.NumericVectorFeatureRule;

/**
 * Extract the numeric vector of whether an address appeared as a recipient 
 * of the starting message 
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class EmailRecipientIdsRule extends NumericVectorFeatureRule implements IBasicFeatureRule{

	/**
	   * Create an binary feature rule for extracting recipient id
	   * 
	   * @param destFeatureName name for extracted feature
	   * @param totalRecipientNum largest address id
	   */
	public EmailRecipientIdsRule(String featureName, int totalRecipientNum) {
		super(featureName, totalRecipientNum);
	}

	/**
	 * Extract the numeric vector of whether an address is 
	 * a recipient of the starting message of given thread
	 * 
	 * @param aThread the source thread data
	 * @return double array of whether addresses are recipients
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {
		double[] val = new double[length];
		for(int i=0; i<val.length; i++){
			val[i]=0;
		}
		
		MessageData msg = aThread.getKthEarlest(0);
		int[] recipients = (int[]) msg.getAttribute(EmailDataConfig.RECIPIENTS);
		
		for(int i=0; i<recipients.length; i++){
			val[recipients[i]-1] = 1;
		}
		
		return val;
	}

}
