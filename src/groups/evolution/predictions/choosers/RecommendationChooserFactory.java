package groups.evolution.predictions.choosers;

public interface RecommendationChooserFactory<V> {

	public RecommendationChooser<V> createPredictionChooser();
}
