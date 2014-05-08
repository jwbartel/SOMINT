package groups.evolution.composed.cleanuppers;

import groups.evolution.GroupPredictionList;
import groups.evolution.composed.oldchoosers.OldGroupAndPredictionPair;

import java.util.Collection;
import java.util.Set;


public interface RecommendationCleanupper<V> {

	void removeSelection(Set<V> usedOldGroup, Set<V> usedRecommendedEvolution,
			Collection<GroupPredictionList<V>> predictionLists,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups,
			Collection<Set<V>> usedRecommendedEvolutions);
}
