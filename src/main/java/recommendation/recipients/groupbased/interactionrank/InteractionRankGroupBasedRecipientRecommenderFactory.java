package recommendation.recipients.groupbased.interactionrank;

import recommendation.recipients.groupbased.GroupBasedRecipientRecommender;
import recommendation.recipients.groupbased.GroupBasedRecipientRecommenderFactory;
import recommendation.recipients.groupbased.GroupScorer;

public class InteractionRankGroupBasedRecipientRecommenderFactory<V extends Comparable<V>>
		implements GroupBasedRecipientRecommenderFactory<V> {

	public static final double DEFAULT_W_OUT = 0.25;

	// One Week
	public static final long DEFAULT_HALF_LIFE = 1000L * 3600L * 24L * 7L;

	@Override
	public GroupBasedRecipientRecommender<V> createRecommender() {
		return new InteractionRankGroupBasedRecipientRecommender<>(DEFAULT_W_OUT, DEFAULT_HALF_LIFE);
	}
	
	public GroupBasedRecipientRecommender<V> createRecommender(double wOut, double halfLife) {
		return new InteractionRankGroupBasedRecipientRecommender<>(wOut, halfLife);
	}
	
	public GroupBasedRecipientRecommender<V> createRecommender(GroupScorer<V> groupScorer) {
		return new InteractionRankGroupBasedRecipientRecommender<>(groupScorer);
	}

}
