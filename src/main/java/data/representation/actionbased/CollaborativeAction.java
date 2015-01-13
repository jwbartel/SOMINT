package data.representation.actionbased;

import java.util.Collection;
import java.util.Date;

public interface CollaborativeAction<V> extends Comparable<CollaborativeAction<V>>{

	public String getId();
	public Collection<V> getCreators();
	public Date getStartDate();
	public Date getLastActiveDate();
	public Collection<V> getCollaborators();
	public boolean wasSent();
}
