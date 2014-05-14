package recommendation.groups.evolution.composed.cleanuppers;

public class MultiRecommenderEngineResultRecommendationCleanupperFactory<V> implements
		RecommendationCleanupperFactory<V> {

	@Override
	public RecommendationCleanupper<V> createRecommendationCleanupper() {
		return new MultiRecommenderEngineResultRecommendationCleanupper<V>();
	}

}
