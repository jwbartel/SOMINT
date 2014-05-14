package recommendation.groups.seedless;

import java.util.Collection;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public interface SeedlessGroupRecommenderFactory<V> {

	public SeedlessGroupRecommender<V> create(UndirectedGraph<V, DefaultEdge> graph);

	public SeedlessGroupRecommender<V> create(UndirectedGraph<V, DefaultEdge> graph, Collection<Set<V>> maximalCliques);
	
}
