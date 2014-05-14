package recommendation.groups.evolution.composed.cleanuppers;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import recommendation.groups.evolution.GroupPredictionList;
import recommendation.groups.evolution.composed.listmaker.GroupAndPredictionPair;



public class MultiRecommenderEngineResultRecommendationCleanupper<V> 
		implements RecommendationCleanupper<V> {
	
	@Override
	public void removeSelection(Set<V> usedOldGroup, Set<V> usedRecommendedEvolution, 
			Collection<GroupPredictionList<V>> predictionLists, 
			Collection<GroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedRecommendedEvolutions){
		
		usedOldGroups.add(usedOldGroup);
		usedRecommendedEvolutions.add(usedRecommendedEvolution);
		
		//Only use each (old group, prediction) pair at most once
		GroupAndPredictionPair<V> currPair = new GroupAndPredictionPair<V>(usedOldGroup, usedRecommendedEvolution);
		usedPairings.add(currPair);
		
		
		Set<GroupPredictionList<V>> toRemove = new TreeSet<GroupPredictionList<V>>();
		for(GroupPredictionList<V> predictionList: predictionLists){
			
			if(predictionList.getF().equals(usedOldGroup)){
				//This already made all predicted expansions for this group
				toRemove.add(predictionList);
				continue;
			}
			
		}
		predictionLists.removeAll(toRemove);
		
	}

}
