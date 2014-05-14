package recommendation.groups.old.evolution.predictions.loading;

public class HybridPredictionLoaderFactory implements PredictionLoaderFactory {

	
	public PredictionLoader<Integer> getPredictionLoader() {
		return new HybridPredictionLoader();
	}

}
