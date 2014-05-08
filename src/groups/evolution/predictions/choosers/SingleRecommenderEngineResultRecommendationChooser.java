package groups.evolution.predictions.choosers;

import groups.evolution.GroupPredictionList;
import groups.evolution.old.GroupMaintainer;
import groups.evolution.old.GroupMorphingTuple;
import groups.evolution.predictions.oldchoosers.OldGroupAndPredictionPair;
import groups.evolution.recommendations.RecommendedGroupChangeEvolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class SingleRecommenderEngineResultRecommendationChooser<V> implements
		RecommendationChooser<V> {
	
	ArrayList<GroupMorphingTuple<V>> tuples = new ArrayList<GroupMorphingTuple<V>>();
	
	@Override
	public Collection<RecommendedGroupChangeEvolution<V>> modelPredictionChoosingCase1(
			ArrayList<GroupPredictionList<V>> smallestPredictionLists, Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups) {
	
		Collection<RecommendedGroupChangeEvolution<V>> recommendations = new TreeSet<RecommendedGroupChangeEvolution<V>>();
		
		for(int i=0; i<smallestPredictionLists.size(); i++){
			
			GroupPredictionList<V> predictionList = smallestPredictionLists.get(i);
			
			Set<V> oldGroup = predictionList.getF();
			if(usedOldGroups.contains(oldGroup) || predictionList.size() == 0){
				continue;
			}
			
			Set<V> recommenderEngineResult = predictionList.getPredictions().iterator().next();
			RecommendedGroupChangeEvolution<V> recommendedEvolution = new RecommendedGroupChangeEvolution<V>(oldGroup, recommenderEngineResult, newMembers);
			recommendations.add(recommendedEvolution);
			
			removeSelection(oldGroup, recommenderEngineResult, predictionLists, usedPairings, usedOldGroups, usedPredictedGroups);
			
		}
		
		return recommendations;
	}

	@Override
	public Collection<RecommendedGroupChangeEvolution<V>> modelPredictionChoosingCase2(ArrayList<GroupPredictionList<V>> smallestPredictionLists, Collection<GroupPredictionList<V>> intersectingLists,
			Collection<V> newMembers,Collection<GroupPredictionList<V>> predictionLists,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups) {
		
		ArrayList<GroupPredictionList<V>> disjointLists = new ArrayList<GroupPredictionList<V>>(smallestPredictionLists);
		disjointLists.removeAll(intersectingLists);
		if(disjointLists.size() != 0){
			return modelPredictionChoosingCase1(disjointLists, newMembers, predictionLists, usedPairings, usedOldGroups, usedPredictedGroups);
		}else{
			return null;
		}
		
		//TODO:handle non-disjoint lists
	}

	@Override
	public Collection<RecommendedGroupChangeEvolution<V>> modelPredictionChoosingCase3(GroupPredictionList<V> predictionList, Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists, Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups, Collection<Set<V>> usedIdeals) {
		
		
		/*Set<V> oldGroup = predictionList.getF();
		if(!usedOldGroups.contains(oldGroup) && predictionList.size() != 0){
			
			for(Set<V> prediction: predictionList.getPredictions()){

				Set<V> membersToAdd = new TreeSet<V>(prediction);
				membersToAdd.retainAll(newMembers);
				
				ArrayList<Set<V>> possibleIdealExpansions = oldToIdealGroupsMap.get(oldGroup);
				Set<V> bestIdealExpansion = getBestIdealExpansion(oldGroup, membersToAdd, possibleIdealExpansions, usedIdeals);
				
				
			}
			
		}
		*/
		return null;
	}

	@Override
	public Collection<RecommendedGroupChangeEvolution<V>> modelPredictionChoosingCase4(ArrayList<GroupPredictionList<V>> smallestPredictionLists, Collection<V> newMembers,
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
