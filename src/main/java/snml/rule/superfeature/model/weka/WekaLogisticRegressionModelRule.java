package snml.rule.superfeature.model.weka;

import java.util.ArrayList;

import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.WekaDataSet;
import weka.classifiers.functions.Logistic;
import weka.core.Instances;

/**
 * Logistic regression from Weka for extracting feature
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class WekaLogisticRegressionModelRule extends WekaClassifyModelRule implements IWekaModelRule{

	/**
	 * Create a logistic regression model rule
	   * Initialize name and value domain for extracted feature
	   * 
	   * @param featureName name for extracted feature
	   * @param aDomain domain of classes' names
	   */
	public WekaLogisticRegressionModelRule(String featureName,
			ArrayList<String> aDomain) {
		super(featureName, aDomain);
	}
	
	/**
	 * Create a logistic regression model rule
	   * Initialize name for extracted feature
	   * Initialize domain as 1, 2, ..., classNum
	   * 
	   * @param featureName name for extracted feature
	   * @param classNum number of total classes
	   */
	public WekaLogisticRegressionModelRule(String featureName,
			int domainSize) {
		super(featureName, domainSize);
	}

	/**
	 * Train the model with given training set and options
	 * Format of options should fit Weka format
	 * 
	 * @param trainingSet data to train model
	 * @param options options of the model
	 * @throws Exception if training process has error
	 */
	@Override
	public void train(IntermediateDataSet trainingSet, String[] options)
			throws Exception {		
		Instances insts = ((WekaDataSet)trainingSet).getDataSet();
		
		classifier = new Logistic();		
		//((LinearRegression)classifier).setOptions(options);//TODO wrap options
		classifier.buildClassifier(insts);
		
	}

}
