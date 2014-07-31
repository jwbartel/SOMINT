package recommendation.recipients.groupbased.interactionrank;

import data.representation.actionbased.CollaborativeAction;
import recommendation.recipients.groupbased.GroupBasedRecipientRecommender;
import recommendation.recipients.groupbased.GroupBasedRecipientRecommenderFactory;
import recommendation.recipients.groupbased.GroupScorer;

public class InteractionRankGroupBasedRecipientRecommenderFactory<Collaborator extends Comparable<Collaborator>, Action extends CollaborativeAction<Collaborator>>
		implements GroupBasedRecipientRecommenderFactory<Collaborator, Action> {

	public static final double DEFAULT_W_OUT = 0.25;

	// One Week
	public static final long DEFAULT_HALF_LIFE = 1000L * 3600L * 24L * 7L;

	@Override
	public GroupBasedRecipientRecommender<Collaborator, Action> createRecommender() {
		return new InteractionRankGroupBasedRecipientRecommender<Collaborator, Action>(DEFAULT_W_OUT, DEFAULT_HALF_LIFE);
	}
	
	public GroupBasedRecipientRecommender<Collaborator, Action> createRecommender(double wOut, double halfLife) {
		return new InteractionRankGroupBasedRecipientRecommender<Collaborator, Action>(wOut, halfLife);
	}
	
	public GroupBasedRecipientRecommender<Collaborator, Action> createRecommender(GroupScorer<Collaborator> groupScorer) {
		return new InteractionRankGroupBasedRecipientRecommender<Collaborator, Action>(groupScorer);
	}

}
