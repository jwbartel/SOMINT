package groups.evolution.predictions.loading;

public class SnapHybridPredictionLoaderFactory implements PredictionLoaderFactory {

	
	public PredictionLoader<Integer> getPredictionLoader() {
		return new SnapHybridPredictionLoader();
	}

}
