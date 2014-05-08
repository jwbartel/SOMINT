package groups.evolution.composed.cleanuppers;

public class SingleRecommenderEngineResultRecommendationCleanupperFactory<V> implements
		RecommendationCleanupperFactory<V> {

	@Override
	public RecommendationCleanupper<V> createPredictionChooser() {
		return new SingleRecommenderEngineResultRecommendationCleanupper<V>();
	}

}
