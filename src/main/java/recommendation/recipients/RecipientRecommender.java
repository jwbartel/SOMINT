package recommendation.recipients;

import java.util.Collection;

import recommendation.general.actionbased.ActionBasedRecommender;
import recommendation.general.actionbased.CollaborativeAction;

public interface RecipientRecommender<V extends Comparable<V>> extends
		ActionBasedRecommender<V> {
	
	public String getTypeOfRecommender();

	public Collection<RecipientRecommendation<V>> recommendRecipients(
			CollaborativeAction<V> action);

	public Collection<RecipientRecommendation<V>> recommendRecipients(
			CollaborativeAction<V> action, int maxPredictions);
}