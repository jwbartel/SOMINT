package recommendation.recipients.old.predictionchecking.hierarchical;

import java.util.Set;

import data.structures.ComparableSet;

public interface Prediction {
	
	
	public int getSize();
	
	public void setParent(GroupPrediction parent);
	public ComparableSet<String> getAssociatedGroup();
	
	public Set<String> getMembers();
}
