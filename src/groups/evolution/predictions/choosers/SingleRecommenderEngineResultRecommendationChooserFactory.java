package groups.evolution.predictions.choosers;

public class SingleRecommenderEngineResultRecommendationChooserFactory<V> implements
		RecommendationChooserFactory<V> {

	@Override
	public RecommendationChooser<V> createPredictionChooser() {
		return new SingleRecommenderEngineResultRecommendationChooser<V>();
	}

}
