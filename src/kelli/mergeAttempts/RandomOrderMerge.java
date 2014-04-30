package kelli.mergeAttempts;
/**DIAwithRelationshipCount = Do It All (Intersection and Set Diff merges)
 *   and keep track of the number of people each person relates to in each returned clique*/ 
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import kelli.mergeAttempts.incremental.ATrace;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
//@SuppressWarnings({"static-access"})
@SuppressWarnings({"static-access","unused"})
public class RandomOrderMerge {
   public static Collection<Set<Integer>> mergedCliques = new ArrayList<Set<Integer>>();
   private static UndirectedGraph<Integer, DefaultEdge> origGraph = null;
   //private static HashMap<Integer, String> uidNames = new HashMap<Integer, String>();
   private static IOFunctions ioHelp = new IOFunctions();
   private static String inputCliquesFile;
   private static String Participant = "PD";
   private static String OMEGA       = "20";
   private static double threshold   = .2;
   public static void main(String[] args){
	  long start, elapsedTime;
	  float elapsedTimeMin;
	  start = System.currentTimeMillis();
	  System.out.println("~~~~~~~~~~~~~~~~~~~SET UP~~~~~~~~~~~~~~~~~~~~~~~~~~");
	  doSetup();
	  doIntersection();
	  //doSetDiff();
	  //doAvgConnection();
	  //doMinConnection();
	  //doConnection();
	  //doDensity();
	  System.out.println("~~~~~~~~~~~~~~~~~~~~FINAL~~~~~~~~~~~~~~~~~~~~~~~~~~");
//		   postprocessCliques(); 
//		   System.out.println("after removing cliques size <= 3 cliques.size: "+cliques.size());
	  elapsedTime = System.currentTimeMillis() - start;
	  elapsedTimeMin = elapsedTime/(60*1000F);
	  System.out.println("   total elapsed time: "+elapsedTimeMin+" min ");
	  System.out.println("All done!");
   }
   static Vector<ATrace> mergeTrace = new Vector<ATrace>();
   public static Vector<ATrace> getMergeTrace(){
	   return mergeTrace;
   }
   
   
   private static void doSetup(){
	  inputCliquesFile = "data/Kelli/Cliques/UID/"+Participant+"_BKCliques.txt";
//	  inputCliquesFile = "data/Kelli/Cliques/UID/BKCliques.txt";
	  String inputFriendshipData = "data/Kelli/FriendshipData/"+Participant+"FriendOfFriends.txt";
	  origGraph = ioHelp.createUIDGraph(inputFriendshipData);
	  ioHelp.fillNames("data/Kelli/FriendshipData/PeopleNames.txt");
   }
   
   private static void doIntersection(){
	  System.out.println("~~~~~~~~~~~~~~~~Intersection~~~~~~~~~~~~~~~~~~~~~~~");
	  Collection<Set<Integer>> IntersectionCliques = new ArrayList<Set<Integer>>();
	  IntersectionCliques = ioHelp.loadCliques(inputCliquesFile);
	  preprocessCliques(IntersectionCliques);
	  IntersectionV1.doIntersectionMerges(IntersectionCliques);
	  String outputLargeGroupFile     = "data/Kelli/FriendGrouperResults/Random/"+Participant+"_LargeGroups.txt";
	  ioHelp.printCliquesToFile(outputLargeGroupFile, IntersectionCliques);
   }
   private static void doSetDiff(){
	  System.out.println("~~~~~~~~~~~~~~~~~~Set Diff~~~~~~~~~~~~~~~~~~~~~~~~~");
	  Collection<Set<Integer>> SetDiffCliques = new ArrayList<Set<Integer>>();
	  SetDiffCliques = ioHelp.loadCliques(inputCliquesFile);
	  preprocessCliques(SetDiffCliques);
	  SetDiffV2.doSetDiffMerges(SetDiffCliques, .2F);
	  String outputFile = "data/Kelli/FriendGrouperResults/Random/"+Participant+"_Subcliques.txt";
	  ioHelp.printCliquesToFile(outputFile, SetDiffCliques);
   }
   private static void doAvgConnection(){
	  System.out.println("~~~~~~~~~~~~~~~~~~Avg Connection~~~~~~~~~~~~~~~~~~~~~~~~~");
	  Collection<Set<Integer>> AvgConnCliques = new ArrayList<Set<Integer>>();
	  AvgConnCliques = ioHelp.loadCliques(inputCliquesFile);
	  preprocessCliques(AvgConnCliques);
	  AvgConnectionMerge.doConnectionCliques(AvgConnCliques, origGraph, threshold);
	  String outputFile  = "data/Kelli/FriendGrouperResults/Random/"+Participant+"_"+OMEGA+"_AvgConnection.txt";
	  ioHelp.printCliquesToFile(outputFile, AvgConnCliques);
   }
   private static void doMinConnection(){
	  System.out.println("~~~~~~~~~~~~~~~~~~Min Connection~~~~~~~~~~~~~~~~~~~~~~~~~");
	  Collection<Set<Integer>> MinConnCliques = new ArrayList<Set<Integer>>();
	  MinConnCliques = ioHelp.loadCliques(inputCliquesFile);
	  preprocessCliques(MinConnCliques);
	  MinConnectionMerge.doConnectionCliques(MinConnCliques, origGraph, threshold);
	  String outputFile  = "data/Kelli/FriendGrouperResults/Random/"+Participant+"_"+OMEGA+"_MINConnection.txt";
	  ioHelp.printCliquesToFile(outputFile, MinConnCliques);
   }

   private static void doConnection(){
	  System.out.println("~~~~~~~~~~~~~~~~~~MinV2 Connection~~~~~~~~~~~~~~~~~~~~~~~~~");
	  Collection<Set<Integer>> MinConnCliques = new ArrayList<Set<Integer>>();
	  MinConnCliques = ioHelp.loadCliques(inputCliquesFile);
	  preprocessCliques(MinConnCliques);
	  ConnectionMerge.doConnectionCliques(MinConnCliques, origGraph, threshold);
	  String outputFile   =   "data/Kelli/FriendGrouperResults/Random/"+Participant+"_"+OMEGA+"_Connection.txt";
	  ioHelp.printCliquesToFile(outputFile, MinConnCliques);
   }
   private static void doDensity(){
	  System.out.println("~~~~~~~~~~~~~~~~~~Density Merge~~~~~~~~~~~~~~~~~~~~~~~~~");
	  Collection<Set<Integer>> Cliques = new ArrayList<Set<Integer>>();
	  Cliques = ioHelp.loadCliques(inputCliquesFile);
	  preprocessCliques(Cliques);
	  DensityMerge.doMerges(Cliques, origGraph, threshold);
	  String outputFile   =   "data/Kelli/FriendGrouperResults/Random/"+Participant+"_"+OMEGA+"_Density.txt";
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
	   /**
	    * remove all cliques size <= 3
	   */
	  private static void postprocessCliques(Collection<Set<Integer>> cliques){
		  Iterator<Set<Integer>> cliqueIter = cliques.iterator();
		  Set<Integer> currClique;
		  while(cliqueIter.hasNext()){
		     currClique = cliqueIter.next();
			 if(currClique.size() <=3){
				cliqueIter.remove();
			 }
		  }
	  }
   
}
