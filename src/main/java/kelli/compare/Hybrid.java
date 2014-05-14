package kelli.compare;

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

import kelli.FriendGrouper.Int.IOFunctions;
import kelli.FriendGrouper.Int.NetworkFinder;
import kelli.FriendGrouper.Int.SubgroupFinder;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;


public class Hybrid {
   private static Vector<Integer> vertexList = new Vector<Integer>();
   private static UndirectedGraph<Integer, DefaultEdge> UIDGraph = null;
   private static String inFileName;
   private static Collection<Set<Integer>> cliques;
   private static HashMap<Integer, String> uidNames = new HashMap<Integer, String>();
   private static IOFunctions ioHelp = new IOFunctions();
  // static String participant = "JC";
   static String participantID;
   static float threshold = .15F;
   
   public Hybrid(UndirectedGraph<Integer, DefaultEdge> ugraph, float t, String id){
	   UIDGraph = ugraph;
	   threshold = t;
	   participantID = id;
   }

   public static void main(String[] args){
	  long start, elapsedTime;
	  float elapsedTimeMin;
	  start = System.currentTimeMillis();
	  doSetup();
	  findCliques();
	  findNetworks(cliques);
	  System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~FINAL Integer~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	  elapsedTime = System.currentTimeMillis() - start;
	  elapsedTimeMin = elapsedTime/(60*1000F);
	  System.out.println("   total elapsed time: "+elapsedTimeMin+" min ");
	  System.out.println("All done!");// 	run cliqueFinder
	}
   public static void init(Collection<Set<Integer>> c){
	  long start, elapsedTime;
	  float elapsedTimeMin;
	  start = System.currentTimeMillis();
	  findNetworks(c);
	  System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~FINAL~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	  elapsedTime = System.currentTimeMillis() - start;
	  elapsedTimeMin = elapsedTime/(60*1000F);
	  System.out.println("   total elapsed time: "+elapsedTimeMin+" min ");
	  System.out.println("All done!");// 	run cliqueFinder
   }
   
   @SuppressWarnings("static-access")
   private static void doSetup(){
	  System.out.println("~~~~~~~~~~~~~~~~~~~~~~~SET UP~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	  inFileName = "data/Kelli/FriendshipData/"+participantID+"_MutualFriends.txt";
	  String idNameMap = "data/Kelli/FriendshipData/"+participantID+"_People.txt";
	//  String idNameMap = "data/Kelli/FriendshipData/PeopleNames.txt";
	//  inFileName = "data/Kelli/FriendshipData/"+participant+"FriendOfFriends.txt";
	  System.out.println("created the UIDGraph");
	  UIDGraph = ioHelp.createUIDGraph(inFileName);
	  ioHelp.fillNames(idNameMap);
	  fillNames(idNameMap);
   }
   public static void findCliques() {
	  System.out.println("~~~~~~~~~~~~~~~~~~~Find All Maximal Cliques~~~~~~~~~~~~~~~~~~~~~~~~~~");
	  long start, elapsedTime, getAllTime;
	  BronKerboschCliqueFinder<Integer, DefaultEdge> BKcliqueFind = new BronKerboschCliqueFinder<Integer, DefaultEdge>(UIDGraph);
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
private static void findNetworks(Collection<Set<Integer>> c){
	  System.out.println("~~~~~~~~~~~~~~~~Intersection~~~~~~~~~~~~~~~~~~~~~~~");
	  preprocessCliques(c);
	  //compareCliques();
	  Collection<Set<Integer>> networkCliques = NetworkFinder.doNetworkFind(c);
	  String outputLargeGroupFile     = "data/Kelli/CompareResults/"+participantID+"_LargeGroups.txt";
	  //String outputLargeGroupFile     = "data/Kelli/FriendGrouperResults/"+participant+"_LargeGroups.txt";
	  ioHelp.printCliqueNamesToFile(outputLargeGroupFile, networkCliques);
	  findSubcliques(networkCliques);
   }
   private static Set<Integer> findLargeGroups(Collection<Set<Integer>> networkCliques){
	   Iterator<Set<Integer>> cliqueIter = networkCliques.iterator();
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
   private static UndirectedGraph<Integer, DefaultEdge> findSubgraph(Collection<Set<Integer>> intersectionCliques){
	   UndirectedGraph<Integer, DefaultEdge> subGraph = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
	   Set<Integer> vSet = UIDGraph.vertexSet();
	   Set<DefaultEdge> eSet;
	   Integer source, target;
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
	   System.out.println("Vertices: "+subGraph.vertexSet().size());
	   System.out.println("Edges: "+subGraph.edgeSet().size());
	   return subGraph;
   }
   @SuppressWarnings("static-access")
private static void findSubcliques(Collection<Set<Integer>> intersectionCliques){
	   long start = System.currentTimeMillis();
	   System.out.println("~~~~~~~~~~~~~~~~~~Find Subcliques~~~~~~~~~~~~~~~~~~~~~~~~~");
	   UndirectedGraph<Integer, DefaultEdge> subGraph = findSubgraph(intersectionCliques);
	   intersectionCliques.clear();
	   BronKerboschCliqueFinder<Integer, DefaultEdge> BKcliqueFind2 = new BronKerboschCliqueFinder<Integer, DefaultEdge>(subGraph);
	   Collection<Set<Integer>> SubgroupCliques;
	   SubgroupCliques = BKcliqueFind2.getAllMaximalCliques();
	   System.out.println("Time to find allMaxCliques(sub): "+((System.currentTimeMillis()-start)/(60*1000F)));
	   System.out.println("all max cliques: "+SubgroupCliques.size());
	   SubgroupCliques = SubgroupFinder.doSubgroupFind(SubgroupCliques, threshold);
	   System.out.println("SubCliques size: "+SubgroupCliques.size());
	   //String outputFile = "data/Kelli/FriendGrouperResults/"+participant+"_Subcliques.txt";
	   String outputFile = "data/Kelli/CompareResults/"+participantID+"_Subcliques.txt";
	   ioHelp.printCliqueNamesToFile(outputFile, SubgroupCliques);
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
