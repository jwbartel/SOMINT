package snml.rule.superfeature.model.weka;

import java.util.ArrayList;

import snml.dataconvert.IntermediateData;
import snml.dataconvert.weka.WekaData;
import snml.dataconvert.weka.WekaDataSet;
import snml.rule.superfeature.model.ClusterModelRule;
import weka.clusterers.Clusterer;

/**
 * Abstract, superclass of all Weka cluster model extracting feature rules
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public abstract class WekaClusterModelRule extends ClusterModelRule implements IWekaModelRule{

	/** Weka clusterer */
	Clusterer clusterer;
	
	/**
	   * Initialize name and value domain for extracted feature
	   * 
	   * @param featureName name for extracted feature
	   * @param aDomain domain of clusters' names
	   */
	public WekaClusterModelRule(String featureName, ArrayList<String> aDomain) {
		super(featureName, aDomain);
	}

	/**
	   * Initialize name for extracted feature
	   * Initialize domain as 1, 2, ..., clusterNum
	   * 
	   * @param featureName name for extracted feature
	   * @param clusterNum number of total clusters
	   */
	public WekaClusterModelRule(String featureName, int clusterNum) {
		super(featureName, clusterNum);
	}


	/**
	 * Save trained model to given path in Weka format
	 * 
	 * @param modelFilePath path to save model
	 * @throws Exception while saving process has error
	 */
	@Override
	public void save(String modelFilePath) throws Exception{
		weka.core.SerializationHelper.write(modelFilePath, clusterer);
	}

	/**
	 * Load trained model from given path in Weka format
	 * 
	 * @param modelFilePath path to load model
	 * @throws Exception while loading process has error
	 */
	@Override
	public void load(String modelFilePath) throws Exception{
		clusterer = (Clusterer) weka.core.SerializationHelper.read(modelFilePath);	
	}
	
	/**
	 * Classify an intermediate data instance on the clusters
	 * 
	 * @param anInstData the source intermediate data instance
	 * @return name of cluster it belongs to
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(IntermediateData anInstData) throws Exception {
		int cluster = clusterer.clusterInstance(((WekaData)anInstData).getInstValue());
		return domain.get(cluster);
	}
	
	/**
	 * Weka Build-in evaluation of the model
	 * 
	 * @param train training set for model
	 * @param test testing set for model
	 * @throws Exception if Weka defined exception 
	 */
	@Override
	public void evaluate(WekaDataSet train, WekaDataSet test) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public Clusterer getClusterer() {
		return clusterer;
	}

	/**
	 * Gets the cluster assignment for each instance.
	 * @return Array of indexes of the cluster assigned to each instance
	 * @throws Exception 
	 */
	public abstract int[] getAssignments() throws Exception;

}
