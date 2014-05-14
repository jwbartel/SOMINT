package recommendation.groups.evolution.composed.listmaker;

public class JaccardCoefficientPredictionListMakerFactory<V> implements
		PredictionListMakerFactory<V> {

	@Override
	public PredictionListMaker<V> getPredictionListMaker() {
		
		return new JaccardCoefficientPredictionListMaker<V>();
	}

}
