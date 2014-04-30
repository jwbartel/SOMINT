package kelli.mergeAttempts;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;


@SuppressWarnings("unused")
public class implementLCMApaper {
   private static Vector<Integer> vertexList = new Vector<Integer>();
   private static UndirectedGraph<Integer, DefaultEdge> UIDGraph = null;
   //private static Collection<Set<Integer>> cliques;
   private static Set<UndirectedGraph<Integer, DefaultEdge>> LC; //Local Cliques
   private static HashMap<Integer,List<Integer>> AL; //Adjacency List <v, AL(v)>
   //my global variables
   private static HashMap<Integer, String> uidNames = new HashMap<Integer, String>();
   private static String inFileName =  "data/Kelli/FriendshipData/FriendOfFriends.txt";
   private static String printCliques =     "data/Kelli/Cliques/BKCliques.txt";
   private static String printCliqueNames = "data/Kelli/Cliques/BKCliquesNames.txt";
   private static String namesFileName = "data/Kelli/FriendshipData/PeopleNames.txt";
   
   public static void main(String[] args) {
	   LC = new HashSet<UndirectedGraph<Integer, DefaultEdge>>();
	   AL = new HashMap<Integer,List<Integer>>();
	   long start, elapsedTime, getAllTime;
	   start = System.currentTimeMillis();
	   fillNames();
	   UIDGraph = createUIDGraph();
	   //status message
	   System.out.println("created the UIDGraph");
	   //status message
	   System.out.println("running LCMAalgo1");
	   LCMAalgo1();
	   //
	 //  cliques = BKcliqueFind.getAllMaximalCliques();
	   getAllTime = System.currentTimeMillis() - start;
	   float elapsedTimeMin = getAllTime/(60*1000F);
	   //status message
	   System.out.println("found AllMaximalCliques ("+LC.size()+") in "+elapsedTimeMin+" minutes");
	   printCliquesToFile();
	   elapsedTime = System.currentTimeMillis() - start;
	   elapsedTimeMin = elapsedTime/(60*1000F);
	   //status message: all done :)
	   System.out.println("printedCliques to file...all done in "+elapsedTimeMin+ "!!!");
	}
   /**
    * for each vertex v in G do
    * 	Construct v's Adjacency List AL(v)
    * 	Sort AL(v) according to their degree in v's neighborhood (smallest first);
    * end for
    */
   private static void createAL(){
	   Set<Integer> vertSet = UIDGraph.vertexSet();
	   Iterator<Integer> vertIter = vertSet.iterator();
	   Iterator<DefaultEdge> edgeIter;
	   DefaultEdge edge;
	   int v, target, e;
	   Set<DefaultEdge> edgeSet;
	   while (vertIter.hasNext()){
		   v = vertIter.next();
		   AdjacencyList al = new AdjacencyList();
		   edgeSet = UIDGraph.edgesOf(v);
		   edgeIter = edgeSet.iterator();
		   while(edgeIter.hasNext()){
			   edge = edgeIter.next();
			   target = UIDGraph.getEdgeTarget(edge);
			   if(target == v)
				   target = UIDGraph.getEdgeSource(edge);
			   al.add(target, UIDGraph.degreeOf(target));
		   }
		   //System.out.print("vertex: "+v+" AL: ");
		   List<Integer> adjacencyList = al.returnAL();
		   //System.out.println();
		   AL.put(v, adjacencyList);
	   }
   }
   private static float computeDensity(UndirectedGraph<Integer, DefaultEdge> gPrime){
	   int E = gPrime.edgeSet().size();
	   int V = gPrime.vertexSet().size();
	   return (2*E)/(V*(V-1));
   }
   private static float computeDensity(int vSize, int eSize){
	   return (2*eSize)/(vSize*(vSize-1));
   }
   private static void LCMAalgo1(){
	  //steps 1 - 6
	  float lambda;
	  createAL();
	  int vPrime = 0;
	  for (int vertex: UIDGraph.vertexSet()){
		List<Integer> al = AL.get(vertex);
		lambda = computeDensity(al.size(), edgeCount(al));
		boolean stop = false;
		while(stop!=true){
		  //Find the vertex v' with minimum degree in AL(v), v' = arg min d(vi);
		  //List<Integer> al = AL.get(vertex); 	
		  if(al.size() > 0){
			 vPrime = al.get(0);
			 float lambdaPrime = computeDensity(al.size()-1, edgeCount(al)-UIDGraph.edgesOf(vPrime).size());
			 if(lambdaPrime > lambda){
				 al.remove(vPrime);
				 //   * 		update the degree for vertices that connected with v' in AL(v);     ??????
				 lambda = lambdaPrime;
			 }
			 else stop = true;				 
		  }
		  else System.out.println("tried to get vertex from AL of size 0");
		}
	  }
	   /** for each graph g in LC do 
		    * 	if |g| <= 2 then 
		    * 		LC = LC - {g};
		    * 	end if
		    * end for
		    * END
		    */
   }
   private static int edgeCount(List<Integer> AL){
		int count = 0;
		for(int i=0; i<AL.size(); i++){
			count = count+ UIDGraph.edgesOf(AL.get(i)).size();
		}
		return count;
	}
   //prints the maximal cliques to output files printCliques and printCliqueNames
   //printCliques holds the clique information to be used in the next step, MergeCliques.java
   //printCliqueNames hold the human-readable cliques for human convenience.  This information is not used in
   //  other parts of the project.
   private static void printCliquesToFile(){
      int cliqueCount = 1;
	  try {
	     PrintWriter pw = new PrintWriter(new FileWriter(printCliques));
	     PrintWriter pw2 = new PrintWriter(new FileWriter(printCliqueNames));
	     Iterator<UndirectedGraph<Integer, DefaultEdge>> collectionIter = LC.iterator();
	     Iterator<Integer> uidIter;
	     UndirectedGraph<Integer, DefaultEdge> currClique;
	     Set<Integer> vertSet;
	     int currUID;
	     while (collectionIter.hasNext()){
	    	pw.println("Clique: "+cliqueCount);
	    	pw2.println("Clique: "+cliqueCount);
	    	currClique = collectionIter.next();
	    	vertSet = currClique.vertexSet();
	    	uidIter = vertSet.iterator();
	    	while (uidIter.hasNext()){
	    	   currUID = uidIter.next();
	    	   if(uidNames.containsKey(currUID)){
	    		   pw2.println(uidNames.get(currUID));
	    		   pw.println(currUID);
	    	   }
	    	   else {
	    		   pw.println(currUID);
	    		   pw2.println(currUID);
	    	   }
	    	}
			pw.println("*");
			pw2.println();
			cliqueCount++;
		  }
		  pw.close();
	   } catch (Exception e){
		   System.out.println("!!! Problem in PrintCliquesToFile: "+e.getMessage());
		   System.exit(0);
	   }
   }
   //the graph is made of vertices which are uniquely identified via unique identification numbers
   //this method creates an undirected graph from the friendship pairs in file inFileName
   @SuppressWarnings("deprecation")
private static UndirectedGraph<Integer, DefaultEdge> createUIDGraph()
   {
	  UndirectedGraph<Integer, DefaultEdge> g = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

	  int linesReadCount = 0;
	  try {
		 DataInputStream in = new DataInputStream(new FileInputStream(inFileName));
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
   //the graph is made of vertices which are uniquely identified via unique identification numbers
   //this function enters the names that correspond to the identifications numbers into a table
   //  to be used for printing human-legible names
   @SuppressWarnings("deprecation")
private static void fillNames(){
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
}
class AdjacencyList {
	List<Integer> AL = new ArrayList<Integer>();
	private HashMap<Integer, Integer> vertToDeg = new HashMap<Integer, Integer>();
	
	public void add(int vertex, int degree){
		vertToDeg.put(vertex, degree);
		addVertexInOrder(vertex, degree);
	}
	public void remove(int vertex){
		AL.remove(vertex);
		
	}
	private void addVertexInOrder(int v, int d){
		if(AL.isEmpty())
			AL.add(v);
		else{
			int i = 0;
			while (d > vertToDeg.get(v) && i < AL.size()) i++;
			if(i == AL.size())
				AL.add(v);
			else{
				AL.add(i, v);
			}
		}
	}
	public List<Integer> returnAL(){
		return AL;
	}
}
