package recommendation.recipients.groupbased;

import data.representation.actionbased.CollaborativeAction;
import recommendation.recipients.RecipientRecommenderFactory;

public interface GroupBasedRecipientRecommenderFactory<Collaborator extends Comparable<Collaborator>, Action extends CollaborativeAction<Collaborator>>
		extends RecipientRecommenderFactory<Collaborator, Action> {

	public GroupBasedRecipientRecommender<Collaborator,Action> createRecommender();
	public GroupBasedRecipientRecommender<Collaborator,Action> createRecommender(GroupScorer<Collaborator> groupScorer);

}
