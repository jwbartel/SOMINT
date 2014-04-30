package groups.evolution.predictions.lists;

import groups.evolution.GroupPredictionList;
import groups.evolution.predictions.oldchoosers.OldGroupAndPredictionPair;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


public interface PredictionListMaker<V> {

	
	public GroupPredictionList<V> getPredictionList(Set<V> oldGroup, Collection<Set<V>> unusedRecommenderEngineResults,
			Collection<OldGroupAndPredictionPair<V>> usedPairings, Set<V> newIndividuals, double percentNew, double threshold);

	public GroupPredictionList<V> getPredictionList(Set<V> oldGroup, String oldGroupName, Collection<Set<V>> unusedRecommenderEngineResults,
			Collection<OldGroupAndPredictionPair<V>> usedPairings, Set<V> newIndividuals, double percentNew, double threshold, Map<Set<V>, String> predictionNames);
}
