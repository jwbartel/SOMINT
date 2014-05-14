package kelli.mergeAttempts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IntersectionClean {
   private static Collection<Set<Integer>> mergedCliques = new ArrayList<Set<Integer>>();
   private static HashMap<Integer, Boolean> toRemoveClique = null;
   private static boolean mergeHappened = true;
   static int mergeCount =0;
   static int compareCount = 0;
   static int iterationCount; static int innerIter;
   public static Collection<Set<Integer>> doIntersectionMerges(Collection<Set<Integer>> cliques){
		  long passStart, elapsedTime;
		  float elapsedTimeMin;
		  int passNumber = 1;
		  while(mergeHappened){
			 mergeHappened = false;
			 iterationCount = 0; innerIter = 0;
			 toRemoveClique = new HashMap<Integer, Boolean>();
			 for(int i = 0; i < cliques.size(); i++)
				toRemoveClique.put(i, false);
			 passStart = System.currentTimeMillis();
			 combineIntersectionCliques(cliques);
			 for(Set<Integer> clique: mergedCliques){
				cliques.remove(clique);
			 }
			 mergedCliques = new ArrayList<Set<Integer>>(); 
			 elapsedTime = System.currentTimeMillis() - passStart;
			 elapsedTimeMin = elapsedTime/(60*1000F);
			 System.out.println("pass "+passNumber+" cliques.size: "+cliques.size()+"   pass time: "+elapsedTimeMin);
			 passNumber++;
		  }
		  System.out.println("final cliques.size: "+cliques.size());
		  return cliques;
	   }
    static void doNextMerge(Set<Integer> c1, List<Set<Integer>> cliques){ //needs a better name ;)
       Set<Integer> c2 = cliques.get(innerIter);
       boolean firstOfCliquePairMerged = false;
       if(!toRemoveClique.get(innerIter) ){
    	  if (!c1.equals(c2)){
    		 if(c1.size() >= c2.size()) 
    			mergeIntersectionCliques(c1, c2, iterationCount, innerIter);
    		 else
    			firstOfCliquePairMerged = mergeIntersectionCliques(c2, c1, innerIter, iterationCount); 
    		 //possible problem...if c1 merged into c5, i shouldn't be able to try c6 into c1
    	  }
    	  if(firstOfCliquePairMerged) {
    		  innerIter = 0;
    		  return;
    	  } 
       }
       innerIter++;
    }
    
	private static void combineIntersectionCliques(Collection<Set<Integer>> cliques0){  
		  List<Set<Integer>> cliques = (List<Set<Integer>>) cliques0;
		  for (Set<Integer> c1: cliques){
			 if(!toRemoveClique.get(iterationCount) ){
				innerIter = 0;
				for (Set<Integer> c2: cliques){
				   doNextMerge(c1, cliques);
				}
			 }
			 iterationCount++;
		  }
	   }
	private static boolean mergeIntersectionCliques(Set<Integer> c1, Set<Integer> c2, int largerCliqueIndex, int smallerCliqueIndex){
		  boolean merged = false;
		  if(mergeCount == 152){
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
		  compareCount++;
		  percentSame = (float)intersectionCount/(float)c2.size();
		  percentDiff = (float) numDifferent/(float)c2.size();
		  //aka...percentDiff <= .35F   that makes the percentSame >= .9F obsolete.  duh
		  if(percentSame >= .9F || percentDiff <= .35F){
			 for(int uid: difference){
				c1.add(uid);
			 }
			 mergeHappened = true;
			 merged = true;
			 mergedCliques.add(c2); //the cliques that were merged into a larger clique...to be removed
			 toRemoveClique.put(smallerCliqueIndex, true);
			 mergeCount++;
		  }
		  
		  return merged;
	   }
}
