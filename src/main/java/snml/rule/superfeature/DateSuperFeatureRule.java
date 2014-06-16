package snml.rule.superfeature;


import snml.rule.DateFeatureRule;

/**
 * Abstract, superclass of all rules extracting date super features
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class DateSuperFeatureRule extends DateFeatureRule implements ISuperFeatureRule {

	/**
	   * Create an date super feature extracting rule
	   * Initialize date feature format
	   * 
	   * @param destFeatureName name for extracted feature
	   * @param dateFormat formate of date it process
	   */
	public DateSuperFeatureRule(String destFeatureName, String dateFormat) {
		super(destFeatureName, dateFormat);
	}

	

}
