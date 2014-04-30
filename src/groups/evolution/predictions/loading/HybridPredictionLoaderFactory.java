package groups.evolution.predictions.loading;

public class HybridPredictionLoaderFactory implements PredictionLoaderFactory {

	
	public PredictionLoader<Integer> getPredictionLoader() {
		return new HybridPredictionLoader();
	}

}
