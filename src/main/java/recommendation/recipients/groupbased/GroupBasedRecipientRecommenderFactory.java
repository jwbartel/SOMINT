package recommendation.recipients.groupbased;

import recommendation.recipients.RecipientRecommenderFactory;

public interface GroupBasedRecipientRecommenderFactory<V extends Comparable<V>>
		extends RecipientRecommenderFactory<V> {

	public GroupBasedRecipientRecommender<V> createRecommender();
	public GroupBasedRecipientRecommender<V> createRecommender(GroupScorer<V> groupScorer);

}
