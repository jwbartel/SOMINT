package kelli.mergeAttempts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class IntersectionV1 {
   private static Collection<Set<Integer>> mergedCliques = new ArrayList<Set<Integer>>();
   private static HashMap<Integer, Boolean> toRemoveClique = null;
   private static boolean mergeHappened = true;
   static int mergeCount =0;
   static int compareCount = 0;
   public static Collection<Set<Integer>> doIntersectionMerges(Collection<Set<Integer>> cliques){
		  long passStart, elapsedTime;
		  float elapsedTimeMin;
		  int passNumber = 1;
		  while(mergeHappened){
			 mergeHappened = false;
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
	private static void combineIntersectionCliques(Collection<Set<Integer>> cliques0){
		  int iterationCount = 0; int innerIter;
		  List<Set<Integer>> cliques = (List<Set<Integer>>) cliques0;
		  boolean firstOfCliquePairMerged = false;
		  for (Set<Integer> c1: cliques){
			 if(!toRemoveClique.get(iterationCount) ){
				innerIter = 0;
				for (Set<Integer> c2: cliques){
//				   if(innerIter == 187){
//					   System.out.println("c2: "+c2);
//					   System.out.println("cliques: "+cliques.get(187));
//				   }
				   if(!toRemoveClique.get(innerIter) ){
					  if (!c1.equals(c2)){
						 if(c1.size() >= c2.size()) 
							mergeIntersectionCliques(c1, c2, iterationCount, innerIter);
						 else
							 firstOfCliquePairMerged = mergeIntersectionCliques(c2, c1, innerIter, iterationCount); 
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
	private static boolean mergeIntersectionCliques(Set<Integer> c1, Set<Integer> c2, int largerCliqueIndex, int smallerCliqueIndex){
		  boolean merged = false;
		  if(compareCount >= 304){
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
		  if(percentSame >= .9F || percentDiff <= .35F){
			 for(int uid: difference){
				c1.add(uid);
			 }
			 mergeHappened = true;
			 merged = true;
			 mergedCliques.add(c2); //the cliques that were merged into a larger clique...to be removed
			 toRemoveClique.put(smallerCliqueIndex, true);
			 mergeCount++;
			 //ATrace(int _smallerCliqueSize, int _largerCliqueSize, Set<Integer> _smallerClique,
			 //   	Set<Integer> _largerClique,	int _smallerCliqueIndex, int _largerCliqueIndex, 
			 //     int _mergeNumber, int _comparisonNumber)
//			 RandomOrderMerge.mergeTrace.add(new ATrace(c2.size(), c1.size(), c2, c1, 
//					 smallerCliqueIndex, largerCliqueIndex, mergeCount, compareCount));
			 //System.out.println("merger: "+FindSocialGroups.merger.getCliques().get(187));
//			 FindSocialGroups.merger.doNextComparison();
//			 FindSocialGroups.compareCliques();
			 //System.out.println(mergeCount+"  "+c1+", "+c2);
			 //System.out.println(c1.size() + " and  "+c2.size());
		  }	  else {
			  //System.out.println("noMergeHere: largerCliqueNum = "+largerCliqueIndex);
		  }
		  //FindSocialGroups.merger.doNextComparison();
		  //FindSocialGroups.compareCliques();
		  
		  return merged;
	   }
}
