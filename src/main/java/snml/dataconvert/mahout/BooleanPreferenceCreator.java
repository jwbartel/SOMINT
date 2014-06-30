package snml.dataconvert.mahout;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.model.DataModel;

import snml.dataconvert.UserItemPair;

public class BooleanPreferenceCreator implements DataModelInitializer {
	
	private FastByIDMap<FastIDSet> initializeUserData(
			Map<Object, Long> userToId,
			Map<Object, FastIDSet> userToPreferences) {

		FastByIDMap<FastIDSet> userData = new FastByIDMap<>(
				userToPreferences.size());
		for (Entry<Object, FastIDSet> entry : userToPreferences.entrySet()) {
			Object user = entry.getKey();
			Long userId = userToId.get(user);
			FastIDSet preferences = entry.getValue();
			userData.put(userId, preferences);
		}

		return userData;
	}

	@Override
	public DataModel initializeDataModel(
			Map<UserItemPair, Object> seenPreferences) throws Exception {
		
		// Assign user ids and sort preferences by user
		Map<Object,Long> userToId = new HashMap<>();
		Map<Object,Long> itemToId = new HashMap<>();
		Map<Object,FastIDSet> userToPreferences = new HashMap<>();
		for (Entry<UserItemPair, Object> entry : seenPreferences.entrySet()) {
			UserItemPair userItemPair = entry.getKey();
			Object user = userItemPair.getUser();
			Object item = userItemPair.getItem();
			Object preference = entry.getValue();
			if (preference == null) {
				continue;
			}
			
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
			FastIDSet preferences = userToPreferences.get(user);
			if(preferences == null) {
				preferences = new FastIDSet();
				userToPreferences.put(user, preferences);
			}
			preferences.add(itemId);
		}
		
		DataModel dataModel = new GenericBooleanPrefDataModel(initializeUserData(userToId, userToPreferences));
		return dataModel;
	}

}
