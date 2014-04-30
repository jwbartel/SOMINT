package groups.seedless.kelli;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import kelli.FriendGrouper.Int.IOFunctions;
import kelli.compare.FriendList;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;



public class ModifiedCompareAlgos {
   private static String inRelationships;
   private static String participantID = "19";
   private static UndirectedGraph<Integer, DefaultEdge> UIDGraph = null;
   private static IOFunctions ioHelp = new IOFunctions();
   private static HashMap<Integer, String> uidNames = new HashMap<Integer, String>();
   private static Collection<Set<Integer>> cliques;
   static float threshold = .15F;
   static double w = 0.25;

   @SuppressWarnings("static-access")
public static void main(String[] args){
	   /*doSetup();
	   findCliques();
	   /*System.out.println("***************** Intersection ******************");
	   Collection<Set<Integer>> IntersectionCliques = new ArrayList<Set<Integer>>(cliques);
	   IntersectionCliques = Intersection.doNetworkFind(IntersectionCliques);
	   String outputIntersectionFile     = "data/Kelli/CompareResults/"+participantID+"_Intersection.txt";
	   ioHelp.printCliqueNamesToFile(outputIntersectionFile, IntersectionCliques);
	   IntersectionCliques.clear();
	   System.out.println("******************** SetDiff ********************");
	   Collection<Set<Integer>> SetDiffCliques = new ArrayList<Set<Integer>>(cliques);
	   SetDiffCliques = SetDiff.doSubgroupFind(SetDiffCliques, threshold);
	   String outputSetDiffFile     = "data/Kelli/CompareResults/"+participantID+"_SetDiff.txt";
	   ioHelp.printCliqueNamesToFile(outputSetDiffFile, SetDiffCliques);
	   SetDiffCliques.clear();*/
	   /*System.out.println("******************** Hybrid  ********************");
	   Hybrid h = new Hybrid(UIDGraph, threshold, participantID);
	   Collection<Set<Integer>> HybridCliques = new ArrayList<Set<Integer>>(cliques);
	   /*h.init(HybridCliques);
	   HybridCliques.clear();
	   /*System.out.println("********************** LCMA **********************");
	   LCMA.init(cliques, "data/Jacob/LCMA/"+participantID+"_LCMA.txt", w, uidNames);*/
	   accuracyCheck();
	   /*System.out.println("******************** Fellows *********************");
	   Fellows f = new Fellows(UIDGraph, participantID, false);
	   ArrayList<Set<Integer>> fellowCliques = new ArrayList<Set<Integer>>(cliques);
	   f.init(fellowCliques);*/
	   System.out.println("**************************************************");
	   System.out.println("********************** DONE **********************");
	   System.out.println("**************************************************");
   }

   @SuppressWarnings("static-access")
   private static void accuracyCheck(){
	   String accuracyCompareOutFile = "data/Kelli/CompareResults/"+participantID+"_Accuracy.txt";
	   ModifiedAccuracyChecker ac = new ModifiedAccuracyChecker(participantID, accuracyCompareOutFile);
	   ac.printStatus("Accuracy Check for Participant "+participantID, false);
	   ac.setUpIdeal();
	   hybridCompare(ac);
	   //IntersectionCompare(ac);
	   //SetDiffCompare(ac);
	   //LCMACompare(ac);
   }
   private static void LCMACompare(ModifiedAccuracyChecker ac) {
	   ac.printStatus("~~~~~~~~~~~~~~~~~~ LCMA ~~~~~~~~~~~~~~~~~~");
	   int idealSize = ac.idealLists.size();
	   ArrayList<FriendList> LCMALists = ac.setUpLCMA();
	   double costMatrix[][] = new double[LCMALists.size()][idealSize];
	   double kAccMatrix[][] = new double[LCMALists.size()][idealSize];
	   ac.compareLists(LCMALists, costMatrix, kAccMatrix);
       ac.printkAccMatrix(kAccMatrix, LCMALists);
	   ac.printCostMatrix(costMatrix, LCMALists);
   }

   @SuppressWarnings("static-access")
   private static void IntersectionCompare(ModifiedAccuracyChecker ac) {
	   ac.printStatus("~~~~~~~~~~~~~~~~~~ Intersection ~~~~~~~~~~~~~~~~~~");
	   int idealSize = ac.idealLists.size();
	   ArrayList<FriendList> intersectionLists = ac.setUpIntersection();
	   double costMatrix[][] = new double[intersectionLists.size()][idealSize];
	   double kAccMatrix[][] = new double[intersectionLists.size()][idealSize];
	   ac.compareLists(intersectionLists, costMatrix, kAccMatrix);
       ac.printkAccMatrix(kAccMatrix, intersectionLists);
	   ac.printCostMatrix(costMatrix, intersectionLists);
   }

