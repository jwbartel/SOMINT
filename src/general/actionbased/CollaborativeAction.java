package general.actionbased;

import java.util.Collection;
import java.util.Date;

public interface CollaborativeAction<V> {

	public V getCreator();
	public Date getStartDate();
	public Date getLastActiveDate();
	public Collection<V> getCollaborators();
	
}
