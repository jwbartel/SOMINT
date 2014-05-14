package recommendation.groups.seedless.hybrid;

import java.util.Collection;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import recommendation.groups.seedless.SeedlessGroupRecommender;
import recommendation.groups.seedless.SeedlessGroupRecommenderFactory;

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
