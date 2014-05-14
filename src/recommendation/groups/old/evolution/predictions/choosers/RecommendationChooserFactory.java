package recommendation.groups.old.evolution.predictions.choosers;

public interface RecommendationChooserFactory<V> {

	public RecommendationChooser<V> createPredictionChooser();
}
