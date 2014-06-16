package snml.rule.superfeature.model.weka;

import java.util.ArrayList;

import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.WekaDataSet;
import weka.classifiers.trees.J48;

/**
 * Decision tree from Weka for extracting feature
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class WekaDecisionTreeModelRule extends WekaClassifyModelRule implements IWekaModelRule{
	
	/**
	 * Create a decision tree model rule
	   * Initialize name and value domain for extracted feature
	   * 
	   * @param featureName name for extracted feature
	   * @param aDomain domain of classes' names
	   */
	public WekaDecisionTreeModelRule(String featureName, ArrayList<String> aDomain) {
		super(featureName, aDomain);
	}
	
	/**
	 * Create a decision tree model rule
	   * Initialize name for extracted feature
	   * Initialize domain as 1, 2, ..., classNum
	   * 
	   * @param featureName name for extracted feature
	   * @param classNum number of total classes
	   */
	public WekaDecisionTreeModelRule(String featureName, int classNum) {
		super(featureName, classNum);
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
	public void train(IntermediateDataSet trainingSet, String[] options) throws Exception {
		
		classifier = new J48();
			
		((J48)classifier).setOptions(options);//TODO wrap options
		classifier.buildClassifier(((WekaDataSet)trainingSet).getDataSet());				
	}


}
