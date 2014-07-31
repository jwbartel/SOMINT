package data.representation.actionbased;

import java.util.Collection;

public interface ActionBasedRecommender<Collaborator, Action extends CollaborativeAction<Collaborator>> {

	public void addPastAction(Action action);
	public Collection<Action> getPastActions();
}
