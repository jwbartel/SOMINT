package jinjing.rule.superfeature.copy;

import jinjing.rule.NumericVectorFeatureRule;
import jinjing.rule.superfeature.ISuperFeatureRule;
import jinjingdataconvert.IntermediateData;

/**
 * Copy numeric vector feature from intermediate data
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class NumericVectorCopySuperFeatureRule 
	extends NumericVectorFeatureRule  
	implements ISuperFeatureRule {

	/** name of src feature to be copied */
	protected String srcAttrName;
	
	/**
	   * Create an numeric vector super feature copying rule
	   * 
	   * @param destFeatureName name for extracted feature
	   * @param srcAttrName name of src feature to be copied 
	   * @param l length of vector
	   */
	public NumericVectorCopySuperFeatureRule(String destFeatureName,
			String srcAttrName, int length) {
		super(destFeatureName, length);
		this.srcAttrName = srcAttrName;
	}
	
	
	/**
	 * Copy nominal vector feature value from an intermediate data instance
	 * 
	 * @param anInstData the source intermediate data instance
	 * @return extracted feature value
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(IntermediateData anInstData) throws Exception {
		double[] val = new double[length];
		for(int i=0; i<length; i++){
			val[i] = anInstData.getNumericAttrValue(srcAttrName+i);
		}
		return val;
	}

}
