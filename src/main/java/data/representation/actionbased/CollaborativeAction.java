package data.representation.actionbased;

import java.util.Collection;
import java.util.Date;

public interface CollaborativeAction<V> {

	public Collection<V> getCreators();
	public Date getStartDate();
	public Date getLastActiveDate();
	public Collection<V> getCollaborators();
	public boolean wasSent();
}
