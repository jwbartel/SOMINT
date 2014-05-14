package kelli.FriendGrouper;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import kelli.mergeAttempts.IOFunctions;
import kelli.mergeAttempts.IntersectionClean;
import kelli.mergeAttempts.incremental.CliqueDriver;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class FindIntersectionGroups {
   private static Vector<Integer> vertexList = new Vector<Integer>();
   //private static UndirectedGraph<Integer, DefaultEdge> UIDGraph = null;
   private static String inFileName;
   private static Collection<Set<Integer>> cliques;
   private static HashMap<Integer, String> uidNames = new HashMap<Integer, String>();
   private static IOFunctions ioHelp = new IOFunctions();
   static String participant = "PD";
   static float threshold = .2F;
   public static void main(String[] args){
	  long start, elapsedTime;
	  float elapsedTimeMin;
	  start = System.currentTimeMillis();
	  doSetup();
	  findCliques(participant);
	  //compareCliques();
	  doIntersection();
	  System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~FINAL~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	  elapsedTime = System.currentTimeMillis() - start;
	  elapsedTimeMin = elapsedTime/(60*1000F);
	  System.out.println("   total elapsed time: "+elapsedTimeMin+" min ");
	  CliqueDriver.compareTraces();
	  System.out.println("All done!");// 	run cliqueFinder
	  
	}
   public static void init(){
		  long start, elapsedTime;
		  float elapsedTimeMin;
		  start = System.currentTimeMillis();
		  doSetup();
		  findCliques(participant);
		  //compareCliques();
		  doIntersection();
		  System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~FINAL~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		  elapsedTime = System.currentTimeMillis() - start;
		  elapsedTimeMin = elapsedTime/(60*1000F);
		  System.out.println("   total elapsed time: "+elapsedTimeMin+" min ");
		  System.out.println("All done!");// 	run cliqueFinder
	   }
   
   @SuppressWarnings("static-access")
private static void doSetup(){
	   System.out.println("~~~~~~~~~~~~~~~~~~~~~~~SET UP~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	   inFileName = "data/Kelli/FriendshipData/"+participant+"FriendOfFriends.txt";
	   //UIDGraph = IncrementalMerger.createUIDGraph(inFileName);
	   //UIDGraph = merger.getUIDGraph();
	   System.out.println("creating the UIDGraph");
	   ioHelp.createUIDGraph(inFileName);
	   ioHelp.fillNames("data/Kelli/FriendshipData/PeopleNames.txt");
	   fillNames("data/Kelli/FriendshipData/PeopleNames.txt");
   }
   public static void findCliques(String participant) {
	   System.out.println("~~~~~~~~~~~~~~~~~~~Find All Maximal Cliques~~~~~~~~~~~~~~~~~~~~~~~~~~");
	   long start, elapsedTime, getAllTime;
	   BronKerboschCliqueFinder<Integer, DefaultEdge> BKcliqueFind = new BronKerboschCliqueFinder<Integer, DefaultEdge>(ioHelp.UIDGraph);
	   //status message
	   System.out.println("running getAllMaximalCliques");
	   start = System.currentTimeMillis();
	   cliques = BKcliqueFind.getAllMaximalCliques();
	   //compareCliques();
	   getAllTime = System.currentTimeMillis() - start;
	   float elapsedTimeMin = getAllTime/(60*1000F);
	   //status message
	   System.out.println("found AllMaximalCliques ("+cliques.size()+") in "+elapsedTimeMin+" minutes");
	   elapsedTime = System.currentTimeMillis() - start;
	   elapsedTimeMin = elapsedTime/(60*1000F);
	   //status message: all done :)
	   System.out.println("this stage done in "+elapsedTimeMin+ " minutes !!!");
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
   private static void fillNames(String namesFileName){
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
   @SuppressWarnings("static-access")
private static void doIntersection(){
	  System.out.println("~~~~~~~~~~~~~~~~Intersection~~~~~~~~~~~~~~~~~~~~~~~");
	  preprocessCliques(cliques);
	  //compareCliques();
	  Collection<Set<Integer>> intersectionCliques = IntersectionClean.doIntersectionMerges(cliques);
	  String outputLargeGroupFile     = "data/Kelli/FriendGrouperResults/Random/"+participant+"_LargeGroups.txt";
	  ioHelp.printCliqueNamesToFile(outputLargeGroupFile, intersectionCliques);
   }

   /**
    * remove all cliques size 2
    */
   private static void preprocessCliques(Collection<Set<Integer>> cliques){
	  System.out.println("cliques.size = "+cliques.size());
	   Iterator<Set<Integer>> cliqueIter = cliques.iterator();
	  Set<Integer> currClique;
	  while (cliqueIter.hasNext()){ 
		 currClique = cliqueIter.next();
		 if(currClique.size() <= 2)
			cliqueIter.remove();
	  }
	  System.out.println("cliques.size = "+cliques.size());
   }   

}
