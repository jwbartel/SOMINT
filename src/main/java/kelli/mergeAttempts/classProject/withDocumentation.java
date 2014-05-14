package kelli.mergeAttempts.classProject;

/**
 * TODO fill allPeopleNames.  create table currentUser_Mutual_Friends
 * TODO run on FB_App data without running areFriends?
 * TODO run this on 5 people: Kelli, Jason, Natalie, Brian, Amanda Walker? 
 * TODO run on 10 people : KB, JC, NR, BB, AW, Julinda, Prasun, 8, 9, 10
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
//MergeCliques.java
@SuppressWarnings("unused")
public class withDocumentation {
   private static Collection<Set<Integer>> cliques = new ArrayList<Set<Integer>>();
   public static Collection<Set<Integer>> mergedCliques = new ArrayList<Set<Integer>>();
   private static HashMap<Integer, String> uidNames = new HashMap<Integer, String>();
   private static HashMap<Integer, Boolean> toRemoveClique = new HashMap<Integer, Boolean>();
   private static float varyPercentage = .15F;   // try .1, .15, .2   checking percentDifferent, so 14% different means 86% the same
   private static float varyLargerPercentage = .25F;
   private static String inputCliquesFile = "data/Kelli/Cliques/JC_BKCliques.txt";
   private static String outputMergedFile = "data/Kelli/Results/JC_15_MergedCliques.txt";
   private static String nameTable = "data/Kelli/FriendshipData/JCPeopleNames.txt";
   private static boolean mergeHappened = true;
   public static void main(String[] args){
	   /*for timing purposes*/
	   long start, passStart, elapsedTime;
	   float elapsedTimeMin;
	   int passNumber = 1;
	   start = System.currentTimeMillis();
	   /*initialize global fields*/
	   fillNames();
	   loadCliques();
	   /*debugging helper output*/
	   System.out.println("original cliques.size: "+cliques.size());
	   passStart = System.currentTimeMillis();
	   combineCliques();
	   elapsedTime = System.currentTimeMillis() - passStart;
	   elapsedTimeMin = elapsedTime/(60*1000F);
	   System.out.println("pass "+passNumber+" cliques.size: "+cliques.size()+"   pass time: "+elapsedTimeMin);
	   while(mergeHappened){
		  /*set up*/
		  mergeHappened = false;
		  toRemoveClique = new HashMap<Integer, Boolean>();
		  for(int i = 0; i < cliques.size(); i++)
			 toRemoveClique.put(i, false);
		  passStart = System.currentTimeMillis();
		  /*prepare cliques for the next run*/
		  cliques = new ArrayList<Set<Integer>>(mergedCliques); 
		  //mergedCliques = new ArrayList<Set<Integer>>();
		  /*run joining algorithm*/
		  combineMergedCliques();
		  /*debugging output*/
		  elapsedTime = System.currentTimeMillis() - passStart;
		  elapsedTimeMin = elapsedTime/(60*1000F);
		  passNumber++;
		  System.out.println("pass "+passNumber+" cliques.size: "+cliques.size()+"   pass time: "+elapsedTimeMin);
	   }
	   /*print out result*/
	   printCliquesToFile();
	   elapsedTime = System.currentTimeMillis() - start;
	   elapsedTimeMin = elapsedTime/(60*1000F);
	   System.out.println("final mergedCliques.size: "+mergedCliques.size());
	   System.out.println("final cliques.size: "+cliques.size()+"   total elapsed time: "+elapsedTimeMin+" min ");
   }

   private static void combineCliques(){
	  int iterationCount = 0;
	  int innerIter;
	  boolean firstOfCliquePairMerged = false;
	  for (Set<Integer> c1: cliques){
		 if(!toRemoveClique.get(iterationCount) /*&& (c1.size() >= 5)*/){
			innerIter = 0;
			for (Set<Integer> c2: cliques){
			   if(!toRemoveClique.get(innerIter) /*&& (c2.size() >= 5)*/){
				  if (!c1.equals(c2)){ 
					 if(c1.size() >= c2.size()) 
						mergeCliques(c1, c2, innerIter);
					 else
						 firstOfCliquePairMerged = mergeCliques(c2, c1, iterationCount); //possible problem...if c1 merged into c5, 
					 										  // i shouldn't be able to try c6 into c1
				  }
				  /*if(firstOfCliquePairMerged) {
					  firstOfCliquePairMerged = false;
					  break; //break forloop from (clique x, clique y) to (clique x+1, clique init)
				  }*/
			   }
			   innerIter++;
			}
		 }
		 iterationCount++;
	  }
   }
   
   private static void combineMergedCliques(){
	  int iterationCount = 0;
	  int innerIter;
	  boolean firstOfCliquePairMerged = false;
	  for (Set<Integer> c1: cliques){
		 innerIter = 0;
		 for (Set<Integer> c2: cliques){
			 if(!c1.equals(c2)){
			   if(c1.size() >= c2.size()) 
				  mergeCliques(c1, c2, innerIter);
			   else
				  firstOfCliquePairMerged = mergeCliques(c2, c1, iterationCount); //possible problem...if c1 merged into c5, 
			 }										    // i shouldn't be able to try c6 into c1
			 /*if(firstOfCliquePairMerged) {
				  firstOfCliquePairMerged = false;
				  break; //break forloop from (clique x, clique y) to (clique x+1, clique init)
			  }*/
			innerIter++;
		 }
		 iterationCount++;
	  }
   }
   
   /**
    * remove all cliques size 2
    */
   private static void preprosessCliques(){
	  Iterator<Set<Integer>> cliqueIter = cliques.iterator();
	  Set<Integer> currClique;
	  while (cliqueIter.hasNext()){ 
		 currClique = cliqueIter.next();
		 if(currClique.size() <= 2)
			 cliqueIter.remove();
	  }
   }
   private static boolean mergeCliques(Set<Integer> c1, Set<Integer> c2, int smallerCliqueNumber){
	  boolean merged = false;
	  float percentage;
	  int numDifferent = 0;
	  int c1Size = c1.size();
	  int c2Size = c2.size();
	  Set<Integer> difference = new HashSet<Integer>();
	  for (int uid: c2){
		 if(!c1.contains(uid)){
			numDifferent++;
			difference.add(uid);
		 }
	  }
	  percentage = (float)numDifferent/(float)c2.size();
	  //change to <= ???
	  if(percentage < varyPercentage){
		 // Remove both cliques, if they are already in mergedCliques.  Only keep the resulting mergedClique
	//    ORIGINAL METHOD
	 	 mergedCliques.remove(c1);
		 mergedCliques.remove(c2);
		 for(int uid: difference){
			c1.add(uid);
		 }
		 mergedCliques.add(c1);
		 mergeHappened = true;
		 merged = true;
		 toRemoveClique.put(smallerCliqueNumber,true);
	  }
	  /*else if(c1Size <=5){ //c1 is always the larger clique
		  //varyLargerPercentage = 1F/(float)c1.size();
		  if(percentage < varyLargerPercentage){
			  mergedCliques.remove(c1);
			  mergedCliques.remove(c2);
			  for(int uid: difference){
				 c1.add(uid);
			  }
			  mergedCliques.add(c1);
			  mergeHappened = true;
			  merged = true;
			  toRemoveClique.put(smallerCliqueNumber,true);
		  }
	  }*/
	  return merged;
   }
   @SuppressWarnings("deprecation")
private static void loadCliques(){
	  int linesReadCount = 0;
	  try {
		 DataInputStream in = new DataInputStream(new FileInputStream(inputCliquesFile));
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
			toRemoveClique.put(cliqueCount++, false);
		 }
		 // dispose all the resources after using them.
		 in.close();
	  } catch (Exception e){
		  System.out.println("!!! CreateUIDGraph, line:"+linesReadCount+": "+e.getMessage());
	  }
   }
   
   private static void printCliquesToFile(){
      int cliqueCount = 1;
      try {
    	 PrintWriter pw = new PrintWriter(new FileWriter(outputMergedFile));
    	 Iterator<Set<Integer>> collIter = mergedCliques.iterator();
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
    		   /*else {
    			  pw.println(currUID);
    		   }*/
    		}
    		pw.println();
    		cliqueCount++;
    	 }
    	 pw.close();
      } catch (Exception e){
    	  System.out.println("!!! Problem in PrintCliquesToFile: "+e.getMessage());
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
