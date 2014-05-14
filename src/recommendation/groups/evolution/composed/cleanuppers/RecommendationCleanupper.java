package recommendation.groups.evolution.composed.cleanuppers;

import java.util.Collection;
import java.util.Set;

import recommendation.groups.evolution.GroupPredictionList;
import recommendation.groups.evolution.composed.oldchoosers.OldGroupAndPredictionPair;


public interface RecommendationCleanupper<V> {

	void removeSelection(Set<V> usedOldGroup, Set<V> usedRecommendedEvolution,
			Collection<GroupPredictionList<V>> predictionLists,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups,
			Collection<Set<V>> usedRecommendedEvolutions);
}
