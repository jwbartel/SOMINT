package kelli.mergeAttempts.incremental;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import kelli.friends.AFriendListManager;
import kelli.friends.FriendListManager;
import kelli.friends.NamedString;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import util.models.AListenableVector;

public class IncrementalMerger {
   private Collection<Set<Integer>> cliques = new ArrayList<Set<Integer>>();
//	private static Collection<Set<Integer>> cliques = new AListenableVector<Set<Integer>>();
   private Collection<Set<Integer>> mergedCliques = new ArrayList<Set<Integer>>();
   //private Collection<NamedString> namedCliques = new AListenableVector();
   FriendListManager friendsManager;
   //Collection<String> printedCliques = new ArrayList<String>(); 
   Collection<String> printedCliques = new AListenableVector<String>(); 
//	public static Collection<Set<Integer>> mergedCliques = new AListenableVector<Set<Integer>>();
   private HashMap<Integer, String> uidNames = new HashMap<Integer, String>();
   private HashMap<Integer, Boolean> toRemoveClique;// = new HashMap<Integer, Boolean>();
   private float varyPercentage = .90F;   // try .1, .15, .2   checking percentDifferent, so 14% different means 86% the same
   public float getVaryPercentage() {
	  return varyPercentage;
   }

   public void setVaryPercentage(float varyPercentage) {
	  this.varyPercentage = varyPercentage;
	}
private  String participantId = "PD";
   private String inFileName;
   private  String outputMergedFile = "data/Kelli/RemoveMergedResults/PD_10_MergedCliquesIntersection.txt";
   private  String nameTable;
   private  boolean mergeHappened = true;
   boolean effectiveMergeHappened = true;
   boolean newLargerSet = true;
   long start, passStart, elapsedTime;
   float elapsedTimeMin;
//   int passNumber = 1;
   int passNumber = 0;
   static int comparisonNumber = 0;
   static int mergeNumber = 0;
   int effectiveMergeNumber = 0;
   CliqueMerger cliqueMerger;
   public IncrementalMerger (String participant, String outFile, float percent, CliqueMerger theCliqueMerger){
	   varyPercentage = percent;
	   participantId = participant;
	   outputMergedFile = outFile;
	   passNumber = 0;
	   start = System.currentTimeMillis();
	   doSetup();
	   findCliques();
//	   fillNames();
//	   loadCliques();

	   System.out.println("original cliques.size: "+cliques.size());
	   passStart = System.currentTimeMillis();
	   preprosessCliques();
	   System.out.println("after removing cliques size <=3 cliques.size: "+cliques.size());
//	   ObjectEditor.treeEdit(cliques);
	   cliqueMerger = theCliqueMerger;
	   toRemoveClique = new HashMap<Integer, Boolean>();
	   cliqueMerger.init(varyPercentage, mergedCliques, toRemoveClique, this);
	   initNextMergePass();
	  
   }

   
   public void setCliqueMerger (CliqueMerger theCliqueMerger) {
	   cliqueMerger = theCliqueMerger;
   }
   
   public String toString(Set<Integer> s){
	   String retVal = s.hashCode() + "(" + s.size() + ")";
	   int numItemsInCurrentLine = 0;
	   for (Integer element:s){
//		   if (numItemsInCurrentLine > 7) {
//			   numItemsInCurrentLine = 0;
//			   retVal += "\n";			   
//		   }
		   
//		   retVal += ":" + uidNames.get(element)+"("+element+")";
		   retVal +=  uidNames.get(element) + ":";

	   }
	   return retVal;
   }
   
   public int getPassNumber() {
	   return passNumber;
   }
   public static int getComparisonNumber() {
	   return comparisonNumber;
   }
   public static int getMergeNumber() {
	   return mergeNumber;
   }
   public int getEffectiveMergeNumber() {
	   return effectiveMergeNumber;
   }
   public Set<Integer> getLargerClique() {
	   return nextLargerClique;
   }
   public String geCliqueToString(Set<Integer> clique) {
	   if (clique != null)
			return toString(clique);
	   else
		   return "uninitialized";		   
	   
   }
   public String getLargerCliqueToString() {
	   return geCliqueToString(nextLargerClique);
   }
   public String getSmallerCliqueToString() {
	   return geCliqueToString(nextSmallerClique);
   }
   public Set<Integer> getSmallerClique() {
	   return nextLargerClique;
   }
   public void toString(Collection<Set<Integer>> sets){
	  toStringBatch(sets);
   }
   public void toStringBatch(Collection<Set<Integer>> sets){
	   Collection<String> retVal = new ArrayList<String>();
	   //Collection<String> retVal = printedCliques;
	   //printedCliques.clear();
	   for(Set<Integer> set: sets){
		   retVal.add(toString(set));
	   }
	   printedCliques.clear();
	   printedCliques.addAll(retVal);
	   //retVal = printedCliques;
	   //return retVal;
   }
   
