package recommendation.recipients;

import java.util.Collection;

import data.representation.actionbased.ActionBasedRecommender;
import data.representation.actionbased.CollaborativeAction;

public interface RecipientRecommender<V extends Comparable<V>> extends
		ActionBasedRecommender<V> {
	
	public String getTypeOfRecommender();

	public Collection<RecipientRecommendation<V>> recommendRecipients(
			CollaborativeAction<V> action);

	public Collection<RecipientRecommendation<V>> recommendRecipients(
			CollaborativeAction<V> action, int maxPredictions);
}