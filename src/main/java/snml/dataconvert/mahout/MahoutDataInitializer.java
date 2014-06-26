package snml.dataconvert.mahout;

import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.IntermediateRecommendationDataInitializer;
import snml.rule.basicfeature.IBasicFeatureRule;

/**
 * Implementation of intermediate data set initializer for Mahout intermediate recommendation dataset
 */
public class MahoutDataInitializer extends
		IntermediateRecommendationDataInitializer {

	@Override
	public MahoutDataSet initDestDataSet(String destDataSetName, int threadNum,
			IBasicFeatureRule userRule, IBasicFeatureRule itemRule,
			IBasicFeatureRule preferenceRule) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MahoutData initADataInstance(IntermediateDataSet relatedDataset,
			IBasicFeatureRule userRule, IBasicFeatureRule itemRule,
			IBasicFeatureRule preferenceRule) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
