package snml.dataconvert;

import java.lang.reflect.Array;

import snml.dataimport.ThreadData;
import snml.dataimport.ThreadDataSet;
import snml.rule.basicfeature.IBasicFeatureRule;

/**
 * Feature extractor which extract basic features from ThreadDataSet
 * to IntermediateRecommendationDataSet
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class RecommendationFeatureExtractor extends IFeatureExtractor{
	
	/**
	   * Create a basic feature extractor with a given output 
	   * IntermediateDataSet initializer
	   * 
	   * @param init the initializer of output dataset
	   */
	public RecommendationFeatureExtractor(IntermediateRecommendationDataInitializer init) {
		super(init);
	}
	
	public IntermediateRecommendationDataInitializer getRecommendationDataInitiatilizer() {
		return (IntermediateRecommendationDataInitializer) this.destDatasetInit;
	}
	
	/**
	   * Extract a series of features from a given ThreadDataSet.
	   * 
	   * @param srcDataSet the input dataset to extract feature from
	   * @param destDataSetName the name of output dataset
	   * @param rules an array of basic feature rules for extracting feature
	   * @return the created output dataset to store the features
	   * @throws Exception if the input dataset contains a null instance
	   */
	public IntermediateRecommendationDataSet extract(ThreadDataSet srcDataSet, 
			String destDataSetName,
			IBasicFeatureRule userFeature,
			IBasicFeatureRule itemFeature,
			IBasicFeatureRule preferenceFeature) throws Exception{
		
		if(srcDataSet==null || destDataSetName==null ||
				userFeature==null || itemFeature==null || preferenceFeature==null ){
			return null;
		}
		
		int threadNum = srcDataSet.size();	
		IntermediateRecommendationDataSet destDataSet = getRecommendationDataInitiatilizer()
				.initDestDataSet(destDataSetName, threadNum, userFeature,
						itemFeature, preferenceFeature);
		
		for(int threadId = 0; threadId < threadNum; threadId ++){
			if(srcDataSet.getDataInstance(threadId)==null){
				throw new Exception("null threadId");
			}
			
			ThreadData srcThread = srcDataSet.getDataInstance(threadId);

			IntermediateRecommendationData inst = getRecommendationDataInitiatilizer()
					.initADataInstance(destDataSet,
							userFeature,
							itemFeature,
							preferenceFeature);
			int attrId = 0;
			
			inst.setUserAttribute(attrId);
			attrId = addFeature(destDataSet, inst, srcThread, userFeature, attrId);
			
			inst.setItemAttribute(attrId);
			attrId = addFeature(destDataSet, inst, srcThread, itemFeature, attrId);
			
			inst.setPreferenceAttribute(attrId);
			attrId = addFeature(destDataSet, inst, srcThread, preferenceFeature, attrId);
			
			destDataSet.addDataInstance(inst);			
		}
		
		return destDataSet;		
		
	}
	
	private int addFeature(IntermediateDataSet destDataSet,
			IntermediateData inst, ThreadData srcThread,
			IBasicFeatureRule rule, int attrId) throws Exception {
		Object valObj = rule.extract(srcThread);
		if (valObj == null) {
			return inst.setMissing(destDataSet, attrId);
		}
		if (valObj.getClass().isArray()) {
			for (int i = 0; i < Array.getLength(valObj); i++) {
				Object val = Array.get(valObj, i);
				attrId = inst.setAttrValue(destDataSet, attrId, val);
			}
		} else {
			attrId = inst.setAttrValue(destDataSet, attrId, valObj);
		}
		return attrId;
	}

}
