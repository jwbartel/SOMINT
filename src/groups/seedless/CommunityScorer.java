package groups.seedless;

import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public interface CommunityScorer<V> {

	public double scoreCommunityExpansion(Set<V> community, Set<V> border, Set<V> unknown, UndirectedGraph<V, DefaultEdge> graph);
	public double scoreCommunityExpansion(V newMember, Set<V> community, Set<V> border, Set<V> unknown, UndirectedGraph<V, DefaultEdge> graph);
	public boolean goodScore(double oldScore, double newScore);
}
