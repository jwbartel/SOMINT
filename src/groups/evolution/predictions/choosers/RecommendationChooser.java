package groups.evolution.predictions.choosers;

import groups.evolution.GroupPredictionList;
import groups.evolution.RecommendedEvolution;
import groups.evolution.old.GroupMorphingTuple;
import groups.evolution.predictions.oldchoosers.OldGroupAndPredictionPair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;


public interface RecommendationChooser<V> {

	public Collection<RecommendedEvolution<V>> modelPredictionChoosingCase1(ArrayList<GroupPredictionList<V>> smallestPredictionLists, Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups);
	
	public Collection<RecommendedEvolution<V>> modelPredictionChoosingCase2(ArrayList<GroupPredictionList<V>> smallestPredictionLists, Collection<GroupPredictionList<V>> intersectingLists,
			Collection<V> newMembers, Collection<GroupPredictionList<V>> predictionLists, 
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups);
	
	public Collection<RecommendedEvolution<V>> modelPredictionChoosingCase3(GroupPredictionList<V> predictionList, Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists, Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups, Collection<Set<V>> usedIdeals);

	public Collection<RecommendedEvolution<V>> modelPredictionChoosingCase4(ArrayList<GroupPredictionList<V>> smallestPredictionLists, Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists, Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups, Collection<Set<V>> usedIdeals);
	
	public ArrayList<GroupMorphingTuple<V>> getTuples();
}
