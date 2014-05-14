package recommendation.groups.old.evolution.predictions.choosers;

public class MultiRecommenderEngineResultRecommendationChooserFactory<V> implements
		RecommendationChooserFactory<V> {

	@Override
	public RecommendationChooser<V> createPredictionChooser() {
		return new MultiRecommenderEngineResultRecommendationChooser<V>();
	}

}
