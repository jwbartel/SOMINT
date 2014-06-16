package jinjing.rule.superfeature;

import jinjing.rule.NumericVectorFeatureRule;

/**
 * Abstract, superclass of all rules extracting numeric vector super features
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class NumericVectorSuperFeatureRule extends NumericVectorFeatureRule implements ISuperFeatureRule {

	/**
	 * Create a numeric-type super feature extracting rule
	 * Initialize name for extracted feature, and certain length of feature vector
	 * 
	 * @param featureName
	 * @param l
	 */
	public NumericVectorSuperFeatureRule(String featureName, int l) {
		super(featureName, l);
	}
	

}
