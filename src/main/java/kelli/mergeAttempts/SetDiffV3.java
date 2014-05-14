package kelli.mergeAttempts;

//um...are we sure that there needs to be two functions for combining?  Why can't it be structured like Intersection?

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SetDiffV3 {
   private static float threshold = 0;
   static int mergeCount = 0;
private static HashMap<Integer, Boolean> toRemoveClique = null;
   private static Boolean mergeHappened = true;
   private static List<Set<Integer>> mergedCliques = new ArrayList<Set<Integer>>();
	public static List<Set<Integer>> doSetDiffMerges(List<Set<Integer>> SubCliques, float _threshold){
		System.out.println("orig cliques.size = "+SubCliques.size()); 
		threshold = _threshold;
		long passStart, elapsedTime;
		  float elapsedTimeMin;
		  int passNumber = 1;
		  toRemoveClique = new HashMap<Integer, Boolean>();
		  for(int i = 0; i < SubCliques.size(); i++)
				toRemoveClique.put(i, false);
		  passStart = System.currentTimeMillis();
		  combineSetDiffCliques(SubCliques);
		  elapsedTime = System.currentTimeMillis() - passStart;
		  elapsedTimeMin = elapsedTime/(60*1000F);
		  System.out.println("pass "+passNumber+" SubCliques.size: "+mergedCliques.size()+"   pass time: "+elapsedTimeMin);
		  while(mergeHappened){
			 mergeHappened = false;
			 toRemoveClique = new HashMap<Integer, Boolean>();
			 for(int i = 0; i < SubCliques.size(); i++)
				toRemoveClique.put(i, false);
			 passStart = System.currentTimeMillis();
			 SubCliques = new ArrayList<Set<Integer>>(mergedCliques); 
			 //mergedCliques = new ArrayList<Set<Integer>>();
			 combineMergedCliques(SubCliques);
			 elapsedTime = System.currentTimeMillis() - passStart;
			 elapsedTimeMin = elapsedTime/(60*1000F);
			 passNumber++;
			 System.out.println("pass "+passNumber+" SubCliques.size: "+SubCliques.size()+"   pass time: "+elapsedTimeMin);
		  }
		  System.out.println("final cliques.size: "+SubCliques.size());
		  return SubCliques;
	   }
	   private static void combineSetDiffCliques(List<Set<Integer>> SubCliques){
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
		   private static void combineMergedCliques(List<Set<Integer>> SubCliques){
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
				  System.out.println(mergeCount+":"+c1+", "+c2);
			  }
			  return merged;
		   }
	
}
