package recommendation.groups.old.evolution.predictions.oldchoosers;

import java.util.Collection;
import java.util.Set;

import recommendation.groups.old.evolution.old.GroupMaintainer;


public class MultiPredictionSingleIdealPredictionChooser<V> extends
		MultiPredictionMultiIdealPredictionChooser<V> {

	protected Set<V> getBestIdealExpansion(Set<V> oldGroup, Set<V> membersToAdd, Collection<Set<V>> possibleIdealExpansions,
			Collection<Set<V>> usedIdeals){
		
		Set<V> bestIdealExpansion = null;
		int costBestIdealExpansion = -1;
		
		for(Set<V> possibleIdealExpansion : possibleIdealExpansions){
			
			if(usedIdeals.contains(bestIdealExpansion)) continue;
			
			int[] addsAndRemoves = GroupMaintainer.getAddsAndRemoves(oldGroup, membersToAdd, possibleIdealExpansion);
			int adds = addsAndRemoves[0];
			int removes = addsAndRemoves[1];
			int cost = adds + removes;
			

			if(costBestIdealExpansion == -1 || cost < costBestIdealExpansion - GroupMaintainer.IDEAL_MATCHING_THRESHOLD){
				bestIdealExpansion = possibleIdealExpansion;
				costBestIdealExpansion = cost;
			}
		}
		
		return bestIdealExpansion;
		
	}
}
