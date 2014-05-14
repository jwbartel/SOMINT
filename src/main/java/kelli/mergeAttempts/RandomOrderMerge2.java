package kelli.mergeAttempts;
/**DIAwithRelationshipCount = Do It All (Intersection and Set Diff merges)
 *   and keep track of the number of people each person relates to in each returned clique*/ 
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
@SuppressWarnings({"static-access","unused"})
public class RandomOrderMerge2 {
   private static Collection<Set<Integer>> IntersectionCliques = new ArrayList<Set<Integer>>();
   private static Collection<Set<Integer>> SubCliques = new ArrayList<Set<Integer>>();
   public static Collection<Set<Integer>> mergedCliques = new ArrayList<Set<Integer>>();
   private static UndirectedGraph<Integer, DefaultEdge> origGraph = null;
   //private static HashMap<Integer, String> uidNames = new HashMap<Integer, String>();
   private static IOFunctions ioHelp = new IOFunctions();
   private static HashMap<Integer, Boolean> toRemoveClique;// = new HashMap<Integer, Boolean>();
   private static String inputFriendshipData  =              "data/Kelli/FriendshipData/PDFriendOfFriends.txt";
   private static String inputCliquesFile     =                 "data/Kelli/Cliques/UID/PD_BKCliques.txt";
   private static String outputLargeGroupFile = "data/Kelli/FriendGrouperResults/Random/PD_LargeGroups.txt";
   private static String outputSubcliquesFile = "data/Kelli/FriendGrouperResults/Random/PD_Subcliques.txt";
   private static String outputLooseFile      = "data/Kelli/FriendGrouperResults/Random/PD_Loose.txt"; 
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
	  //System.out.println("~~~~~~~~~~~~~~~~~~Set Diff~~~~~~~~~~~~~~~~~~~~~~~~~");
	  //doSetDiffMerges();
	  System.out.println("~~~~~~~~~~~~~~~~~~~~FINAL~~~~~~~~~~~~~~~~~~~~~~~~~~");
//		   postprocessCliques(); 
//		   System.out.println("after removing cliques size <= 3 cliques.size: "+cliques.size());
	  elapsedTime = System.currentTimeMillis() - start;
	  elapsedTimeMin = elapsedTime/(60*1000F);
	  System.out.println("   total elapsed time: "+elapsedTimeMin+" min ");
	  System.out.println("All done!");
   }
   private static void doSetup(){
	  origGraph = ioHelp.createUIDGraph(inputFriendshipData);
	  ioHelp.fillNames(nameTable);
	  IntersectionCliques = ioHelp.loadCliques(inputCliquesFile);
	  SubCliques = ioHelp.loadCliques(inputCliquesFile);
	  System.out.println("original cliques.size: "+IntersectionCliques.size());
	  preprocessCliques();
	  System.out.println("after removing cliques size 2 cliques.size: "+IntersectionCliques.size());
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
	  ioHelp.printCliquesToFile(outputLargeGroupFile, IntersectionCliques);
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
		  ioHelp.printCliquesToFile(outputSubcliquesFile, SubCliques);
	   }
   private static void combineIntersectionCliques(){
	  int iterationCount = 0; int innerIter;
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
						 firstOfCliquePairMerged = mergeIntersectionCliques(c2, c1, iterationCount); 
					 //possible problem...if c1 merged into c5, i shouldn't be able to try c6 into c1
				  }
			   }
			   if(firstOfCliquePairMerged) { 
				   firstOfCliquePairMerged = false;
				   break; } //break forloop
			   innerIter++;
			}
		 }
		 /*if(firstOfCliquePairMerged) {
			firstOfCliquePairMerged = false;
			break; //break forloop from (clique x, clique y) to (clique x+1, clique init)
		 }*/
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
	  int numDifferent = 0; int intersectionCount = 0;
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
	   
}
