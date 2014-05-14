package kelli.mergeAttempts;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;


@SuppressWarnings("unused")
public class cliqueFinder {
   private static Vector<Integer> vertexList = new Vector<Integer>();
   private static UndirectedGraph<Integer, DefaultEdge> UIDGraph = null;
   private static String inFileName =  "data/Kelli/FriendshipData/FriendOfFriends.txt";
   private static String printCliques =     "data/Kelli/Cliques/UID/BKCliques.txt";
   private static String printCliqueNames = "data/Kelli/Cliques/BKCliquesNames.txt";
   private static String namesFileName = "data/Kelli/FriendshipData/PeopleNames.txt";
   private static Collection<Set<Integer>> cliques;
   private static HashMap<Integer, String> uidNames = new HashMap<Integer, String>();
   
   public static void main(String[] args) {
	   long start, elapsedTime, getAllTime;
	   fillNames();
	   UIDGraph = createUIDGraph(inFileName);
	   //status message
	   System.out.println("created the UIDGraph");
	   BronKerboschCliqueFinder<Integer, DefaultEdge> BKcliqueFind = new BronKerboschCliqueFinder<Integer, DefaultEdge>(UIDGraph);
	   //status message
	   System.out.println("running getAllMaximalCliques");
	   start = System.currentTimeMillis();
	   //
	   cliques = BKcliqueFind.getAllMaximalCliques();
	   getAllTime = System.currentTimeMillis() - start;
	   float elapsedTimeMin = getAllTime/(60*1000F);
	   //status message
	   System.out.println("found AllMaximalCliques ("+cliques.size()+") in "+elapsedTimeMin+" minutes");
	   printCliquesToFile();
	   elapsedTime = System.currentTimeMillis() - start;
	   elapsedTimeMin = elapsedTime/(60*1000F);
	   //status message: all done :)
	   System.out.println("printedCliques to file...all done in "+elapsedTimeMin+ "!!!");
	}
   //prints the maximal cliques to output files printCliques and printCliqueNames
   //printCliques holds the clique information to be used in the next step, MergeCliques.java
   //printCliqueNames hold the human-readable cliques for human convenience.  This information is not used in
   //  other parts of the project.
   private static void printCliquesToFile(){
      int cliqueCount = 1;
	  try {
	     PrintWriter pw = new PrintWriter(new FileWriter(printCliques));
	     PrintWriter pw2 = new PrintWriter(new FileWriter(printCliqueNames));
	     Iterator<Set<Integer>> collectionIter = cliques.iterator();
	     Iterator<Integer> uidIter;
	     Set<Integer> currClique;
	     int currUID;
	     while (collectionIter.hasNext()){
	    	pw.println("Clique: "+cliqueCount);
	    	pw2.println("Clique: "+cliqueCount);
	    	currClique = collectionIter.next();
	    	uidIter = currClique.iterator();
	    	while (uidIter.hasNext()){
	    	   currUID = uidIter.next();
	    	   if(uidNames.containsKey(currUID)){
	    		   pw2.println(uidNames.get(currUID));
	    		   pw.println(currUID);
	    	   }
	    	   else {
	    		   pw.println(currUID);
	    		   pw2.println(currUID);
	    	   }
	    	}
			pw.println("*");
			pw2.println();
			cliqueCount++;
		  }
		  pw.close();
	   } catch (Exception e){
		   System.out.println("!!! Problem in PrintCliquesToFile: "+e.getMessage());
		   System.exit(0);
	   }
   }
   //the graph is made of vertices which are uniquely identified via unique identification numbers
   //this method creates an undirected graph from the friendship pairs in file inFileName
   @SuppressWarnings("deprecation")
private static UndirectedGraph<Integer, DefaultEdge> createUIDGraph(String inputFile)
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
			if(!vertexList.contains(friend1)){
				vertexList.add(friend1);
				g.addVertex(friend1);
			}
			if(!vertexList.contains(friend2)){
				vertexList.add(friend2);
				g.addVertex(friend2);
			}
			g.addEdge(friend1, friend2);
		 }
		 System.out.println("UID graph has "+vertexList.size()+" vertices and "+linesReadCount+" edges");
		 // dispose all the resources after using them.
		 in.close();
	  } catch (Exception e){
		  System.out.println("!!! CreateUIDGraph, line:"+linesReadCount+": "+e.getMessage());
	  }
	  
	  return g;
   }	
   //the graph is made of vertices which are uniquely identified via unique identification numbers
   //this function enters the names that correspond to the identifications numbers into a table
   //  to be used for printing human-legible names
   @SuppressWarnings("deprecation")
private static void fillNames(){
      int linesReadCount = 0;
      try {
    	 DataInputStream in = new DataInputStream(new FileInputStream(namesFileName));
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
    	  System.out.println("!!!Error in "+namesFileName+", line:"+linesReadCount+": "+e.getMessage());
      }
   }
}
