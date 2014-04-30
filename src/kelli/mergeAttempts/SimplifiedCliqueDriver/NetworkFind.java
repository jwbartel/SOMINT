package kelli.mergeAttempts.SimplifiedCliqueDriver;

// good results from earlier run on KB found in FriendGrouperResults/Random/KB(etc)
// run with only one pass on Brian's subgroup data.  result: they should all be in one group.  multipass ok

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;

public class NetworkFind {
   private static Vector<Integer> vertexList = new Vector<Integer>();
   private static UndirectedGraph<Integer, DefaultEdge> UIDGraph = null;
   private static String inFileName;
   private static Collection<Set<Integer>> cliques;
   private static HashMap<Integer, String> uidNames = new HashMap<Integer, String>();
   private static IOFunctions ioHelp = new IOFunctions();
   private static MergeStatsRecord mStatsRecord = new MergeStatsRecord();
   static Collection<Set<Integer>> networkCliques;
  // static String participant = "JC";
   static String participantID = "2720658";
   static float threshold = .15F;
   public static void main(String[] args){
	  long start, elapsedTime;
	  float elapsedTimeMin;
	  start = System.currentTimeMillis();
	  doSetup();
	  findCliques();
	  findNetworks();
	  System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~FINAL Integer~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	  elapsedTime = System.currentTimeMillis() - start;
	  elapsedTimeMin = elapsedTime/(60*1000F);
	  System.out.println("   total elapsed time: "+elapsedTimeMin+" min ");
	  System.out.println("All done!");// 	run cliqueFinder
	}
   public static MergeStatsRecord init(){
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
	  String outputFile = "data/Kelli/FriendGrouperResults/"+participantID+"_NetworkLists.txt";
	  ioHelp.printCliqueNamesToFile(outputFile, networkCliques);
	  mStatsRecord.setNumSubgroups("n/a");
	  mStatsRecord.setOutfile(outputFile);
	  System.out.println("All done!");// 	run cliqueFinder
	  return mStatsRecord;
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
private static void findNetworks(){
	  System.out.println("~~~~~~~~~~~~~~~~Intersection~~~~~~~~~~~~~~~~~~~~~~~");
	  preprocessCliques(cliques);
	  //compareCliques();
	  networkCliques = NetworkFinder.doNetworkFind(cliques);
	  mStatsRecord.setNumNetworks(""+networkCliques.size());
	  //String outputLargeGroupFile     = "data/Kelli/FriendGrouperResults/"+participantID+"_LargeGroups.txt";
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
