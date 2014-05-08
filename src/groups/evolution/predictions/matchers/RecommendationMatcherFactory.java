package groups.evolution.predictions.matchers;

public interface RecommendationMatcherFactory<V> {

	public RecommendationMatcher<V> createPredictionChooser();
}
