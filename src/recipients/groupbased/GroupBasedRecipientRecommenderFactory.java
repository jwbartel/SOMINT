package recipients.groupbased;

import recipients.RecipientRecommenderFactory;
import recipients.groupbased.google.scoring.GroupScorer;

public interface GroupBasedRecipientRecommenderFactory<V extends Comparable<V>>
		extends RecipientRecommenderFactory<V> {

	public GroupBasedRecipientRecommender<V> createRecommender();
	public GroupBasedRecipientRecommender<V> createRecommender(GroupScorer<V> groupScorer);

}
