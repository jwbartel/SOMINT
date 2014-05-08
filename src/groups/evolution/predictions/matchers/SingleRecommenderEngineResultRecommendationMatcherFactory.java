package groups.evolution.predictions.matchers;

public class SingleRecommenderEngineResultRecommendationMatcherFactory<V> implements
		RecommendationMatcherFactory<V> {

	@Override
	public RecommendationMatcher<V> createPredictionChooser() {
		return new SingleRecommenderEngineResultRecommendationMatcher<V>();
	}

}
