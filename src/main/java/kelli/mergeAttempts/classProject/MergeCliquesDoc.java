package kelli.mergeAttempts.classProject;

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
public class MergeCliquesDoc {
   private static Collection<Set<Integer>> cliques = new ArrayList<Set<Integer>>();
   public static Collection<Set<Integer>> mergedCliques = new ArrayList<Set<Integer>>();
   private static HashMap<Integer, String> uidNames = new HashMap<Integer, String>();
   private static HashMap<Integer, Boolean> toRemoveClique = new HashMap<Integer, Boolean>();
   private static float varyPercentage = .15F;   // try .1, .15, .2   checking percentDifferent, so 14% different means 86% the same
   private static String inputCliquesFile = "data/Kelli/Cliques/KB_BKCliques.txt";
   private static String outputMergedFile = "data/Kelli/Results/KB_15_MergedCliques.txt";
   private static String nameTable = "data/Kelli/FriendshipData/PeopleNames.txt";
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
	  /* while(mergeHappened){
		  mergeHappened = false;
		  toRemoveClique = new HashMap<Integer, Boolean>();
		  for(int i = 0; i < cliques.size(); i++)
			 toRemoveClique.put(i, false);
		  passStart = System.currentTimeMillis();
		  cliques = new ArrayList<Set<Integer>>(mergedCliques); 
		  //mergedCliques = new ArrayList<Set<Integer>>();
		  combineMergedCliques();
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
	   System.out.println("total elapsed time: "+elapsedTimeMin+" min ");
   }

   private static void combineCliques(){
	  int iterationCount = 0;
	  int innerIter;
	  for (Set<Integer> c1: cliques){
		 if(!toRemoveClique.get(iterationCount)){
			innerIter = 0;
			for (Set<Integer> c2: cliques){
			   if(!toRemoveClique.get(innerIter) ){
				  if (!c1.equals(c2)){ 
					 if(c1.size() >= c2.size()) 
						mergeCliques(c1, c2, innerIter);
					 else
						 mergeCliques(c2, c1, iterationCount); 
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
	  for (Set<Integer> c1: cliques){
		 innerIter = 0;
		 for (Set<Integer> c2: cliques){
			 if(!c1.equals(c2)){
			   if(c1.size() >= c2.size()) 
				  mergeCliques(c1, c2, innerIter);
			   else
				  mergeCliques(c2, c1, iterationCount);  
			 }										    
			 innerIter++;
		 }
		 iterationCount++;
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
	  if(percentage < varyPercentage){
		 // Remove both cliques, if they are already in mergedCliques.  Only keep the resulting mergedClique
	 	 mergedCliques.remove(c1);
		 mergedCliques.remove(c2);
		 for(int uid: difference){
			c1.add(uid);
		 }
		 mergedCliques.add(c1);
		// mergeHappened = true;    //uncomment this line to try running more than two passes on the algorithm
		 merged = true;
		 toRemoveClique.put(smallerCliqueNumber,true);
	  }
	  return merged;
   }
   // mergeCliques compares the items in each clique.  integer comparison is faster and more reliable (as the
   //   integers are unique identification numbers, so there concern for two friends with name "John Smith")
   //   thus the cliques are stored as a collection of sets of integers.
   // the information is retrieved from inputCliquesFile, which is printCliques file from cliqueFinder.java
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
   //only print the human legible names available in the table of names.
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
		 System.out.println("!!!Error in "+nameTable+", line:"+linesReadCount+": "+e.getMessage());
	  }
   }
}
