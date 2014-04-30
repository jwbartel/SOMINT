package kelli.FriendGrouper;
/**DIAwithRelationshipCount = Do It All (Intersection and Set Diff merges)
 *   and keep track of the number of people each person relates to in each returned clique*/ 
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import kelli.mergeAttempts.IOFunctions;
import kelli.mergeAttempts.IntersectionV1;
import kelli.mergeAttempts.SetDiffV1;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

@SuppressWarnings({"static-access"})
//@SuppressWarnings({"static-access","unused"})
public class ROMerge {
   public static Collection<Set<Integer>> mergedCliques = new ArrayList<Set<Integer>>();
   //private static HashMap<Integer, String> uidNames = new HashMap<Integer, String>();
   private static UndirectedGraph<Integer, DefaultEdge> origGraph = null;
   private static IOFunctions ioHelp = new IOFunctions();
   private static String inputCliquesFile;
   private static String Participant;
   public static void doMerges(String participant){
	  long start, elapsedTime;
	  float elapsedTimeMin;
	  Participant = participant;
	  start = System.currentTimeMillis();
	  System.out.println("~~~~~~~~~~~~~~~~~~~SET UP~~~~~~~~~~~~~~~~~~~~~~~~~~");
	  doSetup();
	  doIntersection();
	  doSetDiff();
	  System.out.println("~~~~~~~~~~~~~~~~~~~~FINAL~~~~~~~~~~~~~~~~~~~~~~~~~~");
	  elapsedTime = System.currentTimeMillis() - start;
	  elapsedTimeMin = elapsedTime/(60*1000F);
	  System.out.println("   total elapsed time: "+elapsedTimeMin+" min ");
	  System.out.println("All done!");
   }
   private static void doSetup(){
	  inputCliquesFile = "data/Kelli/Cliques/UID/"+Participant+"_BKCliques.txt";
	  String inputFriendshipData = "data/Kelli/FriendshipData/"+Participant+"FriendOfFriends.txt";
	  origGraph = ioHelp.createUIDGraph(inputFriendshipData);
	  ioHelp.fillNames("data/Kelli/FriendshipData/PeopleNames.txt");
   }
   private static Set<Integer> findLargeGroups(Collection<Set<Integer>> intersectionCliques){
	   Iterator<Set<Integer>> cliqueIter = intersectionCliques.iterator();
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
	   Set<Integer> vSet = origGraph.vertexSet();
	   Set<DefaultEdge> eSet;
	   int source, target;
	   Set<Integer> largeGroups = findLargeGroups(intersectionCliques);
	   for(int v: vSet){
		   if(largeGroups.contains(v)){
			   subGraph.addVertex(v);
			   eSet = origGraph.edgesOf(v);
			   for(DefaultEdge e: eSet){
				   source = origGraph.getEdgeSource(e);
				   target = origGraph.getEdgeTarget(e);
				   if(largeGroups.contains(source)){
					   if(largeGroups.contains(target))
						   subGraph.addEdge(source, target);
				   }
			   }
		   }
	   }
	   return subGraph;
   }
   private static void findCliquesInLargeGroups(Collection<Set<Integer>> intersectionCliques){
	   System.out.println("~~~~~~~~~~~~~~~~~~Find Subcliques~~~~~~~~~~~~~~~~~~~~~~~~~");
	   UndirectedGraph<Integer, DefaultEdge> subGraph = findSubgraph(intersectionCliques);
	   BronKerboschCliqueFinder<Integer, DefaultEdge> BKcliqueFind = new BronKerboschCliqueFinder<Integer, DefaultEdge>(subGraph);
	   Collection<Set<Integer>> SetDiffCliques = new ArrayList<Set<Integer>>();
	   SetDiffCliques = BKcliqueFind.getAllMaximalCliques();
	   preprocessCliques(SetDiffCliques);
	   SetDiffV1.doSetDiffMerges(SetDiffCliques);
	   String outputFile = "data/Kelli/FriendGrouperResults/Random/"+Participant+"_Subcliques.txt";
	   ioHelp.printCliquesToFile(outputFile, SetDiffCliques);
   }
   private static void doIntersection(){
	  System.out.println("~~~~~~~~~~~~~~~~Intersection~~~~~~~~~~~~~~~~~~~~~~~");
	  Collection<Set<Integer>> IntersectionCliques = new ArrayList<Set<Integer>>();
	  IntersectionCliques = ioHelp.loadCliques(inputCliquesFile);
	  preprocessCliques(IntersectionCliques);
	  IntersectionV1.doIntersectionMerges(IntersectionCliques);
	  String outputLargeGroupFile     = "data/Kelli/FriendGrouperResults/Random/"+Participant+"_LargeGroups.txt";
	  ioHelp.printCliqueNamesToFile(outputLargeGroupFile, IntersectionCliques);
	  findCliquesInLargeGroups(IntersectionCliques);
   }
   private static void doSetDiff(){
	  System.out.println("~~~~~~~~~~~~~~~~~~Set Diff~~~~~~~~~~~~~~~~~~~~~~~~~");
	  Collection<Set<Integer>> SetDiffCliques = new ArrayList<Set<Integer>>();
	  SetDiffCliques = ioHelp.loadCliques(inputCliquesFile);
	  preprocessCliques(SetDiffCliques);
	  SetDiffV1.doSetDiffMerges(SetDiffCliques);
	  String outputFile = "data/Kelli/FriendGrouperResults/Random/"+Participant+"_Subcliques.txt";
	  ioHelp.printCliquesToFile(outputFile, SetDiffCliques);
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
