package snml.rule.filterrule;

import snml.rule.BinaryFeatureRule;
import snml.rule.basicfeature.IBasicFeatureRule;

/**
 * Abstract, superclass of all rules to create the subset of a thread dataset
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class ThreadFilterRule extends BinaryFeatureRule implements IBasicFeatureRule {

	/**
	   * Create a thread data filtering rule
	   * 
	   * @param destFeatureName can be null
	   */
	public ThreadFilterRule(String destFeatureName) {
		super(destFeatureName);
	}

}
