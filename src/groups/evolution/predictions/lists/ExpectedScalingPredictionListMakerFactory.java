package groups.evolution.predictions.lists;

public class ExpectedScalingPredictionListMakerFactory<V> implements
		PredictionListMakerFactory<V> {

	@Override
	public PredictionListMaker<V> getPredictionListMaker() {
		return new ExpectedScalingPredictionListMaker<V>();
	}

}
