package data.representation.actionbased.messages;

import data.representation.actionbased.CollaborativeActionThread;

public abstract class MessageThread<V, MessageType extends SingleMessage<V>> extends
		CollaborativeActionThread<V, MessageType> {

}