   FriendListManager toFriendsListManager(Collection<Set<Integer>> sets) {
	   return new AFriendListManager( sets, uidNames);
   }
   
   public FriendListManager getFriendsListManager() {
	   if (friendsManager == null)
		   friendsManager = toFriendsListManager(cliques);
	   return friendsManager;
   }
   
/*   void toNamedCliques(Collection<Set<Integer>> sets){
//	   namedCliques.clear();
//	   Set<String> names = new HashSet();
//	   for(Set<Integer> set: sets){
//		   Integer[] template = {0};
//		   Integer[] elements = set.toArray(template);
//		   String name = uidNames.get(elements[0]) + "..." + uidNames.get(elements[elements.length-1]);
//		   if (names.contains(name)) {
//			   name += "(" + set.size() + ")";
//			   if (names.contains(name))
//				   name = "(" + set.hashCode() + ")";
//		   }
//		   names.add(name);
//		   NamedString namedClique = new ANamedString(name, set, uidNames);
//		   namedCliques.add(namedClique);
//	   }
//	   
//	   //retVal = printedCliques;
//	   //return retVal;
//   }
//   public void computeNamedCliques() {
//	   toNamedCliques(mergedCliques);
//   }   */
   public Collection<NamedString> getNamedStringCliques() {
	   FriendListManager friendsManager = getFriendsListManager();
	   return friendsManager.getNamedStringCliques();
//	   if (namedCliques.size() == 0)
//		   toNamedCliques(cliques);
//	   return namedCliques;
   }
   public void toStringIncremental(Collection<Set<Integer>> sets){
	   //Collection<String> retVal = new ArrayList<String>();
	   Collection<String> retVal = printedCliques;
	   printedCliques.clear();
	   for(Set<Integer> set: sets){
		   retVal.add(toString(set));
	   }
	   //printedCliques.clear();
	   //printedCliques.addAll(retVal);
	   //return retVal;
   }
   public Collection<String> printedCliques(){
	   return printedCliques;
   }
   public boolean mergeHappened(){
	   return mergeHappened;
   }
   public boolean hasMoreComparisons() {
	   return mergeHappened || (hasNextOuterClique() || hasNextInnerClique());
   }
   public void doNextComparison() {
	   if (!hasMoreComparisons()) 
		   return ;
//	   Set<Integer> oldLargerSet = nextLargerClique;	   
//	   int oldSize = 0;
//	   if (nextLargerClique != null)
//		   oldSize = nextLargerClique.size();
	   if ((nextOuterCliqueIndex < 0 || !hasNextInnerClique()) && hasNextOuterClique())
		   advanceOuterClique();
	   mergeNextInnerClique();
   }
   public void doNextMerge() {
	   doNextComparison();
	   while (!mergeHappened && 
			   hasMoreComparisons())
		   doNextComparison(); 
	   
   }
   
   
   
