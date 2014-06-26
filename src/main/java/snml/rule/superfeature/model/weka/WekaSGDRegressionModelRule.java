package snml.rule.superfeature.model.weka;

import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.weka.WekaDataSet;
import weka.classifiers.functions.SGD;
import weka.core.Instances;

/**
 * A Weka SGD regression model extracting feature rules
 * @version $1$
 */
public class WekaSGDRegressionModelRule extends WekaRegressionModelRule implements IWekaModelRule{

	/**
	   * Create a weka linear regression model for feature extracting
	   * with given name for extracted feature
	   * 
	   * @param featureName name for extracted feature
	   */
	public WekaSGDRegressionModelRule(String featureName) {
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
				
		classifier = new SGD();
		
		//((LinearRegression)classifier).setOptions(options);//TODO wrap options
		classifier.buildClassifier(insts);	
		
	}

	
}
