package data.representation.actionbased;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import data.representation.actionbased.messages.MessageThread;

public abstract class CollaborativeActionThread<V,ActionType extends CollaborativeAction<V>> implements CollaborativeAction<V>{

	public abstract void addThreadedAction(ActionType action);
	public abstract Collection<ActionType> getThreadedActions();
	
	@Override
	public Collection<V> getCreators() {
		Date earliestStart = null;
		CollaborativeAction<V> earliestAction = null;
		for (CollaborativeAction<V> action : getThreadedActions()) {
			if (earliestStart == null || earliestStart.after(action.getStartDate())) {
				earliestStart = action.getStartDate();
				earliestAction = action;
			}
		}
		
		if (earliestAction != null) {
			return earliestAction.getCreators();
		}
		return null;
	}
	
	@Override
	public String getId() {
		Date earliestStart = null;
		CollaborativeAction<V> earliestAction = null;
		for (CollaborativeAction<V> action : getThreadedActions()) {
			if (earliestStart == null || earliestStart.after(action.getStartDate())) {
				earliestStart = action.getStartDate();
				earliestAction = action;
			}
		}
		
		if (earliestAction != null) {
			return earliestAction.getId();
		}
		return null;
	}
	
	@Override
	public boolean wasSent() {
		Date earliestStart = null;
		CollaborativeAction<V> earliestAction = null;
		for (CollaborativeAction<V> action : getThreadedActions()) {
			if (earliestStart == null || earliestStart.after(action.getStartDate())) {
				earliestStart = action.getStartDate();
				earliestAction = action;
			}
		}
		
		if (earliestAction != null) {
			return earliestAction.wasSent();
		}
		return false;
	}
	
	@Override
	public Date getStartDate() {
		Date earliestStart = null;
		for (CollaborativeAction<V> action : getThreadedActions()) {
			if (earliestStart == null || earliestStart.after(action.getStartDate())) {
				earliestStart = action.getStartDate();
			}
		}
		return earliestStart;
	}
	
	@Override
	public Date getLastActiveDate() {
		Date latestLastActive = null;
		for (CollaborativeAction<V> action : getThreadedActions()) {
			if (latestLastActive == null || latestLastActive.after(action.getLastActiveDate())) {
				latestLastActive = action.getLastActiveDate();
			}
		}
		return latestLastActive;
	}
	
	@Override
	public Collection<V> getCollaborators() {
		Set<V> collaborators = new HashSet<>();
		for (CollaborativeAction<V> action : getThreadedActions()) {
			if (action.getCollaborators() != null) {
				collaborators.addAll(action.getCollaborators());
			}
		}
		return collaborators;
	}
	
	@Override
	public int compareTo(CollaborativeAction<V> action) {
		if (action instanceof CollaborativeActionThread) {
			CollaborativeActionThread thread = (CollaborativeActionThread) action;
			
			if (!getStartDate().equals(thread.getStartDate())) {
				return getStartDate().compareTo(thread.getStartDate());
			}
			return this.toString().compareTo(thread.toString());
		}
		throw new RuntimeException("Thread cannont be compared to a non thread object");
	}
}
