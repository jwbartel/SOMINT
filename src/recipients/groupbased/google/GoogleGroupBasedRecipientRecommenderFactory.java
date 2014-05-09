package recipients.groupbased.google;

import recipients.groupbased.GroupBasedRecipientRecommender;
import recipients.groupbased.GroupBasedRecipientRecommenderFactory;
import recipients.groupbased.google.scoring.GroupScorer;

public class GoogleGroupBasedRecipientRecommenderFactory<V extends Comparable<V>>
		implements GroupBasedRecipientRecommenderFactory<V> {

	public static final double DEFAULT_W_OUT = 0.25;

	// One Week
	public static final long DEFAULT_HALF_LIFE = 1000 * 3600 * 24 * 7;

	@Override
	public GroupBasedRecipientRecommender<V> createRecommender() {
		return new GoogleGroupBasedRecipientRecommender<>(DEFAULT_W_OUT, DEFAULT_HALF_LIFE);
	}
	
	public GroupBasedRecipientRecommender<V> createRecommender(double wOut, double halfLife) {
		return new GoogleGroupBasedRecipientRecommender<>(wOut, halfLife);
	}
	
	public GroupBasedRecipientRecommender<V> createRecommender(GroupScorer<V> groupScorer) {
		return new GoogleGroupBasedRecipientRecommender<>(groupScorer);
	}

}
