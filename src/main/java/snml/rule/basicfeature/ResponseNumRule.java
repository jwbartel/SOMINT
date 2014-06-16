
package snml.rule.basicfeature;

import snml.dataimport.ThreadData;
import snml.rule.NumericFeatureRule;

/**
 * Count the number of response messages
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class ResponseNumRule extends NumericFeatureRule implements IBasicFeatureRule{

	/**
	   * Create an ResponseNumRule
	   * 
	   * @param destFeatureName name for extracted feature
	   */
	public ResponseNumRule(String destFeatureName) {
		super(destFeatureName);
	}

	/**
	 * Extract the number of response messages of given thread
	 * 
	 * @param aThread the source thread data
	 * @return the number of response messages 
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {
		int responseNum = aThread.size()-1;	
		return responseNum;
	}

}
