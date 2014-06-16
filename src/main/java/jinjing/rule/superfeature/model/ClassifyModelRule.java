package jinjing.rule.superfeature.model;

import java.util.ArrayList;

import jinjing.rule.superfeature.NominalSuperFeatureRule;

/**
 * Abstract, superclass of all classify model extracting feature rules
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class ClassifyModelRule extends NominalSuperFeatureRule
		implements IModelRule {

	/**
	   * Initialize name and value domain for extracted feature
	   * 
	   * @param featureName name for extracted feature
	   * @param aDomain domain of classes' names
	   */
	public ClassifyModelRule(String featureName, ArrayList<String> aDomain) {
		super(featureName, aDomain);
	}
	
	/**
	   * Initialize name for extracted feature
	   * Initialize domain as 1, 2, ..., classNum
	   * 
	   * @param featureName name for extracted feature
	   * @param classNum number of total classes
	   */
	public ClassifyModelRule(String featureName, int classNum) {
		super(featureName, null);
		domain = new ArrayList<String>(classNum);
		for(int i=0; i<classNum; i++){
			domain.add(String.valueOf(i));
		}
	}


}
