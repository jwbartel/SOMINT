package recommendation.recipients;

import general.actionbased.ActionBasedRecommender;
import general.actionbased.CollaborativeAction;

import java.util.Collection;

public interface RecipientRecommender<V extends Comparable<V>> extends
		ActionBasedRecommender<V> {
	
	public String getTypeOfRecommender();

	public Collection<RecipientRecommendation<V>> recommendRecipients(
			CollaborativeAction<V> action);

	public Collection<RecipientRecommendation<V>> recommendRecipients(
			CollaborativeAction<V> action, int maxPredictions);
}