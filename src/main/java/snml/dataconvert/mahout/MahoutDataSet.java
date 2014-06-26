package snml.dataconvert.mahout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;

import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.IntermediateRecommendationDataSet;
import snml.dataconvert.UserItemPair;
import snml.rule.basicfeature.IBasicFeatureRule;

/**
 * An intermediate recommendation data set implemented in Mahout format
 */
public class MahoutDataSet extends IntermediateRecommendationDataSet {

	private PreferenceCreator preferenceCreator;
	
	public MahoutDataSet(IBasicFeatureRule userFeature,
			IBasicFeatureRule itemFeature,
			IBasicFeatureRule preferenceFeature,
			PreferenceCreator preferenceCreator) {
		super(userFeature, itemFeature, preferenceFeature);
		this.preferenceCreator = preferenceCreator;
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
		
		// Assign user ids and sort preferences by user
		Map<Object,Long> userToId = new HashMap<>();
		Map<Object,Long> itemToId = new HashMap<>();
		Map<Object,ArrayList<Preference>> userToPreferences = new HashMap<>();
		for (Entry<UserItemPair, Object> entry : seenPreferences.entrySet()) {
			UserItemPair userItemPair = entry.getKey();
			Object user = userItemPair.getUser();
			Object item = userItemPair.getItem();
			Object preference = entry.getValue();
			Long userId = userToId.get(user);
			if (userId == null) {
				userId = (long) userToId.size();
				userToId.put(user, userId);
			}
			Long itemId = itemToId.get(item);
			if (itemId == null) {
				itemId = (long) itemToId.size();
				itemToId.put(item, itemId);
			}
			ArrayList<Preference> preferences = userToPreferences.get(user);
			if(preferences == null) {
				preferences = new ArrayList<>();
				userToPreferences.put(user, preferences);
			}
			Preference preferenceObj = preferenceCreator.initializePreference(userId, itemId, preference);
			preferences.add(preferenceObj);
		}
		
		// TODO Create data model from preference lists
		return null;
	}

}
