package jinjing.rule.superfeature.model.weka;

import java.util.ArrayList;

import jinjing.rule.superfeature.model.ClassifyModelRule;
import jinjingdataconvert.IntermediateData;
import jinjingdataconvert.WekaData;
import jinjingdataconvert.WekaDataSet;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;

/**
 * Abstract, superclass of all Weka classify model extracting feature rules
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class WekaClassifyModelRule extends ClassifyModelRule implements IWekaModelRule{

	/** Weka classifier */
	Classifier classifier;
	
	/**
	   * Initialize name and value domain for extracted feature
	   * 
	   * @param featureName name for extracted feature
	   * @param aDomain domain of classes' names
	   */
	public WekaClassifyModelRule(String featureName, ArrayList<String> aDomain) {
		super(featureName, aDomain);
	}	
	
	/**
	   * Initialize name for extracted feature
	   * Initialize domain as 1, 2, ..., classNum
	   * 
	   * @param featureName name for extracted feature
	   * @param classNum number of total classes
	   */
	public WekaClassifyModelRule(String featureName, int classNum) {
		super(featureName, classNum);
	}
	
	/**
	 * Classify an intermediate data instance
	 * 
	 * @param anInstData the source intermediate data instance
	 * @return name of class it belongs to
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(IntermediateData anInstData) throws Exception {
		double result = classifier.classifyInstance(((WekaData)anInstData).getInstValue());
		return domain.get((int)result);
	}
	
	/**
	 * Save trained model to given path in Weka format
	 * 
	 * @param modelFilePath path to save model
	 * @throws Exception while saving process has error
	 */
	@Override
	public void save(String modelFilePath) throws Exception{
		weka.core.SerializationHelper.write(modelFilePath, classifier);
	}

	/**
	 * Load trained model from given path in Weka format
	 * 
	 * @param modelFilePath path to load model
	 * @throws Exception while loading process has error
	 */
	@Override
	public void load(String modelFilePath) throws Exception{
		classifier = (Classifier) weka.core.SerializationHelper.read(modelFilePath);	
	}
	
	
	/**
	 * Weka Build-in evaluation for Weka classifier
	 * 
	 * @param train training set for model
	 * @param test testing set for model
	 * @throws Exception if Weka defined exception 
	 */
	public void evaluate(WekaDataSet train, WekaDataSet test) throws Exception{
		Evaluation eval = new Evaluation(train.getDataSet());
		eval.evaluateModel(classifier, test.getDataSet());
		System.out.println(eval.toSummaryString("\nResults\n======\n", false));
	}

}
