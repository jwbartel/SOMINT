package groups.evolution.predictions.oldchoosers;

public class MultiPredictionSingleIdealPredictionChooserFactory<V> implements
		PredictionChooserFactory<V> {

	@Override
	public PredictionChooser<V> createPredictionChooser() {
		return new MultiPredictionSingleIdealPredictionChooser<V>();
	}

}
