package recommendation.groups.seedless.hybrid;

import java.util.Collection;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import recommendation.groups.seedless.SeedlessGroupRecommender;
import recommendation.groups.seedless.SeedlessGroupRecommenderFactory;
import util.tools.io.ValueParser;

public class HybridRecommenderFactory<V> implements SeedlessGroupRecommenderFactory<V>{

	boolean shouldPrintStatus = true;
	ValueParser<V> parser = null;
	Integer maxVerticesInMemory = null;
	
	
	public HybridRecommenderFactory() {
		
	}
	
	public HybridRecommenderFactory(ValueParser<V> parser, int maxVerticesInMemory) {
		this.parser = parser;
		this.maxVerticesInMemory = maxVerticesInMemory;
	}
	
	
	public HybridRecommenderFactory(boolean shouldPrintStatus) {
		this.shouldPrintStatus = shouldPrintStatus;
	}
	
	
	public SeedlessGroupRecommender<V> create(UndirectedGraph<V, DefaultEdge> graph) {
		HybridCliqueMerger<V> recommender;
		if (parser == null || maxVerticesInMemory == null) {
			recommender = new HybridCliqueMerger<V>(graph);
		} else {
			recommender = new HybridCliqueMerger<>(graph, parser, maxVerticesInMemory);
		}
		recommender.setShouldPrintStatus(shouldPrintStatus);
		return recommender;
	}

	
	public SeedlessGroupRecommender<V> create(UndirectedGraph<V, DefaultEdge> graph,
			Collection<Set<V>> maximalCliques) {
		HybridCliqueMerger<V> recommender = new HybridCliqueMerger<V>(graph, maximalCliques);
		recommender.setShouldPrintStatus(shouldPrintStatus);
		return recommender;
	}

}
