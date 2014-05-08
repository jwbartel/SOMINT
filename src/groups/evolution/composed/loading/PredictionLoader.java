package groups.evolution.composed.loading;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface PredictionLoader<V> {

	public Collection<Set<V>> loadPredictions(int participant);
	public Map<Set<V>, String> loadPredictionNames(int participant);
}
