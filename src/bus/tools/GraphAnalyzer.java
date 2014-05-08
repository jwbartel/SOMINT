package bus.tools;


import groups.seedless.hybrid.IOFunctions;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * A tool for analyzing the statistics about a specific social graph
 */
public class GraphAnalyzer<V> {
	UndirectedGraph<V, DefaultEdge> graph;
	
	public GraphAnalyzer(UndirectedGraph<V, DefaultEdge> graph){
		this.graph = graph;
	}
	
	public int countVertices(){
		return graph.vertexSet().size();
	}
	
	public int countEdges(){
		return graph.edgeSet().size();
	}
	
	public static Map<Integer, int[]> countEdgesAndVerticesAcrossParticipants(){
		
		int[] participants = TestingConstants.PARTICIPANTS;
		
		Map<Integer, int[]> collectedCounts = new TreeMap<Integer, int[]>();
		for(int participant: participants){
			
			IOFunctions<Integer> ioHelp = new IOFunctions<Integer>(Integer.class);

			String  inRelationships = "data/Kelli/FriendshipData/2010Study/"+participant+"_MutualFriends.txt";		
			UndirectedGraph<Integer, DefaultEdge> graph = ioHelp.createUIDGraph(inRelationships);
			GraphAnalyzer<Integer> analyzer = new GraphAnalyzer<Integer>(graph);
			
			int[] counts = new int[2];
			counts[0] = analyzer.countEdges();
			counts[1] = analyzer.countVertices();
			collectedCounts.put(participant, counts);
		}
		
		return collectedCounts;
	}
	
	public static void printEdgesAndVerticesAcrossParticipants() {
		
		String edgesLine  = "edges,";
		String verticesLine = "vertices,";
		
		Map<Integer, int[]> collectedCounts = countEdgesAndVerticesAcrossParticipants();
		Set<Integer> participants = collectedCounts.keySet();
		for(int participant: participants){
			int[] counts = collectedCounts.get(participant);
			edgesLine += counts[0] + ",";
			verticesLine += counts[1] + ",";
		}
		
		System.out.println(edgesLine);
		System.out.println(verticesLine);
	}
	
	public ArrayList<Integer> countEdgesByDegree(){
		ArrayList<Integer> counts = new ArrayList<Integer>();
		
		Set<V> vertices = graph.vertexSet();
		for(V vertex: vertices){
			int degree = graph.degreeOf(vertex);
			
			while(counts.size() <= degree ){
				counts.add(0);
			}
			
			Integer count = counts.get(degree);
			if(count == null){
				counts.set(degree, 1);
			}else{
				counts.set(degree, count + 1);
			}
		}
		
		return counts;
	}
	
	public static Map<Integer, ArrayList<Integer>> countDegreesAcrossParticipants(){

		Map<Integer, ArrayList<Integer>> collectedDegreeCounts = new TreeMap<Integer, ArrayList<Integer>>();
		
		int[] participants = TestingConstants.PARTICIPANTS;
		for(int participant: participants){

			IOFunctions<Integer> ioHelp = new IOFunctions<Integer>(Integer.class);
			String inRelationships = "data/Kelli/FriendshipData/2010Study/"+participant+"_MutualFriends.txt";
			UndirectedGraph<Integer, DefaultEdge> graph = ioHelp.createUIDGraph(inRelationships);
			
			GraphAnalyzer<Integer> analyzer = new GraphAnalyzer<Integer>(graph);
			ArrayList<Integer> degreeCounts = analyzer.countEdgesByDegree();
			
			collectedDegreeCounts.put(participant, degreeCounts);
			
		}
		
		return collectedDegreeCounts;
	}
	
	public static void printDegreeCountsAcrossParticipants(){
		Map<Integer, ArrayList<Integer>> collectedDegreeCounts = countDegreesAcrossParticipants();
		
		Set<Integer> participants = collectedDegreeCounts.keySet();
		int maxNumDegrees = 0;
		System.out.print("degree"); //For the header
		for(int participant: participants){
			System.out.print(",participant "+participant); //For the header
			ArrayList<Integer> degreeCounts = collectedDegreeCounts.get(participant);
			if(maxNumDegrees < degreeCounts.size()) maxNumDegrees = degreeCounts.size();
		}
		System.out.println();

		for(int degree=0; degree<maxNumDegrees; degree++){
			System.out.print(degree+",");
			for(int participant: participants){
				ArrayList<Integer> degreeCounts = collectedDegreeCounts.get(participant);
				if(degree>=degreeCounts.size()){
					System.out.print(0+",");
				}else{
					System.out.print(degreeCounts.get(degree)+",");
				}
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args){
		printEdgesAndVerticesAcrossParticipants();
	}
	
}
