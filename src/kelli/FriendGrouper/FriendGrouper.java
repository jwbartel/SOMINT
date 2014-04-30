package kelli.FriendGrouper;

/**
 * TODO run this on 5 people 
 * TODO run on 10 people 
 * TODO run on 15 people
 */

/**
 * 1/10 = .1
 * 1/9 = .11
 * 1/8 = .125
 * 1/7 = .14
 * 1/6 = .16
 * 1/5 = .2
 * 1/4 = .25
 * 1/3 = .333
 */

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
@SuppressWarnings("unused")
public class FriendGrouper {
	//currently just does LargeGroups (intersection)
	//also do older version (that returns subcliques)
   private static Collection<Set<Integer>> IntersectionCliques = new ArrayList<Set<Integer>>();
   private static Collection<Set<Integer>> SubCliques = new ArrayList<Set<Integer>>();
   public static Collection<Set<Integer>> mergedCliques = new ArrayList<Set<Integer>>();
  // public static Set<Integer> allFriends = new HashSet<Integer>();
   public static HashMap<Integer, Boolean> groupedFriendsInter = new HashMap<Integer, Boolean>();
   public static HashMap<Integer, Boolean> groupedFriendsSetDiff = new HashMap<Integer, Boolean>();
   private static HashMap<Integer, String> uidNames = new HashMap<Integer, String>();
   private static HashMap<Integer, Boolean> toRemoveClique;// = new HashMap<Integer, Boolean>();
   private static String inputCliquesFile            =       "data/Kelli/Cliques/KB_BKCliques.txt";
   private static String outputLargeGroupFile = "data/Kelli/FriendGrouperResults/KB_LargeGroups.txt";
   private static String outputSubcliquesFile = "data/Kelli/FriendGrouperResults/KB_Subcliques15.txt";
   private static String outputLooseFile    =   "data/Kelli/FriendGrouperResults/KB_Loose.txt"; 
   private static String inputAllFriendsList      =   "data/Kelli/FriendshipData/KB_allFriendsList";
   private static String nameTable = "data/Kelli/FriendshipData/PeopleNames.txt";
   private static boolean mergeHappened = true;
   public static void main(String[] args){
	   long start, elapsedTime;
	   float elapsedTimeMin;
	   start = System.currentTimeMillis();
	   System.out.println("~~~~~~~~~~~~~~~~~~~SET UP~~~~~~~~~~~~~~~~~~~~~~~~~~");
	   doSetup();
	   System.out.println("~~~~~~~~~~~~~~~~Intersection~~~~~~~~~~~~~~~~~~~~~~~");
	   doIntersectionMerges();
	   System.out.println("~~~~~~~~~~~~~~~~~~Set Diff~~~~~~~~~~~~~~~~~~~~~~~~~");
	   doSetDiffMerges();
	   System.out.println("~~~~~~~~~~~~~~~~~~~~FINAL~~~~~~~~~~~~~~~~~~~~~~~~~~");
//	   postprocessCliques(); 
//	   System.out.println("after removing cliques size <= 3 cliques.size: "+cliques.size());
	   elapsedTime = System.currentTimeMillis() - start;
	   elapsedTimeMin = elapsedTime/(60*1000F);
	   System.out.println("   total elapsed time: "+elapsedTimeMin+" min ");
	   System.out.println("All done!");
   }
   private static void doSetup(){
	   fillNames();
	   loadCliques();
	   System.out.println("original cliques.size: "+IntersectionCliques.size());
	   preprocessCliques();
	   System.out.println("after removing cliques size 2 cliques.size: "+IntersectionCliques.size());
	   //loadAllFriends();//TODO check this ... and then check printLooseFriendsToFile
   }
   private static void doIntersectionMerges(){
	  long passStart, elapsedTime;
	  float elapsedTimeMin;
	  int passNumber = 1;
	  while(mergeHappened){
		 mergeHappened = false;
		 toRemoveClique = new HashMap<Integer, Boolean>();
		 for(int i = 0; i < IntersectionCliques.size(); i++)
			toRemoveClique.put(i, false);
		 	passStart = System.currentTimeMillis();
		 	combineIntersectionCliques();
		 	for(Set<Integer> clique: mergedCliques){
		 	   IntersectionCliques.remove(clique);
		 	}
		 	mergedCliques = new ArrayList<Set<Integer>>(); 
		 	elapsedTime = System.currentTimeMillis() - passStart;
		 	elapsedTimeMin = elapsedTime/(60*1000F);
		 	System.out.println("pass "+passNumber+" cliques.size: "+IntersectionCliques.size()+"   pass time: "+elapsedTimeMin);
		 	passNumber++;
	  }
	  System.out.println("final cliques.size: "+IntersectionCliques.size());
	  printCliquesToFile();
   }
   private static void doSetDiffMerges(){
	  long passStart, elapsedTime;
	  float elapsedTimeMin;
	  int passNumber = 1;
	  toRemoveClique = new HashMap<Integer, Boolean>();
	  for(int i = 0; i < SubCliques.size(); i++)
			toRemoveClique.put(i, false);
	  passStart = System.currentTimeMillis();
	  combineSetDiffCliques();
	  elapsedTime = System.currentTimeMillis() - passStart;
	  elapsedTimeMin = elapsedTime/(60*1000F);
	  System.out.println("pass "+passNumber+" cliques.size: "+mergedCliques.size()+"   pass time: "+elapsedTimeMin);
	  while(mergeHappened){
		 mergeHappened = false;
		 toRemoveClique = new HashMap<Integer, Boolean>();
		 for(int i = 0; i < SubCliques.size(); i++)
			toRemoveClique.put(i, false);
		 passStart = System.currentTimeMillis();
		 SubCliques = new ArrayList<Set<Integer>>(mergedCliques); 
		 //mergedCliques = new ArrayList<Set<Integer>>();
		 combineMergedCliques();
		 elapsedTime = System.currentTimeMillis() - passStart;
		 elapsedTimeMin = elapsedTime/(60*1000F);
		 passNumber++;
		 System.out.println("pass "+passNumber+" cliques.size: "+SubCliques.size()+"   pass time: "+elapsedTimeMin);
	  }
	  System.out.println("final cliques.size: "+SubCliques.size());
	  printSubCliquesToFile();
   }
   private static void combineIntersectionCliques(){
	  int iterationCount = 0;
	  int innerIter;
	  boolean firstOfCliquePairMerged = false;
	  for (Set<Integer> c1: IntersectionCliques){
		 if(!toRemoveClique.get(iterationCount) ){
			innerIter = 0;
			for (Set<Integer> c2: IntersectionCliques){
			   if(!toRemoveClique.get(innerIter) ){
				  if (!c1.equals(c2)){
					 if(c1.size() >= c2.size()) 
						mergeIntersectionCliques(c1, c2, innerIter);
					 else
						firstOfCliquePairMerged = mergeIntersectionCliques(c2, c1, iterationCount); //possible problem...if c1 merged into c5, 
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
   }
   
   private static void combineSetDiffCliques(){
	  int iterationCount = 0;
	  int innerIter;
	  for (Set<Integer> c1: SubCliques){
		 if(!toRemoveClique.get(iterationCount)){
			innerIter = 0;
			for (Set<Integer> c2: SubCliques){
			   if(!toRemoveClique.get(innerIter) ){
				  if (!c1.equals(c2)){ 
					 if(c1.size() >= c2.size()) 
						mergeSetDiffCliques(c1, c2, innerIter);
					 else
						mergeSetDiffCliques(c2, c1, iterationCount); 
				  }
			   }
			   innerIter++;
			}
		 }
		 iterationCount++;
	  }
   }
 //used in multiple passes
   private static void combineMergedCliques(){
	  int iterationCount = 0;
	  int innerIter;
	  for (Set<Integer> c1: SubCliques){
		 innerIter = 0;
		 for (Set<Integer> c2: SubCliques){
			 if(!c1.equals(c2)){
			   if(c1.size() >= c2.size()) 
				  mergeSetDiffCliques(c1, c2, innerIter);
			   else
				  mergeSetDiffCliques(c2, c1, iterationCount);  
			 }										    
			 innerIter++;
		 }
		 iterationCount++;
	  }
   }
   
   private static boolean mergeSetDiffCliques(Set<Integer> c1, Set<Integer> c2, int smallerCliqueNumber){
	  boolean merged = false;
	  float percentage;
	  int numDifferent = 0;
	  Set<Integer> difference = new HashSet<Integer>();
	  for (int uid: c2){
		 if(!c1.contains(uid)){
			numDifferent++;
			difference.add(uid);
		 }
	  }
	  percentage = (float)numDifferent/(float)c2.size();
	  if(percentage < .15F){
		 // Remove both cliques, if they are already in mergedCliques.  Only keep the resulting mergedClique
		  mergedCliques.remove(c1);
		  mergedCliques.remove(c2);
		  for(int uid: difference){
			  c1.add(uid);
		  }
		  mergedCliques.add(c1);
		  mergeHappened = true;    //un-comment this line to try running more than two passes on the algorithm
		  merged = true;
		  toRemoveClique.put(smallerCliqueNumber,true);
	  }
	  return merged;
   }
		
   /**
    * remove all cliques size 2
    */
   private static void preprocessCliques(){
	  Iterator<Set<Integer>> cliqueIter = IntersectionCliques.iterator();
	  Set<Integer> currClique;
	  while (cliqueIter.hasNext()){ 
		 currClique = cliqueIter.next();
		 if(currClique.size() <= 2)
			 cliqueIter.remove();
	  }
	  cliqueIter = SubCliques.iterator();
	  while (cliqueIter.hasNext()){
		 currClique = cliqueIter.next();
		 if(currClique.size() <= 2)
			cliqueIter.remove();
	  }
   }
   /**
    * remove all cliques size <= 3
   */
  private static void postprocessCliques(){
	  Iterator<Set<Integer>> cliqueIter = IntersectionCliques.iterator();
	  Set<Integer> currClique;
	  while(cliqueIter.hasNext()){
	     currClique = cliqueIter.next();
		 if(currClique.size() <=3){
			cliqueIter.remove();
		 }
	  }
  }
   
   private static boolean mergeIntersectionCliques(Set<Integer> c1, Set<Integer> c2, int smallerCliqueNumber){
	  boolean merged = false;
	  float percentSame, percentDiff;
	  int numDifferent = 0;
	  int intersectionCount = 0;
	  Set<Integer> difference = new HashSet<Integer>();
	  //intersection:
	  for (int uid: c2){
		 if(c1.contains(uid))
			intersectionCount++;
		 else{
			numDifferent++;
			difference.add(uid);
		 }
	  }
	  percentSame = (float)intersectionCount/(float)c2.size();
	  percentDiff = (float) numDifferent/(float)c2.size();
	  if(percentSame >= .9F || percentDiff <= .35F){
		 for(int uid: difference){
			c1.add(uid);
		 }
		 mergeHappened = true;
		 merged = true;
		 mergedCliques.add(c2); //the cliques that were merged into a larger clique...to be removed
		 toRemoveClique.put(smallerCliqueNumber, true);
	  }
	  return merged;
   }
   
   @SuppressWarnings("deprecation")
private static void loadCliques(){
	  int linesReadCount = 0;
	  try {
		 DataInputStream in = new DataInputStream(new FileInputStream(inputCliquesFile));
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
					groupedFriendsInter.put(uid, false);
					groupedFriendsSetDiff.put(uid, false);
					if(in.available() != 0) inputLine = in.readLine();
				} else if (in.available() != 0) inputLine = in.readLine();
			}
			Set<Integer> theClique = new HashSet<Integer>(currClique);
			Set<Integer> forSubClique = new HashSet<Integer>(currClique);
			IntersectionCliques.add(theClique);
			SubCliques.add(forSubClique);
		 }
		 // dispose all the resources after using them.
		 in.close();
	  } catch (Exception e){
		  System.out.println("!!! CreateUIDGraph, line:"+linesReadCount+": "+e.getMessage());
	  }
   }
   
   @SuppressWarnings({ "deprecation" })
private static void loadAllFriends(){
	  int linesReadCount = 0;
	  try {
		 DataInputStream in = new DataInputStream(new FileInputStream(inputAllFriendsList));
		 String inputLine = null;
		 List<Integer> friends = new ArrayList<Integer>();
		 // in.available() returns 0 if the file does not have more lines.
		 while (in.available() != 0) {
			 inputLine = in.readLine();
			 linesReadCount++;
			 while(!inputLine.contains("*")){
				 friends.add(Integer.parseInt(inputLine));
				 if(in.available() != 0) inputLine = in.readLine();
			 }
		 }
		 //allFriends = new HashSet<Integer>(friends);
		 //System.out.println("allFriends filled");
		 // dispose all the resources after using them.
		 in.close();
	  } catch (Exception e){
		  System.out.println("!!! LoadAllFriends, line:"+linesReadCount+": "+e.getMessage());
	  }
   }
   
   private static void printCliquesToFile(){
      int cliqueCount = 1;
      try {
    	 PrintWriter pw = new PrintWriter(new FileWriter(outputLargeGroupFile));
    	 Iterator<Set<Integer>> collIter = IntersectionCliques.iterator();
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
    			  groupedFriendsInter.put(currUID, true);
    		   }
    		}
    		pw.println();
    		cliqueCount++;
    	 }
    	 int isCoveredCount = 0;
    	 for(int uid: groupedFriendsInter.keySet()){
    		 if(groupedFriendsInter.get(uid)){
  			   isCoveredCount++;
  			}
    	 }
    	 System.out.println("~~~~~ Intersetion Merge Coverage: "+isCoveredCount+" out of "+groupedFriendsInter.size());
    	 pw.close();
    	 System.out.println("Results of Intersection-Merge can be found in: "+outputLargeGroupFile);
      } catch (Exception e){
    	  System.out.println("!!! Problem in PrintCliquesToFile: "+e.getMessage());
    	  System.exit(0);
      }
   }
   private static void printSubCliquesToFile(){
	  int cliqueCount = 1;
	  try {
		 PrintWriter pw = new PrintWriter(new FileWriter(outputSubcliquesFile));
		 Iterator<Set<Integer>> collIter = SubCliques.iterator();
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
				  groupedFriendsSetDiff.put(currUID, true);
			   }
			}
			pw.println();
			cliqueCount++;
		 }
		 int isCoveredCount = 0;
    	 for(int uid: groupedFriendsSetDiff.keySet()){
    		 if(groupedFriendsSetDiff.get(uid)){
  			   isCoveredCount++;
  			}
    	 }
    	 System.out.println("~~~~~ Intersetion Merge Coverage: "+isCoveredCount+" out of "+groupedFriendsSetDiff.size());
		 pw.close();
		 System.out.println("Results of SetDiff-Merge can be found in: "+outputSubcliquesFile);
	  } catch (Exception e){
		 System.out.println("!!! Problem in PrintSubCliquesToFile: "+e.getMessage());
		 System.exit(0);
      }
   }
   private static void printLooseFriendsToFile(){
	  try {
		 PrintWriter pw = new PrintWriter(new FileWriter(outputLooseFile));
		 Iterator<Set<Integer>> collIter = SubCliques.iterator();
		 Iterator<Integer> uidIter;
		 Set<Integer> currClique;
		 int currUID;
		 while (collIter.hasNext()){
			 currClique = collIter.next();
			 uidIter = currClique.iterator();
			 while (uidIter.hasNext()){
				 currUID = uidIter.next();
				 if(uidNames.containsKey(currUID)){
					 pw.println(uidNames.get(currUID));
				 }	
			 }
		 }
		 pw.close();
		 System.out.println("Friends who are not in a clique can be found in: "+outputLooseFile);
	  } catch (Exception e){
		  System.out.println("!!! Problem in PrintLooseFriendsToFile: "+e.getMessage());
		  System.exit(0);
	  }
   }
   @SuppressWarnings("deprecation")
private static void fillNames(){
	  int linesReadCount = 0;
	  try {
		 DataInputStream in = new DataInputStream(new FileInputStream(nameTable));
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
