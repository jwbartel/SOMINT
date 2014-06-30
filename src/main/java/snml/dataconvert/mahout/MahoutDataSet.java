package snml.dataconvert.mahout;

import org.apache.mahout.cf.taste.model.DataModel;

import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.IntermediateRecommendationDataSet;
import snml.rule.basicfeature.IBasicFeatureRule;

/**
 * An intermediate recommendation data set implemented in Mahout format
 */
public class MahoutDataSet extends IntermediateRecommendationDataSet {

	private DataModelInitializer modelInitializer;
	
	public MahoutDataSet(IBasicFeatureRule userFeature,
			IBasicFeatureRule itemFeature,
			IBasicFeatureRule preferenceFeature,
			DataModelInitializer preferenceCreator) {
		super(userFeature, itemFeature, preferenceFeature);
		this.modelInitializer = preferenceCreator;
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
	
	/**
	   * Get the wrapped Weka instances
	   *
	   * @return the wrapped Weka instances
	   * @throws Exception 
	   */
	public DataModel getDataSet() throws Exception{

		return modelInitializer.initializeDataModel(seenPreferences);

	}

}
