package jinjing.rule.basicfeature.copyraw;

import java.util.ArrayList;

import jinjing.dataimport.ThreadData;
import jinjing.rule.NominalFeatureRule;
import jinjing.rule.basicfeature.IBasicFeatureRule;

/**
 * Copy nominal attribute from message in thread data as feature
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class NominalRawFeatureRule extends NominalFeatureRule implements IBasicFeatureRule {

	/** raw rule to copy attributes */
	RawFeatureRule rawRule;
	
	/**
	 * Create a nominal feature copying rule
	 * Initialize listed variables
	 * 
	 * @param destFeatureName name for extracted feature
	 * @param srcAttrName name of attribute to copy
	 * @param dateFormat date format of attribute to copy
	 * @param inOrder select message in earlist or latest order
	 * @param kth select kth message
	 */
	public NominalRawFeatureRule(String destFeatureName, String srcAttrName, ArrayList<String> aDomain,
			int inOrder, int kth) {
		super(destFeatureName, aDomain);
		
		rawRule = new RawFeatureRule(destFeatureName, srcAttrName, inOrder, kth);
	}
	
	/**
	 * Create a nominal feature copying rule
	 * Copy from default earliest message
	 * Initialize listed variables
	 * 
	 * @param destFeatureName name for extracted feature
	 * @param srcAttrName name of attribute to copy
	 * @param aDomain domain of feature value
	 */
	public NominalRawFeatureRule(String destFeatureName, String srcAttrName, ArrayList<String> aDomain) {
		super(destFeatureName, aDomain);
		
		rawRule = new RawFeatureRule(destFeatureName, srcAttrName, RawFeatureRule.ACCENDING, 0);
	}
	
	/**
	 * Extracted nominal feature value from a thread data
	 * 
	 * @param aThread the source thread data
	 * @return extracted feature value
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {
		Object val = rawRule.extract(aThread);
		checkValid(val);
		
		return val;
	}
	
}
