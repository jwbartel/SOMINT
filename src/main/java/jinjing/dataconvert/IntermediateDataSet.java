package jinjing.dataconvert;

/**
 * Intermediate data set interface. All schemes of intermediate dataset in
 * SoMMinT implement this interface. 
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public interface IntermediateDataSet{
	
	/**
	   * Adds one instance to the end of the set. 
	   *
	   * @param inst the instance to be added
	   * @throws Exception if adding fails
	   */
	public void addDataInstance(IntermediateData inst) throws Exception;
	
	/**
	   * Get the instance at given place of the data set. 
	   *
	   * @param instId the id of desired instance
	   * @return the instance at given place of the data set
	   * @throws Exception if instId is invalid
	   */
	public IntermediateData getDataInstance(int instId) throws Exception;
	
	/**
	   * Get the number of attributes of the data set. 
	   *
	   * @return number of attributes of the data set. 
	   * @throws Exception if number of attributes is unavailable
	   */
	public int numAttributes() throws Exception;
	
	/**
	   * Get the number of instances of the data set. 
	   *
	   * @return number of instances of the data set. 
	   */
	public int size();
	
	/**
	   * Save the data set at given path. 
	   *
	   * @param path the path of file to save the dataset. 
	   * @throws Exception if path unavailable or invalid operation appears
	   */
	public void save(String path) throws Exception;
	
	/**
	   * Merge current dataset and another dataset by combining their attributes.
	   * Attributes of another dataset is attached after current dataset's
	   * The numbers of instances must match.
	   *
	   * @param anotherDataSet the dataset to be merged
	   * @return the merged dataset
	   * @throws Exception if numbers of instances do not match.
	   */
	public IntermediateDataSet mergeByAttributes(IntermediateDataSet anotherDataSet) throws Exception;
	
	/**
	   * Merge current dataset and another dataset by combining their instances.
	   * Instances of another dataset is attached after current dataset's
	   * The numbers and types of attributes must match.
	   *
	   * @param otherDataSets the datasets to be merged
	   * @return the merged dataset
	   * @throws Exception if numbers and types of attributes do not match.
	   */
	public IntermediateDataSet mergeByDataInstances(IntermediateDataSet[] otherDataSets) throws Exception;
	
	/**
	   * Split the dataset into train & test sets
	   *
	   * @param trainPercent the percent of train set size over original set's
	   * @return array of [train set, test set]
	   * @throws Exception if train percent is invalid
	   */
	public IntermediateDataSet[] splitToTrainAndTest(double trainPercent) throws Exception;
	
	/**
	   * Split the dataset into folds with equal size
	   *
	   * @param foldNum the number of folds to split into
	   * @return array of folds of the set with size foldNum
	   * @throws Exception if foldNum is invalid
	   */
	public IntermediateDataSet[] splitToFolds(int foldNum) throws Exception;
	
	/**
	   * Set the attribute index of dependent variable in the dataset as
	   * the last attribute
	   *
	   */
	public void setTargetIndex();
	
	/**
	   * Set the attribute index of dependent variable in the dataset as
	   * given index
	   *
	   * @param index the given index of dependent variable
	   */
	public void setTargetIndex(int index);
	

}
