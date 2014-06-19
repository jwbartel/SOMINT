package snml.rule.superfeature.model.weka;

import snml.dataconvert.IntermediateData;
import snml.dataconvert.WekaData;
import snml.dataconvert.WekaDataSet;
import snml.rule.superfeature.model.NumericModelRule;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;

/**
 * Abstract, superclass of all Weka regression model extracting feature rules
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class WekaRegressionModelRule extends NumericModelRule implements IWekaModelRule{

	/** Weka regression model */
	Classifier classifier;
	
	/**
	   * Initialize name for extracted feature
	   * 
	   * @param featureName name for extracted feature
	   */
	public WekaRegressionModelRule(String featureName) {
		super(featureName);
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
	 * Classify an intermediate data instance with the regression model
	 * 
	 * @param anInstData the source intermediate data instance
	 * @return result of casting on the regression model
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(IntermediateData anInstData) throws Exception {
		Instance inst = ((WekaData)anInstData).getInstValue();
		double result = classifier.classifyInstance(inst);
		return result;
	}
	
	
	/**
	 * Weka Build-in evaluation of the model
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
	
	public Classifier getClassifier() {
		return classifier;
	}

}
