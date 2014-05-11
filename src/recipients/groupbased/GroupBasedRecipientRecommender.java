package recipients.groupbased;

import general.actionbased.CollaborativeAction;

import java.util.Collection;

import recipients.RecipientRecommender;
import recipients.ScoredRecipientRecommendation;

public interface GroupBasedRecipientRecommender<V extends Comparable<V>>
		extends RecipientRecommender<V> {

	public Collection<ScoredRecipientRecommendation<V>> recommendRecipients(
			CollaborativeAction<V> action);
}