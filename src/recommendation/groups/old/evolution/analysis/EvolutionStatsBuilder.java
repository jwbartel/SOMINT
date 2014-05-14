package recommendation.groups.old.evolution.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import recommendation.groups.old.evolution.MembershipChangeFinder;
import recommendation.groups.old.evolution.RecommendedEvolution;

public class EvolutionStatsBuilder<V> {
	

	
	public static final double IDEAL_MATCHING_THRESHOLD = 0;

	
	public Map<Set<V>, ArrayList<Set<V>>> ComputeManualStats(Collection<Set<V>> ideals, Set<V> newIndividuals, EvolutionStats stats){
		
		Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap = new HashMap<Set<V>, ArrayList<Set<V>>>();
		
		int manualAddCost = 0;
		int manualDeleteCost = 0;
		int manualSplitCost = 0;
		
		MembershipChangeFinder<V> membershipChangeFinder = new MembershipChangeFinder<V>();
		
		for(Set<V> ideal:ideals){ 
			
			Set<V> oldGroup = membershipChangeFinder.getUnmaintainedGroup(ideal, newIndividuals);
		
			if(oldGroup.size() == 0) continue;
			
			manualAddCost += (ideal.size() - oldGroup.size());
			
			if(!oldToIdealGroupsMap.containsKey(oldGroup)){
				ArrayList<Set<V>> idealMappings = new ArrayList<Set<V>>();
				idealMappings.add(ideal);
				oldToIdealGroupsMap.put(oldGroup, idealMappings);
			}else{
				ArrayList<Set<V>> idealMappings = oldToIdealGroupsMap.get(oldGroup);
				idealMappings.add(ideal);
				manualSplitCost++;
			}
			
		}
		stats.setExpectedIdeals(ideals.size());
		stats.setNumOldGroups(oldToIdealGroupsMap.keySet().size());
		stats.setManualAdds(manualAddCost);
		stats.setManualDeletions(manualDeleteCost);
		stats.setManualSplits(manualSplitCost);
		return oldToIdealGroupsMap;
	}
	
	public void computeEvolutionStats(Set<V> newIndividuals, Collection<Set<V>> ideals, Collection<RecommendedEvolution<V>> recommendations,
			Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap, EvolutionStats stats){
		
		
		int additions = 0;
		int deletions = 0;
		
		int expandedGroups = 0;
		int automatedAdds = 0;
		
		Collection<Set<V>> usedIdeals = new HashSet<Set<V>>();
		
		for(RecommendedEvolution<V> recommendation: recommendations){
			Set<V> membersToAdd = new TreeSet<V>(recommendation.getMerging());
			membersToAdd.removeAll(recommendation.getOldGroup());
			
			Collection<Set<V>> possibleIdeals = oldToIdealGroupsMap.get(recommendation.getOldGroup());
			Set<V> bestIdeal = getBestIdealExpansion(recommendation.getOldGroup(), membersToAdd, possibleIdeals, usedIdeals);
			
			if(bestIdeal != null){
				int[] additionsAndDeletions = getAdditionsAndDeletions(recommendation.getOldGroup(), membersToAdd, bestIdeal);
				if(additionsAndDeletions[0] >= 0 && additionsAndDeletions[1] >= 0){
					additions += additionsAndDeletions[0];
					deletions += additionsAndDeletions[1];
					expandedGroups++;
					automatedAdds += membersToAdd.size();
					
					usedIdeals.add(bestIdeal);
					
				}
			}
		}
		
		Collection<Set<V>> unusedIdeals = new HashSet<Set<V>>(ideals);
		unusedIdeals.removeAll(usedIdeals);
		
		stats.setAdditions(additions);
		stats.setDeletions(deletions);
		stats.setGroupsExpanded(expandedGroups);
		stats.setAutomatedAdds(automatedAdds);
		stats.setMissedIdeals(unusedIdeals.size());
		
		int unchangedMissedIdeals = 0;
		for (Set<V> ideal : unusedIdeals) {
			Set<V> intersection = new HashSet<V>(ideal);
			intersection.retainAll(newIndividuals);
			if (intersection.size() == 0) {
				unchangedMissedIdeals++;
			}
		}
		stats.setUnchangedMissedIdeals(unchangedMissedIdeals);
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int[] getAdditionsAndDeletions(Set oldGroup, Set newMembers, Set ideal){
		int additions = 0;
		int deletions = 0;
		
		if(newMembers == null || ideal == null){
			int[] vals = {-1,-1};
			return vals;
		}
		
		Set tempGroup = new TreeSet(newMembers);
		tempGroup.addAll(oldGroup);
		
		boolean intersection = false;
		Iterator groupIter = tempGroup.iterator();
		while(groupIter.hasNext()){
			Object o = groupIter.next();
			if(!ideal.contains(o)){
				deletions++;
			}else{
				intersection= true;
			}
		}
		
		if(!intersection){
			int[] vals = {-1,-1};
			return vals;
		}
		
		Iterator  idealIter = ideal.iterator();
		while(idealIter.hasNext()){
			Object o = idealIter.next();
			if(!tempGroup.contains(o)){
				additions++;
			}
		}
		
		int[] vals = {additions,deletions};
		return vals;
	}

	
	private Set<V> getBestIdealExpansion(Set<V> oldGroup, Set<V> membersToAdd, Collection<Set<V>> possibleIdealExpansions,
			Collection<Set<V>> usedIdeals){
		
		Set<V> bestIdealExpansion = null;
		int costBestIdealExpansion = -1;
		
		for(Set<V> possibleIdealExpansion : possibleIdealExpansions){
			
			int[] addsAndRemoves = getAdditionsAndDeletions(oldGroup, membersToAdd, possibleIdealExpansion);
			int adds = addsAndRemoves[0];
			int removes = addsAndRemoves[1];
			int cost = adds + removes;
			

			if(costBestIdealExpansion == -1 ||
					(cost < costBestIdealExpansion && (usedIdeals.contains(bestIdealExpansion) || !usedIdeals.contains(bestIdealExpansion) || costBestIdealExpansion > cost + IDEAL_MATCHING_THRESHOLD)) ||
					(cost >= costBestIdealExpansion - IDEAL_MATCHING_THRESHOLD && usedIdeals.contains(bestIdealExpansion) && !usedIdeals.contains(possibleIdealExpansion))){
				bestIdealExpansion = possibleIdealExpansion;
				costBestIdealExpansion = cost;
			}
		}
		
		return bestIdealExpansion;
		
	}
	
}
