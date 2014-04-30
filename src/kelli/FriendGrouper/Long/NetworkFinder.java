package kelli.FriendGrouper.Long;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NetworkFinder {
   private static Collection<Set<Long>> mergedCliques = new ArrayList<Set<Long>>();
   private static HashMap<Integer, Boolean> toRemoveClique = null;
   private static boolean mergeHappened = true;
   static int mergeCount =0;
   static int compareCount = 0;
   public static Collection<Set<Long>> doNetworkFind(Collection<Set<Long>> cliques){
      long passStart, elapsedTime;
      float elapsedTimeMin;
      int passNumber = 1;
      while(mergeHappened){
    	 mergeHappened = false;
    	 toRemoveClique = new HashMap<Integer, Boolean>();
    	 for(int i = 0; i < cliques.size(); i++)
    		toRemoveClique.put(i, false);
    	 passStart = System.currentTimeMillis();
    	 combineNetworkCliques(cliques);
    	 for(Set<Long> clique: mergedCliques){
    		cliques.remove(clique);
    	 }
    	 mergedCliques = new ArrayList<Set<Long>>(); 
    	 elapsedTime = System.currentTimeMillis() - passStart;
    	 elapsedTimeMin = elapsedTime/(60*1000F);
    	 System.out.println("pass "+passNumber+" cliques.size: "+cliques.size()+"   pass time: "+elapsedTimeMin);
    	 passNumber++;
      }
      System.out.println("final cliques.size: "+cliques.size());
      return cliques;
   }
   private static void combineNetworkCliques(Collection<Set<Long>> cliques0){
	  int iterationCount = 0; int innerIter;
	  List<Set<Long>> cliques = (List<Set<Long>>) cliques0;
	  boolean firstOfCliquePairMerged = false;
	  for (Set<Long> c1: cliques){
		 if(!toRemoveClique.get(iterationCount) ){
			innerIter = 0;
			for (Set<Long> c2: cliques){
			   if(!toRemoveClique.get(innerIter) ){
				  if (!c1.equals(c2)){
					 if(c1.size() >= c2.size()) 
						mergeNetworkCliques(c1, c2, iterationCount, innerIter);
					 else
						firstOfCliquePairMerged = mergeNetworkCliques(c2, c1, innerIter, iterationCount); 
					 	//possible problem...if c1 merged into c5, i shouldn't be able to try c6 into c1
				  }
				  if(firstOfCliquePairMerged) {
					 firstOfCliquePairMerged = false;
					 break; //break forloop from (clique x, clique y) to (clique x+1, clique init)
				  }
			   }
			   innerIter++;
			}
		 }
		 iterationCount++;
	  }
   }
   private static boolean mergeNetworkCliques(Set<Long> c1, Set<Long> c2, int largerCliqueIndex, int smallerCliqueIndex){
	  boolean merged = false;
	  float percentSame, percentDiff;
	  int numDifferent = 0; int intersectionCount = 0;
	  Set<Long> difference = new HashSet<Long>();
	  //intersection:
	  for (Long uid: c2){
		 if(c1.contains(uid))
			intersectionCount++;
		 else{
			numDifferent++;
			difference.add(uid);
		 }
	  }
	  compareCount++;
	  percentSame = (float)intersectionCount/(float)c2.size();
	  percentDiff = (float) numDifferent/(float)c2.size();
	  if(percentSame >= .9F || percentDiff <= .35F){
		 for(Long uid: difference){
			c1.add(uid);
		 }
		 mergeHappened = true;
		 merged = true;
		 mergedCliques.add(c2); //the cliques that were merged into a larger clique...to be removed
		 toRemoveClique.put(smallerCliqueIndex, true);
		 mergeCount++;
	  }	  else {
		  //System.out.println("noMergeHere: largerCliqueNum = "+largerCliqueIndex);
	  }
	  return merged;
   }
}
