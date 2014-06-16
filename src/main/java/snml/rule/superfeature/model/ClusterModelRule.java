package snml.rule.superfeature.model;

import java.util.ArrayList;

import snml.rule.superfeature.NominalSuperFeatureRule;

/**
 * Abstract, superclass of all cluster model extracting feature rules
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class ClusterModelRule extends NominalSuperFeatureRule implements IModelRule{

	/**
	   * Create a cluster model extracting feature rules
	   * Initialize name for extracted feature
	   * 
	   * @param featureName name for extracted feature
	   * @param aDomain domain of classes' names
	   */
	public ClusterModelRule(String featureName, ArrayList<String> aDomain) {
		super(featureName, aDomain);
	}
	
	/**
	   * Create a cluster model extracting feature rules
	   * Initialize name for extracted feature
	   * Initialize domain as 1, 2, ..., classNum
	   * 
	   * @param featureName name for extracted feature
	   * @param classNum number of total classes
	   */
	public ClusterModelRule(String featureName, int clusterNum) {
		super(featureName, new ArrayList<String>(clusterNum));
		for(int i=0; i< clusterNum; i++){
			domain.add(String.valueOf(i));
		}
	}

}
