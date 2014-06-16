package jinjing.rule.superfeature.model;

import jinjing.rule.superfeature.NumericVectorSuperFeatureRule;

/**
 * Abstract, superclass of all model extracting numeric vector feature
 * Especially for topic modeling models
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class NumericVectorModelRule extends NumericVectorSuperFeatureRule implements IModelRule{

	/**
	 * Initialize name and vector length for extracted feature
	 * 
	 * @param featureName  name for extracted feature
	 * @param l length of numeric vector
	 */
	public NumericVectorModelRule(String featureName, int l) {
		super(featureName, l);
	}

}
