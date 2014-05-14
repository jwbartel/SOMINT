package recommendation.groups.old.seedless.fellows;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import recommendation.groups.old.seedless.CommunityScorer;
import recommendation.groups.seedless.fellows.Triangle;

public class TrianglesCommunityScorer<V extends Comparable<V>> implements CommunityScorer<V> {

	static Set<Triangle> triangles = null;
	static Map<Point, Integer> binomialCoefficients = new HashMap<Point, Integer>();
	
	@Override
	public double scoreCommunityExpansion(Set<V> community, Set<V> border,
			Set<V> unknown, UndirectedGraph<V, DefaultEdge> graph) {
		
		if(triangles == null){
			triangles = getTriangles(graph);
		}
		
		return getCohesion(community);
	}

	@Override
	public double scoreCommunityExpansion(V newMember, Set<V> community,
			Set<V> border, Set<V> unknown, UndirectedGraph<V, DefaultEdge> graph) {
		
		Set<V> possibleExpansion = new TreeSet<V>(community);
		possibleExpansion.add(newMember);
		
		return scoreCommunityExpansion(possibleExpansion, border, unknown, graph);
	}

	@Override
	public boolean goodScore(double oldScore, double newScore) {
		return newScore >= oldScore;
	}
	
	protected double getCohesion(Set<V> clique){
		
		if(clique.size() < 3) return 0.0;
		
		int[] counts = sortTriangles(clique, triangles);
		return cohesion(clique.size(), counts[0], counts[1]);
		
	}

	private int binomialCoefficient(int n, int k){
		if(k == 0) return 1;
		if(n == 0) return 0;
		
		Integer retVal = binomialCoefficients.get(new Point(n,k));
		if(retVal != null) return retVal;
		
		retVal =  binomialCoefficient(n-1, k-1) + binomialCoefficient(n-1, k);
		binomialCoefficients.put(new Point(n,k), retVal);
		return retVal;
	}
	
	private double cohesion(int groupSize, int containedTriangles, int outboundTriangles){
		
		if(containedTriangles < 0) return 0.0;
		
		double density = ((double)containedTriangles)/((double) binomialCoefficient(groupSize, 3));
		double isolation = ((double) containedTriangles )/ ((double) (containedTriangles + outboundTriangles));
		
		return density * isolation;
	}
	
	private int[] sortTriangles(Set<V> group, Set<Triangle> triangles){
		//Returns the number of inner triangles and the number of outbound triangles given a set of vertices and a set of triangles
		
		int[] retVal = new int[2]; //{triangles in group, triangles outbound from group}
		Iterator<Triangle> trianglesIter = triangles.iterator();
		while(trianglesIter.hasNext()){
			Triangle<V> triangle = trianglesIter.next();
			Set<V> elements = triangle.getVertices();
			
			if(group.containsAll(elements)){
				retVal[0]++;
			}else{
				elements.removeAll(group);
				if(elements.size() == 1){
					retVal[1]++;
				}
			}
		}
		return retVal;
		
		
	}
	

	
	private Set<Triangle> getTriangles(UndirectedGraph<V, DefaultEdge> UIDGraph){
		System.out.println("~~~~~~~~~~~~~Finding Triangles~~~~~~~~~~~~~~~");
		
		
		
		Set<Triangle> toReturn = new TreeSet<Triangle>();
		
		ArrayList<V> vertices = new ArrayList<V>(UIDGraph.vertexSet());
		for(int i=0; i<vertices.size(); i++){
			
			V v1 = vertices.get(i);
			for(int j=i+1; j<vertices.size(); j++){
				V v2 = vertices.get(j);
				if(UIDGraph.containsEdge(v1, v2) || UIDGraph.containsEdge(v2, v1)){ 
					//Only check for a third vertex if the first two are connected
					for(int k=j+1; k<vertices.size(); k++){
						V v3 = vertices.get(k);
						if( (UIDGraph.containsEdge(v1, v3) || UIDGraph.containsEdge(v3, v1) ) && ( UIDGraph.containsEdge(v2, v3) || UIDGraph.containsEdge(v3, v2) ) ){
							toReturn.add(new Triangle<V>(v1, v2, v3));
						}
					}
				}
			}
		}
		

		System.out.println("total triangles: "+toReturn.size());
		
		return toReturn;	
	}
	
	public static void setTriangles(Set<Triangle> t){
		triangles = t;
	}

}
