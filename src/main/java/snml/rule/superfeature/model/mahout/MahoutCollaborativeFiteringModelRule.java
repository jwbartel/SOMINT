package snml.rule.superfeature.model.mahout;

import java.lang.reflect.Array;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import snml.dataconvert.IntermediateData;
import snml.dataconvert.mahout.MahoutData;
import snml.rule.superfeature.model.NumericModelRule;

public abstract class MahoutCollaborativeFiteringModelRule extends
		NumericModelRule implements IMahoutModelRule {

	public MahoutCollaborativeFiteringModelRule(String featureName) {
		super(featureName);
	}

	/**
	 * Returns the predicted preference for a user-item pair
	 * @param user The user to predict a preference for
	 * @param item The item to predict a preference for
	 * @return A float representing the preference.  This will be null if no preference can be predicted.
	 */
	public abstract Float estimatePreference(Object user, Object item);

	/**
	 * Returns the predicted preference for a user-item pair
	 * @param user The user to predict a preference for
	 * @param item The item to predict a preference for
	 * @return A float representing the preference.  This will be null if no preference can be predicted.
	 */
	public Float estimatePreference(MahoutData data) {
		Object user = data.getUserAttribute();
		Object item = data.getItemAttribute();

		if (user == null || item == null) {
			return null;
		}

		if (user.getClass().isArray() || item.getClass().isArray()) {

			DescriptiveStatistics stats = new DescriptiveStatistics();
			if (user.getClass().isArray()) {
				for (int i = 0; i < Array.getLength(user); i++) {
					if (item.getClass().isArray()) {
						for (int j = 0; j < Array.getLength(item); j++) {
							Float preference = estimatePreference(
									Array.get(user, i), Array.get(item, j));
							if (preference != null) {
								stats.addValue((float) preference);
							}
						}
					} else {
						Float preference = estimatePreference(
								Array.get(user, i), item);
						if (preference != null) {
							stats.addValue((float) preference);
						}
					}
				}
			} else {
				for (int i = 0; i < Array.getLength(item); i++) {
					Float preference = estimatePreference(user,
							Array.get(item, i));
					if (preference != null) {
						stats.addValue((float) preference);
					}
				}
			}
			if (stats.getN() > 0) {
				return (float) stats.getMean();
			} else {
				return null;
			}
		} else {
			return estimatePreference(data.getUserAttribute(),
					data.getItemAttribute());
		}
	}

	@Override
	public Object extract(IntermediateData anInstData) throws Exception {
		if (anInstData instanceof MahoutData) {
			return estimatePreference((MahoutData) anInstData);
		}
		throw new Exception("Data must of type MahoutData");
	}
}
