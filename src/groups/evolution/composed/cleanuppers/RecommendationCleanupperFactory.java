package groups.evolution.composed.cleanuppers;

public interface RecommendationCleanupperFactory<V> {

	public RecommendationCleanupper<V> createRecommendationCleanupper();
}
