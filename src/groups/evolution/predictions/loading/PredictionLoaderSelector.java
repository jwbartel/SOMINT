package groups.evolution.predictions.loading;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class PredictionLoaderSelector {

	static PredictionLoaderFactory factory = null;
	
	public static void setFactory(PredictionLoaderFactory f){
		factory = f;
	}
	
	public static Collection<Set<Integer>> loadPredictions(int participant){
		return factory.getPredictionLoader().loadPredictions(participant);
	}

	public static Map<Set<Integer>, String> loadPredictionNames(int participant){
		return factory.getPredictionLoader().loadPredictionNames(participant);
	}
}
