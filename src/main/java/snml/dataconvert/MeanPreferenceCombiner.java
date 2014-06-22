package snml.dataconvert;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 * Combines multiple preferences for a user-item pair by returning the mean of all preferences for that pair
 */
public class MeanPreferenceCombiner implements PreferenceCombiner {
	
	Map<UserItemPair, SummaryStatistics> pastPreferences = new TreeMap<>();
	
	/**
	 * Combines a new preference with a previous preference and returns the combined value.
	 * @param pair The user and item for which it has made a preference.
	 * @param previousPreference The previous preference that system has recorded for this user-item pair
	 * @param newPreference The new preference expressed.
	 * @return The mean of the new preference and all previous preferences
	 */
	@Override
	public Object combine(UserItemPair pair, Object previousPreference,
			Object newPreference) {
		
		SummaryStatistics statsForPair = pastPreferences.get(pair);
		if (statsForPair == null) {
			statsForPair = new SummaryStatistics();
			pastPreferences.put(pair, statsForPair);
		}
		double prefVal = Double.parseDouble(newPreference.toString());
		statsForPair.addValue(prefVal);
		
		return statsForPair.getMean();
	}

}
