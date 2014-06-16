package jinjing.rule.basicfeature;


import jinjing.dataimport.ThreadData;
import jinjing.rule.BinaryFeatureRule;

/**
 * subclass of BinaryFeatureRule deciding if a thread has response message
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class ContainsFollowMessageRule extends BinaryFeatureRule implements IBasicFeatureRule{

	/**
	   * Create an rule for deciding where contains follow message
	   * Feature value domain is initialized as {y, n}
	   * 
	   * @param destFeatureName name for extracted feature
	   */
	public ContainsFollowMessageRule(String destFeatureName) {
		super(destFeatureName);
	}

	/**
	 * Decide if given thread data has follow messages
	 * 
	 * @param aThread the source thread data
	 * @return whether the thread data has follow messages
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {
		if(aThread==null) return this.domain.get(1);
		double msgNum = aThread.size();
		if(msgNum >= 2){
			return this.domain.get(0);
		}else{
			return this.domain.get(1);
		}
	}

}
