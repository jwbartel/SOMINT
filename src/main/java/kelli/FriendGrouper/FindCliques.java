package kelli.FriendGrouper;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class FindCliques {
   private static Vector<Integer> vertexList = new Vector<Integer>();
   private static UndirectedGraph<Integer, DefaultEdge> UIDGraph = null;
   private static String inFileName;
   private static String printCliques;
   private static Collection<Set<Integer>> cliques;
   
   public static void findCliques(String participant) {
	   long start, elapsedTime, getAllTime;
	   printCliques = "data/Kelli/Cliques/UID/"+participant+"_BKCliques.txt";
	   inFileName = "data/Kelli/FriendshipData/"+participant+"FriendOfFriends.txt";
	   //fillNames();
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
	     Iterator<Set<Integer>> collectionIter = cliques.iterator();
	     Iterator<Integer> uidIter;
	     Set<Integer> currClique;
	     int currUID;
	     while (collectionIter.hasNext()){
	    	pw.println("Clique: "+cliqueCount);
	    	currClique = collectionIter.next();
	    	uidIter = currClique.iterator();
	    	while (uidIter.hasNext()){
	    	   currUID = uidIter.next();
	    	   pw.println(currUID);
	    	}
			pw.println("*");
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
}

