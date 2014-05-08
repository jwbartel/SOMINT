package groups.evolution.composed.listmaker;

import groups.evolution.GroupPredictionList;
import groups.evolution.composed.oldchoosers.OldGroupAndPredictionPair;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class PredictionListSelector {
	static PredictionListMakerFactory factory = new ExpectedScalingPredictionListMakerFactory<Integer>();
	
	public static <V> void setFactory(PredictionListMakerFactory<V> f){
		factory = f;
	}
	
	public static <V> Set<GroupPredictionList<V>> getAllMatchings(Collection<Set<V>> unusedOldGroups, Collection<Set<V>> unusedRecommenderEngineResults, 
			Collection<OldGroupAndPredictionPair<V>> usedPairings, Set<V> newIndividuals, double percentNew, double threshold){
		
		Set<GroupPredictionList<V>> unusedPredictionLists = new TreeSet<GroupPredictionList<V>>();
		
		for(Set<V> oldGroup : unusedOldGroups){ 
			
			GroupPredictionList<V> predictionList = PredictionListSelector.getPredictionList(oldGroup, unusedRecommenderEngineResults, usedPairings, newIndividuals, percentNew, threshold);
			
			if(predictionList.size() > 0){
				unusedPredictionLists.add(predictionList);
			}
		}
		return unusedPredictionLists;
	}
	
	public static <V> GroupPredictionList<V> getPredictionList(Set<V> oldGroup, Collection<Set<V>> unusedRecommenderEngineResults,
			Collection<OldGroupAndPredictionPair<V>> usedPairings, Set<V> newIndividuals, double percentNew, double threshold){
		
		return factory.getPredictionListMaker().getPredictionList(oldGroup, unusedRecommenderEngineResults, usedPairings, newIndividuals, percentNew, threshold);
	}
	
	public static GroupPredictionList<Integer> getPredictionList(Set<Integer> oldGroup, String oldGroupName, Collection<Set<Integer>> unusedRecommenderEngineResults, 
			Collection<OldGroupAndPredictionPair<Integer>> usedPairings, Set<Integer> newIndividuals, double percentNew, double threshold, Map<Set<Integer>, String> predictionNames){
		
		return factory.getPredictionListMaker().getPredictionList(oldGroup, oldGroupName, unusedRecommenderEngineResults, usedPairings, newIndividuals, percentNew, threshold, predictionNames);
	}
}
