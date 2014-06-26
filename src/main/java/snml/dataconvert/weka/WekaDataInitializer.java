package snml.dataconvert.weka;

import java.util.ArrayList;

import snml.dataconvert.IntermediateData;
import snml.dataconvert.IntermediateDataInitializer;
import snml.dataconvert.IntermediateDataSet;
import snml.dataimport.MsgDataConfig;
import snml.rule.DateFeatureRule;
import snml.rule.IFeatureRule;
import snml.rule.NominalFeatureRule;
import snml.rule.NumericFeatureRule;
import snml.rule.NumericVectorFeatureRule;
import snml.rule.StringFeatureRule;
import weka.core.Attribute;
import weka.core.Instances;

/**
 * Implementation of intermediate data set initializer for Weka intermediate dataset
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class WekaDataInitializer implements IntermediateDataInitializer {
	
	/** Attribute number of the Weka dataset to be created */
	protected int attrNum;

	/**
	   * Create an Weka intermediate dataset with given name, instance number,
	   * and define the attributes with feature extracting rules.
	   *
	   * @param destDataSetName the name of created dataset
	   * @param threadNum the number of instances in the dataset
	   * @param rules the array of feature extracting rules used in feature extractor
	   * @throws Exception if data set creation fails
	   */
	@Override
	public IntermediateDataSet initDestDataSet(String destDataSetName,
			int threadNum, IFeatureRule[] rules) throws Exception {
		
		ArrayList<Attribute> attributes = new ArrayList<Attribute>(rules.length);
		for(int i=0; i<rules.length; i++){

			if(rules[i] instanceof NumericVectorFeatureRule){
				int l = ((NumericVectorFeatureRule)rules[i]).length;
				for(int j=0; j<l; j++){
					Attribute attr = new Attribute(rules[i].getDestFeatureName()+j);
					attributes.add(attr);	
				}
			}
			else{
				Attribute attr;

				if(rules[i] instanceof DateFeatureRule){
					attr = new Attribute(rules[i].getDestFeatureName(), MsgDataConfig.DATEFORMAT_DEFAULT);
				}
				else if(rules[i] instanceof NumericFeatureRule){
					attr = new Attribute(rules[i].getDestFeatureName());
				}
				else if(rules[i] instanceof NominalFeatureRule){
					ArrayList<String> domain = ((NominalFeatureRule)rules[i]).getDomain();
					attr = new Attribute(rules[i].getDestFeatureName(), domain);
				}
				else if(rules[i] instanceof StringFeatureRule){
					ArrayList<String> domain = null;
					attr = new Attribute(rules[i].getDestFeatureName(), domain);
				}
				else if(rules[i] instanceof DateFeatureRule){
					attr = new Attribute(rules[i].getDestFeatureName(), MsgDataConfig.DATEFORMAT_DEFAULT);
				}
				else{
					throw new Exception("What kind of feature rule it is!?");
				}
				attributes.add(attr);
			}
		}

		attributes.trimToSize();
		attrNum = attributes.size();
		
		Instances dataset = new Instances(destDataSetName, attributes, threadNum);		

		return new WekaDataSet(dataset);
	}

	/**
	   * Create an Weka intermediate data instance in given dataset.
	   * The attributes are defined by feature extracting rules.
	   *
	   * @param relatedDataset the dataset which contains the created instance
	   * @param rules the array of feature extracting rules used in feature extractor
	   * @throws Exception if data creation fails
	   */
	@Override
	public IntermediateData initADataInstance(
			IntermediateDataSet relatedDataset, IFeatureRule[] rules)
			throws Exception {
		
		WekaData inst = new WekaData(attrNum);
		inst.setRelatedDataset((WekaDataSet)relatedDataset);
		return inst;
	}

}
