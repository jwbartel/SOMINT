package snml.rule.superfeature.model;

import snml.rule.superfeature.NumericSuperFeatureRule;

/**
 * Abstract, superclass of all model extracting numeric feature
 * Especially for regression models
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class NumericModelRule extends NumericSuperFeatureRule implements
IModelRule {

	/**
	 * Initialize name for extracted feature
	 * 
	 * @param featureName  name for extracted feature
	 */
	public NumericModelRule(String featureName) {
		super(featureName);
	}

}
