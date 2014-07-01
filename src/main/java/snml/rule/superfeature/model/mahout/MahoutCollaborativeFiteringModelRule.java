package snml.rule.superfeature.model.mahout;

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
		return estimatePreference(data.getUserAttribute(), data.getItemAttribute());
	}

	@Override
	public Object extract(IntermediateData anInstData) throws Exception {
		if (anInstData instanceof MahoutData) {
			return estimatePreference((MahoutData) anInstData);
		}
		throw new Exception("Data must of type MahoutData");
	}
}
