package recipients;

import general.actionbased.CollaborativeAction;

import java.util.Collection;

public interface RecipientRecommender<V extends Comparable<V>> {
	
	public Collection<ScoredRecipientRecommendation<V>> recommendRecipients(CollaborativeAction<V> action);
	
	public Collection<ScoredRecipientRecommendation<V>> recommendRecipients(CollaborativeAction<V> action, int maxPredictions);
}