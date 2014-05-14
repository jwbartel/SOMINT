package recommendation.groups.old.evolution.predictions.oldchoosers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import recommendation.groups.old.evolution.GroupPredictionList;



public class MultiPredictionMultiIdealPredictionChooser<V> 
		extends SinglePredictionMultiIdealPredictionChooser<V>
		implements PredictionChooser<V> {
	
	
	protected void removeSelection(Set<V> usedOldGroup, Set<V> usedPrediction, Set<V> usedIdeal, Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap,
			Collection<GroupPredictionList<V>> predictionLists, Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups, Collection<Set<V>> usedIdealGroups){
		
		usedOldGroups.add(usedOldGroup);
		usedPredictedGroups.add(usedPrediction);
		usedIdealGroups.add(usedIdeal);
		
		//Only use each (old group, prediction) pair at most once
		OldGroupAndPredictionPair<V> currPair = new OldGroupAndPredictionPair<V>(usedOldGroup, usedPrediction);
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
