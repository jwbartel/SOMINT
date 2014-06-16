package snml.dataconvert;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVSaver;

/**
 * An intermediate data set implemented in Weka format
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class WekaDataSet implements IntermediateDataSet {

	/** The wrapped Weka dateset */
	protected Instances dataset;
	
	/**
	   * Create a Weka intermediate data set with given capacity
	   *
	   * @param datasetName the name of the dataset
	   * @param attrNum number of instances the dataset will have
	   */
	public WekaDataSet(String datasetName, int capacity){
		dataset = new Instances(datasetName, new ArrayList<Attribute>(), capacity);	
	}
	
	/**
	   * Create a Weka intermediate data set with given Weka instances
	   *
	   * @param dataset the Weka instances to be wrapped
	   */
	public WekaDataSet(Instances dataset){
		this.dataset = dataset;
	}
	
	/**
	   * Get the wrapped Weka instances
	   *
	   * @return the wrapped Weka instances
	   */
	public Instances getDataSet(){
		return dataset;
	}
	
	/**
	   * Adds one instance to the end of the set. 
	   *
	   * @param inst the instance to be added
	   * @throws Exception if adding fails
	   */
	@Override
	public void addDataInstance(IntermediateData inst) throws Exception {
		if(!(inst instanceof WekaData)){
			throw new Exception("Wrong data type passed in");
		}

		Instance wekaInst = ((WekaData)inst).getInstValue();
		dataset.add(wekaInst);
	}

	/**
	   * Get the Weka attribute at given index
	   *
	   * @param index the index of the attribute
	   * @return the Weka attribute
	   */
	public Attribute attribute(int index){
		return dataset.attribute(index);
	}
	
	/**
	   * Save the data set in Weka ARFF format at given path. 
	   *
	   * @param path the path of file to save the dataset. 
	   * @throws Exception if path unavailable or invalid operation appears
	   */
	@Override
	public void save(String path) throws Exception{
		if(path.endsWith(".arff")){
			ArffSaver saver = new ArffSaver();
			saver.setInstances(dataset);
			saver.setFile(new File(path));
			saver.writeBatch();
		}else if(path.endsWith(".csv")){
			CSVSaver saver = new CSVSaver();
			saver.setInstances(dataset);
			saver.setFile(new File(path));
			saver.writeBatch();
		}
	}
	
	/**
	   * Print the dataset as a string
	   *
	   * @return the string expression of the dataset
	   */
	@Override
	public String toString(){
		return dataset.toString();
	}

	/**
	   * Merge current dataset and another dataset by combining their attributes.
	   * Attributes of another dataset is attached after current dataset's
	   * The numbers of instances must match.
	   *
	   * @param anotherDataSet the dataset to be merged
	   * @return the merged dataset
	   * @throws Exception if numbers of instances do not match.
	   */
	@Override
	public IntermediateDataSet mergeByAttributes(IntermediateDataSet anotherDataSet) throws Exception {
		if(!(anotherDataSet instanceof WekaDataSet)){
			throw new Exception("uncampatible dataset type");
		}
		
		Instances insts = Instances.mergeInstances(dataset, ((WekaDataSet)anotherDataSet).getDataSet());
		return new WekaDataSet(insts);
	}

	/**
	   * Get the intermediate data instance at given place of the data set. 
	   *
	   * @param instId the id of desired instance
	   * @return the intermediate data instance at given place of the data set
	   * @throws Exception if instId is invalid
	   */
	@Override
	public IntermediateData getDataInstance(int index) throws Exception {
		WekaData inst = new WekaData(dataset.get(index));
		inst.setRelatedDataset(this);
		return inst;
	}

	/**
	   * Get the number of instances of the data set. 
	   *
	   * @return number of instances of the data set. 
	   */
	@Override
	public int size() {
		return dataset.numInstances();
	}

	/**
	   * Merge current dataset and another dataset by combining their instances.
	   * Instances of another dataset is attached after current dataset's
	   * The numbers and types of attributes must match.
	   *
	   * @param otherDataSets the datasets to be merged
	   * @return the merged dataset
	   * @throws Exception if numbers and types of attributes do not match.
	   */
	@Override
	public IntermediateDataSet mergeByDataInstances(IntermediateDataSet[] otherDataSets)
			throws Exception {
		if(otherDataSets==null || otherDataSets.length<1){ 
			throw new Exception("empty input");
		}
		if(!(otherDataSets instanceof WekaDataSet[])){
			throw new Exception("uncampatible datasets type");
		}
		
		WekaDataSet[] datasets = (WekaDataSet[])otherDataSets;
		
		Instances insts = new Instances(datasets[0].getDataSet());
		for(int i=1; i<datasets.length; i++){
			for(int j=0; j<datasets[i].size(); j++){
				insts.add(((WekaData)datasets[i].getDataInstance(j)).getInstValue());
			}
		}
		return new WekaDataSet(insts);
	}

	/**
	   * Shuffle the instances of the data set into random order 
	   *
	   */
	public void randomize(){
		dataset.randomize(new java.util.Random(System.currentTimeMillis()));
	}
	
	/**
	   * Shuffle the instances of the data set into random order with given seed
	   *
	   * @param seed the random seed
	   */
	public void randomize(int seed){
		dataset.randomize(new java.util.Random(seed));
	}
	
	/**
	   * Set the attribute index of dependent variable in the dataset as
	   * the last attribute
	   *
	   */
	@Override
	public void setTargetIndex(){
		if(dataset.classIndex()<0){
			dataset.setClassIndex(dataset.numAttributes()-1);
		}
	}
	
	/**
	   * Set the attribute index of dependent variable in the dataset as
	   * given index
	   *
	   * @param index the given index of dependent variable
	   */
	@Override
	public void setTargetIndex(int index){
		if(index>=0 && index < dataset.numAttributes()){
			dataset.setClassIndex(index);
		}
	}
	
	/**
	   * Split the dataset into train & test sets
	   *
	   * @param trainPercent the percent of train set size over original set's
	   * @return array of [train set, test set]
	   * @throws Exception if train percent is invalid
	   */
	@Override
	public IntermediateDataSet[] splitToTrainAndTest(double trainPercent)
			throws Exception {
		
		if(trainPercent >=1 || trainPercent<=0){
			throw new Exception("invalid trainPercent: "+trainPercent);
		}
		
		int trainSize = (int) Math.round(dataset.numInstances() * trainPercent);
		int testSize = dataset.numInstances() - trainSize;		
		Instances train = new Instances(dataset, 0, trainSize);
		Instances test = new Instances(dataset, trainSize, testSize);
		
		IntermediateDataSet[] splits = new IntermediateDataSet[2];
		splits[0] = new WekaDataSet(train);
		splits[1] = new WekaDataSet(test);
		
		return splits;
	}

	/**
	   * Split the dataset into folds with equal size
	   *
	   * @param foldNum the number of folds to split into
	   * @return array of folds of the set with size foldNum
	   * @throws Exception if foldNum is invalid
	   */
	@Override
	public IntermediateDataSet[] splitToFolds(int foldNum) throws Exception {
		if(foldNum <=1){
			throw new Exception("invalid foldNum: "+foldNum);
		}
		
		dataset.stratify(foldNum);
		
		IntermediateDataSet[] splits = new IntermediateDataSet[foldNum];
		for(int i=0; i<foldNum; i++){
			splits[i] = new WekaDataSet(dataset.testCV(foldNum, i));
		}
		
		return splits;
	}
	
	/**
	   * Split the dataset into two sets: one of a fold and another with other folds 
	   * combined together
	   *
	   * @param foldNum the number of total folds
	   * @param fold the fold to be split alone
	   * @return array of datasets = [fold, otherfolds]
	   * @throws Exception if foldNum or fold is invalid
	   */
	public IntermediateDataSet[] getFold(int foldNum, int fold) throws Exception {
		if(foldNum <=1){
			throw new Exception("invalid foldNum: "+foldNum);
		}
		
		Instances tmp = new Instances(dataset);
		tmp.randomize(new java.util.Random(System.currentTimeMillis()));
		tmp.stratify(foldNum);
		
		IntermediateDataSet[] splits = new IntermediateDataSet[2*foldNum];
		for(int i=0; i<foldNum; i++){
			splits[2*i] = new WekaDataSet(tmp.trainCV(foldNum, i));
			splits[2*i+1] = new WekaDataSet(tmp.testCV(foldNum, i));
		}
		
		return splits;
	}

	/**
	   * Get the number of attributes of the data set. 
	   *
	   * @return number of attributes of the data set. 
	   * @throws Exception if number of attributes is unavailable
	   */
	@Override
	public int numAttributes() throws Exception {
		return dataset.numAttributes();
	}

}
