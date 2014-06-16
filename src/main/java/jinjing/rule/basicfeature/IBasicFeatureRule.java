package jinjing.rule.basicfeature;

import jinjing.dataimport.ThreadData;
import jinjing.rule.IFeatureRule;

/**
 * Interface of all basic feature extracting (from thread) rules
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public interface IBasicFeatureRule extends IFeatureRule{
	
	/**
	 * Extracted feature value from a thread data
	 * 
	 * @param aThread the source thread data
	 * @return extracted feature value
	 * @throws Exception when extracted value is invalid
	 */
	public Object extract(ThreadData aThread) throws Exception;
	
}
