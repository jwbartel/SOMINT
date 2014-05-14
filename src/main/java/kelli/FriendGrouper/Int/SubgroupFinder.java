package kelli.FriendGrouper.Int;

//um...are we sure that there needs to be two functions for combining?  Why can't it be structured like Intersection?

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SubgroupFinder {
   private static float threshold = 0;
   static int mergeCount = 0;
   private static HashMap<Integer, Boolean> toRemoveClique = null;
   private static Boolean mergeHappened = true;
   private static Collection<Set<Integer>> mergedCliques = new ArrayList<Set<Integer>>();
   public static Collection<Set<Integer>> doSubgroupFind(Collection<Set<Integer>> subgroupCliques, float _threshold){
	 // System.out.println("orig cliques.size = "+subgroupCliques.size()); 
	  threshold = _threshold;
	  Collection<Set<Integer>> m = mergedCliques;
	  long passStart, elapsedTime, start = System.currentTimeMillis();
	  float elapsedTimeMin;
	  int passNumber = 1;
	  toRemoveClique = new HashMap<Integer, Boolean>();
	  for(int i = 0; i < subgroupCliques.size(); i++)
		 toRemoveClique.put(i, false);
	  passStart = System.currentTimeMillis();
	  combineSetDiffCliques(subgroupCliques);
	  elapsedTime = System.currentTimeMillis() - passStart;
	  elapsedTimeMin = elapsedTime/(60*1000F);
	  System.out.println("pass "+passNumber+" SubCliques.size: "+mergedCliques.size()+"   pass time: "+elapsedTimeMin);
	  while(mergeHappened){
		  mergeHappened = false;
		  toRemoveClique = new HashMap<Integer, Boolean>();
		  for(int i = 0; i < subgroupCliques.size(); i++)
			  toRemoveClique.put(i, false);
		  passStart = System.currentTimeMillis();
		  subgroupCliques = new ArrayList<Set<Integer>>(mergedCliques); 
		  //mergedCliques = new ArrayList<Set<Integer>>();
		  combineMergedCliques(subgroupCliques);
		  elapsedTime = System.currentTimeMillis() - passStart;
		  elapsedTimeMin = elapsedTime/(60*1000F);
		  passNumber++;
		  System.out.println("pass "+passNumber+" SubCliques.size: "+subgroupCliques.size()+"   pass time: "+elapsedTimeMin);
	  }
	  System.out.println("final cliques.size: "+subgroupCliques.size());
	  elapsedTime = System.currentTimeMillis() - start;
      elapsedTimeMin = elapsedTime/(60*1000F);
      System.out.println("this stage done in "+elapsedTimeMin+ " minutes !!!");
	  return subgroupCliques;
   }
   private static void combineSetDiffCliques(Collection<Set<Integer>> SubCliques){
	  int iterationCount = 0;
	  int innerIter;
	  for (Set<Integer> c1: SubCliques){
		 if(!toRemoveClique.get(iterationCount)){
			innerIter = 0;
			for (Set<Integer> c2: SubCliques){
			   if(!toRemoveClique.get(innerIter) ){
				  if (!c1.equals(c2)){ 
					 if(c1.size() >= c2.size()) 
						mergeSetDiffCliques(c1, c2, innerIter);
					 else
						mergeSetDiffCliques(c2, c1, iterationCount); 
				  }
			   }
			   innerIter++;
			}
		 }
		 iterationCount++;
	  }
   }
   //used in multiple passes
   private static void combineMergedCliques(Collection<Set<Integer>> SubCliques){
	  int iterationCount = 0;
	  int innerIter;
	  for (Set<Integer> c1: SubCliques){
		 innerIter = 0;
		 for (Set<Integer> c2: SubCliques){
			if(!c1.equals(c2)){
			   if(c1.size() >= c2.size()) 
				  mergeSetDiffCliques(c1, c2, innerIter);
			   else
				  mergeSetDiffCliques(c2, c1, iterationCount);  
			}										    
			innerIter++;
		 }
		 iterationCount++;
	  }
   }

   private static boolean mergeSetDiffCliques(Set<Integer> c1, Set<Integer> c2, int smallerCliqueNumber){
	  boolean merged = false;
	  float percentage;
	  int numDifferent = 0;
	  Set<Integer> difference = new HashSet<Integer>();
	  for (int uid: c2){
		 if(!c1.contains(uid)){
			numDifferent++;
			difference.add(uid);
		 }
	  }
	  percentage = (float)numDifferent/(float)c2.size();
	  if(percentage < threshold){
		  // Remove both cliques, if they are already in mergedCliques.  Only keep the resulting mergedClique
		  mergedCliques.remove(c1);
		  mergedCliques.remove(c2);
		  for(int uid: difference){
			  c1.add(uid);
		  }
		  mergedCliques.add(c1);
		  mergeHappened = true;    //un-comment this line to try running more than two passes on the algorithm
		  merged = true;
		  toRemoveClique.put(smallerCliqueNumber,true);
		  mergeCount++;
	  }
	  return merged;
   }
	
}
