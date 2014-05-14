package recommendation.groups.old.seedless;

import java.util.Collection;
import java.util.Set;

public interface SeedlessGroupRecommender<V> {

	public Collection<Set<V>> getRecommendations();
}
