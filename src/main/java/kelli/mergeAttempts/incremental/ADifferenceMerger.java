package kelli.mergeAttempts.incremental;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ADifferenceMerger implements CliqueMerger {
	float varyPercentage;
	int mergeCount = 0;
	Collection<Set<Integer>> mergedCliques;
	HashMap<Integer, Boolean> toRemoveClique;
	IncrementalMerger incrementalMerger;
	public boolean checkToRemoveCliques(int passNumber) {
		if(passNumber >= 1)
			return false;
		else return true;
	}

	public void init(float theVaryPercentage,
			Collection<Set<Integer>> theMergedCliques,
			HashMap<Integer, Boolean> theToRemoveClique,
			IncrementalMerger theIncrementalMerger) {
		varyPercentage = theVaryPercentage;
		mergedCliques = theMergedCliques;
		toRemoveClique = theToRemoveClique;
		incrementalMerger = theIncrementalMerger;
	}

	public boolean mergeCliques(Set<Integer> largerClique,
			Set<Integer> smallerClique, int largerCliqueNumber, int smallerCliqueNumber) {
	   boolean merged = false;
	   float percentage;
	   int numDifferent = 0;
	   Set<Integer> difference = new HashSet<Integer>();
	   for (int uid: smallerClique){
		  if(!largerClique.contains(uid)){
			 numDifferent++;
			 difference.add(uid);
		  }
	   }
	   percentage = (float)numDifferent/(float)smallerClique.size();
	   varyPercentage = incrementalMerger.getVaryPercentage();
	   if(percentage < varyPercentage){
		  // Remove both cliques, if they are already in mergedCliques.  Only keep the resulting mergedClique
		  mergedCliques.remove(largerClique);
		  mergedCliques.remove(smallerClique);
		  for(int uid: difference){
			  largerClique.add(uid);
		  }
		  mergedCliques.add(largerClique);
		  merged = true;
		  incrementalMerger.setMergeHappened(true);
		  toRemoveClique.put(smallerCliqueNumber,true);
		  mergeCount++;
		  System.out.println("Incremental:"+mergeCount+"   "+largerClique+", "+smallerClique);
	   }
	   return merged;
	}

}
