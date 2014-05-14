package recommendation.groups.evolution.composed.loading;

public class HybridPredictionLoaderFactory implements PredictionLoaderFactory {

	
	public PredictionLoader<Integer> getPredictionLoader() {
		return new HybridPredictionLoader();
	}

}
