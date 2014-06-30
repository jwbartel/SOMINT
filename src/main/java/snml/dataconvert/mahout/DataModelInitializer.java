package snml.dataconvert.mahout;

import java.util.Map;

import org.apache.mahout.cf.taste.model.DataModel;

import snml.dataconvert.UserItemPair;

public interface DataModelInitializer {
	
	/**
	 * Creates the model based on the previously seen preferences
	 * @param seenPreferences  The collection of past preferences
	 * @return the model that stores all the previously seen preferences
	 * 			in the Mahout format
	 * @param userIds The ids of users
	 * @param itemIds The ids of items
	 * @throws Exception If the DataModel cannot be created
	 */
	public DataModel initializeDataModel(
			Map<UserItemPair, Object> seenPreferences,
			Map<Object, Long> userIds, Map<Object, Long> itemIds)
			throws Exception;
}
