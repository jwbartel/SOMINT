package recommendation.recipients.old.predictionchecking.hierarchical;

import java.util.Set;
import java.util.TreeSet;

import bus.data.structures.ComparableSet;

public class IndividualPrediction implements Prediction{
	String value;
	ComparableSet<String> associatedGroup;
	
	public IndividualPrediction(String value, ComparableSet<String> associatedGroup){
		this.value = value;
		this.associatedGroup = associatedGroup;
	}
	
	public int getSize(){
		return 1;
	}
	
	public String getValue(){
		return value;
	}
	
	public void setParent(GroupPrediction parent){}
	
	public String toString(){
		return value;
	}
	
	public ComparableSet<String> getAssociatedGroup(){
		return associatedGroup;
	}
	
	public Set<String> getMembers(){
		Set<String> toReturn = new TreeSet<String>();
		toReturn.add(value);
		return toReturn;
	}
}
