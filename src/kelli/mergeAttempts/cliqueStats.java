package kelli.mergeAttempts;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class cliqueStats {
	private static String inputCliquesFile     =                 "data/Kelli/Cliques/UID/KB_BKCliques.txt";
	private static Collection<Set<Integer>> cliques = new ArrayList<Set<Integer>>();
	public static void main(String[] args){
		cliques = loadCliques(inputCliquesFile);
		sortCliques();
		printCliquesSizeToFile("KBcliqueSizes.txt");
	}
	private static void sortCliques(){
		   Collection<Set<Integer>> CliqueSort = new ArrayList<Set<Integer>>();
		   Collection<Set<Integer>> InterCopy = new ArrayList<Set<Integer>>(cliques);
		   int largestSetSize = 0;
		   int sizes[] = new int[cliques.size()];
		   int arrayIndex = 0;
		   for(Set<Integer> i: cliques){
			   sizes[arrayIndex] = i.size();
			   if(i.size()>largestSetSize)
				   largestSetSize = i.size();
			   arrayIndex++;
		   }
		   Arrays.sort(sizes);
		   largestSetSize = sizes[--arrayIndex];
		   while(InterCopy.size()!=0){
			   for(Set<Integer> i: cliques){
				   if(i.size()==largestSetSize){
					   CliqueSort.add(i);
					   InterCopy.remove(i);
				   }   
			   }
			   cliques = InterCopy;
			   InterCopy = new ArrayList<Set<Integer>>(cliques);
			   while(arrayIndex>0 && sizes[arrayIndex]>=largestSetSize){
				   arrayIndex--;
			   } 
			   largestSetSize = sizes[arrayIndex]; 
		   }
		   cliques = new ArrayList<Set<Integer>> (CliqueSort);
	   }
	public static Collection<Set<Integer>> loadCliques(String inCliquesFile){
	   int linesReadCount = 0;
	   Collection<Set<Integer>> returnCollection = new ArrayList<Set<Integer>>();
	   try {
		  DataInputStream in = new DataInputStream(new FileInputStream(inCliquesFile));
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
				   if(in.available() != 0) inputLine = in.readLine();
				} else if (in.available() != 0) inputLine = in.readLine();
			 }
			 Set<Integer> theClique = new HashSet<Integer>(currClique);
			 returnCollection.add(theClique);
		  }
		  // dispose all the resources after using them.
		  in.close();
	   } catch (Exception e){
		   System.out.println("!!! CreateUIDGraph, line:"+linesReadCount+": "+e.getMessage());
	   }
	   return returnCollection;
	}
	public static void printCliquesSizeToFile(String outputFile){
	   try {
		  PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
		  Iterator<Set<Integer>> collIter = cliques.iterator();
		  Set<Integer> currClique;
		  pw.println("size count_at_that_size");
		  Hashtable<Integer, Integer> sizeTable = new Hashtable<Integer, Integer>();
		  while (collIter.hasNext()){
			 currClique = collIter.next();
			 if(sizeTable.containsKey(currClique.size())){
				 sizeTable.put(currClique.size(), sizeTable.get(currClique.size())+1);
			 } else sizeTable.put(currClique.size(), 1);
			 //pw.println(currClique.size());
		  }
		  for(int size: sizeTable.keySet()){
			 int count = sizeTable.get(size);
			 pw.println(size+" "+sizeTable.get(size));
		  }
		  System.out.println("Results can be found in: "+outputFile);
		  pw.close();
	      } catch (Exception e){
	    	  System.out.println("!!! Problem in PrintCliquesSizeToFile: "+e.getMessage());
	    	  System.exit(0);
	      }
	   }
}
