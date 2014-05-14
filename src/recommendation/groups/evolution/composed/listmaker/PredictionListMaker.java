package recommendation.groups.evolution.composed.listmaker;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import recommendation.groups.evolution.GroupPredictionList;
import recommendation.groups.evolution.composed.oldchoosers.OldGroupAndPredictionPair;


public abstract class PredictionListMaker<V> {
	
	public Set<GroupPredictionList<V>> getAllMatchings(Collection<Set<V>> unusedOldGroups, Collection<Set<V>> unusedRecommenderEngineResults, 
			Collection<OldGroupAndPredictionPair<V>> usedPairings, Set<V> newIndividuals, double percentNew, double threshold){
		
		Set<GroupPredictionList<V>> unusedPredictionLists = new TreeSet<GroupPredictionList<V>>();
		
		for(Set<V> oldGroup : unusedOldGroups){ 
			
			GroupPredictionList<V> predictionList = getPredictionList(oldGroup, unusedRecommenderEngineResults, usedPairings, newIndividuals, percentNew, threshold);
			
			if(predictionList.size() > 0){
				unusedPredictionLists.add(predictionList);
			}
		}
		return unusedPredictionLists;
	}

	
	public abstract GroupPredictionList<V> getPredictionList(Set<V> oldGroup, Collection<Set<V>> unusedRecommenderEngineResults,
			Collection<OldGroupAndPredictionPair<V>> usedPairings, Set<V> newIndividuals, double percentNew, double threshold);

	public abstract GroupPredictionList<V> getPredictionList(Set<V> oldGroup, String oldGroupName, Collection<Set<V>> unusedRecommenderEngineResults,
			Collection<OldGroupAndPredictionPair<V>> usedPairings, Set<V> newIndividuals, double percentNew, double threshold, Map<Set<V>, String> predictionNames);
}
