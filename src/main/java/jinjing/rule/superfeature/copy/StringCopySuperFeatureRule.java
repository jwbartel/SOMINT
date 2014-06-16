package jinjing.rule.superfeature.copy;

import jinjing.dataconvert.IntermediateData;
import jinjing.rule.StringFeatureRule;
import jinjing.rule.superfeature.ISuperFeatureRule;

/**
 * Copy string feature from intermediate data
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class StringCopySuperFeatureRule extends StringFeatureRule implements ISuperFeatureRule {
	
	/** name of src feature to be copied */
	protected String srcAttrName;
	
	/**
	   * Create an string super feature copying rule
	   * 
	   * @param destFeatureName name for extracted feature
	   * @param srcAttrName name of src feature to be copied 
	   */
	public StringCopySuperFeatureRule(String destFeatureName,
			String srcAttrName) {
		super(destFeatureName);
		this.srcAttrName = srcAttrName;
	}
	
	
	/**
	 * Copy string feature value from an intermediate data instance
	 * 
	 * @param anInstData the source intermediate data instance
	 * @return extracted feature value
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(IntermediateData anInstData) throws Exception {
		return anInstData.getStringAttrValue(srcAttrName);
	}

}
