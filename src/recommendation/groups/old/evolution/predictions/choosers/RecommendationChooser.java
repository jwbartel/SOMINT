package recommendation.groups.old.evolution.predictions.choosers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import recommendation.groups.old.evolution.GroupPredictionList;
import recommendation.groups.old.evolution.RecommendedEvolution;
import recommendation.groups.old.evolution.old.GroupMorphingTuple;
import recommendation.groups.old.evolution.predictions.oldchoosers.OldGroupAndPredictionPair;


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
