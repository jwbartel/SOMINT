package recommendation.groups.seedless.fellows;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import recommendation.groups.seedless.CommunityScorer;
import recommendation.groups.seedless.LocalCommunityExpander;


public class ClaudetCommunityScorer<V> implements CommunityScorer<V>{

	@Override
	public double scoreCommunityExpansion(Set<V> community, Set<V> border,
			Set<V> unknown, UndirectedGraph<V, DefaultEdge> graph) {
		
		return R(community, border, unknown, graph);
	}
	
	@Override
	public double scoreCommunityExpansion(V newNode, Set<V> community, Set<V> border,
			Set<V> unknown, UndirectedGraph<V, DefaultEdge> graph) {

		Set<V> possibleCommunity = new TreeSet<V>(community);
		possibleCommunity.add(newNode);
		
		Set<V> possibleBorder = new TreeSet<V>(border);
		possibleBorder.add(newNode);
		
		Set<V> possibleUnknown = new TreeSet<V>(unknown);
		LocalCommunityExpander.updateUnknown(newNode, possibleCommunity, possibleUnknown, graph);
		LocalCommunityExpander.pruneBorder(possibleBorder, possibleUnknown, graph);
		
		return R(possibleCommunity, possibleBorder, possibleUnknown, graph);
	}
	
	double R(Set<V> community, Set<V> border,
			Set<V> unknown, UndirectedGraph<V, DefaultEdge> graph) {
		
		Set<DefaultEdge> numeratorEdges = new HashSet<DefaultEdge>();
		Set<DefaultEdge> denominatorEdges = new HashSet<DefaultEdge>();
		for(V borderVertex: border){
			for(V unknownVertex: unknown){
				DefaultEdge edge = graph.getEdge(borderVertex, unknownVertex);
				if(edge != null){
					numeratorEdges.add(edge);
				}
			}
			
			denominatorEdges.addAll(graph.edgesOf(borderVertex));
		}
		
		double r =  ((double) numeratorEdges.size())/( (double) denominatorEdges.size());
		
		return r;
		
	}

	@Override
	public boolean goodScore(double oldScore, double newScore) {
		
		return true;
	}
}
