package snml.dataconvert.mahout;

import org.apache.mahout.cf.taste.model.Preference;

public interface PreferenceCreator {

	/*
	 * Creates a preference object basd on a userId, an itemId, and an Object preference
	 * @return The Preference object. Null if userId or itemId is null
	 * @throws Exception if the Preference cannot be created
	 */
	public Preference initializePreference(Long userId, Long itemId, Object preference) throws Exception;
	
}
