package recipients;

import recipients.groupbased.google.scoring.GroupScorer;

public interface RecipientRecommenderFactory<V extends Comparable<V>> {

	public RecipientRecommender<V> createRecommender();
	public RecipientRecommender<V> createRecommender(GroupScorer<V> groupScorer);
	
}
