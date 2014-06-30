package snml.dataconvert.mahout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import snml.dataconvert.UserItemPair;

public class GenericDataModelInitializer implements DataModelInitializer {

	/**
	 * Creates a preference object based on a userId, an itemId, and an Object preference
	 * @param userId
	 * @param itemId
	 * @param preference
	 * @return The Preference object. Null if userId or itemId is null
	 * @throws Exception if the Preference cannot be created
	 */
	private Preference initializePreference(Long userId, Long itemId, Object preference)
			throws Exception {
		if (userId == null || itemId == null) {
			return null;
		}
		try {
			if ((preference instanceof Number) || (preference instanceof String)) {
				float floatPreference = Float.parseFloat(preference.toString());
				return new GenericPreference(userId, itemId, floatPreference);
			}
		} catch (NumberFormatException e) {
		}

		throw new Exception("Preference could not be converted to a float");
	}
	
	private FastByIDMap<PreferenceArray> initializeUserData(
			Map<Object, Long> userToId,
			Map<Object, ArrayList<Preference>> userToPreferences) {

		FastByIDMap<PreferenceArray> userData = new FastByIDMap<>(
				userToPreferences.size());
		for (Entry<Object,ArrayList<Preference>> entry : userToPreferences.entrySet()) {
			Object user = entry.getKey();
			Long userId = userToId.get(user);
			ArrayList<Preference> preferences = entry.getValue();
			userData.put(userId, new GenericUserPreferenceArray(preferences));
		}

		return userData;
	}

	@Override
	public DataModel initializeDataModel(
			Map<UserItemPair, Object> seenPreferences,
			Map<Object, Long> userIds, Map<Object, Long> itemIds)
			throws Exception {

		// Assign user ids and sort preferences by user
		Map<Object,ArrayList<Preference>> userToPreferences = new HashMap<>();
		for (Entry<UserItemPair, Object> entry : seenPreferences.entrySet()) {
			UserItemPair userItemPair = entry.getKey();
			Object user = userItemPair.getUser();
			Object item = userItemPair.getItem();
			Object preference = entry.getValue();
			
			Long userId = userIds.get(user);
			Long itemId = itemIds.get(item);
			
			ArrayList<Preference> preferences = userToPreferences.get(user);
			if(preferences == null) {
				preferences = new ArrayList<>();
				userToPreferences.put(user, preferences);
			}
			Preference preferenceObj = initializePreference(userId, itemId, preference);
			preferences.add(preferenceObj);
		}
		
		DataModel dataModel = new GenericDataModel(initializeUserData(userIds, userToPreferences));
		return dataModel;
	}

}
