package groups.evolution.composed.listmaker;

import groups.evolution.GroupPredictionList;
import groups.evolution.composed.oldchoosers.OldGroupAndPredictionPair;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


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
