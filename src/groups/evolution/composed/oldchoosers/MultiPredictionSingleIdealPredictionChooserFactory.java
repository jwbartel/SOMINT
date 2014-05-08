package groups.evolution.composed.oldchoosers;

public class MultiPredictionSingleIdealPredictionChooserFactory<V> implements
		PredictionChooserFactory<V> {

	@Override
	public PredictionChooser<V> createPredictionChooser() {
		return new MultiPredictionSingleIdealPredictionChooser<V>();
	}

}
