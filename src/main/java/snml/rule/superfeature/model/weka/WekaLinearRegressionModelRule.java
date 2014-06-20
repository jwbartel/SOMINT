package snml.rule.superfeature.model.weka;

import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.WekaDataSet;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instances;

/**
 * A Weka linear regression model extracting feature rules
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class WekaLinearRegressionModelRule extends WekaRegressionModelRule implements IWekaModelRule{

	/**
	   * Create a weka linear regression model for feature extracting
	   * with given name for extracted feature
	   * 
	   * @param featureName name for extracted feature
	   */
	public WekaLinearRegressionModelRule(String featureName) {
		super(featureName);
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
				
		classifier = new LinearRegression();
		
		//((LinearRegression)classifier).setOptions(options);//TODO wrap options
		classifier.buildClassifier(insts);	
		
	}

	
}
