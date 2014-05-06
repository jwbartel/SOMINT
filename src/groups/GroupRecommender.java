package groups;

import java.util.Collection;
import java.util.Set;

public interface GroupRecommender<V> {

	public Collection<Set<V>> getRecommendations();
}
