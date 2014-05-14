package recommendation.groups.seedless.fellows;

import java.util.Collection;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import recommendation.groups.seedless.SeedlessGroupRecommender;
import recommendation.groups.seedless.SeedlessGroupRecommenderFactory;

public class FellowsRecommenderFactory<V extends Comparable<V>> implements SeedlessGroupRecommenderFactory<V>{

	@Override
	public SeedlessGroupRecommender<V> create(UndirectedGraph<V, DefaultEdge> graph) {
		return new Fellows<V>(graph);
	}

	@Override
	public SeedlessGroupRecommender<V> create(UndirectedGraph<V, DefaultEdge> graph,
			Collection<Set<V>> maximalCliques) {
		return new Fellows<V>(graph);
	}

}
