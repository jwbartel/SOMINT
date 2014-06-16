package jinjing.dataconvert;

import jinjing.rule.DateFeatureRule;
import jinjing.rule.superfeature.ISuperFeatureRule;

/**
 * Feature extractor which extract basic features from IntermediateDataSet
 * to a new IntermediateDataSet
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class SuperFeatureExtractor extends IFeatureExtractor{
	
	/**
	   * Create a super feature extractor with a given output 
	   * IntermediateDataSet initializer
	   * 
	   * @param init the initializer of output dataset
	   */
	public SuperFeatureExtractor(IntermediateDataInitializer init) {
		super(init);
	}

	/**
	   * Extract a series of features from a given IntermediateDataSet.
	   * The features are defined by an array of super feature rules
	   * and stored in a newly created IntermediateDataSet.
	   * The features are stored in the order of super feature rules 
	   * in array.
	   * 
	   * @param srcDataSet the input dataset to extract feature from
	   * @param destDataSetName the name of output dataset
	   * @param rules an array of super feature rules for extracting feature
	   * @return the created output dataset to store the features
	   * @throws Exception if the input dataset contains a null instance
	   */
	public IntermediateDataSet extract(IntermediateDataSet srcDataSet, 
			String destDataSetName,
			ISuperFeatureRule[] rules) throws Exception{
		
		if(srcDataSet==null || destDataSetName==null || rules==null){
			return null;
		}
		
		int threadNum = srcDataSet.size();	
		IntermediateDataSet destDataSet = this.destDatasetInit.initDestDataSet(destDataSetName, threadNum, rules);
			
		
		for(int threadId = 0; threadId < threadNum; threadId ++){
			if(srcDataSet.getDataInstance(threadId)==null){
				throw new Exception("null threadId");
			}
			
			IntermediateData srcData = srcDataSet.getDataInstance(threadId);
			
			IntermediateData inst = this.destDatasetInit.initADataInstance(destDataSet, rules);
			int attrId = 0;
						
			for(int i=0; i<rules.length; i++){				
				Object val = rules[i].extract(srcData);
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
