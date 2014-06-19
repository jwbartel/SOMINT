package snml.dataconvert;

import snml.dataimport.ThreadData;
import snml.dataimport.ThreadDataSet;
import snml.rule.DateFeatureRule;
import snml.rule.basicfeature.IBasicFeatureRule;

/**
 * Feature extractor which extract basic features from ThreadDataSet
 * to IntermediateDataSet
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class BasicFeatureExtractor extends IFeatureExtractor{
	
	/**
	   * Create a basic feature extractor with a given output 
	   * IntermediateDataSet initializer
	   * 
	   * @param init the initializer of output dataset
	   */
	public BasicFeatureExtractor(IntermediateDataInitializer init) {
		super(init);
	}
	
	/**
	   * Extract a series of features from a given ThreadDataSet.
	   * The features are defined by an array of basic feature rules
	   * and stored in a newly created IntermediateDataSet.
	   * The features are stored in the order of basic feature rules 
	   * in array.
	   * 
	   * @param srcDataSet the input dataset to extract feature from
	   * @param destDataSetName the name of output dataset
	   * @param rules an array of basic feature rules for extracting feature
	   * @return the created output dataset to store the features
	   * @throws Exception if the input dataset contains a null instance
	   */
	public IntermediateDataSet extract(ThreadDataSet srcDataSet, 
			String destDataSetName,
			IBasicFeatureRule[] rules) throws Exception{
		
		if(srcDataSet==null || destDataSetName==null || rules==null){
			return null;
		}
		
		int threadNum = srcDataSet.size();	
		IntermediateDataSet destDataSet = this.destDatasetInit.initDestDataSet(destDataSetName, threadNum, rules);
			
		
		for(int threadId = 0; threadId < threadNum; threadId ++){
			if(srcDataSet.getDataInstance(threadId)==null){
				throw new Exception("null threadId");
			}
			
			ThreadData srcThread = srcDataSet.getDataInstance(threadId);
			
			IntermediateData inst = this.destDatasetInit.initADataInstance(destDataSet, rules);
			int attrId = 0;
						
			for(int i=0; i<rules.length; i++){				
				Object val = rules[i].extract(srcThread);
				if(val==null){
					attrId = inst.setMissing(destDataSet, attrId);
					continue;
				}
					
				rules[i].checkValid(val);
				
				if(rules[i] instanceof DateFeatureRule){
					attrId = inst.setDateAttrValue(destDataSet, attrId++, val);
				}else{	
					attrId = inst.setAttrValue(destDataSet, attrId++, val);
				}
			}
			destDataSet.addDataInstance(inst);			
		}
		
		return destDataSet;		
		
	}

}
