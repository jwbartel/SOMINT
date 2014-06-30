package snml.dataconvert;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import snml.rule.NumericVectorFeatureRule;
import snml.rule.basicfeature.IBasicFeatureRule;

/**
 * Intermediate data set interface for recommendations. Recommendations require predicting
 * a user's preference for an object.  Therefore, this object must specify three features, one mapping
 * to a user, one to an item, and one to a preference.
 */
public abstract class IntermediateRecommendationDataSet implements IntermediateDataSet {
	
	protected IBasicFeatureRule userFeature;
	protected IBasicFeatureRule itemFeature;
	protected IBasicFeatureRule preferenceFeature;

	// Used to combine preferences when there are multiple preferences a single
	// user has specified for the same item
	PreferenceCombiner combiner;
	
	protected List<IntermediateRecommendationData> data = new ArrayList<>();
	protected Map<UserItemPair, Object> seenPreferences = new TreeMap<>();
	
	/**
	 * Creates an instance with the {@link MeanPreferenceCombiner} as the default combiner
	 * @param userFeature The feature the specifies the user(s) of a data item
	 * @param itemFeature The feature that specifies the item(s) of a data item
	 * @param preferenceFeature The feature that specifies the preference of a data item
	 */
	public IntermediateRecommendationDataSet(IBasicFeatureRule userFeature, IBasicFeatureRule itemFeature, IBasicFeatureRule preferenceFeature) {
		if (preferenceFeature instanceof NumericVectorFeatureRule) {
			throw new RuntimeException("Preference may not be a vector of values");
		}
		this.userFeature = userFeature;
		this.itemFeature = itemFeature;
		this.preferenceFeature = preferenceFeature;
		this.combiner = new MeanPreferenceCombiner();
	}
	
	
	/* (non-Javadoc)
	 * @see snml.dataconvert.IntermediateDataSet#addDataInstance(snml.dataconvert.IntermediateData)
	 */
	@Override
	public void addDataInstance(IntermediateData inst) throws Exception {
		if(!(inst instanceof IntermediateRecommendationData)) {
			throw new Exception("instance must be of type " +IntermediateRecommendationData.class.getName());
		}
		data.add((IntermediateRecommendationData) inst);
		addPreferenceInformation((IntermediateRecommendationData) inst);;
	}
	
	private void addPreferenceInformation(IntermediateRecommendationData inst) {
		Object userAttrib = inst.getUserAttribute();
		if (userAttrib.getClass().isArray()) {
			for (int i=0; i<Array.getLength(userAttrib); i++) {
				addUsersPreferenceInformation(Array.get(userAttrib, i), inst);
			}
		} else {
			addUsersPreferenceInformation(userAttrib, inst);
		}
	}
	
	private void addUsersPreferenceInformation(Object user, IntermediateRecommendationData inst) {
		Object preference = inst.getPreferenceAttribute();
		Object itemAttrib = inst.getItemAttribute();
		if (itemAttrib.getClass().isArray()) {
			for (int i=0; i<Array.getLength(itemAttrib); i++) {
				addUserItemPreferenceInformation(user, Array.get(itemAttrib, i), preference);
			}
	 	} else {
	 		addUserItemPreferenceInformation(user, itemAttrib, preference);
	 	}
		
	}
	
	private void addUserItemPreferenceInformation(Object user, Object item, Object preference) {
		UserItemPair pair = new UserItemPair(user, item);
		Object prevPreference = seenPreferences.get(pair);
		seenPreferences.put(pair, combiner.combine(pair, prevPreference, preference));
	}

	/* (non-Javadoc)
	 * @see snml.dataconvert.IntermediateDataSet#getDataInstance(int)
	 */
	@Override
	public IntermediateRecommendationData getDataInstance(int instId) throws Exception {
		return data.get(instId);
	}

	/* (non-Javadoc)
	 * @see snml.dataconvert.IntermediateDataSet#numAttributes()
	 */
	@Override
	public int numAttributes() throws Exception {
		return 3;
	}

	/* (non-Javadoc)
	 * @see snml.dataconvert.IntermediateDataSet#size()
	 */
	@Override
	public int size() {
		return data.size();
	}
	
	/**
	 * Creates and instance of the class with the same user, item, and
	 * preference feature, but with none of the contained data instances
	 * 
	 * @return The empty dataset
	 */
	protected abstract IntermediateRecommendationDataSet createEmptyVersion();
	
	/**
	 * A parent method this is not supported here since the number of attributes
	 * must always be 3
	 * 
	 * @param anotherDataSet
	 *            the dataset to be merged
	 * @return the merged dataset
	 * @throws Exception
	 *             if called.
	 */
	@Override
	public IntermediateRecommendationDataSet mergeByAttributes(
			IntermediateDataSet anotherDataSet) throws Exception {
		throw new Exception("mergeByAttributes not supported");
	}
	
	/* (non-Javadoc)
	 * @see snml.dataconvert.IntermediateDataSet#mergeByDataInstances(snml.dataconvert.IntermediateDataSet[])
	 */
	@Override
	public IntermediateRecommendationDataSet mergeByDataInstances(IntermediateDataSet[] otherDataSets) throws Exception {
		IntermediateRecommendationDataSet mergedDataSet = createEmptyVersion();
		for (IntermediateData dataItem : this.data) {
			mergedDataSet.addDataInstance(dataItem);
		}
		for (IntermediateDataSet anotherDataSet : otherDataSets) {
			if (!(anotherDataSet instanceof IntermediateRecommendationDataSet)){
				throw new Exception("uncampatible datasets type");
			}
			for (int i=0; i<anotherDataSet.size(); i++) {
				mergedDataSet.addDataInstance(anotherDataSet.getDataInstance(i));
			}
		}
		return mergedDataSet;
	}
	
	/* (non-Javadoc)
	 * @see snml.dataconvert.IntermediateDataSet#splitToTrainAndTest(double)
	 */
	@Override
	public IntermediateRecommendationDataSet[] splitToTrainAndTest(double trainPercent) throws Exception {
		IntermediateRecommendationDataSet train = createEmptyVersion();
		IntermediateRecommendationDataSet test = createEmptyVersion();
		
		for(int i=0; i<size(); i++) {
			if (i < size()*trainPercent) {
				train.addDataInstance(getDataInstance(i));
			} else {
				test.addDataInstance(getDataInstance(i));
			}
		}
		
		IntermediateRecommendationDataSet[] retVal = new IntermediateRecommendationDataSet[2];
		retVal[0] = train;
		retVal[1] = test;
		return retVal;
	}
	
	/**
	   * A no-op since the target index is always the preference
	   */
	public void setTargetIndex() {
		
	}
	
	/**
	   * A no-op since the target index is always the preference
	   *
	   * @param index the given index of dependent variable
	   */
	public void setTargetIndex(int index) {
		
	}
	

}
