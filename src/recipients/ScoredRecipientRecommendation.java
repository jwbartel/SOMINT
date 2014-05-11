package recipients;

public class ScoredRecipientRecommendation<V extends Comparable<V>> implements Comparable<ScoredRecipientRecommendation<V>>, RecipientRecommendation<V> {
	private final V recipient;
	private double similarity;
	
	public ScoredRecipientRecommendation(V recipient, double similarity){
		this.recipient = recipient;
		this.similarity = similarity;
	}
	
	public V getRecipient() {
		return recipient;
	}
	
	public double getSimilarity() {
		return similarity;
	}
	
	public String toString(){
		return ""+recipient+"\t"+similarity;
	}

	public int compareTo(ScoredRecipientRecommendation<V> prediction) {
		int toReturn = -1* (new Double(this.similarity).compareTo(prediction.similarity));
		
		if(toReturn == 0){
			toReturn = this.recipient.compareTo(prediction.recipient);
		}
		
		return toReturn;
	}
}
