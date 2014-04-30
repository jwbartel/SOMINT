package groups.evolution.predictions.lists;

public class JaccardCoefficientPredictionListMakerFactory<V> implements
		PredictionListMakerFactory<V> {

	@Override
	public PredictionListMaker<V> getPredictionListMaker() {
		
		return new JaccardCoefficientPredictionListMaker<V>();
	}

}
