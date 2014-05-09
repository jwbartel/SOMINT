package recipients.groupbased;

import general.actionbased.ActionBasedRecommender;
import general.actionbased.CollaborativeAction;

import java.util.Collection;

import recipients.RecipientRecommender;
import recipients.ScoredRecipientRecommendation;

public interface GroupBasedRecipientRecommender<V extends Comparable<V>>
		extends RecipientRecommender<V>, ActionBasedRecommender<V> {

	public Collection<ScoredRecipientRecommendation<V>> recommendRecipients(
			CollaborativeAction<V> action);
}