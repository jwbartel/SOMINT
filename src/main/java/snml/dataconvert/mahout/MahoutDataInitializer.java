package snml.dataconvert.mahout;

import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.IntermediateRecommendationDataInitializer;
import snml.rule.BinaryFeatureRule;
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
		
		DataModelInitializer modelInitializer;
		if (preferenceRule instanceof BinaryFeatureRule) {
			modelInitializer = new BooleanModelInitializer();
		} else {
			modelInitializer = new GenericDataModelInitializer();
		}
		
		return new MahoutDataSet(userRule, itemRule, preferenceRule, modelInitializer);
	}

	@Override
	public MahoutData initADataInstance(IntermediateDataSet relatedDataset,
			IBasicFeatureRule userRule, IBasicFeatureRule itemRule,
			IBasicFeatureRule preferenceRule) throws Exception {
		
		return new MahoutData();
	}

}
