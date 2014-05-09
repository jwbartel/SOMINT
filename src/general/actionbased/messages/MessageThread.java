package general.actionbased.messages;

import general.actionbased.CollaborativeActionThread;

public abstract class MessageThread<V, MessageType extends SingleMessage<V>> extends
		CollaborativeActionThread<V, MessageType> {

}
