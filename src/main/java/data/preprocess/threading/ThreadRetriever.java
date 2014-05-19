package data.preprocess.threading;

import java.util.Collection;

import data.representation.actionbased.CollaborativeAction;
import data.representation.actionbased.CollaborativeActionThread;

@SuppressWarnings("hiding")
public interface ThreadRetriever<Collaborator, Action extends CollaborativeAction<Collaborator>, Thread extends CollaborativeActionThread<Collaborator, Action>> {

	public Collection<? extends CollaborativeActionThread<Collaborator, Action>> retrieveThreads(Collection<Action> actions);
	
}
