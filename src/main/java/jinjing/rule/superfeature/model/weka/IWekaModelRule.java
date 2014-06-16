package jinjing.rule.superfeature.model.weka;

import jinjing.dataconvert.WekaDataSet;
import jinjing.rule.superfeature.model.IModelRule;

/**
 * interface of all model extracting feature rules 
 * that wrap models from Weka
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public interface IWekaModelRule extends IModelRule {
	
	/**
	 * Weka Build-in evaluation of the model
	 * 
	 * @param train training set for model
	 * @param test testing set for model
	 * @throws Exception if Weka defined exception 
	 */
	public void evaluate(WekaDataSet train, WekaDataSet test) throws Exception;
	
}
