package groups.evolution.predictions.choosers;

import groups.evolution.GroupPredictionList;
import groups.evolution.RecommendedEvolution;
import groups.evolution.old.GroupMaintainer;
import groups.evolution.old.GroupMorphingTuple;
import groups.evolution.predictions.oldchoosers.OldGroupAndPredictionPair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class SingleRecommenderEngineResultRecommendationChooser<V> implements
		RecommendationChooser<V> {
	
	ArrayList<GroupMorphingTuple<V>> tuples = new ArrayList<GroupMorphingTuple<V>>();
	
	@Override
	public Collection<RecommendedEvolution<V>> modelPredictionChoosingCase1(
			ArrayList<GroupPredictionList<V>> smallestPredictionLists, Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups) {
	
		Collection<RecommendedEvolution<V>> recommendations = new TreeSet<RecommendedEvolution<V>>();
		
		for(int i=0; i<smallestPredictionLists.size(); i++){
			
			GroupPredictionList<V> predictionList = smallestPredictionLists.get(i);
			
			Set<V> oldGroup = predictionList.getF();
			if(usedOldGroups.contains(oldGroup) || predictionList.size() == 0){
				continue;
			}
			
			Set<V> recommenderEngineResult = predictionList.getPredictions().iterator().next();
			RecommendedEvolution<V> recommendedEvolution = new RecommendedEvolution<V>(oldGroup, recommenderEngineResult, newMembers);
			recommendations.add(recommendedEvolution);
			
			removeSelection(oldGroup, recommenderEngineResult, predictionLists, usedPairings, usedOldGroups, usedPredictedGroups);
			
		}
		
		return recommendations;
	}

	@Override
	public Collection<RecommendedEvolution<V>> modelPredictionChoosingCase2(ArrayList<GroupPredictionList<V>> smallestPredictionLists, Collection<GroupPredictionList<V>> intersectingLists,
			Collection<V> newMembers,Collection<GroupPredictionList<V>> predictionLists,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups) {
		
		int expandedGroups = 0;
		int addCount = 0;
		int removeCount = 0;
		int selectCount = 0;
		int automatedAdds = 0;
		
		boolean stopMorphing = false;
		
		ArrayList<GroupPredictionList<V>> disjointLists = new ArrayList<GroupPredictionList<V>>(smallestPredictionLists);
		disjointLists.removeAll(intersectingLists);
		int[] stats;
		if(disjointLists.size() != 0){
			return modelPredictionChoosingCase1(disjointLists, newMembers, predictionLists, usedPairings, usedOldGroups, usedPredictedGroups);
		}else{
			return null;
		}
		
		//TODO:handle non-disjoint lists
		
		/*stats = new int[6];
		stats[0] = expandedGroups;
		stats[1] = addCount;
		stats[2] = removeCount;
		stats[3] = selectCount;
		stats[4] = automatedAdds;
		stats[5] = (stopMorphing)? 1:0;
		return stats;*/
	}

	@Override
	public Collection<RecommendedEvolution<V>> modelPredictionChoosingCase3(GroupPredictionList<V> predictionList, Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists, Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups, Collection<Set<V>> usedIdeals) {
		
		
		/*int expandedGroups = 0;
		int addCount = 0;
		int removeCount = 0;
		int selectCount = 0;
		int automatedAdds = 0;
		
		Set<V> oldGroup = predictionList.getF();
		if(!usedOldGroups.contains(oldGroup) && predictionList.size() != 0){
			
			for(Set<V> prediction: predictionList.getPredictions()){

				Set<V> membersToAdd = new TreeSet<V>(prediction);
				membersToAdd.retainAll(newMembers);
				
				ArrayList<Set<V>> possibleIdealExpansions = oldToIdealGroupsMap.get(oldGroup);
				Set<V> bestIdealExpansion = getBestIdealExpansion(oldGroup, membersToAdd, possibleIdealExpansions, usedIdeals);
				
				int[] addsAndRemoves = GroupMaintainer.getAddsAndRemoves(oldGroup, membersToAdd, bestIdealExpansion);
				int adds = addsAndRemoves[0];
				int removes = addsAndRemoves[1];
				
				expandedGroups++;
				addCount += adds;
				removeCount += removes;
				automatedAdds += membersToAdd.size();
				removeSelection(oldGroup, prediction, bestIdealExpansion, oldToIdealGroupsMap, predictionLists, usedOldGroups, usedPredictedGroups, usedIdeals);
			}
			
		}
		int[] stats = new int[5];
		stats[0] = expandedGroups;
		stats[1] = addCount;
		stats[2] = removeCount;
		stats[3] = selectCount;
		stats[4] = automatedAdds;
		return stats;*/
		return null;
	}

	@Override
	public Collection<RecommendedEvolution<V>> modelPredictionChoosingCase4(ArrayList<GroupPredictionList<V>> smallestPredictionLists, Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists, Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups, Collection<Set<V>> usedIdeals) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected Set<V> getBestIdealExpansion(Set<V> oldGroup, Set<V> membersToAdd, Collection<Set<V>> possibleIdealExpansions,
			Collection<Set<V>> usedIdeals){
		
		Set<V> bestIdealExpansion = null;
		int costBestIdealExpansion = -1;
		
		for(Set<V> possibleIdealExpansion : possibleIdealExpansions){
			
			int[] addsAndRemoves = GroupMaintainer.getAddsAndRemoves(oldGroup, membersToAdd, possibleIdealExpansion);
			int adds = addsAndRemoves[0];
			int removes = addsAndRemoves[1];
			int cost = adds + removes;
			

			if(costBestIdealExpansion == -1 ||
					(cost < costBestIdealExpansion && (usedIdeals.contains(bestIdealExpansion) || !usedIdeals.contains(bestIdealExpansion) || costBestIdealExpansion > cost + GroupMaintainer.IDEAL_MATCHING_THRESHOLD)) ||
					(cost >= costBestIdealExpansion - GroupMaintainer.IDEAL_MATCHING_THRESHOLD && usedIdeals.contains(bestIdealExpansion) && !usedIdeals.contains(possibleIdealExpansion))){
				bestIdealExpansion = possibleIdealExpansion;
				costBestIdealExpansion = cost;
			}
		}
		
		return bestIdealExpansion;
		
	}
	
	
	protected void removeSelection(Set<V> usedOldGroup, Set<V> usedRecommendedEvolution,
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
	
	public ArrayList<GroupMorphingTuple<V>> getTuples(){
		return tuples;
	}

}
