package groups.evolution.predictions.matchers;

public class MultiRecommenderEngineResultRecommendationMatcherFactory<V> implements
		RecommendationMatcherFactory<V> {

	@Override
	public RecommendationMatcher<V> createPredictionChooser() {
		return new MultiRecommenderEngineResultRecommendationMatcher<V>();
	}

}
