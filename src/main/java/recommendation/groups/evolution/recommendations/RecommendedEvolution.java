package recommendation.groups.evolution.recommendations;

import java.util.Set;

public interface RecommendedEvolution<V> {

	public abstract Set<V> getRecommenderEngineResult();

}