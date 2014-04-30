package groups.evolution.predictions.oldchoosers;

public class SinglePredictionSingleIdealPredictionChooserFactory<V> implements
		PredictionChooserFactory<V> {

	@Override
	public PredictionChooser<V> createPredictionChooser() {
		return new SinglePredictionSingleIdealPredictionChooser<V>();
	}

}
