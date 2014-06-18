package prediction.features.messages;

import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;
import snml.rule.basicfeature.IBasicFeatureRule;

public interface FeatureRuleFactory<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>> {

	public IBasicFeatureRule create(
			ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties);
}
