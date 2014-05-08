package groups.seedless.hybrid;

import groups.seedless.SeedlessGroupRecommender;
import groups.seedless.SeedlessGroupRecommenderFactory;

import java.util.Collection;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class HybridRecommenderFactory<V> implements SeedlessGroupRecommenderFactory<V>{

	@Override
	public SeedlessGroupRecommender<V> create(UndirectedGraph<V, DefaultEdge> graph) {
		return new HybridCliqueMerger<>(graph);
	}

	@Override
	public SeedlessGroupRecommender<V> create(UndirectedGraph<V, DefaultEdge> graph,
			Collection<Set<V>> maximalCliques) {
		return new HybridCliqueMerger<>(graph, maximalCliques);
	}

}
