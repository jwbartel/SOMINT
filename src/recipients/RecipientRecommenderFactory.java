package recipients;

public interface RecipientRecommenderFactory<V extends Comparable<V>> {

	public RecipientRecommender<V> createRecommender();
	
}
