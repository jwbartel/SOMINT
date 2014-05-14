package kelli.FriendGrouper;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import kelli.mergeAttempts.IOFunctions;
import kelli.mergeAttempts.IntersectionV2;
import kelli.mergeAttempts.SetDiffV3;
import kelli.mergeAttempts.incremental.AnIntersectionMerger;
import kelli.mergeAttempts.incremental.CliqueDriver;
import kelli.mergeAttempts.incremental.IncrementalMerger;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class ListFindSocialGroup {
   private static Vector<Integer> vertexList = new Vector<Integer>();
   private static UndirectedGraph<Integer, DefaultEdge> UIDGraph = null;
   private static String inFileName;
   private static List<Set<Integer>> cliques;
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
   public static IncrementalMerger merger = new IncrementalMerger(participant, 
		   "data/Kelli/FriendGrouperResults/Random/"+participant+"_LargeGroupsIncremental.txt", 
		   threshold, new AnIntersectionMerger());
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
   public static void compareCliques(){
	   Collection<Set<Integer>> mcliques = merger.getCliques();
	   Iterator<Set<Integer>> mIter = mcliques.iterator();
	   Iterator<Set<Integer>> cIter = cliques.iterator();
	   Set<Integer> mSet;
	   Set<Integer> cSet;
	   int count = 0;
	   System.out.println("cliques.size "+cliques.size()+"     mCliques.size: "+mcliques.size());
	   while(mIter.hasNext()&&cIter.hasNext()){
		   mSet = mIter.next();
		   cSet = cIter.next(); 
		   if(!mSet.equals(cSet)){
			   System.out.println("mSet: "+mSet+"   cSet: "+cSet);
		   }
//		   if(count == 187){
//			   System.out.println("mSet: "+mSet+"  \n cSet: "+cSet);
//		   }
		   count++;
	   }
	   System.out.println("cliques.size "+cliques.size()+"     mCliques.size: "+mcliques.size());
   }
   
   @SuppressWarnings("static-access")
private static void doSetup(){
	   System.out.println("~~~~~~~~~~~~~~~~~~~~~~~SET UP~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	   inFileName = "data/Kelli/FriendshipData/"+participant+"FriendOfFriends.txt";
	   //UIDGraph = IncrementalMerger.createUIDGraph(inFileName);
	   UIDGraph = merger.getUIDGraph();
	   System.out.println("created the UIDGraph");
	   ioHelp.createUIDGraph(inFileName);
	   ioHelp.fillNames("data/Kelli/FriendshipData/PeopleNames.txt");
	   fillNames("data/Kelli/FriendshipData/PeopleNames.txt");
   }
   public static void findCliques(String participant) {
	   System.out.println("~~~~~~~~~~~~~~~~~~~Find All Maximal Cliques~~~~~~~~~~~~~~~~~~~~~~~~~~");
	   long start, elapsedTime, getAllTime;
	   BronKerboschCliqueFinder<Integer, DefaultEdge> BKcliqueFind = new BronKerboschCliqueFinder<Integer, DefaultEdge>(UIDGraph);
	   //status message
	   System.out.println("running getAllMaximalCliques");
	   start = System.currentTimeMillis();
	   cliques = (List<Set<Integer>>)BKcliqueFind.getAllMaximalCliques();
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
	  List<Set<Integer>> intersectionCliques = IntersectionV2.doIntersectionMerges(cliques);
	  String outputLargeGroupFile     = "data/Kelli/FriendGrouperResults/Random/"+participant+"_LargeGroups.txt";
	  ioHelp.printCliqueNamesToFile(outputLargeGroupFile, intersectionCliques);
	 //TODO uncomment this next line
	  // findCliquesInLargeGroups(intersectionCliques);
   }
   private static Set<Integer> findLargeGroups(List<Set<Integer>> intersectionCliques){
	   
	   
	   Iterator<Set<Integer>> cliqueIter = intersectionCliques.iterator();
	   Set<Integer> currClique;
	   Set<Integer> largeGroups = new HashSet<Integer>();
	   while(cliqueIter.hasNext()){
		   currClique = cliqueIter.next();
		   if(currClique.size()>=50){
			   Iterator<Integer> uidIter = currClique.iterator();
			   int uid;
			   while(uidIter.hasNext()){
				   uid = uidIter.next();
				   largeGroups.add(uid);
			   }
		   }
	   }
	   return largeGroups;
   }
   private static UndirectedGraph<Integer, DefaultEdge> findSubgraph(List<Set<Integer>> intersectionCliques){
	   UndirectedGraph<Integer, DefaultEdge> subGraph = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
	   Set<Integer> vSet = UIDGraph.vertexSet();
	   Set<DefaultEdge> eSet;
	   int source, target;
	   Set<Integer> largeGroups = findLargeGroups(intersectionCliques);
	   for(int v: vSet){
		  if(largeGroups.contains(v)){
			  subGraph.addVertex(v);
		  }
	   }
	   for(int v: vSet){
		  if(largeGroups.contains(v)){
			 eSet = UIDGraph.edgesOf(v);
			 for(DefaultEdge e: eSet){
				source = UIDGraph.getEdgeSource(e);
				target = UIDGraph.getEdgeTarget(e);
				if(largeGroups.contains(source)){
				   if(largeGroups.contains(target))
					  subGraph.addEdge(source, target);
				}
			 }
		  }
	   }
	   return subGraph;
   }
   @SuppressWarnings("static-access")
private static void findCliquesInLargeGroups(List<Set<Integer>> intersectionCliques){
	   System.out.println("~~~~~~~~~~~~~~~~~~Find Subcliques~~~~~~~~~~~~~~~~~~~~~~~~~");
	   UndirectedGraph<Integer, DefaultEdge> subGraph = findSubgraph(intersectionCliques);
	   BronKerboschCliqueFinder<Integer, DefaultEdge> BKcliqueFind2 = new BronKerboschCliqueFinder<Integer, DefaultEdge>(subGraph);
	   List<Set<Integer>> SetDiffCliques;
	   SetDiffCliques = (List<Set<Integer>>)BKcliqueFind2.getAllMaximalCliques();
	   System.out.println("all max cliques: "+SetDiffCliques.size());
	   SetDiffCliques = SetDiffV3.doSetDiffMerges(SetDiffCliques, threshold);
	   System.out.println("SubCliques size: "+SetDiffCliques.size());
	   String outputFile = "data/Kelli/FriendGrouperResults/Random/"+participant+"_Subcliques.txt";
	   ioHelp.printCliqueNamesToFile(outputFile, SetDiffCliques);
   }
   /**
    * remove all cliques size 2
    */
   private static void preprocessCliques(List<Set<Integer>> cliques){
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
