package kelli.mergeAttempts;

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

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

@SuppressWarnings("unused")
public class LCMA {
	//algorithm used by http://ai.stanford.edu/~csfoo/papers/LCMA.pdf
   private static Collection<Set<Integer>> LCMACliques = new ArrayList<Set<Integer>>();
   private static Set<UndirectedGraph<Integer, DefaultEdge>> LC; //Local Cliques
   public static Collection<Set<Integer>> mergedCliques = new ArrayList<Set<Integer>>();
   public static Set<Integer> allFriends = new HashSet<Integer>();
   public static HashMap<Integer, Boolean> groupedFriends = new HashMap<Integer, Boolean>();
   private static HashMap<Integer, String> uidNames = new HashMap<Integer, String>();
   private static String inputCliquesFile            =       "data/Kelli/Cliques/TH_BKCliques.txt";
   private static String outputLCMAGroupFile 	 	 =   "data/Kelli/LCMAResults/TH_08_CMAGroups.txt";
   private static String outputLooseFile    		 =   "data/Kelli/LCMAResults/TH_08_Loose.txt"; 
   private static String nameTable = "data/Kelli/FriendshipData/PeopleNames.txt";
   // "Given a set LC of local cliques, our LCMA algorithm tries to merge the local cliques that have a similarity greater than a threshold omega"
   private static double omega = 0.80; 			//threshold value, varies in their paper
   public static void main(String[] args){
	   long start, elapsedTime;
	   float elapsedTimeMin;
	   start = System.currentTimeMillis();
	   System.out.println("~~~~~~~~~~~~~~~~~~~SET UP~~~~~~~~~~~~~~~~~~~~~~~~~~");
	   doSetup();
	   System.out.println("~~~~~~~~~~~~~~~~LCMA~~~~~~~~~~~~~~~~~~~~~~~");
	   algo2();
	   System.out.println("~~~~~~~~~~~~~~~~~~~~FINAL~~~~~~~~~~~~~~~~~~~~~~~~~~");
	   printLooseFriendsToFile();
//	   postprocessCliques(); 
//	   System.out.println("after removing cliques size <= 3 cliques.size: "+cliques.size());
	   elapsedTime = System.currentTimeMillis() - start;
	   elapsedTimeMin = elapsedTime/(60*1000F);
	   System.out.println("   total elapsed time: "+elapsedTimeMin+" min ");
	   System.out.println("All done!");
   }
   //density of the graph G' = (V', E') is defined as cc(G') = (2*|E'|)/(|V'|*|V'-1|)
   private static double computeDensity(UndirectedGraph<Integer, DefaultEdge> gPrime){
	   double E = gPrime.edgeSet().size();
	   double V = gPrime.vertexSet().size();
	   return (2*E)/(V*(V-1));
   }
   private static float computeDensity(int vSize, int eSize){
	   return (2*eSize)/(vSize*(vSize-1));
   }
   private static double neighborhoodAffinity(UndirectedGraph<Integer, DefaultEdge> a, 
		   									UndirectedGraph<Integer, DefaultEdge> b){
	   double num = 0;
	   for(int v: a.vertexSet()){
		   if(b.vertexSet().contains(v))
			   num = num + 1;
	   }
	   double dem = a.vertexSet().size()*b.vertexSet().size();
	   num = Math.pow(num, 2);
	   return num/dem;
   }
   private static void doSetup(){
	   fillNames();
	   loadCliques();
	   System.out.println("original cliques.size: "+LCMACliques.size());
	   removeSmallCliques();
	   System.out.println("after removing cliques size 2 cliques.size: "+LCMACliques.size());
	   buildLCgraphs();
   }
   private static void buildLCgraphs(){
	  //System.out.println("buildLCgraphs");
	  //System.out.println("LCMACliques.size = "+LCMACliques.size());
      LC = new HashSet<UndirectedGraph<Integer, DefaultEdge>>();
      for (Set<Integer> c1: LCMACliques){
    	 UndirectedGraph<Integer, DefaultEdge> clique = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
    	 if(clique.vertexSet().size()>0){
    		 Set<DefaultEdge> eSet = new HashSet<DefaultEdge>(clique.edgeSet());
    		 Set<Integer> vSet = new HashSet<Integer>(clique.vertexSet());
    		 clique.removeAllEdges(eSet);
    		 clique.removeAllVertices(vSet);
    	 }
    	 for(int v: c1){
    		clique.addVertex(v);
    		groupedFriends.put(v, false); //diff from LCMAalgo2
    	 }
    	 for(int v: c1){
    		for(int t: c1)
    		   if (t != v)  clique.addEdge(v, t);
    	 }
    	 LC.add(clique);
      }
   }
   private static double findAvgDensity(){
	   double AD = 0;
	   for(UndirectedGraph<Integer, DefaultEdge> n: LC){
		  AD = AD + computeDensity(n);
	   }
	   AD = AD / LC.size();
	   return AD;
   }
   private static UndirectedGraph<Integer, DefaultEdge> copyGraph(UndirectedGraph<Integer, DefaultEdge> g){
	   UndirectedGraph<Integer, DefaultEdge> S = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
		for(int v: g.vertexSet()){
			S.addVertex(v);
		}
		for(DefaultEdge e: g.edgeSet()){
			S.addEdge(g.getEdgeSource(e), g.getEdgeTarget(e));
		} 
		return S;
   }
   private static HashMap<Integer, Boolean> setVertexNotUsed(){
	   HashMap<Integer, Boolean> vNotUsed = new HashMap<Integer, Boolean>();
	   int vCount = 0;
	   for(UndirectedGraph<Integer, DefaultEdge> ni: LC){
		   vCount++;
		   vNotUsed.put(vCount, true);
	   }
	   return vNotUsed;
   }
   private static void algo2(){
	  HashMap<Integer, Boolean> niNotUsed = null;
	  UndirectedGraph<Integer, DefaultEdge> S = null;
	  boolean stop = false, sameSize = false;  boolean newComplexProduced = true;
	  //average density AD = sum (density(clique)) for all cliques / numberOfCliques
	  double AD = findAvgDensity();
	  while(!stop && newComplexProduced){  //might have to remove the newComplexProduced variable
		 newComplexProduced = false;
		 Set<UndirectedGraph<Integer, DefaultEdge>> C = new HashSet<UndirectedGraph<Integer, DefaultEdge>>();
		 niNotUsed = setVertexNotUsed();		 
		 for(UndirectedGraph<Integer, DefaultEdge> ni: LC){  // * for each ni in LC do
		    S = copyGraph(ni); 			// * *	S = {ni};
			for(UndirectedGraph<Integer, DefaultEdge> nj: LC){ //* *	for each nj in CL, j != i do
			   if(!nj.equals(ni)){
				  double na = neighborhoodAffinity(nj, ni);
				  if(na > omega){  //	* *	*	if NA(nj, ni) > w then
					 S = merge(S, nj); //  * *	*		S = S union {nj};
					 newComplexProduced = true;
				  }
			   }
			}
			if(newComplexProduced){
				C.add(S); //* *	C = C union S;
			}
		 } //			   * end for*/
		 double ADprime = 0;
		 for(UndirectedGraph<Integer, DefaultEdge> n: C){  ADprime = ADprime + computeDensity(n);  }
		 ADprime = ADprime / C.size();
		 if(ADprime > (.95*AD)){
			System.out.print("AD = "+AD+", ADprime = "+ADprime);
			AD = ADprime;
			LC = C;
		 } else {
			System.out.println("stop = true");
			stop = true;
		 }
		 System.out.println(" stop = "+stop+", newComplexProduced = "+newComplexProduced+", LC.size = "+LC.size());
	  }
	  System.out.println("final cliques.size: "+LC.size());
	  printCliquesToFile();
   }
   private static UndirectedGraph<Integer, DefaultEdge> merge(UndirectedGraph<Integer, DefaultEdge> a, 
		   													UndirectedGraph<Integer, DefaultEdge> b){
	   UndirectedGraph<Integer, DefaultEdge> S = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
	   int source, target;
	   for(int v: a.vertexSet())
		   S.addVertex(v);
	   for(int v: b.vertexSet())
		   S.addVertex(v);
	   for(DefaultEdge e: a.edgeSet()){
		   source = a.getEdgeSource(e);
		   target = a.getEdgeTarget(e);
		   S.addEdge(source, target);
	   }
	   for(DefaultEdge e: b.edgeSet()){
		   source = b.getEdgeSource(e);
		   target = b.getEdgeTarget(e);
		   S.addEdge(source, target);
	   }
	   return S;
   }
  
