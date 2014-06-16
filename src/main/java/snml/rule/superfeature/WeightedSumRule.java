package snml.rule.superfeature;

import snml.dataconvert.IntermediateData;

/**
 * Calculate the weighted sum of all attributes of  a intermediate data
 * The attributes must be numeric 
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class WeightedSumRule extends NumericSuperFeatureRule implements ISuperFeatureRule{

	/** weights for sum */
	double[] weight;
	
	/**
	 * Create a WeightedSumRule
	 * 
	 * @param featureName name for extracted feature
	 * @param weight 
	 */
	public WeightedSumRule(String featureName, double[] weight) {
		super(featureName);
		this.weight = weight;
	}

	@Override
	public Object extract(IntermediateData anInstData) throws Exception {
		int n = weight.length;
		double sum = 0;
		for(int i=0; i<n; i++){
			sum += weight[i] * anInstData.getNumericAttrValue(i);
		}
		return sum;
	}

}
