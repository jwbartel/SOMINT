package groups.evolution.predictions.matchers;

import groups.evolution.GroupPredictionList;
import groups.evolution.old.GroupMorphingTuple;
import groups.evolution.predictions.oldchoosers.OldGroupAndPredictionPair;
import groups.evolution.recommendations.RecommendedGroupChangeEvolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;


public interface RecommendationMatcher<V> {

	public Collection<RecommendedGroupChangeEvolution<V>> modelPredictionChoosingCase1(ArrayList<GroupPredictionList<V>> smallestPredictionLists, Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups);
	
	public Collection<RecommendedGroupChangeEvolution<V>> modelPredictionChoosingCase2(ArrayList<GroupPredictionList<V>> smallestPredictionLists, Collection<GroupPredictionList<V>> intersectingLists,
			Collection<V> newMembers, Collection<GroupPredictionList<V>> predictionLists, 
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups);
	
	public Collection<RecommendedGroupChangeEvolution<V>> modelPredictionChoosingCase3(GroupPredictionList<V> predictionList, Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists, Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups, Collection<Set<V>> usedIdeals);

	public Collection<RecommendedGroupChangeEvolution<V>> modelPredictionChoosingCase4(ArrayList<GroupPredictionList<V>> smallestPredictionLists, Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists, Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups, Collection<Set<V>> usedIdeals);
	
	public ArrayList<GroupMorphingTuple<V>> getTuples();

	void removeSelection(Set<V> usedOldGroup, Set<V> usedRecommendedEvolution,
			Collection<GroupPredictionList<V>> predictionLists,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups,
			Collection<Set<V>> usedRecommendedEvolutions);
}
