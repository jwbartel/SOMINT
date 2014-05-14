package kelli.FriendGrouper.Double;

// good results from earlier run on KB found in FriendGrouperResults/Random/KB(etc)
// run with only one pass on Brian's subgroup data.  result: they should all be in one group.  multipass ok

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class FindSocialGroups {
   private static Vector<Double> vertexList = new Vector<Double>();
   private static UndirectedGraph<Double, DefaultEdge> UIDGraph = null;
   private static String inFileName;
   private static Collection<Set<Double>> cliques;
   private static HashMap<Double, String> uidNames = new HashMap<Double, String>();
   private static IOFunctions ioHelp = new IOFunctions();
   static String participant = "JC";
  // static String participantID = "2718074";
   static float threshold = .15F;
   public static void main(String[] args){
	  long start, elapsedTime;
	  float elapsedTimeMin;
	  start = System.currentTimeMillis();
	  doSetup();
	  findCliques();
	  findNetworks();
	  System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~FINAL~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	  elapsedTime = System.currentTimeMillis() - start;
	  elapsedTimeMin = elapsedTime/(60*1000F);
	  System.out.println("   total elapsed time: "+elapsedTimeMin+" min ");
	  System.out.println("All done!");// 	run cliqueFinder
	}
   public static void init(){
	  long start, elapsedTime;
	  float elapsedTimeMin;
	  start = System.currentTimeMillis();
	  doSetup();
	  findCliques();
	  findNetworks();
	  System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~FINAL~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	  elapsedTime = System.currentTimeMillis() - start;
	  elapsedTimeMin = elapsedTime/(60*1000F);
	  System.out.println("   total elapsed time: "+elapsedTimeMin+" min ");
	  System.out.println("All done!");// 	run cliqueFinder
   }
   
   @SuppressWarnings("static-access")
   private static void doSetup(){
	  System.out.println("~~~~~~~~~~~~~~~~~~~~~~~SET UP~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	//  inFileName = "data/Kelli/FriendshipData/"+participantID+"_MutualFriends.txt";
	//  String idNameMap = "data/Kelli/FriendshipData/"+participantID+"_People.txt";
	  String idNameMap = "data/Kelli/FriendshipData/PeopleNames.txt";
	  inFileName = "data/Kelli/FriendshipData/"+participant+"FriendOfFriends.txt";
	  System.out.println("created the UIDGraph");
	  UIDGraph = ioHelp.createUIDGraph(inFileName);
	  ioHelp.fillNames(idNameMap);
	  fillNames(idNameMap);
   }
   public static void findCliques() {
	  System.out.println("~~~~~~~~~~~~~~~~~~~Find All Maximal Cliques~~~~~~~~~~~~~~~~~~~~~~~~~~");
	  long start, elapsedTime, getAllTime;
	  BronKerboschCliqueFinder<Double, DefaultEdge> BKcliqueFind = new BronKerboschCliqueFinder<Double, DefaultEdge>(UIDGraph);
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
   private static UndirectedGraph<Double, DefaultEdge> createUIDGraph(String inputFile)
   {
	  UndirectedGraph<Double, DefaultEdge> g = new SimpleGraph<Double, DefaultEdge>(DefaultEdge.class);
	  int linesReadCount = 0;
	  try {
		 DataInputStream in = new DataInputStream(new FileInputStream(inputFile));
		 String FriendPair = null;
		 double friend1 = -1;
		 double friend2 = -1;
		 int parsingSpace = -1;
		 // in.available() returns 0 if the file does not have more lines.
		 while (in.available() != 0) {
		    FriendPair = in.readLine();
			linesReadCount++;
			parsingSpace = FriendPair.indexOf(' ');
			friend1 = Double.parseDouble(FriendPair.substring(0, parsingSpace));
			FriendPair = FriendPair.substring(parsingSpace+1);
			parsingSpace = FriendPair.indexOf(' ');
			if(parsingSpace != -1)
			   friend2 = Double.parseDouble(FriendPair.substring(0,parsingSpace));
			else friend2 = Double.parseDouble(FriendPair);
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
    	 double friendUID = -1;
    	 int parsingComma = -1;
    	 // in.available() returns 0 if the file does not have more lines.
    	 while (in.available() != 0) {
    		friendName = in.readLine();
    		linesReadCount++;
    		parsingComma = friendName.indexOf(',');
    		friendUID = Double.parseDouble(friendName.substring(0, parsingComma));
    		friendName = friendName.substring(parsingComma+2);
    		uidNames.put(friendUID, friendName);
    	 }
    	 in.close();
      } catch (Exception e){
    	  System.out.println("!!!Error in "+namesFileName+", line:"+linesReadCount+": "+e.getMessage());
      }
   }
   @SuppressWarnings("static-access")
private static void findNetworks(){
	  System.out.println("~~~~~~~~~~~~~~~~Intersection~~~~~~~~~~~~~~~~~~~~~~~");
	  preprocessCliques(cliques);
	  //compareCliques();
	  Collection<Set<Double>> networkCliques = NetworkFinder.doNetworkFind(cliques);
	  //String outputLargeGroupFile     = "data/Kelli/FriendGrouperResults/"+participantID+"_LargeGroups.txt";
	  String outputLargeGroupFile     = "data/Kelli/FriendGrouperResults/"+participant+"_LargeGroups.txt";
	  ioHelp.printCliqueNamesToFile(outputLargeGroupFile, networkCliques);
	  findSubcliques(networkCliques);
   }
   private static Set<Double> findLargeGroups(Collection<Set<Double>> networkCliques){
	   Iterator<Set<Double>> cliqueIter = networkCliques.iterator();
	   Set<Double> currClique;
	   Set<Double> largeGroups = new HashSet<Double>();
	   while(cliqueIter.hasNext()){
		   currClique = cliqueIter.next();
		   if(currClique.size()>=50){
			   Iterator<Double> uidIter = currClique.iterator();
			   double uid;
			   while(uidIter.hasNext()){
				   uid = uidIter.next();
				   largeGroups.add(uid);
			   }
		   }
	   }
	   return largeGroups;
   }
   private static UndirectedGraph<Double, DefaultEdge> findSubgraph(Collection<Set<Double>> intersectionCliques){
	   UndirectedGraph<Double, DefaultEdge> subGraph = new SimpleGraph<Double, DefaultEdge>(DefaultEdge.class);
	   Set<Double> vSet = UIDGraph.vertexSet();
	   Set<DefaultEdge> eSet;
	   double source, target;
	   Set<Double> largeGroups = findLargeGroups(intersectionCliques);
	   for(double v: vSet){
		  if(largeGroups.contains(v)){
			  subGraph.addVertex(v);
		  }
	   }
	   for(double v: vSet){
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
private static void findSubcliques(Collection<Set<Double>> intersectionCliques){
	   System.out.println("~~~~~~~~~~~~~~~~~~Find Subcliques~~~~~~~~~~~~~~~~~~~~~~~~~");
	   UndirectedGraph<Double, DefaultEdge> subGraph = findSubgraph(intersectionCliques);
	   BronKerboschCliqueFinder<Double, DefaultEdge> BKcliqueFind2 = new BronKerboschCliqueFinder<Double, DefaultEdge>(subGraph);
	   Collection<Set<Double>> SubgroupCliques;
	   SubgroupCliques = BKcliqueFind2.getAllMaximalCliques();
	   System.out.println("all max cliques: "+SubgroupCliques.size());
	   SubgroupCliques = SubgroupFinder.doSubgroupFind(SubgroupCliques, threshold);
	   System.out.println("SubCliques size: "+SubgroupCliques.size());
	   String outputFile = "data/Kelli/FriendGrouperResults/"+participant+"_Subcliques.txt";
	   //String outputFile = "data/Kelli/FriendGrouperResults/"+participantID+"_Subcliques.txt";
	   ioHelp.printCliqueNamesToFile(outputFile, SubgroupCliques);
   }
   /**
    * remove all cliques size 2
    */
   private static void preprocessCliques(Collection<Set<Double>> cliques){
	  System.out.println("cliques.size = "+cliques.size());
	   Iterator<Set<Double>> cliqueIter = cliques.iterator();
	  Set<Double> currClique;
	  while (cliqueIter.hasNext()){ 
		 currClique = cliqueIter.next();
		 if(currClique.size() <= 2)
			cliqueIter.remove();
	  }
	  System.out.println("cliques.size = "+cliques.size());
   }   

}
