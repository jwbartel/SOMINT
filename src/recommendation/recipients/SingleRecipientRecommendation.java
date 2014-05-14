package recommendation.recipients;

public interface SingleRecipientRecommendation<V extends Comparable<V>> extends
		RecipientRecommendation<V> {

	public V getRecipient();

}