   /**
    * remove cliques >= size of 2
    */
   private static void removeSmallCliques(){
	  Iterator<Set<Integer>> cliqueIter = LCMACliques.iterator();
	  Set<Integer> currClique;
	  while (cliqueIter.hasNext()){ 
		 currClique = cliqueIter.next();
		 if(currClique.size() <= 2)
			 cliqueIter.remove();
	  }
   }
     
   @SuppressWarnings("deprecation")
   private static void loadCliques(){
      int linesReadCount = 0;
	  try {
		 DataInputStream in = new DataInputStream(new FileInputStream(inputCliquesFile));
		 String inputLine = null;
		 List<Integer> currClique = null;
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
			LCMACliques.add(theClique);
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
    	 PrintWriter pw = new PrintWriter(new FileWriter(outputLCMAGroupFile));
    	 pw.println("omega: "+omega);
    	 Iterator<UndirectedGraph<Integer, DefaultEdge>> collIter = LC.iterator();
    	 UndirectedGraph<Integer, DefaultEdge> currClique;
    	 while (collIter.hasNext()){
    		pw.println("Clique: "+cliqueCount);
    		currClique = collIter.next();
    		for(int v: currClique.vertexSet()){
    			if(uidNames.containsKey(v)){
    				pw.println(uidNames.get(v));
      			  	groupedFriends.put(v, true);
    			}
    		}
    		pw.println();
    		cliqueCount++;
    	 }
    	 pw.close();
    	 System.out.println("Results of Intersection-Merge can be found in: "+outputLCMAGroupFile);
      } catch (Exception e){
    	  System.out.println("!!! Problem in PrintCliquesToFile: "+e.getMessage());
    	  System.exit(0);
      }
   }
   
   private static void printLooseFriendsToFile(){
      try {
	     PrintWriter pw = new PrintWriter(new FileWriter(outputLooseFile));
		 for(int v: groupedFriends.keySet()){
			if(!groupedFriends.get(v)){
			   if(uidNames.containsKey(v))
			      pw.println(uidNames.get(v));
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
