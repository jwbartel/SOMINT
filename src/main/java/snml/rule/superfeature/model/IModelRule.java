package snml.rule.superfeature.model;

import snml.dataconvert.IntermediateDataSet;
import snml.rule.superfeature.ISuperFeatureRule;

/**
 * interface of all model extracting feature rules
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public interface IModelRule extends ISuperFeatureRule{
	
	/**
	 * Train the model with given training set and options
	 * Format of options should fit the wrapped toolkit's model
	 * 
	 * @param trainingSet data to train model
	 * @param options options of the model
	 * @throws Exception if training process has error
	 */
	public void train(IntermediateDataSet trainingSet, String[] options) throws Exception;

	/**
	 * Save trained model to given path
	 * 
	 * @param modelFilePath path to save model
	 * @throws Exception while saving process has error
	 */
	public void save(String modelFilePath) throws Exception;
	
	/**
	 * Load trained model from given path
	 * 
	 * @param modelFilePath path to load model
	 * @throws Exception while loading process has error
	 */
	public void load(String modelFilePath) throws Exception;
	
}
