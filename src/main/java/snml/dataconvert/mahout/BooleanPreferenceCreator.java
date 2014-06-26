package snml.dataconvert.mahout;

import org.apache.mahout.cf.taste.impl.model.BooleanPreference;
import org.apache.mahout.cf.taste.model.Preference;

public class BooleanPreferenceCreator implements PreferenceCreator {

	@Override
	public Preference initializePreference(Long userId, Long itemId, Object preference)
			throws Exception {
		if (userId == null || itemId == null) {
			return null;
		}
		if ((preference instanceof Boolean) || (preference instanceof String)) {
			boolean booleanPreference = Boolean.parseBoolean(preference.toString());
			if (booleanPreference) {
				return new BooleanPreference(userId, itemId);
			} else {
				return null;
			}
			
		}

		throw new Exception("Preference could not be converted to a float");
	}

}
