package recommendation.general.actionbased.messages;

import recommendation.general.actionbased.CollaborativeActionThread;

public abstract class MessageThread<V, MessageType extends SingleMessage<V>> extends
		CollaborativeActionThread<V, MessageType> {

}
