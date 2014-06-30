package snml.dataconvert.mahout;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.mahout.cf.taste.model.DataModel;

import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.IntermediateRecommendationData;
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
		String output = "";
		for (IntermediateRecommendationData dataItem : data) {
			output += dataItem.getUserAttribute() + ","
					+ dataItem.getItemAttribute() + ","
					+ dataItem.getPreferenceAttribute() + "\n";
		}

		File outputFile = new File(path);
		FileUtils.write(outputFile, output);
	}

	@Override
	public IntermediateDataSet[] splitToFolds(int foldNum) throws Exception {
		
		int foldSize = data.size() / foldNum;
		int remainderSize = data.size() % foldNum;
		
		int pos = 0;
		IntermediateDataSet[] folds = new IntermediateDataSet[foldNum];
		for (int i = 0; i < foldNum; i++) {
			IntermediateRecommendationDataSet fold = createEmptyVersion();
			for (int j=0; j<foldSize; j++) {
				fold.addDataInstance(data.get(pos));
				pos++;
			}
			if (remainderSize > 0) {
				fold.addDataInstance(data.get(pos));
				pos++;
				remainderSize--;
			}
			folds[i] = fold;
		}
		
		return folds;
	}

	@Override
	protected IntermediateRecommendationDataSet createEmptyVersion() {
		return new MahoutDataSet(userFeature, itemFeature, preferenceFeature, modelInitializer);
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
