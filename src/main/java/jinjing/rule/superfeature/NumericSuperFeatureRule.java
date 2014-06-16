package jinjing.rule.superfeature;

import jinjing.rule.NumericFeatureRule;

/**
 * Abstract, superclass of all rules extracting numeric super features
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class NumericSuperFeatureRule extends NumericFeatureRule implements ISuperFeatureRule {

	/**
	   * Create a numeric-type super feature extracting rule
	   * Initialize name for extracted feature
	   * 
	   * @param destFeatureName name for extracted feature
	   */
	public NumericSuperFeatureRule(String destFeatureName) {
		super(destFeatureName);
	}
	
}
