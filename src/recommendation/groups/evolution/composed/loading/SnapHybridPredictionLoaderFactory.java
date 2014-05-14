package recommendation.groups.evolution.composed.loading;

public class SnapHybridPredictionLoaderFactory implements PredictionLoaderFactory {

	
	public PredictionLoader<Integer> getPredictionLoader() {
		return new SnapHybridPredictionLoader();
	}

}
