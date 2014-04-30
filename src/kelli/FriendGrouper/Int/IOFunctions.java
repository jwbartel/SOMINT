package kelli.FriendGrouper.Int;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
@SuppressWarnings({ "deprecation" })
public class IOFunctions {
   private static HashMap<Integer, String> uidNames = new HashMap<Integer, String>();
   private static HashMap<Integer, Boolean> uidInClique = new HashMap<Integer, Boolean>();
   public static UndirectedGraph<Integer,DefaultEdge> UIDGraph = null;
   //creates the UIDGraph from the original friendship pairs.  Stores here for printing purposes, returns for comparison purposes 
   public static UndirectedGraph<Integer, DefaultEdge> createUIDGraph(String inputFile)
   {
      UndirectedGraph<Integer, DefaultEdge> g = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
      int linesReadCount = 0;
      try {
    	 DataInputStream in = new DataInputStream(new FileInputStream(inputFile));
    	 String FriendPair = null;
    	 int friend1 = -1;
    	 int friend2 = -1;
    	 int parsingSpace = -1;
    	 // in.available() returns 0 if the file does not have more lines.
    	 while (in.available() != 0) {
    		FriendPair = in.readLine();
    		linesReadCount++;
    		parsingSpace = FriendPair.indexOf(' ');
    		friend1 = Integer.parseInt(FriendPair.substring(0, parsingSpace));
    		FriendPair = FriendPair.substring(parsingSpace+1);
    		parsingSpace = FriendPair.indexOf(' ');
    		if(parsingSpace != -1)
    		   friend2 = Integer.parseInt(FriendPair.substring(0,parsingSpace));
    		else friend2 = Integer.parseInt(FriendPair);
    		g.addVertex(friend1); uidInClique.put(friend1, false);
    		g.addVertex(friend2); uidInClique.put(friend2, false);
    		g.addEdge(friend1, friend2);
    	 }
    	 // dispose all the resources after using them.
    	 in.close();
      } catch (Exception e){
    	  System.out.println("!!! CreateUIDGraph, line:"+linesReadCount+": "+e.getMessage());
      }
      UIDGraph = g;
      return g;
   }
   public static Collection<Set<Integer>> loadCliques(String inCliquesFile){
		  int linesReadCount = 0;
		  Collection<Set<Integer>> returnCollection = new ArrayList<Set<Integer>>();
		  try {
			 DataInputStream in = new DataInputStream(new FileInputStream(inCliquesFile));
			 String inputLine = null;
			 List<Integer> currClique = null;
			 int uid = 0;
			 // in.available() returns 0 if the file does not have more lines.
			 while (in.available() != 0) {
				currClique = new ArrayList<Integer>();
				inputLine = in.readLine();
				linesReadCount++;
				while(!inputLine.contains("*")){
					if(!inputLine.contains("Clique:")){
						uid = Integer.parseInt(inputLine);
						currClique.add(uid);
						if(in.available() != 0) inputLine = in.readLine();
					} else if (in.available() != 0) inputLine = in.readLine();
				}
				Set<Integer> theClique = new HashSet<Integer>(currClique);
				returnCollection.add(theClique);
			 }
			 // dispose all the resources after using them.
			 in.close();
		  } catch (Exception e){
			  System.out.println("!!! CreateUIDGraph, line:"+linesReadCount+": "+e.getMessage());
		  }
		  return returnCollection;
	   }
   private static Collection<Set<String>> alphabetizeCliques(Collection<Set<Integer>> networkCliques){
	   Collection<Set<String>> cliques = new ArrayList<Set<String>>();
	   Set<String> cliqueNames;
	   Iterator<Set<Integer>> cliqueIter = networkCliques.iterator();
	   Set<Integer> currClique;
	   Iterator<Integer> uidIter;
	   int currUID;
	   while(cliqueIter.hasNext()){
		   currClique = cliqueIter.next();
		   uidIter = currClique.iterator();
		   cliqueNames = new TreeSet<String>();
		   while(uidIter.hasNext()){
			   currUID = uidIter.next();
			   if(uidNames.containsKey(currUID)){
				   cliqueNames.add(uidNames.get(currUID));
			   }
		   }
		   cliques.add(cliqueNames);
	   }
	   return cliques;
   }
   public static void printCliqueNamesToFile(String outputFile, Collection<Set<Integer>> networkCliques){
	  int cliqueCount = 1;
	  try {
		 PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
		 Collection<Set<String>> cliques = alphabetizeCliques(networkCliques);
		 Iterator<Set<String>> collIter = cliques.iterator();
		 Iterator<String> uidIter;
		 Set<String> currClique;
		 String currUID;
		 while (collIter.hasNext()){
			pw.println("Clique: "+cliqueCount);
			currClique = collIter.next();
			pw.println("clique size: "+currClique.size());
			uidIter = currClique.iterator();
			while (uidIter.hasNext()){
			   currUID = uidIter.next();
			   pw.println(currUID);
			}
			pw.println();
			cliqueCount++;
		 }
		 System.out.println("Results can be found in: "+outputFile);
		 pw.close();
	  } catch (Exception e){
		  System.out.println("!!! Problem in PrintCliquesToFile: "+e.getMessage());
		  System.exit(0);
	  }
   }
   public static void printCliquesToFile(String outputFile, Collection<Set<Integer>> cliques){
	  int cliqueCount = 1;
	  try {
    	 PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
    	 Iterator<Set<Integer>> collIter = cliques.iterator();
    	 Iterator<Integer> uidIter;
    	 Set<Integer> currClique;
    	 Set<DefaultEdge> edgeSet;
    	 HashSet<Integer> friendSet = null;
    	 int currUID;
    	 int connectionLevel = 0;
    	 int averageConnectionLevel = 0;
    	 HashMap<Integer, Boolean> uidNOTinClique = new HashMap<Integer, Boolean>(uidInClique);
    	 while (collIter.hasNext()){
    		pw.println("Clique: "+cliqueCount);
     		currClique = collIter.next();
     		pw.println("clique size: "+currClique.size());
    		uidIter = currClique.iterator();
    		while (uidIter.hasNext()){
    		   currUID = uidIter.next();
    		   if(uidNames.containsKey(currUID)){
    			  edgeSet = UIDGraph.edgesOf(currUID);
    			  friendSet = new HashSet<Integer>();
    			  for(DefaultEdge edge: edgeSet){
    				 int source = UIDGraph.getEdgeSource(edge);
    				 if(friendSet.contains(source) || currUID == source){
    					 source = UIDGraph.getEdgeTarget(edge);
    				 } 
    				 friendSet.add(source);
    			  }
    			  for (Integer friend: friendSet){
    				  if (currClique.contains(friend)){
    					  connectionLevel++;
    				  }
    			  }
    			  averageConnectionLevel = averageConnectionLevel+connectionLevel;
    			  pw.println(uidNames.get(currUID)+ "  ~ "+connectionLevel+ " in this clique out of "
    					  +friendSet.size()+ " total mutual friends");
    			  uidNOTinClique.put(currUID, true);
    		   } 
    		   connectionLevel = 0;
    		}
    		pw.println("average connection is "+ averageConnectionLevel/currClique.size());
    		pw.println();
    		averageConnectionLevel = 0;
    		cliqueCount++;
    	 }
    	 pw.println("Friends Not Grouped:");
    	 int coverageCount = 0;
    	 for(Integer uid: uidNOTinClique.keySet()){
    		 if(uidNOTinClique.get(uid)){
    			coverageCount++;
    		 } else {
    		    if(uidNames.containsKey(uid)) pw.println(uidNames.get(uid));
    		 }
    	 }
    	 System.out.println("Coverage: "+coverageCount+" out of "+uidNOTinClique.size()+ " friends.");
    	 System.out.println("Results can be found in: "+outputFile);
    	 pw.close();
      } catch (Exception e){
    	  System.out.println("!!! Problem in PrintCliquesToFile: "+e.getMessage());
    	  System.exit(0);
      }
   }
   public static void fillNames(String inputNames){
		  int linesReadCount = 0;
		  try {
			 DataInputStream in = new DataInputStream(new FileInputStream(inputNames));
			 String friendName = null;
			 int friendUID = -1;
			 int parsingComma = -1;
			 // in.available() returns 0 if the file does not have more lines.
			 while (in.available() != 0) {
				friendName = in.readLine();
				linesReadCount++;
				parsingComma = friendName.indexOf(',');
				friendUID = Integer.parseInt(friendName.substring(0, parsingComma));
				friendName = friendName.substring(parsingComma+2);
				uidNames.put(friendUID, friendName);
			 }
			 in.close();
		  } catch (Exception e){
			 System.out.println("!!! fillNames, line:"+linesReadCount+": "+e.getMessage());
		  }
	   }
}
