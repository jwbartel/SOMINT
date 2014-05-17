package data.representation.actionbased.messages.email;

import java.util.ArrayList;
import java.util.Collection;

import data.representation.actionbased.messages.MessageThread;

public class EmailThread<RecipientType, MessageType extends EmailMessage<RecipientType>> extends
		MessageThread<RecipientType, MessageType> {

	private Collection<MessageType> messages = new ArrayList<>();

	@Override
	public void addThreadedAction(MessageType message) {
		messages.add(message);
	}

	@Override
	public Collection<MessageType> getThreadedActions() {
		return new ArrayList<>(messages);
	}

}
