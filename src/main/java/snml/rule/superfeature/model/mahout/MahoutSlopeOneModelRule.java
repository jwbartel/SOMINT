package snml.rule.superfeature.model.mahout;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.slopeone.SlopeOneRecommender;

import snml.dataconvert.IntermediateData;
import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.mahout.MahoutData;
import snml.dataconvert.mahout.MahoutDataSet;

@SuppressWarnings("deprecation")
public class MahoutSlopeOneModelRule extends MahoutCollaborativeFiteringModelRule {

	MahoutDataSet dataSet;
	SlopeOneRecommender recommender;

	public MahoutSlopeOneModelRule(String featureName) {
		super(featureName);
	}
	
	@Override
	public void train(IntermediateDataSet trainingSet, String[] options)
			throws Exception {
		
		if (! (trainingSet instanceof MahoutDataSet) ) {
			throw new Exception("Cannot train using a non-mahout dataset");
		}
		
		dataSet = (MahoutDataSet) trainingSet;
		recommender = new SlopeOneRecommender(( (MahoutDataSet) trainingSet).getDataSet());
		
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
