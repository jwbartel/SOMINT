package jinjing.rule.superfeature.copy;

import java.util.ArrayList;

import jinjing.rule.NominalFeatureRule;
import jinjing.rule.superfeature.ISuperFeatureRule;
import jinjingdataconvert.IntermediateData;

/**
 * Copy nominal feature from intermediate data
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class NominalCopySuperFeatureRule extends NominalFeatureRule implements ISuperFeatureRule {
	
	/** name of src feature to be copied */
	protected String srcAttrName;
	
	/**
	   * Create an nominal super feature copying rule
	   * 
	   * @param destFeatureName name for extracted feature
	   * @param srcAttrName name of src feature to be copied 
	   * @param aDomain domain of feature value
	   */
	public NominalCopySuperFeatureRule(String destFeatureName,
			String srcAttrName, ArrayList<String> aDomain) {
		super(destFeatureName, aDomain);
		this.srcAttrName = srcAttrName;
	}
	

	/**
	 * Extracted nominal feature value from an intermediate data instance
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
