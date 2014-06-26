package snml.dataconvert.mahout;

import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.model.Preference;

public class GenericPreferenceCreator implements PreferenceCreator {

	@Override
	public Preference initializePreference(Long userId, Long itemId, Object preference)
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

}
