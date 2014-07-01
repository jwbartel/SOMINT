package prediction.response.time.message;

import java.util.Collection;

import prediction.features.messages.ThreadSetProperties;
import snml.rule.basicfeature.IBasicFeatureRule;
import snml.rule.superfeature.model.mahout.MahoutSlopeOneModelRule;
import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

public class SlopeOneResponseTimePredictor<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
		extends
		MahoutCollaborativeFilteringResponseTimePredictor<Collaborator, Message, ThreadType> {
	
	public static <Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
			MahoutCollaborativeFilteringPredictorFactory<Collaborator, Message, ThreadType>
			factory(final String title,
					Class<Collaborator> collaboratorClass, Class<Message> messageClass,
					Class<ThreadType> threadClass) {

		return new MahoutCollaborativeFilteringPredictorFactory<Collaborator, Message, ThreadType>() {

			@Override
			public MahoutCollaborativeFilteringResponseTimePredictor<Collaborator, Message, ThreadType> createCollaborativeFilteringPredictor(
					IBasicFeatureRule userFeature,
					IBasicFeatureRule itemFeature,
					ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties) {
				return new SlopeOneResponseTimePredictor<>(title, userFeature,
						itemFeature, threadsProperties);
			}
		};
	}

	public SlopeOneResponseTimePredictor(
			String title,
			IBasicFeatureRule userFeatureRule,
			IBasicFeatureRule itemFeatureRule,
			ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties) {
		super(title, userFeatureRule, itemFeatureRule, threadsProperties);
		snmlModel = new MahoutSlopeOneModelRule("responseTime");
	}

	@Override
	public void validate(Collection<ThreadType> validationSet) throws Exception {
		train();
	}

	@Override
	public String getModelInfo() throws Exception {
		return "Slope One predictor";
	}

}
