package data.representation.actionbased;

import java.util.Collection;

public interface ActionBasedRecommender<V> {

	public void addPastAction(CollaborativeAction<V> action);
	public Collection<CollaborativeAction<V>> getPastActions();
}
