package jinjing.rule.superfeature;

import java.util.ArrayList;

import jinjing.rule.NominalFeatureRule;

/**
 * Abstract, superclass of all rules extracting nominal super features
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class NominalSuperFeatureRule extends NominalFeatureRule implements ISuperFeatureRule {
	
	/**
	   * Create an nominal super feature extracting rule
	   * Initialize feature value domain
	   * 
	   * @param destFeatureName name for extracted feature
	   * @param aDomain domain of feature value
	   */
	public NominalSuperFeatureRule(String featureName, ArrayList<String> aDomain){
		super(featureName, aDomain);
	}
	
	/*
	public NominalSuperFeatureRule(String featureName){
		super(featureName);
	}
	*/
	
	

}
