package recommendation.recipients.predictionchecking;

public class TopPrediction implements Comparable<TopPrediction> {
	public final String recipient;
	public double similarity;
	
	public TopPrediction(String recipient, double similarity){
		this.recipient = recipient;
		this.similarity = similarity;
	}
	
	public String toString(){
		return ""+recipient+"\t"+similarity;
	}

	public int compareTo(TopPrediction prediction) {
		int toReturn = -1* (new Double(this.similarity).compareTo(prediction.similarity));
		
		if(toReturn == 0){
			toReturn = this.recipient.compareTo(prediction.recipient);
		}
		
		return toReturn;
	}
}
