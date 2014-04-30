package kelli.FriendGrouper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import kelli.mergeAttempts.AvgConnectionMerge;
import kelli.mergeAttempts.ConnectionMerge;
import kelli.mergeAttempts.DensityMerge;
import kelli.mergeAttempts.IOFunctions;
import kelli.mergeAttempts.IntersectionV1;
import kelli.mergeAttempts.MinConnectionMerge;
import kelli.mergeAttempts.SetDiffV1;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

@SuppressWarnings({  "static-access" })
public class SortThenMerge {
   private static UndirectedGraph<Integer, DefaultEdge> origGraph = null;
   public static Collection<Set<Integer>> mergedCliques = new ArrayList<Set<Integer>>();
   private static IOFunctions ioHelp = new IOFunctions();
   public static HashMap<Integer, Boolean> groupedFriendsInter = new HashMap<Integer, Boolean>();
   public static HashMap<Integer, Boolean> groupedFriendsSetDiff = new HashMap<Integer, Boolean>();
   private static String inputCliquesFile;
   private static String Participant = "KB";
   private static String OMEGA       = "40";
   private static double threshold   = .4;
   public static void main(String[] args){
	   long start, elapsedTime;
	   float elapsedTimeMin;
	   start = System.currentTimeMillis();
	   System.out.println("~~~~~~~~~~~~~~~~~~~SET UP~~~~~~~~~~~~~~~~~~~~~~~~~~");
	   doSetup();
	   //doIntersection();
	   //doSetDiff();
	   doAvgConnection();
	   doMinConnection();
	   doConnection();
	   //doDensity();
	   System.out.println("~~~~~~~~~~~~~~~~~~~~FINAL~~~~~~~~~~~~~~~~~~~~~~~~~~");
	   elapsedTime = System.currentTimeMillis() - start;
	   elapsedTimeMin = elapsedTime/(60*1000F);
	   System.out.println("   total elapsed time: "+elapsedTimeMin+" min ");
	   System.out.println("All done!");
   }
   private static void doSetup(){
	  inputCliquesFile = "data/Kelli/Cliques/UID/"+Participant+"_BKCliques.txt";
//		  inputCliquesFile = "data/Kelli/Cliques/UID/BKCliques.txt";
	  String inputFriendshipData = "data/Kelli/FriendshipData/"+Participant+"FriendOfFriends.txt";
	  origGraph = ioHelp.createUIDGraph(inputFriendshipData);
	  ioHelp.fillNames("data/Kelli/FriendshipData/PeopleNames.txt");
   }
   private static void sortCliques(Collection<Set<Integer>> toSort){
	   Collection<Set<Integer>> CliqueSort = new ArrayList<Set<Integer>>();
	   Collection<Set<Integer>> InterCopy = new ArrayList<Set<Integer>>(toSort);
	   int largestSetSize = 0;
	   int sizes[] = new int[toSort.size()];
	   int arrayIndex = 0;
	   for(Set<Integer> i: toSort){
		   sizes[arrayIndex] = i.size();
		   if(i.size()>largestSetSize)
			   largestSetSize = i.size();
		   arrayIndex++;
	   }
	   Arrays.sort(sizes);
	   largestSetSize = sizes[--arrayIndex];
	   while(InterCopy.size()!=0){
		   for(Set<Integer> i: toSort){
			   if(i.size()==largestSetSize){
				   CliqueSort.add(i);
				   InterCopy.remove(i);
			   }   
		   }
		   toSort = InterCopy;
		   InterCopy = new ArrayList<Set<Integer>>(toSort);
		   while(arrayIndex>0 && sizes[arrayIndex]>=largestSetSize){
			   arrayIndex--;
		   } 
		   largestSetSize = sizes[arrayIndex]; 
	   }
	   toSort = new ArrayList<Set<Integer>> (CliqueSort);
   }
   
