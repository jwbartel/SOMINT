package snml.dataconvert;

/**
 * Interface for classes to combine preferences when a user-item pair occurs
 * multiple times for an {@link IntermediateRecommendationDataSet}
 */
public interface PreferenceCombiner {

	/**
	 * Combines a new preference with a previous preference and returns the combined value.
	 * @param pair The user and item for which it has made a preference.
	 * @param previousPreference The previous preference that system has recorded for this user-item pair
	 * @param newPreference The new preference expressed.
	 * @return The combined preference
	 */
	public Object combine(UserItemPair pair, Object previousPreference,
			Object newPreference);

}
