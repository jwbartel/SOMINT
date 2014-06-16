package snml.rule.basicfeature.copyraw;

import snml.dataimport.ThreadData;
import snml.rule.StringFeatureRule;
import snml.rule.basicfeature.IBasicFeatureRule;

/**
 * Copy string attribute from message in thread data as feature
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class StringRawFeatureRule extends StringFeatureRule implements
		IBasicFeatureRule {

	/** raw rule to copy attributes */
	RawFeatureRule rawRule;
	
	/**
	 * Create a string feature copying rule
	 * Initialize listed variables
	 * 
	 * @param destFeatureName name for extracted feature
	 * @param srcAttrName name of attribute to copy
	 * @param inOrder select message in earlist or latest order
	 * @param kth select kth message
	 */
	public StringRawFeatureRule(String destFeatureName, String srcAttrName,
			int inOrder, int kth) {
		super(destFeatureName);
		
		rawRule = new RawFeatureRule(destFeatureName, srcAttrName, inOrder, kth);
	}
	
	/**
	 * Create a string feature copying rule
	 * Copy from default earliest message
	 * Initialize listed variables
	 * 
	 * @param destFeatureName name for extracted feature
	 * @param srcAttrName name of attribute to copy
	 */
	public StringRawFeatureRule(String destFeatureName, String srcAttrName) {
		super(destFeatureName);
		
		rawRule = new RawFeatureRule(destFeatureName, srcAttrName, RawFeatureRule.ACCENDING, 0);
	}
	
	/**
	 * Extracted string feature value from a thread data
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
