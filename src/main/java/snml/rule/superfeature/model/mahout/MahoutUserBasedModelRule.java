package snml.rule.superfeature.model.mahout;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import snml.dataconvert.IntermediateData;
import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.mahout.MahoutData;
import snml.dataconvert.mahout.MahoutDataSet;

public class MahoutUserBasedModelRule extends MahoutCollaborativeFiteringModelRule {

	MahoutDataSet dataSet;
	GenericUserBasedRecommender recommender;
	UserSimilarity similarity;

	public MahoutUserBasedModelRule(String featureName, UserSimilarity similarity) {
		super(featureName);
		this.similarity = similarity;
	}
	
	@Override
	public void train(IntermediateDataSet trainingSet, String[] options)
			throws Exception {
		
		if (! (trainingSet instanceof MahoutDataSet) ) {
			throw new Exception("Cannot train using a non-mahout dataset");
		}
		
		dataSet = (MahoutDataSet) trainingSet;
		DataModel model = dataSet.getDataSet();

		UserNeighborhood neighborhood = new NearestNUserNeighborhood(
				dataSet.getNumUsers(), similarity, model);
		recommender = new GenericUserBasedRecommender(model, neighborhood,
				similarity);
		
	}

	@Override
	public void save(String modelFilePath) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void load(String modelFilePath) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Float estimatePreference(Object user, Object item) {
		Long userId = dataSet.getUserId(user);
		Long itemId = dataSet.getItemId(item);
		
		if (userId == null || itemId == null) {
			return null;
		}
		try {
			return recommender.estimatePreference(userId, itemId);
		} catch (TasteException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Float estimatePreference(MahoutData data) {
		return estimatePreference(data.getUserAttribute(), data.getItemAttribute());
	}

	@Override
	public Object extract(IntermediateData anInstData) throws Exception {
		if (anInstData instanceof MahoutData) {
			return estimatePreference((MahoutData) anInstData);
		}
		throw new Exception("Data must of type MahoutData");
	}

}
