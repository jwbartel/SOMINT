package prediction.response.time.message;

import java.util.Collection;

import prediction.features.messages.ThreadSetProperties;
import snml.rule.basicfeature.IBasicFeatureRule;
import snml.rule.superfeature.model.mahout.MahoutUserBasedModelRule;
import snml.rule.superfeature.model.mahout.SimilarityMeasure;
import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

public class UserBasedCollaborativeFilterResponseTimePredictor<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
		extends
		MahoutCollaborativeFilteringResponseTimePredictor<Collaborator, Message, ThreadType> {
	
	private SimilarityMeasure similarity;
	
	public static <Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
			MahoutCollaborativeFilteringPredictorFactory<Collaborator, Message, ThreadType>
			factory(final String title,
					final SimilarityMeasure similarity,
					Class<Collaborator> collaboratorClass, Class<Message> messageClass,
					Class<ThreadType> threadClass) {

		return new MahoutCollaborativeFilteringPredictorFactory<Collaborator, Message, ThreadType>() {

			@Override
			public MahoutCollaborativeFilteringResponseTimePredictor<Collaborator, Message, ThreadType> createCollaborativeFilteringPredictor(
					IBasicFeatureRule userFeature,
					IBasicFeatureRule itemFeature,
					ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties) {
				return new UserBasedCollaborativeFilterResponseTimePredictor<>(title, userFeature,
						itemFeature, similarity, threadsProperties);
			}
		};
	}

	public UserBasedCollaborativeFilterResponseTimePredictor(
			String title,
			IBasicFeatureRule userFeatureRule,
			IBasicFeatureRule itemFeatureRule,
			SimilarityMeasure similarity,
			ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties) {
		super(title, userFeatureRule, itemFeatureRule, threadsProperties);
		this.similarity = similarity;
		snmlModel = new MahoutUserBasedModelRule("responseTime", similarity);
	}

	@Override
	public void validate(Collection<ThreadType> validationSet) throws Exception {
		train();
	}

	@Override
	public String getModelInfo() throws Exception {
		return "User-based collaborative filtering with " + similarity + " similarity";
	}

}
