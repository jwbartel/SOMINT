/*
 *  Loads predictions from Kelli's Hybrid algorithm
 */
package groups.evolution.composed.loading;

import groups.seedless.hybrid.IOFunctions;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import bus.tools.FileFinder;


public class SnapHybridPredictionLoader extends HybridPredictionLoader {
	

	public Map<Set<Integer>, String> loadPredictionNames(int participant) {
		ioHelp = new IOFunctions<Integer>(Integer.class);
		ioHelp.setSubStepsFolder(FileFinder.getSnapHybridSubsetsFolder(participant));
		//fillNamesAndIDsForFriends(participant, ioHelp);

		Map<Set<Integer>, String> predictedSubcliquesNames =  loadPredictedSubcliqueNames(participant, ioHelp);
		Map<Set<Integer>, String> predictedNetworksNames = loadPredictedNetworkNames(participant, ioHelp);
		
		//Combine subcliques and networks
		Map<Set<Integer>, String> predictedGroupNames = new HashMap<Set<Integer>, String>(predictedSubcliquesNames);
		predictedGroupNames.putAll(predictedNetworksNames);

		//Hybrid algorithm requires a final, single-level merging of all networks and subcliques to find the correct group 
		predictedGroupNames = getSingleLevelMerges(predictedGroupNames);
		
		return predictedGroupNames;
		
	}
	
	private Map<Set<Integer>, String> loadPredictedSubcliqueNames(int participant, IOFunctions<Integer> ioHelp){
		String predictedSubcliquesFile = FileFinder.getSnapHybridSubcliquesFileName(ioHelp, participant);
		Map<Set<Integer>, String> predictedSubcliquesNames = ioHelp.loadGroupIDs("subclique-",predictedSubcliquesFile);
		return predictedSubcliquesNames;
	}
	
	private Map<Set<Integer>, String> loadPredictedNetworkNames(int participant, IOFunctions<Integer> ioHelp){
		String predictedNetworksFile = FileFinder.getSnapHybridNetworksFileName(ioHelp, participant);
		Map<Set<Integer>, String> predictedNetworksNames = ioHelp.loadGroupIDs("network-",predictedNetworksFile);
		return predictedNetworksNames;
	}

}
