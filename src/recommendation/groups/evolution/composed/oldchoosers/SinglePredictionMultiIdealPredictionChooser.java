package recommendation.groups.evolution.composed.oldchoosers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import recommendation.groups.evolution.GroupPredictionList;
import recommendation.groups.evolution.old.GroupMaintainer;
import recommendation.groups.evolution.old.GroupMorphingTuple;
import recommendation.groups.seedless.hybrid.IOFunctions;


public class SinglePredictionMultiIdealPredictionChooser<V> implements
		PredictionChooser<V> {
	
	ArrayList<GroupMorphingTuple<V>> tuples = new ArrayList<GroupMorphingTuple<V>>();

	@Override
	public int[] modelPredictionChoosingCase1(int participant, ArrayList<GroupPredictionList<V>> smallestPredictionLists, Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists, Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups, Collection<Set<V>> usedIdeals) {
		
		IOFunctions<Integer> ioHelp = new IOFunctions<Integer>(Integer.class);
		String idNameMap = "data/Kelli/FriendshipData/2010Study/"+participant+"_People.txt";
		ioHelp.fillNamesAndIDs(idNameMap);
		String idealFile = "data/Jacob/Ideal/"+participant+"_ideal.txt";
		Map<Set<Integer>, String> idealNames = ioHelp.loadIdealGroupNames(idealFile);
		
		int expandedGroups = 0;
		int addCount = 0;
		int removeCount = 0;
		int selectCount = 0;
		int automatedAdds = 0;
	
		for(int i=0; i<smallestPredictionLists.size(); i++){
			
			GroupPredictionList<V> predictionList = smallestPredictionLists.get(i);
			
			Set<V> oldGroup = predictionList.getF();
			if(usedOldGroups.contains(oldGroup) || predictionList.size() == 0){
				continue;
			}
			
			Set<V> prediction = predictionList.getPredictions().iterator().next();
			Set<V> membersToAdd = new TreeSet<V>(prediction);
			membersToAdd.retainAll(newMembers);
			
			ArrayList<Set<V>> possibleIdealExpansions = oldToIdealGroupsMap.get(oldGroup);
			ArrayList<String> possibleIdealExpansionsNames = new ArrayList<String>();
			for(Set<V> ideal: possibleIdealExpansions){
				possibleIdealExpansionsNames.add(idealNames.get(ideal));
			}
			
			Set<V> bestIdealExpansion = getBestIdealExpansion(oldGroup, membersToAdd, possibleIdealExpansions, usedIdeals);
			if(bestIdealExpansion != null){
				

				String bestIdealExpansionName = idealNames.get(bestIdealExpansion);
				
				Set<V> difference = new TreeSet<V>(bestIdealExpansion);
				difference.removeAll(oldGroup);
				int[] addsAndRemoves = GroupMaintainer.getAddsAndRemoves(oldGroup, membersToAdd, bestIdealExpansion);
				int adds = addsAndRemoves[0];
				int removes = addsAndRemoves[1];
				tuples.add(new GroupMorphingTuple<V>(oldGroup, prediction, bestIdealExpansion, difference.size(), adds, removes));
				
				expandedGroups++;
				addCount += adds;
				removeCount += removes;
				automatedAdds += membersToAdd.size();
			}
			removeSelection(oldGroup, prediction, bestIdealExpansion, oldToIdealGroupsMap, predictionLists, usedPairings, usedOldGroups, usedPredictedGroups, usedIdeals);
			
		}
		
		int[] stats = new int[5];
		stats[0] = expandedGroups;
		stats[1] = addCount;
		stats[2] = removeCount;
		stats[3] = selectCount;
		stats[4] = automatedAdds;
		return stats;
	}

	@Override
	public int[] modelPredictionChoosingCase2(int participant, ArrayList<GroupPredictionList<V>> smallestPredictionLists, Collection<GroupPredictionList<V>> intersectingLists,
			Collection<V> newMembers,Collection<GroupPredictionList<V>> predictionLists, Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups, Collection<Set<V>> usedIdeals) {
		
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
			stats = modelPredictionChoosingCase1(participant, disjointLists, newMembers, predictionLists, oldToIdealGroupsMap, usedPairings,usedOldGroups, usedPredictedGroups, usedIdeals);
			expandedGroups += stats[0];
			addCount += stats[1];
			removeCount += stats[2];
			selectCount += stats[3];
			automatedAdds += stats[4];
		}else{
			stopMorphing = true;
		}
		
		//Probably don't checkToStopMorphing
		//checkToStopMorphing = true;
		//break;
		//TODO:handle non-disjoint lists
		
		stats = new int[6];
		stats[0] = expandedGroups;
		stats[1] = addCount;
		stats[2] = removeCount;
		stats[3] = selectCount;
		stats[4] = automatedAdds;
		stats[5] = (stopMorphing)? 1:0;
		return stats;
	}

	@Override
	public int[] modelPredictionChoosingCase3(GroupPredictionList<V> predictionList, Collection<V> newMembers,
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
	public int[] modelPredictionChoosingCase4(ArrayList<GroupPredictionList<V>> smallestPredictionLists, Collection<V> newMembers,
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
	
	
	protected void removeSelection(Set<V> usedOldGroup, Set<V> usedPrediction, Set<V> usedIdeal, Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap,
			Collection<GroupPredictionList<V>> predictionLists, Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups, Collection<Set<V>> usedIdealGroups){
		
		usedOldGroups.add(usedOldGroup);
		usedPredictedGroups.add(usedPrediction);
		if(usedIdeal != null ) usedIdealGroups.add(usedIdeal);
		
		//Only use each prediction once at most
		Set<GroupPredictionList<V>> toRemove = new TreeSet<GroupPredictionList<V>>();
		for(GroupPredictionList<V> predictionList: predictionLists){
			
			if(predictionList.getF().equals(usedOldGroup)){
				//This already made all predicted expansions for this group
				toRemove.add(predictionList);
				continue;
			}
			
			predictionList.removePrediction(usedPrediction);
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
