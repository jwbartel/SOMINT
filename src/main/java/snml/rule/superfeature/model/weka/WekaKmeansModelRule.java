package snml.rule.superfeature.model.weka;

import java.util.ArrayList;

import snml.dataconvert.IntermediateData;
import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.WekaData;
import snml.dataconvert.WekaDataSet;
import weka.clusterers.SimpleKMeans;
import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;

/**
 * A K-means model from Weka extracting feature rules
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class WekaKmeansModelRule extends WekaClusterModelRule implements IWekaModelRule{
	
	//public final static int SIMPLEKMEANS = 0;
	//public final static int FARTHESTFIRST = 1;
	
	/**
	   * Create a kmeans model rule with given feature name and cluster name domain
	   * 
	   * @param featureName name for extracted feature
	   * @param aDomain domain of clusters' names
	   */
	public WekaKmeansModelRule(String featureName, ArrayList<String> aDomain) {
		super(featureName, aDomain);
	}
	
	/**
	   * Create a kmeans model rule with given feature name
	   * Initialize domain as 1, 2, ..., clusterNum
	   * 
	   * @param featureName name for extracted feature
	   * @param clusterNum number of total clusters
	   */
	public WekaKmeansModelRule(String featureName, int clusterNum) {
		super(featureName, clusterNum);		
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
		/*
		int kmeansType = Integer.parseInt(options[0]);
		switch(kmeansType){
		case SIMPLEKMEANS:
			clusterer = new SimpleKMeans();
			((SimpleKMeans)clusterer).setNumClusters(domain.size());
			break;
		case FARTHESTFIRST:
			clusterer = new FarthestFirst();
			((FarthestFirst)clusterer).setNumClusters(domain.size());
			break;
		}
		clusterer.setSeed((int)System.currentTimeMillis());
		clusterer.buildClusterer(((WekaDataSet)trainingSet).getDataSet());
		*/
		
		clusterer = new SimpleKMeans();
		((SimpleKMeans)clusterer).setPreserveInstancesOrder(true);
		((SimpleKMeans)clusterer).setNumClusters(domain.size());
		((SimpleKMeans)clusterer).setSeed((int)System.currentTimeMillis());
		clusterer.buildClusterer(((WekaDataSet)trainingSet).getDataSet());
		
		
	}

	/**
	 * Gets the cluster assignment for each instance.
	 * @return Array of indexes of the cluster assigned to each instance
	 * @throws Exception 
	 */
	public int[] getAssignments() throws Exception {
		return ((SimpleKMeans) clusterer).getAssignments();
	}
	
	private Object findClosestCluster(IntermediateData anInstData) {
		DistanceFunction distanceFunction = ((SimpleKMeans) clusterer).getDistanceFunction();
		
		double shortestDistance = Double.POSITIVE_INFINITY;
		Integer closestCluster = null;
		Instances centroids = ((SimpleKMeans) clusterer).getClusterCentroids();
		for (int i=0; i<centroids.numInstances(); i++) {
			Instance centroid = centroids.get(i);
			double distance = distanceFunction.distance(centroid, ((WekaData) anInstData).getInstValue());
			if (distance < shortestDistance) {
				shortestDistance = distance;
				closestCluster = i;
			}
		}
		return closestCluster;
	}
	
	@Override
	public Object extract(IntermediateData anInstData) throws Exception {
		try {
			return super.extract(anInstData);
		} catch (ArrayIndexOutOfBoundsException e) {
			return findClosestCluster(anInstData);
		}
	}

}