   private static void doIntersection(){
	  System.out.println("~~~~~~~~~~~~~~~~Intersection~~~~~~~~~~~~~~~~~~~~~~~");
	  Collection<Set<Integer>> IntersectionCliques = new ArrayList<Set<Integer>>();
	  IntersectionCliques = ioHelp.loadCliques(inputCliquesFile);
	  preprocessCliques(IntersectionCliques);
	  sortCliques(IntersectionCliques);
	  IntersectionV1.doIntersectionMerges(IntersectionCliques);
	  String outputLargeGroupFile     = "data/Kelli/FriendGrouperResults/Sorted/"+Participant+"_LargeGroups.txt";
	  ioHelp.printCliquesToFile(outputLargeGroupFile, IntersectionCliques);
   }
   private static void doSetDiff(){
	  System.out.println("~~~~~~~~~~~~~~~~~~Set Diff~~~~~~~~~~~~~~~~~~~~~~~~~");
	  Collection<Set<Integer>> SetDiffCliques = new ArrayList<Set<Integer>>();
	  SetDiffCliques = ioHelp.loadCliques(inputCliquesFile);
	  preprocessCliques(SetDiffCliques);
	  sortCliques(SetDiffCliques);
	  SetDiffV1.doSetDiffMerges(SetDiffCliques);
	  String outputFile = "data/Kelli/FriendGrouperResults/Sorted/"+Participant+"_Subcliques.txt";
	  ioHelp.printCliquesToFile(outputFile, SetDiffCliques);
   }
   private static void doAvgConnection(){
	  System.out.println("~~~~~~~~~~~~~~~~~~Avg Connection~~~~~~~~~~~~~~~~~~~~~~~~~");
	  Collection<Set<Integer>> AvgConnCliques = new ArrayList<Set<Integer>>();
	  AvgConnCliques = ioHelp.loadCliques(inputCliquesFile);
	  preprocessCliques(AvgConnCliques);
	  sortCliques(AvgConnCliques);
	  AvgConnectionMerge.doConnectionCliques(AvgConnCliques, origGraph, threshold);
	  String outputFile  = "data/Kelli/FriendGrouperResults/Sorted/"+Participant+"_"+OMEGA+"_AvgConnection.txt";
	  ioHelp.printCliquesToFile(outputFile, AvgConnCliques);
   }
   private static void doMinConnection(){
	  System.out.println("~~~~~~~~~~~~~~~~~~Min Connection~~~~~~~~~~~~~~~~~~~~~~~~~");
	  Collection<Set<Integer>> MinConnCliques = new ArrayList<Set<Integer>>();
	  MinConnCliques = ioHelp.loadCliques(inputCliquesFile);
	  preprocessCliques(MinConnCliques);
	  sortCliques(MinConnCliques);
	  MinConnectionMerge.doConnectionCliques(MinConnCliques, origGraph, threshold);
	  String outputFile  = "data/Kelli/FriendGrouperResults/Sorted/"+Participant+"_"+OMEGA+"_MINConnection.txt";
	  ioHelp.printCliquesToFile(outputFile, MinConnCliques);
   }
   private static void doConnection(){
	  System.out.println("~~~~~~~~~~~~~~~~~~~ Connection ~~~~~~~~~~~~~~~~~~~~~~~~~~");
	  Collection<Set<Integer>> ConnCliques = new ArrayList<Set<Integer>>();
	  ConnCliques = ioHelp.loadCliques(inputCliquesFile);
	  preprocessCliques(ConnCliques);
	  sortCliques(ConnCliques);
	  ConnectionMerge.doConnectionCliques(ConnCliques, origGraph, threshold);
	  String outputFile   =   "data/Kelli/FriendGrouperResults/Sorted/"+Participant+"_"+OMEGA+"_Connection.txt";
	  ioHelp.printCliquesToFile(outputFile, ConnCliques);
   }
   private static void doDensity(){
	  System.out.println("~~~~~~~~~~~~~~~~~~Density Merge~~~~~~~~~~~~~~~~~~~~~~~~~");
	  Collection<Set<Integer>> Cliques = new ArrayList<Set<Integer>>();
	  Cliques = ioHelp.loadCliques(inputCliquesFile);
	  preprocessCliques(Cliques);
	  sortCliques(Cliques);
	  DensityMerge.doMerges(Cliques, origGraph, threshold);
	  String outputFile   =   "data/Kelli/FriendGrouperResults/Sorted/"+Participant+"_"+OMEGA+"_Density.txt";
	  ioHelp.printCliquesToFile(outputFile, Cliques);
   }

   /**
    * remove all cliques size 2
    */
   private static void preprocessCliques(Collection<Set<Integer>> cliques){
	  Iterator<Set<Integer>> cliqueIter = cliques.iterator();
	  Set<Integer> currClique;
	  while (cliqueIter.hasNext()){ 
		 currClique = cliqueIter.next();
		 if(currClique.size() <= 2)
			cliqueIter.remove();
	  }
   }
   
}
