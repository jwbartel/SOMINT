package groups.evolution.composed.cleanuppers;

public class MultiRecommenderEngineResultRecommendationCleanupperFactory<V> implements
		RecommendationCleanupperFactory<V> {

	@Override
	public RecommendationCleanupper<V> createPredictionChooser() {
		return new MultiRecommenderEngineResultRecommendationCleanupper<V>();
	}

}
