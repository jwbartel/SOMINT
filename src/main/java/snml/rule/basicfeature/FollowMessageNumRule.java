package snml.rule.basicfeature;

import snml.dataimport.ThreadData;
import snml.rule.NumericFeatureRule;

/**
 * Extract the number of response messages of a thread
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class FollowMessageNumRule extends NumericFeatureRule implements IBasicFeatureRule{

	/**
	   * Create an FollowMessageNumRule
	   * 
	   * @param destFeatureName name for extracted feature
	   */
	public FollowMessageNumRule(String destFeatureName) {
		super(destFeatureName);
	}

	/**
	 * Extract the number of response messages from given thread
	 * 
	 * @param aThread the source thread data
	 * @return the number of response messages, 
	 * 			-1 if aThread is null or contains no msgdata
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {
		if(aThread==null) return -1.0;
		
		double msgNum = aThread.size();	
		
		return msgNum-1;
	}

}
