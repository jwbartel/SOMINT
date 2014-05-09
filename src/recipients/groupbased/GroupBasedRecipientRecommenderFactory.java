package recipients.groupbased;

import recipients.RecipientRecommenderFactory;

public interface GroupBasedRecipientRecommenderFactory<V extends Comparable<V>>
		extends RecipientRecommenderFactory<V> {

	public GroupBasedRecipientRecommender<V> createRecommender();

}
