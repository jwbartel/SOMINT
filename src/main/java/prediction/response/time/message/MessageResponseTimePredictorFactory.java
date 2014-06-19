package prediction.response.time.message;

import java.util.Collection;

import prediction.features.messages.ThreadSetProperties;
import snml.rule.basicfeature.IBasicFeatureRule;
import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

public interface MessageResponseTimePredictorFactory<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>> {

	public MessageResponseTimePredictor<Collaborator, Message, ThreadType> create(
			Collection<IBasicFeatureRule> features,
			ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties);

}
