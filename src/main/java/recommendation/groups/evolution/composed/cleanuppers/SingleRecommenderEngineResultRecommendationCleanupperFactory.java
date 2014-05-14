package recommendation.groups.evolution.composed.cleanuppers;

public class SingleRecommenderEngineResultRecommendationCleanupperFactory<V> implements
		RecommendationCleanupperFactory<V> {

	@Override
	public RecommendationCleanupper<V> createRecommendationCleanupper() {
		return new SingleRecommenderEngineResultRecommendationCleanupper<V>();
	}

}
