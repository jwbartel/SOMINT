package recommendation.groups.old.evolution.predictions.oldchoosers;

public class MultiPredictionMultiIdealPredictionChooserFactory<V> implements
		PredictionChooserFactory<V> {

	@Override
	public PredictionChooser<V> createPredictionChooser() {
		return new MultiPredictionMultiIdealPredictionChooser<V>();
	}

}
