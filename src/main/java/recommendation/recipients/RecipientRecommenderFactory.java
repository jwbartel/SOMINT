package recommendation.recipients;

import recommendation.recipients.groupbased.GroupScorer;

public interface RecipientRecommenderFactory<V extends Comparable<V>> {

	public RecipientRecommender<V> createRecommender();
	public RecipientRecommender<V> createRecommender(GroupScorer<V> groupScorer);
	
}