   public void doNextEffectiveMerge() {
//	   Set<Integer> oldLargerSet = nextLargerClique;	   
//	   int oldSize = 0;
//	   if (nextLargerClique != null)
//		   oldSize = nextLargerClique.size();
	   doNextMerge();
	   if (newLargerSet || !effectiveMergeHappened)
	   //if (nextLargerClique == oldLargerSet && oldSize == nextLargerClique.size() && hasMoreComparisons()) {
		   doNextEffectiveMerge();
	   //}
	   
	   
   }
   public void initNextMergePass() {
	   //mergeHappened = false;
		  toRemoveClique.clear();
		  for(int i = 0; i < cliques.size(); i++)
			 toRemoveClique.put(i, false);
		  passStart = System.currentTimeMillis();
		  nextOuterCliqueIndex = -1;
		  
		  //combineCliques();
	   
   }
   public void doNextMergePass(){
	   
		  mergeHappened = false;
		  initNextMergePass();
//		  toRemoveClique.clear();
//		  for(int i = 0; i < cliques.size(); i++)
//			 toRemoveClique.put(i, false);
//		  passStart = System.currentTimeMillis();
		  //combineCliques();
		  while (hasNextOuterClique()) {
			  advanceOuterClique();
			  while (hasNextInnerClique()) {
				  mergeNextInnerClique();
			  }			 
		  }
		  for(Set<Integer> clique: mergedCliques){
			  cliques.remove(clique);
		  }
		  mergedCliques.clear();
		  elapsedTime = System.currentTimeMillis() - passStart;
		  elapsedTimeMin = elapsedTime/(60*1000F);
		  System.out.println("pass "+passNumber+" cliques.size: "+cliques.size()+"   pass time: "+elapsedTimeMin);
		  passNumber++;
		  toString(cliques);
		  //toNamedCliques(cliques);
   }
   public void setMergeHappened (boolean newVal) {
	   mergeHappened = newVal;
   }
   Set<Integer> nextInnerClique = null;
   int nextInnerCliqueIndex = -1;   
   Set<Integer> nextOuterClique = null; 
   int nextOuterCliqueIndex = -1;
   Set<Integer> nextLargerClique;
   Set<Integer> nextSmallerClique;
  Vector<Set<Integer>> trace = new Vector<Set<Integer>>();
   public void mergeNextInnerClique() {
	   //TODO check if results are same for SetDiffMerge
//	   trace.add(new HashSet(cliques.get(187)));
//	   if(!trace.get(0).equals(trace.get(trace.size()-1))){
//		   System.out.println("187 changed");
//	   }
//	   if(nextInnerCliqueIndex == 187){
		 //  System.out.println("traces:"+ trace.get(0));
//	   }
	   effectiveMergeHappened = false;
	   Set<Integer> oldLargerSet = nextLargerClique;	   
	   int oldSize = 0;
	   if (nextLargerClique != null)
		   oldSize = nextLargerClique.size();
	   nextInnerCliqueIndex++;
	   nextInnerClique = ((ArrayList<Set<Integer>>) cliques).get(nextInnerCliqueIndex);
	   boolean merged;
	   if(!cliqueMerger.checkToRemoveCliques(passNumber) || 
			   !toRemoveClique.get(nextInnerCliqueIndex) ){
			  if (!nextOuterClique.equals(nextInnerClique)){
				 if(nextOuterClique.size() >= nextInnerClique.size())  {
					//mergeCliques(nextOuterClique, nextInnerClique, nextInnerCliqueIndex);
					 nextLargerClique = nextOuterClique;
					 nextSmallerClique = nextInnerClique;
					 merged = cliqueMerger.mergeCliques(nextLargerClique, nextSmallerClique, nextOuterCliqueIndex, nextInnerCliqueIndex);
				 } else {
					 nextLargerClique = nextInnerClique;
				 	nextSmallerClique = nextOuterClique;
				 	merged = cliqueMerger.mergeCliques(nextLargerClique, nextSmallerClique, nextInnerCliqueIndex, nextOuterCliqueIndex);

				 	if (merged && hasNextOuterClique())
				 		advanceOuterClique();
				 }
				 comparisonNumber++;
				 if (merged)
					 mergeNumber++;
					 //firstOfCliquePairMerged = mergeCliques(nextInnerClique, nextOuterClique, iterationCount); //possible problem...if nextOuterClique merged into c5, 
				 										  // i shouldn't be able to try c6 into nextOuterClique
			  }	
			  else return;
			  newLargerSet = nextLargerClique != oldLargerSet;
			  if (!newLargerSet && oldSize != nextLargerClique.size()) {
			  effectiveMergeHappened =  true ;
			  effectiveMergeNumber++;
			  }
			  
		   }
	   
   }
   public void advanceOuterClique() {
	   if (!hasNextOuterClique() && mergeHappened) {
		   initNextMergePass();
		   mergeHappened = false;
	   }
	   nextInnerCliqueIndex = -1;
	   nextOuterCliqueIndex++;
	   while(hasNextOuterClique() && toRemoveClique.get(nextOuterCliqueIndex) ){
		   nextOuterCliqueIndex++;
	   } 
	   if(true){
		   System.out.println("nextOuterCliqueIndex: "+nextOuterCliqueIndex);
	   }
	   if (hasNextOuterClique())
		   nextOuterClique = ((ArrayList<Set<Integer>>) cliques).get(nextOuterCliqueIndex);  
   }
   public boolean hasNextOuterClique() {
	   return cliques.size() > nextOuterCliqueIndex + 1;
	   
   }
   public boolean hasNextInnerClique() {
	   return cliques.size() > nextInnerCliqueIndex + 1;
   }
   /*private void combineCliques(){
	  int iterationCount = 0;
	  int innerIter;
	  boolean firstOfCliquePairMerged = false;
	  for (Set<Integer> c1: cliques){
		 if(!toRemoveClique.get(iterationCount) ){
			innerIter = 0;
			for (Set<Integer> c2: cliques){
			   if(!toRemoveClique.get(innerIter) ){
				  if (!c1.equals(c2)){
					 if(c1.size() >= c2.size()) 
						mergeCliques(c1, c2, innerIter);
					 else
						firstOfCliquePairMerged = mergeCliques(c2, c1, iterationCount); //possible problem...if c1 merged into c5, 
					 										  // i shouldn't be able to try c6 into c1
				  }
				  if(firstOfCliquePairMerged) {
					  firstOfCliquePairMerged = false;
					  break; //break forloop from (clique x, clique y) to (clique x+1, clique init)
				  }
			   }
			   innerIter++;
			}
		 }
		 iterationCount++;
	  }
   }*/
   /**
    * remove all cliques less than size 3
    */
   private void preprosessCliques(){
	   System.out.println("mcliques.size"+getCliques().size());
	  Iterator<Set<Integer>> cliqueIter = cliques.iterator();
	  Set<Integer> currClique;
	  while (cliqueIter.hasNext()){ 
		 currClique = cliqueIter.next();
		 if(currClique.size() <= 2)
			 cliqueIter.remove();
	  }
	  System.out.println("mcliques.size"+getCliques().size());
   }
   /**
    * remove all cliques size 3
    */
/*   private void postprocessCliques(){
	   Iterator<Set<Integer>> cliqueIter = cliques.iterator();
	   Set<Integer> currClique;
	   while(cliqueIter.hasNext()){
		   currClique = cliqueIter.next();
		   if(currClique.size() <=3){
			   cliqueIter.remove();
		   }
	   }
   }*/
   
