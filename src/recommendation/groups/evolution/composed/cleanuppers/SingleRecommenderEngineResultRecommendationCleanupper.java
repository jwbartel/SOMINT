package recommendation.groups.evolution.composed.cleanuppers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import recommendation.groups.evolution.GroupPredictionList;
import recommendation.groups.evolution.composed.oldchoosers.OldGroupAndPredictionPair;
import recommendation.groups.evolution.old.GroupMaintainer;
import recommendation.groups.evolution.old.GroupMorphingTuple;
import recommendation.groups.evolution.recommendations.RecommendedGroupChangeEvolution;


public class SingleRecommenderEngineResultRecommendationCleanupper<V> implements
		RecommendationCleanupper<V> {
	
	@Override
	public void removeSelection(Set<V> usedOldGroup, Set<V> usedRecommendedEvolution,
			Collection<GroupPredictionList<V>> predictionLists, 
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedRecommendedEvolutions){
		
		usedOldGroups.add(usedOldGroup);
		usedRecommendedEvolutions.add(usedRecommendedEvolution);
		
		//Only use each prediction once at most
		Set<GroupPredictionList<V>> toRemove = new TreeSet<GroupPredictionList<V>>();
		for(GroupPredictionList<V> predictionList: predictionLists){
			
			if(predictionList.getF().equals(usedOldGroup)){
				//This already made all predicted expansions for this group
				toRemove.add(predictionList);
				continue;
			}
			
			predictionList.removePrediction(usedRecommendedEvolution);
			if(predictionList.size() == 0){
				toRemove.add(predictionList);
			}
			
		}
		predictionLists.removeAll(toRemove);
		
	}

}
