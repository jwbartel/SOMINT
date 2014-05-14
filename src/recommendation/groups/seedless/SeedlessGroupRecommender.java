package recommendation.groups.seedless;

import java.util.Collection;
import java.util.Set;

import recommendation.groups.GroupRecommender;

public interface SeedlessGroupRecommender<V> extends GroupRecommender<V> {
	
	public String getTypeOfRecommender();
}
