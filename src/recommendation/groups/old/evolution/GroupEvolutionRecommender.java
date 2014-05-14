package recommendation.groups.old.evolution;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public interface GroupEvolutionRecommender<V> {

	public Collection<RecommendedEvolution<V>> generateRecommendations(
			UndirectedGraph<V, DefaultEdge> socialGraph, Collection<Set<V>> oldGroups,
			Set<V> newMembers, Set<V> removedMembers,
			Collection<Entry<V,V>> removedEdges, Collection<Entry<V,V>> addedEdges);
}
