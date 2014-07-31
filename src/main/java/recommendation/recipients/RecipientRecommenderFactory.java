package recommendation.recipients;

import data.representation.actionbased.CollaborativeAction;
import recommendation.recipients.groupbased.GroupScorer;

public interface RecipientRecommenderFactory<Collaborator extends Comparable<Collaborator>, Action extends CollaborativeAction<Collaborator>> {

	public RecipientRecommender<Collaborator, Action> createRecommender();
	public RecipientRecommender<Collaborator, Action> createRecommender(GroupScorer<Collaborator> groupScorer);
	
}
