package general.actionbased.messages.email;

import java.util.ArrayList;
import java.util.Collection;

import general.actionbased.messages.MessageThread;

public class EmailThread<V> extends MessageThread<V, EmailMessage<V>>{
	
	Collection<EmailMessage<V>> messages = new ArrayList<>();
	

	@Override
	public void addThreadedAction(EmailMessage<V> message) {
		messages.add(message);
	}

	@Override
	public Collection<EmailMessage<V>> getThreadedActions() {
		return new ArrayList<>(messages);
	}

}
