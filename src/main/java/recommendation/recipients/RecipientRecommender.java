package recommendation.recipients;

import java.util.Collection;

import data.representation.actionbased.ActionBasedRecommender;
import data.representation.actionbased.CollaborativeAction;

public interface RecipientRecommender<Collaborator extends Comparable<Collaborator>, Action extends CollaborativeAction<Collaborator>> extends
		ActionBasedRecommender<Collaborator, Action> {
	
	public String getTypeOfRecommender();

	public Collection<RecipientRecommendation<Collaborator>> recommendRecipients(
			CollaborativeAction<Collaborator> action);

	public Collection<RecipientRecommendation<Collaborator>> recommendRecipients(
			CollaborativeAction<Collaborator> action, int maxPredictions);
}