   @SuppressWarnings("static-access")
   static void hybridCompare(ModifiedAccuracyChecker ac){
	   ac.printRequiredManualAdds();
	   ac.printStatus("~~~~~~~~~~~~~~~~~~ HYBRID ~~~~~~~~~~~~~~~~~~");
	   int idealSize = ac.idealLists.size();
	   ArrayList<FriendList> hybridLists = ac.setUpHybrid();
	   double costMatrix[][] = new double[hybridLists.size()][idealSize];
	   int addsMatrix[][] = new int[hybridLists.size()][idealSize];
	   int removesMatrix[][] = new int[hybridLists.size()][idealSize];
	   int jacobCostMatrix[][] = new int[hybridLists.size()][idealSize];
	   double kAccMatrix[][] = new double[hybridLists.size()][idealSize];
	   ac.modifiedCompareLists(hybridLists, costMatrix, addsMatrix, removesMatrix, jacobCostMatrix, kAccMatrix);
	   //ac.modifiedCompareLists(hybridLists, costMatrix, jacobCostMatrix, kAccMatrix);
	   Set<Point> jacobMinCostPositions = ac.getMinCostPositions(jacobCostMatrix);
	   Set<Point> kCostPositions = ac.getMinKCostPositions(kAccMatrix, jacobCostMatrix);
	   
       ac.printkAccMatrix(kAccMatrix, hybridLists);
	   ac.printPosInJacobCostMatrix(jacobCostMatrix, hybridLists, kCostPositions, "kacc Postions");
	   Set<Point> minKAccPoints = ac.getMinCostPos(jacobCostMatrix, kCostPositions);
	   ac.printTotalVals(addsMatrix, removesMatrix, minKAccPoints);
	   ac.printPosInJacobCostMatrix(jacobCostMatrix, hybridLists, jacobMinCostPositions, "Min Values");
	   Set<Point> minJacobPoints = ac.getMinCostPos(jacobCostMatrix, jacobMinCostPositions);
	   ac.printTotalVals(addsMatrix, removesMatrix, minJacobPoints);
	   ac.printJacobCostMatrix(jacobCostMatrix, hybridLists);
	   ac.printCostMatrix(costMatrix, hybridLists);
	   ArrayList<FriendList> joinedLists = ac.joinLists(hybridLists);
	   
	   
	   ac.printStatus("~~~~~~~~~~~~~~~~ AFTER JOIN ~~~~~~~~~~~~~~~~~");
	   double cost2[][] = new double[joinedLists.size()][idealSize];
	   double kAcc2[][] = new double[joinedLists.size()][idealSize];
	   int adds2[][] = new int[hybridLists.size()][idealSize];
	   int removes2[][] = new int[hybridLists.size()][idealSize];
	   int jacob2[][] = new int[hybridLists.size()][idealSize];
	   ac.modifiedCompareLists(joinedLists, cost2, adds2, removes2, jacob2, kAcc2);
	   Set<Point> jacobMins2 = ac.getMinCostPositions(jacob2);
	   Set<Point> kCostPos2 = ac.getMinKCostPositions(kAcc2, jacob2);
	   
	   ac.printkAccMatrix(kAcc2, joinedLists);
	   ac.printPosInJacobCostMatrix(jacob2, joinedLists, kCostPos2, "kacc Postions");
	   Set<Point> minKAcc2 = ac.getMinCostPos(jacob2, kCostPos2);
	   ac.printTotalVals(adds2, removes2, minKAcc2);
	   ac.printPosInJacobCostMatrix(jacob2, joinedLists, jacobMins2, "Min Values");
	   Set<Point> jacobMinPos2 = ac.getMinCostPos(jacob2, jacobMins2);
	   ac.printTotalVals(adds2, removes2, jacobMinPos2);
	   ac.printJacobCostMatrix(jacob2, joinedLists);
	   ac.printCostMatrix(cost2, joinedLists);
   }

   private static void doSetup(){
	  System.out.println("~~~~~~~~~~~~~~~~~~~~~~~SET UP~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	  inRelationships = "data/Kelli/FriendshipData/2010Study/"+participantID+"_MutualFriends.txt";
	  String idNameMap = "data/Kelli/FriendshipData/2010Study/"+participantID+"_People.txt";
	  System.out.println("created the UIDGraph");
	  UIDGraph = ioHelp.createUIDGraph(inRelationships);
	  ioHelp.fillNames(idNameMap);
	  fillNames(idNameMap);
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
   public static void findCliques() {
		  System.out.println("~~~~~~~~~~Find All Maximal Cliques~~~~~~~~~~");
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

}
