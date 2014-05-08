package groups.evolution.composed.oldchoosers;

public class MultiPredictionMultiIdealPredictionChooserFactory<V> implements
		PredictionChooserFactory<V> {

	@Override
	public PredictionChooser<V> createPredictionChooser() {
		return new MultiPredictionMultiIdealPredictionChooser<V>();
	}

}
