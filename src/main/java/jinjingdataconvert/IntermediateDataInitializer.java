package jinjingdataconvert;

import jinjing.rule.IFeatureRule;

/**
 * Intermediate data set initializer interface. Each implementation of intermediate dataset
 * in SoMMinT should have one initializer for feature extraction. 
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public interface IntermediateDataInitializer {
	
	/**
	   * Create an IntermediateDataSet with given name, instance number,
	   * and define the attributes with feature extracting rules.
	   *
	   * @param destDataSetName the name of created dataset
	   * @param threadNum the number of instances in the dataset
	   * @param rules the array of feature extracting rules used in feature extractor
	   * @throws Exception if data set creation fails
	   */
	public IntermediateDataSet initDestDataSet(
			String destDataSetName, int threadNum, IFeatureRule[] rules) throws Exception;
	
	/**
	   * Create an IntermediateData instance in given dataset.
	   * The attributes are defined by feature extracting rules.
	   *
	   * @param relatedDataset the dataset which contains the created instance
	   * @param rules the array of feature extracting rules used in feature extractor
	   * @throws Exception if data creation fails
	   */
	public IntermediateData initADataInstance(
			IntermediateDataSet relatedDataset, IFeatureRule[] rules) throws Exception;

}
