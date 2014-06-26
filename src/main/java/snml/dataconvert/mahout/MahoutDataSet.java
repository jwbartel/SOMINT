package snml.dataconvert.mahout;

import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.IntermediateRecommendationDataSet;
import snml.rule.basicfeature.IBasicFeatureRule;

/**
 * An intermediate recommendation data set implemented in Mahout format
 */
public class MahoutDataSet extends IntermediateRecommendationDataSet {

	public MahoutDataSet(IBasicFeatureRule userFeature,
			IBasicFeatureRule itemFeature, IBasicFeatureRule preferenceFeature) {
		super(userFeature, itemFeature, preferenceFeature);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void save(String path) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public IntermediateDataSet[] splitToFolds(int foldNum) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IntermediateRecommendationDataSet createEmptyVersion() {
		// TODO Auto-generated method stub
		return null;
	}

}
