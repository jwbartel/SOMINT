package kelli.mergeAttempts;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.Vector;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

@SuppressWarnings("unused")
public class jGraphtTrial {
   private Vector<Integer> vertexList; // = new Vector<Integer>();
   public UndirectedGraph<Integer, DefaultEdge> UIDGraph;// = null;
   private String fileName = "FriendOfFriends.txt";

   public jGraphtTrial() {
	  super();
	  vertexList = new Vector<Integer>();
	  UIDGraph = createUIDGraph();
   }
   public jGraphtTrial(String file) {
		  super();
		  vertexList = new Vector<Integer>();
		  fileName = file;
		  UIDGraph = createUIDGraph();
	   }
   
   @SuppressWarnings("deprecation")
   private UndirectedGraph<Integer, DefaultEdge> createUIDGraph()
   {
	  UndirectedGraph<Integer, DefaultEdge> g = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);

	  int linesReadCount = 0;
	  try {
		 DataInputStream in = new DataInputStream(new FileInputStream(fileName));
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
			friend2 = Integer.parseInt(FriendPair.substring(parsingSpace+1));
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
		 
		 // dispose all the resources after using them.
		 in.close();
	  } catch (Exception e){
		  System.out.println("CreateUIDGraph, line:"+linesReadCount+": "+e.getMessage());
	  }
	  
	  return g;
   }	
}
