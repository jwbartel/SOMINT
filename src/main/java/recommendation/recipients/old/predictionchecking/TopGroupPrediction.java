package recommendation.recipients.old.predictionchecking;

import java.util.Iterator;
import java.util.TreeSet;

import data.structures.ComparableSet;

public class TopGroupPrediction implements Comparable<TopGroupPrediction>{
	public final ComparableSet<String> recipients;
	public final double cosineSim;
	
	public TopGroupPrediction(ComparableSet<String> recipients, double cosineSim){
		this.recipients = recipients;
		this.cosineSim = cosineSim;
	}
	

	public int compareTo(TopGroupPrediction prediction) {
		return -1* (new Double(this.cosineSim).compareTo(prediction.cosineSim));
	}
	
	@SuppressWarnings("unchecked")
	public TreeSet<String> getRecipients(){
		return (TreeSet<String>) recipients.clone();
	}
	
	public String toString(){
		String toReturn = "";
		
		Iterator<String> iter = recipients.iterator();
		while(iter.hasNext()){
			toReturn += iter.next()+"\n";
		}
		toReturn+="\t"+cosineSim;
		
		return toReturn;
	}
}
