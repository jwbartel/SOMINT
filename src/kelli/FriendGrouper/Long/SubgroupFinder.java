package kelli.FriendGrouper.Long;

//um...are we sure that there needs to be two functions for combining?  Why can't it be structured like Intersection?

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class SubgroupFinder {
   private static float threshold = 0;
   static int mergeCount = 0;
   private static HashMap<Long, Boolean> toRemoveClique = null;
   private static Boolean mergeHappened = true;
   private static Collection<Set<Long>> mergedCliques = new ArrayList<Set<Long>>();
   public static Collection<Set<Long>> doSubgroupFind(Collection<Set<Long>> subgroupCliques, float _threshold){
	  System.out.println("orig cliques.size = "+subgroupCliques.size()); 
	  threshold = _threshold;
	  long passStart, elapsedTime;
	  float elapsedTimeMin;
	  int passNumber = 1;
	  toRemoveClique = new HashMap<Long, Boolean>();
	  for(long i = 0; i < subgroupCliques.size(); i++)
		 toRemoveClique.put(i, false);
	  passStart = System.currentTimeMillis();
	  combineSetDiffCliques(subgroupCliques);
	  elapsedTime = System.currentTimeMillis() - passStart;
	  elapsedTimeMin = elapsedTime/(60*1000F);
	  System.out.println("pass "+passNumber+" SubCliques.size: "+mergedCliques.size()+"   pass time: "+elapsedTimeMin);
	  while(mergeHappened){
		  mergeHappened = false;
		  toRemoveClique = new HashMap<Long, Boolean>();
		  for(long i = 0; i < subgroupCliques.size(); i++)
			  toRemoveClique.put(i, false);
		  passStart = System.currentTimeMillis();
		  subgroupCliques = new ArrayList<Set<Long>>(mergedCliques); 
		  //mergedCliques = new ArrayList<Set<Long>>();
		  combineMergedCliques(subgroupCliques);
		  elapsedTime = System.currentTimeMillis() - passStart;
		  elapsedTimeMin = elapsedTime/(60*1000F);
		  passNumber++;
		  System.out.println("pass "+passNumber+" SubCliques.size: "+subgroupCliques.size()+"   pass time: "+elapsedTimeMin);
	  }
	  System.out.println("final cliques.size: "+subgroupCliques.size());
	  return subgroupCliques;
   }
   private static void combineSetDiffCliques(Collection<Set<Long>> SubCliques){
	  long iterationCount = 0;
	  long innerIter;
	  for (Set<Long> c1: SubCliques){
		 if(!toRemoveClique.get(iterationCount)){
			innerIter = 0;
			for (Set<Long> c2: SubCliques){
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
   private static void combineMergedCliques(Collection<Set<Long>> SubCliques){
	  long iterationCount = 0;
	  long innerIter;
	  for (Set<Long> c1: SubCliques){
		 innerIter = 0;
		 for (Set<Long> c2: SubCliques){
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

   private static boolean mergeSetDiffCliques(Set<Long> c1, Set<Long> c2, Long smallerCliqueNumber){
	  boolean merged = false;
	  float percentage;
	  int numDifferent = 0;
	  Set<Long> difference = new HashSet<Long>();
	  for (Long uid: c2){
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
		  for(Long uid: difference){
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