   /* for Percent Different...
	   * for (int uid: c2){
		 if(!c1.contains(uid)){
			numDifferent++;
			difference.add(uid);
		 }
	  }
	  percentage = (float)numDifferent/(float)c2.size();
	  if(percentage <= varyPercentage){
		 mergedCliques.remove(c1);
		 mergedCliques.remove(c2);
		 for(int uid: difference){
			c1.add(uid);
		 }
		 mergeHappened = true;
		 merged = true;
		 mergedCliques.add(c2); //the cliques that were merged into a larger clique...to be removed
		 toRemoveClique.put(smallerCliqueNumber, true);
	  } else if(c2.size()<=10){
		  float tryPercentage = 1F/(float)c2.size();
		  if(percentage <= tryPercentage){
			  mergedCliques.remove(c1);
			  mergedCliques.remove(c2);
			  for(int uid: difference){
				 c1.add(uid);
			  }
			  mergedCliques.add(c2);
			  mergeHappened = true;
			  merged = true;
			  toRemoveClique.put(smallerCliqueNumber,true);
		  }
	  }
	  */
   
   /*private boolean mergeCliques(Set<Integer> c1, Set<Integer> c2, int smallerCliqueNumber){
	  boolean merged = false;
	  float percentage;
	  int numDifferent = 0;
	  int intersectionCount = 0;
	  int c1Size = c1.size();
	  int c2Size = c2.size();
	  Set<Integer> difference = new HashSet<Integer>();
	  //intersection:
	  for (int uid: c2){
		  if(c1.contains(uid))
			  intersectionCount++;
		  else difference.add(uid);
	  }
	  percentage = (float)intersectionCount/(float)c2.size();
	  if(percentage >= varyPercentage){
			 mergedCliques.remove(c1);
			 mergedCliques.remove(c2);
			 for(int uid: difference){
				c1.add(uid);
			 }
			 mergeHappened = true;
			 merged = true;
			 mergedCliques.add(c2); //the cliques that were merged into a larger clique...to be removed
			 toRemoveClique.put(smallerCliqueNumber, true);
		  } else if(c1.size()<=10){
			  float tryPercentage = .75F;
			  if(percentage >= tryPercentage){
				  mergedCliques.remove(c1);
				  mergedCliques.remove(c2);
				  for(int uid: difference){
					 c1.add(uid);
				  }
				  mergedCliques.add(c2);
				  mergeHappened = true;
				  merged = true;
				  toRemoveClique.put(smallerCliqueNumber,true);
			  }
		  }
	  return merged;
   }*/
   
