package groups.evolution.composed.oldchoosers;

public class SinglePredictionMultiIdealPredictionChooserFactory<V> implements
		PredictionChooserFactory<V> {

	@Override
	public PredictionChooser<V> createPredictionChooser() {
		return new SinglePredictionMultiIdealPredictionChooser<V>();
	}

}
