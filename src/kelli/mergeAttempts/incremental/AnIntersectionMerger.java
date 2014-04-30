package kelli.mergeAttempts.incremental;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


@SuppressWarnings("unused")
public class AnIntersectionMerger implements CliqueMerger {
	float varyPercentage;
	Collection<Set<Integer>> mergedCliques;
	HashMap<Integer, Boolean> toRemoveClique;
	IncrementalMerger incrementalMerger;
	private int mergeCount;
	//int compareCount;
	public void init (float theVaryPercentage, 
			Collection<Set<Integer>> theMergedCliques, 
			HashMap<Integer, Boolean> theToRemoveClique, 
			IncrementalMerger theIncrementalMerger){
		varyPercentage = theVaryPercentage;
		mergedCliques = theMergedCliques;
		toRemoveClique = theToRemoveClique;
		incrementalMerger = theIncrementalMerger;
		//compareCount = 0;
	}
	public boolean mergeCliques(Set<Integer> c1, Set<Integer> c2, int largerCliqueIndex, int smallerCliqueIndex){
	   boolean merged = false;
	   int mergeNum = IncrementalMerger.getMergeNumber();
		  int compNum = IncrementalMerger.getComparisonNumber();
	   if(compNum >= 305){
		   boolean breakpoint = true;
	   }
	   float percentSame, percentDiff;
	   int numDifferent = 0; int intersectionCount = 0;
	   Set<Integer> difference = new HashSet<Integer>();
	   //intersection:
	   for (int uid: c2){
		  if(c1.contains(uid))
			 intersectionCount++;
		  else{
			 numDifferent++;
			 difference.add(uid);
		  }
	   }
	   percentSame = (float)intersectionCount/(float)c2.size();
	   percentDiff = (float) numDifferent/(float)c2.size();
	   //varyPercentage = incrementalMerger.getVaryPercentage();
	   if(percentSame >= .9F || percentDiff <= .35F){
		  for(int uid: difference){
			 c1.add(uid);
		  }
		  merged = true;
		  incrementalMerger.setMergeHappened(true);
		  mergedCliques.add(c2); //the cliques that were merged into a larger clique...to be removed
		  toRemoveClique.put(smallerCliqueIndex, true);
		  CliqueDriver.incrementalTrace.add(new ATrace(c2.size(), c1.size(), c2, c1, smallerCliqueIndex, largerCliqueIndex, IncrementalMerger.getMergeNumber(), IncrementalMerger.getComparisonNumber()));
	   } else {
//			  System.out.println("incremental, not merged: compNum = "+compNum+" mergeNum = "+mergeNum);
	   }
	   return merged;
	}
	public boolean checkToRemoveCliques(int passNumber) {		
		return true;
	}

}
