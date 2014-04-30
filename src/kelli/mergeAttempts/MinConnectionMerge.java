package kelli.mergeAttempts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class MinConnectionMerge {
   private static HashMap<Integer, Boolean> toRemoveClique = null;
   private static Boolean mergeHappened = true;
   private static Collection<Set<Integer>> mergedCliques = new ArrayList<Set<Integer>>();
   private static HashMap<Integer, Set<Integer>> friendLists = new HashMap<Integer, Set<Integer>>();
   private static UndirectedGraph<Integer, DefaultEdge> origGraph = null;
   static double OMEGA;
   public static void doConnectionCliques(Collection<Set<Integer>> ConnectionCliques, 
		   UndirectedGraph<Integer, DefaultEdge> graph, double omega){
	  origGraph = graph;
	  OMEGA = omega;
	  long passStart, elapsedTime;
	  float elapsedTimeMin;
	  int passNumber = 1;
	  while(mergeHappened){
		 mergeHappened = false;
		 toRemoveClique = new HashMap<Integer, Boolean>();
		 for(int i = 0; i < ConnectionCliques.size(); i++)
			toRemoveClique.put(i, false);
		 passStart = System.currentTimeMillis();
		 combineConnectionCliques(ConnectionCliques);
		 for(Set<Integer> clique: mergedCliques){
			ConnectionCliques.remove(clique);
		 }
		 mergedCliques = new ArrayList<Set<Integer>>(); 
		 elapsedTime = System.currentTimeMillis() - passStart;
		 elapsedTimeMin = elapsedTime/(60*1000F);
		 System.out.println("pass "+passNumber+" cliques.size: "+ConnectionCliques.size()+"   pass time: "+elapsedTimeMin);
		 passNumber++;
	  }
	  System.out.println("final cliques.size: "+ConnectionCliques.size());
   }
   private static void combineConnectionCliques(Collection<Set<Integer>> ConnectionCliques){
	  int iterationCount = 0; int innerIter;
	  boolean firstOfCliquePairMerged = false;
	  for (Set<Integer> c1: ConnectionCliques){
		 if(!toRemoveClique.get(iterationCount) ){
			innerIter = 0;
			for (Set<Integer> c2: ConnectionCliques){
			   if(!toRemoveClique.get(innerIter) ){
				  if (!c1.equals(c2)){
					 if(c1.size() >= c2.size()) 
						mergeHappened = mergeConnectionCliques(c1, c2, innerIter);
					 else
						firstOfCliquePairMerged = mergeConnectionCliques(c2, c1, iterationCount); 
					 //possible problem...if c1 merged into c5, i shouldn't be able to try c6 into c1
				  }
				  if(firstOfCliquePairMerged) {
					 firstOfCliquePairMerged = false;
					 mergeHappened = true;
					 break; //break forloop from (clique x, clique y) to (clique x+1, clique init)
				  }
			   }
			   innerIter++;
			}
		 }
		 iterationCount++;
	  }
   }
   private static boolean mergeConnectionCliques(Set<Integer> c1, Set<Integer> c2, int smallerCliqueNumber){
		  Set<Integer> mergedClique = new HashSet<Integer>(c1);
		  Set<Integer> difference = new HashSet<Integer>();
		  for (int uid: c2){
			 if(!mergedClique.contains(uid)){
				difference.add(uid);
			 }
		  }
		  int minConnection = findMinConnectionLevel(c1,difference);
		  if(minConnection >= (OMEGA*(c1.size()+difference.size()))){
			 for(int uid: difference){
				c1.add(uid);
			 }
			 mergedCliques.add(c2); 
			 toRemoveClique.put(smallerCliqueNumber, true); 
			 return true;
		  }
		  else return false;

   }
   private static int findMinConnectionLevel(Set<Integer> clique, Set<Integer> diff){
	  Iterator<Integer> uidIter = clique.iterator();
	  int currUID;
	  Set<Integer> friendSet = null;
	  int connectionLevel = 0;
	  int minConnectionLevel = clique.size();
	  while(uidIter.hasNext()){
		 currUID = uidIter.next();
		 if(!friendLists.containsKey(currUID)){
			friendSet = findFriendLists(currUID);
		 } else friendSet = friendLists.get(currUID);
		 for (int friend: friendSet){
			if (clique.contains(friend)){
			   connectionLevel++;
			} else if (diff.contains(friend)) connectionLevel++;
		 }
		 if(connectionLevel < minConnectionLevel) minConnectionLevel = connectionLevel;
		 connectionLevel = 0;
	  }
	  uidIter = diff.iterator();
	  while(uidIter.hasNext()){
		 currUID = uidIter.next();
		 if(!friendLists.containsKey(currUID)){
			friendSet = findFriendLists(currUID);
		 } else friendSet = friendLists.get(currUID);
		 for (int friend: friendSet){
			if (clique.contains(friend)){
			   connectionLevel++;
			} else if (diff.contains(friend)) connectionLevel++;
		 }
		 if(connectionLevel < minConnectionLevel) minConnectionLevel = connectionLevel;
		 connectionLevel = 0;
	  }
	  return minConnectionLevel;
   }
   private static Set<Integer> findFriendLists(int id){
	  Set<Integer> friendList = new HashSet<Integer>();
	  Set<DefaultEdge> edgeSet = origGraph.edgesOf(id);
	  for(DefaultEdge edge: edgeSet){
		 int source = origGraph.getEdgeSource(edge);
		 if(friendList.contains(source)|| id == source) source = origGraph.getEdgeTarget(edge);
		 friendList.add(source);
	  }
	  return friendList;
   }


}