   private UndirectedGraph<Integer, DefaultEdge> UIDGraph = null;
   public void setUIDGraph(UndirectedGraph<Integer,DefaultEdge> g){
	   UIDGraph = g;
   }
   public UndirectedGraph<Integer, DefaultEdge> getUIDGraph(){
	   return UIDGraph;
   }
   private void doSetup(){
      //if (UIDGraph.vertexSet().size() == 0){
    	  System.out.println("~~~~~~~~~~~~~~~~~~~~~~~SET UP~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    	  //inFileName = "data/Kelli/FriendshipData/"+participantId+"FriendOfFriends.txt";
    	  inFileName = "data/Kelli/FriendshipData/"+participantId+"_MutualFriends.txt";
    	  setUIDGraph(createUIDGraph(inFileName));
    	  System.out.println("created the UIDGraph");
    	  //ioHelp.createUIDGraph(inFileName);
    	  //ioHelp.fillNames("data/Kelli/FriendshipData/PeopleNames.txt");
    	  /*String peopleNamesFile = "data/Kelli/FriendshipData/"+participantId+"PeopleNames.txt";
    	  try {
    		  DataInputStream in = new DataInputStream(new FileInputStream(peopleNamesFile));
    		  if(in.available() != 0){
    			  fillNames(peopleNamesFile);
    		  } 
    	  } catch (Exception e) {
    		  //	e.printStackTrace();*/
    		String peopleNamesFile = "data/Kelli/FriendshipData/"+participantId+"_People.txt";
    		  fillNames(peopleNamesFile);
    	  //} 
      //}
   }
   public Collection<Set<Integer>> getCliques(){
	   return cliques;
   }
   
   public void findCliques() {
	   System.out.println("~~~~~~~~~~~~~~~~~~~Find All Maximal Cliques~~~~~~~~~~~~~~~~~~~~~~~~~~");
	   long start, elapsedTime, getAllTime;
	   BronKerboschCliqueFinder<Integer, DefaultEdge> BKcliqueFind = new BronKerboschCliqueFinder<Integer, DefaultEdge>(UIDGraph);
	   //status message
	   System.out.println("running getAllMaximalCliques");
	   start = System.currentTimeMillis();
	   cliques = (List<Set<Integer>>) BKcliqueFind.getAllMaximalCliques();
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
   public static UndirectedGraph<Integer, DefaultEdge> createUIDGraph(String inputFile)
   {
	   Vector<Integer> vertexList = new Vector<Integer>();
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

/*   private void loadCliques(){
	  int linesReadCount = 0;
	  try {
		 DataInputStream in = new DataInputStream(new FileInputStream(inFileName));
		 String inputLine = null;
		 List<Integer> currClique = null;
		 int cliqueCount = 0;
		 // in.available() returns 0 if the file does not have more lines.
		 while (in.available() != 0) {
			currClique = new ArrayList<Integer>();
			inputLine = in.readLine();
			linesReadCount++;
			while(!inputLine.contains("*")){
				if(!inputLine.contains("Clique:")){
					currClique.add(Integer.parseInt(inputLine));
					if(in.available() != 0) inputLine = in.readLine();
				} else if (in.available() != 0) inputLine = in.readLine();
			}
			Set<Integer> theClique = new HashSet<Integer>(currClique);
			cliques.add(theClique);
		 }
		 // dispose all the resources after using them.
		 in.close();
	  } catch (Exception e){
		  System.out.println("!!! CreateUIDGraph, line:"+linesReadCount+": "+e.getMessage());
	  }
   }
*/   
   public void printCliquesToFile(){
	  int cliqueCount = 1;
      try {
    	 PrintWriter pw = new PrintWriter(new FileWriter(outputMergedFile));
    	 Iterator<Set<Integer>> collIter = cliques.iterator();
    	 Iterator<Integer> uidIter;
    	 Set<Integer> currClique;
    	 int currUID;
    	 while (collIter.hasNext()){
    		pw.println("Clique: "+cliqueCount);
    		currClique = collIter.next();
    		uidIter = currClique.iterator();
    		while (uidIter.hasNext()){
    		   currUID = uidIter.next();
    		   if(uidNames.containsKey(currUID)){
    			  pw.println(uidNames.get(currUID));
    		   }
    		}
    		pw.println();
    		cliqueCount++;
    	 }
    	 pw.close();
    	 System.out.println("you can find the results of this run in: "+outputMergedFile);
      } catch (Exception e){
    	  System.out.println("!!! Problem in PrintCliquesToFile: "+e.getMessage());
    	  System.exit(0);
      }
   }
   //the graph is made of vertices which are uniquely identified via unique identification numbers
   //this function enters the names that correspond to the identifications numbers into a table
   //  to be used for printing human-legible names
   @SuppressWarnings("deprecation")
private void fillNames(String namesFileName){
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
		 System.out.println("!!! fillNames, line:"+linesReadCount+": "+e.getMessage());
	  }
   }
}
