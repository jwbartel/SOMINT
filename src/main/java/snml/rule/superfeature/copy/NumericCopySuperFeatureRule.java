package snml.rule.superfeature.copy;

import snml.dataconvert.IntermediateData;
import snml.rule.NumericFeatureRule;
import snml.rule.superfeature.ISuperFeatureRule;

/**
 * Copy numeric feature from intermediate data
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class NumericCopySuperFeatureRule extends NumericFeatureRule implements ISuperFeatureRule {

	/** name of src feature to be copied */
	protected String srcAttrName;
	
	/**
	   * Create an numeric super feature copying rule
	   * 
	   * @param destFeatureName name for extracted feature
	   * @param srcAttrName name of src feature to be copied 
	   */
	public NumericCopySuperFeatureRule(String destFeatureName,
			String srcAttrName) {
		super(destFeatureName);
		this.srcAttrName = srcAttrName;
	}


	/**
	 * Copy numeric feature value from an intermediate data instance
	 * 
	 * @param anInstData the source intermediate data instance
	 * @return extracted feature value
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(IntermediateData anInstData) throws Exception {
		return anInstData.getNumericAttrValue(srcAttrName);
	}


}
