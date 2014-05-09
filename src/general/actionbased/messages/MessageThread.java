package general.actionbased.messages;

import java.util.Collection;

import general.actionbased.CollaborativeAction;
import general.actionbased.CollaborativeActionThread;

public abstract class MessageThread<V> extends
		CollaborativeActionThread<V, MessageThread<V>> {

	@Override
	public void addThreadedAction(CollaborativeAction<V> action) {
		// TODO Auto-generated method stub

	}

	@Override
	public Collection<MessageThread<V>> getThreadedActions() {
		// TODO Auto-generated method stub
		return null;
	}

}
