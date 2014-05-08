package groups.evolution.composed.listmaker;

public class ExpectedScalingPredictionListMakerFactory<V> implements
		PredictionListMakerFactory<V> {

	@Override
	public PredictionListMaker<V> getPredictionListMaker() {
		return new ExpectedScalingPredictionListMaker<V>();
	}

}
