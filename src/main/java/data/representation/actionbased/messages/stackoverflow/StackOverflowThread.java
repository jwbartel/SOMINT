package data.representation.actionbased.messages.stackoverflow;

import java.util.Collection;
import java.util.TreeSet;

import data.representation.actionbased.messages.MessageThread;

public class StackOverflowThread<Recipient, Message extends StackOverflowMessage<Recipient>> extends MessageThread<Recipient, Message> {

	Collection<Message> actions = new TreeSet<>();
	
	@Override
	public void addThreadedAction(Message action) {
		actions.add(action);		
	}

	@Override
	public Collection<Message> getThreadedActions() {
		return new TreeSet<>(actions);
	}

}
