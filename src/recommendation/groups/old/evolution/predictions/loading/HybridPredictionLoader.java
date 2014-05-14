/*
 *  Loads predictions from Kelli's Hybrid algorithm
 */
package recommendation.groups.old.evolution.predictions.loading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import recommendation.groups.seedless.hybrid.IOFunctions;
import bus.tools.FileFinder;


public class HybridPredictionLoader implements PredictionLoader<Integer> {

	IOFunctions<Integer> ioHelp;
	Map<Set<Integer>, ArrayList<Set<Integer>>> mergedMappings = new HashMap<Set<Integer>, ArrayList<Set<Integer>>>();
	

	public Collection<Set<Integer>> loadPredictions(int participant) {
		
		return loadPredictionNames(participant).keySet();
		
	}

	public Map<Set<Integer>, String> loadPredictionNames(int participant) {
		ioHelp = new IOFunctions<Integer>(Integer.class);
		fillNamesAndIDsForFriends(participant, ioHelp);

		Map<Set<Integer>, String> predictedSubcliquesNames =  loadPredictedSubcliqueNames(participant, ioHelp);
		Map<Set<Integer>, String> predictedNetworksNames = loadPredictedNetworkNames(participant, ioHelp);
		
		//Combine subcliques and networks
		Map<Set<Integer>, String> predictedGroupNames = new HashMap<Set<Integer>, String>(predictedSubcliquesNames);
		predictedGroupNames.putAll(predictedNetworksNames);

		//Hybrid algorithm requires a final, single-level merging of all networks and subcliques to find the correct group 
		predictedGroupNames = getSingleLevelMerges(predictedGroupNames);
		
		return predictedGroupNames;
		
	}
	
	private void fillNamesAndIDsForFriends(int participant, IOFunctions<Integer> ioHelp){
		String idNameMap = FileFinder.getFriendNameAndIdFileName(participant);
		ioHelp.fillNamesAndIDs(idNameMap);
	}
	
	private Map<Set<Integer>, String> loadPredictedSubcliqueNames(int participant, IOFunctions<Integer> ioHelp){
		String predictedSubcliquesFile = FileFinder.getHybridSubcliquesFileName(ioHelp, participant);
		Map<Set<Integer>, String> predictedSubcliquesNames = ioHelp.loadGroupNames("subclique-",predictedSubcliquesFile);
		return predictedSubcliquesNames;
	}
	
	private Map<Set<Integer>, String> loadPredictedNetworkNames(int participant, IOFunctions<Integer> ioHelp){
		String predictedNetworksFile = FileFinder.getHybridNetworksFileName(ioHelp, participant);
		Map<Set<Integer>, String> predictedNetworksNames = ioHelp.loadGroupNames("network-",predictedNetworksFile);
		return predictedNetworksNames;
	}
	
	protected Map<Set<Integer>, String> getSingleLevelMerges(Map<Set<Integer>, String> groups){
		Map<Set<Integer>, String> merges = new HashMap<Set<Integer>, String>();
		
		for(Set<Integer> group1:groups.keySet()){
			for(Set<Integer> group2:groups.keySet()){
				if(group1.equals(group2) || group1.containsAll(group2) || group2.containsAll(group1)) continue;
				
				Set<Integer> merge = new TreeSet<Integer>(group1);
				merge.addAll(group2);
				
				ArrayList<Set<Integer>> mappings = mergedMappings.get(merge);
				if(mappings == null){
					mappings = new ArrayList<Set<Integer>>();
					mergedMappings.put(merge, mappings);
				}
				mappings.add(group1);
				mappings.add(group2);
				
				
				
				String name1 = groups.get(group1);
				String name2 = groups.get(group2);
				
				merges.put(group1, name1);
				merges.put(group2, name2);
				if(!groups.containsKey(merge)){
					merges.put(merge, name1+" and "+name2);
				}
			}
		}
		
		return merges;
		
	}

}
