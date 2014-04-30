package groups.seedless.fellows;

import groups.seedless.CommunityScorer;
import groups.seedless.CommunityScorerSelector;
import groups.seedless.LocalCommunityExpander;
import groups.seedless.kelli.IOFunctions;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class FellowsCommunityExpander<V extends Comparable<V>> extends LocalCommunityExpander<V>{

	static final double MIN_COHESION = 0.108;
	
	Set<Triangle> triangles;
	static Map<Point, Integer> binomialCoefficients = new HashMap<Point, Integer>();
	
	public FellowsCommunityExpander(UndirectedGraph<V, DefaultEdge> graph) {
		super(graph);
		Triangle.setGraph(graph);
		triangles = getTriangles(graph);
		TrianglesCommunityScorer.setTriangles(triangles);
	}
	
	public Collection<Set<V>> getExpansions(){
		Collection<Set<V>> retVal = new HashSet<Set<V>>();
		
		CommunityScorer scorer = CommunityScorerSelector.createCommunityScorer();
		
		int count = 0;
		for(Triangle triangle: triangles){
			
			Set<V> initialCommunity = new TreeSet<V>();
			initialCommunity.addAll(triangle.getVertices());
			Set<V> localCommunity = expandLocalCommunity(initialCommunity, scorer);
			if(localCommunity != null){
				System.out.println(localCommunity.size());
				retVal.add(localCommunity);
			}
			count++;
		}
		
		
		return retVal;
	}
	
	protected Set<V> expandLocalCommunity(Set<V> initialCommunity, CommunityScorer<V> scorer){
		
		Set<V> community = new TreeSet<V>(initialCommunity);
		
		Set<V> border = new TreeSet<V>(initialCommunity);
		
		Set<V> unknown = new TreeSet<V>();
		for(V initialNode: initialCommunity){
			Set<DefaultEdge> setupEdges = graph.edgesOf(initialNode);
			for(DefaultEdge edge: setupEdges){
				V source = graph.getEdgeSource(edge);
				if(!community.contains(source)){
					unknown.add(source);
				}

				V target = graph.getEdgeTarget(edge);
				if(!community.contains(target)){
					unknown.add(target);
				}
			}
		}
		
		Set<Triangle<V>> internalTriangles = new TreeSet<Triangle<V>>();
		internalTriangles.add(new Triangle<V>(community));
		
		Set<Triangle<V>> outgoingTriangles = findOutgoingTriangles(community, unknown);
		
		pruneBorder(border, unknown, graph);
		
		
		double oldScore = getCohesion(community, internalTriangles, outgoingTriangles);
		
		while(true){
			if(previouslyFoundExpansions.contains(community)){
				return null;
			}
			previouslyFoundExpansions.add(community);
			
			V bestNode = null;
			Set<Triangle<V>> bestInternals = null;
			Set<Triangle<V>> bestOutgoing = null;
			double bestScore = 0;
			
			for(V borderNode: border){
				
				Set<DefaultEdge> edges = graph.edgesOf(borderNode); 
				for(DefaultEdge edge: edges){
					V newNode = null;
					
					V source = graph.getEdgeSource(edge);
					if(unknown.contains(source)){
						 newNode = source;
					}
					
					V target = graph.getEdgeTarget(edge);
					if(unknown.contains(target)){
						newNode = target;
					}
					
					if(newNode != null){
						
						Set<V> newCommunity = new TreeSet<V>(community);
						newCommunity.add(newNode);
						
						Set<V> newUnknown = new TreeSet<V>(unknown);
						updateUnknown(newNode, newCommunity, newUnknown, graph);
						
						Set<Triangle<V>> newInternals = findInternalTriangles(newNode, internalTriangles, outgoingTriangles);
						Set<Triangle<V>> newOutgoing = findExternalTriangles(newNode, outgoingTriangles, community, newUnknown);
						
						double score = getCohesion(newCommunity, newInternals, newOutgoing);
						
						if(bestNode == null || score > bestScore){
							bestScore = score;
							bestNode = newNode;
							bestInternals = newInternals;
							bestOutgoing = newOutgoing;
						}
					}
				}
			}
			
			if(bestNode != null && (oldScore <= bestScore)){
				
				community.add(bestNode);
				oldScore = bestScore;
				
				border.add(bestNode);
				
				updateUnknown(bestNode, community, unknown, graph);
				pruneBorder(border, unknown, graph);
				
				internalTriangles = bestInternals;
				outgoingTriangles = bestOutgoing;
				
			}else{
				if(oldScore < MIN_COHESION) return null;
				return community;
			}
			
		}
	}
	
	protected double getCohesion(Set<V> community, Set<Triangle<V>> internalTriangles, Set<Triangle<V>> outgoingTriangles){
		
		if(community.size() < 3) return 0.0;
		
		return cohesion(community.size(), internalTriangles.size(), outgoingTriangles.size());
		
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
	
	Set<Triangle<V>> findInternalTriangles(V newNode, Set<Triangle<V>> internalTriangles, Set<Triangle<V>> outgoingTriangles){
		Set<Triangle<V>> newInternal = new TreeSet<Triangle<V>>(internalTriangles);
		
		for(Triangle<V> triangle: outgoingTriangles){
			if(triangle.contains(newNode)){
				newInternal.add(triangle);
			}
		}
		
		return newInternal;
	}
	
	Set<Triangle<V>> findExternalTriangles(V newNode, Set<Triangle<V>> outgoingTriangles, Set<V> community, Set<V> unknown){
		
		Set<Triangle<V>> newOutgoing = new TreeSet<Triangle<V>>(outgoingTriangles);
		
		Set<Triangle<V>> toRemove = new TreeSet<Triangle<V>>();
		for(Triangle<V> triangle: newOutgoing){
			if(triangle.contains(newNode)){
				toRemove.add(triangle);
			}
		}
		newOutgoing.removeAll(toRemove);
		
		for(V internal: community){
			for(V external: unknown){
				
				Triangle<V> triangle = new Triangle<V>(newNode, internal, external);
				if(triangle.exists()){
					newOutgoing.add(triangle);
				}
				
			}
		}
		
		return newOutgoing;
	}
	
	Set<Triangle<V>> findOutgoingTriangles(Set<V> community, Set<V> unknown){
		
		Set<Triangle<V>> retVal = new TreeSet<Triangle<V>>();
		
		for(V v1: community){
			for(V v2: community){
				if(v2.equals(v1)) continue;
				
				for(V v3: unknown){
					Triangle<V> outgoing = new Triangle<V>(v1, v2, v3);
					if(outgoing.exists()){
						retVal.add(outgoing);
					}
				}
			}	
		}
		
		return retVal;
	}

	public Set<Triangle> getTriangles(UndirectedGraph<V, DefaultEdge> UIDGraph){
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
	
	public static void savePredictions(int participant){
CommunityScorerSelector.setFactory(new TrianglesCommunityScorerFactory<Integer>());
		
		IOFunctions<Integer> ioHelp = new IOFunctions<Integer>(Integer.class);
		
		String inRelationships = "data/Kelli/FriendshipData/2010Study/" + participant + "_MutualFriends.txt";
		String idNameMap = "data/Kelli/FriendshipData/2010Study/"+participant+"_People.txt";
		UndirectedGraph<Integer, DefaultEdge> graph = ioHelp.createUIDGraph(inRelationships);
		ioHelp.fillNames(idNameMap);
		
		FellowsCommunityExpander<Integer> expander = new FellowsCommunityExpander<Integer>(graph);
		Collection<Set<Integer>> predictions =  expander.getExpansions();
		
		String predictionFile = "data/Jacob/Fellows/"+participant+"_groups.txt";
		ioHelp.printCliquesToFile(predictionFile, predictions);
	}
	
	public static void main(String[] args){
		
		int[] participants = {/*10,12, 13, 16, 17, 19,*/ 21, 22, 23, 24, 25};
		for(int i=0; i<participants.length; i++){
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!PARTICIPANT "+participants[i]+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			savePredictions(participants[i]);
		}
	}
}
