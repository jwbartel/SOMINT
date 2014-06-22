package snml.dataconvert;

import snml.rule.IFeatureRule;
import snml.rule.basicfeature.IBasicFeatureRule;

/**
 * Super class of all initializers for Recommendation Data Initializers
 */
public abstract class IntermediateRecommendationDataInitializer implements IntermediateDataInitializer {
	
	/**
	 * Checks the rules to see if they are compatible with recommendation
	 * @param rules The rules to check
	 * @throws Exception if the rules are not compatible
	 */
	protected void checkRules(IFeatureRule[] rules) throws Exception {
		if (rules.length != 3) {
			throw new Exception("Must have exactly 3 rules for user, item, and preference");
		}
		for (int i=0; i<3; i++) {
			if (!(rules[i] instanceof IBasicFeatureRule)) {
				throw new Exception("Rules must be of type "+IBasicFeatureRule.class.getName());
			}
		}
	}
	
	/**
	 * Create an {@link IntermediateRecommendationDataSet} with given name, instance number,
	 * and define the attributes with feature extracting rules.
	 *
	 * @param destDataSetName the name of created dataset
	 * @param threadNum the number of instances in the dataset
	 * @param userRule the rule determining how a user is identified
	 * @param itemRule the rule determining how an item is identified
	 * @param preferenceRule
	 * @throws Exception if data set creation fails
	 */
	public abstract IntermediateRecommendationDataSet initDestDataSet(String destDataSetName,
			int threadNum, IBasicFeatureRule userRule,
			IBasicFeatureRule itemRule,
			IBasicFeatureRule preferenceRule) throws Exception;

	@Override
	public IntermediateRecommendationDataSet initDestDataSet(String destDataSetName,
			int threadNum, IFeatureRule[] rules) throws Exception {
		
		checkRules(rules);
		return initDestDataSet(destDataSetName, threadNum,
				(IBasicFeatureRule) rules[0],
				(IBasicFeatureRule) rules[1],
				(IBasicFeatureRule) rules[3]);
	}
	
	public abstract IntermediateRecommendationData initADataInstance(
			IntermediateDataSet relatedDataset,
			IBasicFeatureRule userRule,
			IBasicFeatureRule itemRule,
			IBasicFeatureRule preferenceRule) throws Exception;
	
	public IntermediateRecommendationData initADataInstance(
			IntermediateDataSet relatedDataset, IFeatureRule[] rules) throws Exception {
		checkRules(rules);
		return initADataInstance(relatedDataset, (IBasicFeatureRule) rules[0],
				(IBasicFeatureRule) rules[1],
				(IBasicFeatureRule) rules[3]);
	}

}
