package jinjing.rule.filterrule;

import jinjing.rule.BinaryFeatureRule;
import jinjing.rule.superfeature.ISuperFeatureRule;

/**
 * Abstract, superclass of all rules to create the subset of a intermediate dataset
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class IntermediateFilterRule extends BinaryFeatureRule implements ISuperFeatureRule{

	/**
	   * Create a intermediate data filtering rule
	   * 
	   * @param destFeatureName can be null
	   */
	public IntermediateFilterRule(String destFeatureName) {
		super(destFeatureName);
	}

}
