package jinjing.rule.superfeature.model.mallet;

import java.io.File;

import jinjing.dataconvert.IntermediateData;
import jinjing.dataconvert.IntermediateDataSet;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

/**
 * Latent Dirichlet Allocation model from Mallet to extract feature
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class MalletParallelLDAModelRule extends MalletTopicModelRule implements IMalletModelRule{

	/**
	 * Create a feature extracting rule with model LDA
	 * Initialize name and vector length for extracted feature
	 * 
	 * @param featureName  name for extracted feature
	 * @param attrNames name of string attributes involved in the model
	 * @param l number of topics
	 */
	public MalletParallelLDAModelRule(String featureName, String[] attrNames,
			int l) {
		super(featureName, attrNames, l);
	}

	/** the LDA model */
	ParallelTopicModel model;
	

	/**
	 * Train the LDA model with given training set and options
	 * Format of options should fit Mallet format
	 * 
	 * @param trainingSet data to train model
	 * @param options options of the model
	 * @throws Exception if training process has error
	 */
	@Override
	public void train(IntermediateDataSet trainingSet, String[] options)
			throws Exception {
		
		InstanceList instances = this.convert(trainingSet);
		
		// Create a model with length topics, alpha_t = 0.01, beta_w = 0.01
        //  Note that the first parameter is passed as the sum over topics, while
        //  the second is the parameter for a single dimension of the Dirichlet prior.
        int numTopics = this.length;
        model = new ParallelTopicModel(numTopics, 1.0, 0.01);

        model.addInstances(instances);

        // Use two parallel samplers, which each look at one half the corpus and combine
        //  statistics after every iteration.
        model.setNumThreads(2);

        // Run the model for 50 iterations and stop (this is for testing only, 
        //  for real applications, use 1000 to 2000 iterations)
        model.setNumIterations(50);
        model.estimate();       
        
	}

	/**
	 * Save trained LDA model to given path in Mallet format
	 * 
	 * @param modelFilePath path to save model
	 * @throws Exception while saving process has error
	 */
	@Override
	public void save(String modelFilePath) throws Exception {
		File file = new File(modelFilePath);
		if(!file.exists()){
			file.createNewFile();
		}
		model.write(file);		
	}
	
	
	/**
	 * Load trained LDA model from given path in Mallet format
	 * 
	 * @param modelFilePath path to load model
	 * @throws Exception while loading process has error
	 */
	@Override
	public void load(String modelFilePath) throws Exception {
		File file = new File(modelFilePath);	
		model = ParallelTopicModel.read(file);
	}

	/**
	 * Calculate the distribution over trained topics of given intermediate data 
	 * instance
	 * 
	 * @param anInstData the source intermediate data instance
	 * @return distribution over trained topics
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(IntermediateData anInstData) throws Exception {

		Instance inst = this.convert(anInstData);
		
        // Estimate the topic distribution of instance, 
        //  given the current Gibbs state.	
        TopicInferencer inferencer = model.getInferencer();
        double[] testProbabilities = inferencer.getSampledDistribution(inst, 10, 1, 5);
		
        return testProbabilities;
	}


}
