package recommendation.groups.evolution.composed.oldchoosers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import recommendation.groups.evolution.GroupPredictionList;
import recommendation.groups.evolution.old.GroupMorphingTuple;


public interface PredictionChooser<V> {

	public int[] modelPredictionChoosingCase1(int participant, ArrayList<GroupPredictionList<V>> smallestPredictionLists, Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists, Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups, Collection<Set<V>> usedIdeals);

	public int[] modelPredictionChoosingCase2(int participant, ArrayList<GroupPredictionList<V>> smallestPredictionLists, Collection<GroupPredictionList<V>> intersectingLists,
			Collection<V> newMembers, Collection<GroupPredictionList<V>> predictionLists, Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap, 
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups, Collection<Set<V>> usedIdeals);
	
	public int[] modelPredictionChoosingCase3(GroupPredictionList<V> predictionList, Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists, Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups, Collection<Set<V>> usedIdeals);

	public int[] modelPredictionChoosingCase4(ArrayList<GroupPredictionList<V>> smallestPredictionLists, Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists, Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups, Collection<Set<V>> usedIdeals);
	
	public ArrayList<GroupMorphingTuple<V>> getTuples();
}
