package snml.rule;

import java.util.ArrayList;

/**
 * subclass of NominalFeatureRule extracting binary features
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class BinaryFeatureRule extends NominalFeatureRule {

	/**
	   * Create an binary feature extracting rule
	   * Initialize feature value domain as {y, n}
	   * 
	   * @param destFeatureName name for extracted feature
	   */
	public BinaryFeatureRule(String destFeatureName) {
		super(destFeatureName);
		domain.add("y");
		domain.add("n");
	}
	
	/**
	   * Create an binary feature extracting rule
	   * Initialize feature value domain
	   * 
	   * @param destFeatureName name for extracted feature
	   * @param aDomain domain of feature value
	   */
	public BinaryFeatureRule(String destFeatureName, ArrayList<String> aDomain) {
		super(destFeatureName, aDomain);
	}

}
