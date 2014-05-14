package recommendation.groups.old.evolution.old;

import java.util.Set;

public class GroupMorphingTuple<V> {

	Set<V> oldGroup;
	Set<V> prediction;
	Set<V> ideal;
	Integer participant;
	int numNewParticipants;
	double threshold;
	int oldIdealDifference;
	int addCount;
	int deleteCount;
	
	
	public GroupMorphingTuple(Set<V> oldGroup, Set<V> prediction, Set<V> ideal, int oldIdealDifference, int addCount, int deleteCount){
		this.oldGroup = oldGroup;
		this.prediction = prediction;
		this.ideal = ideal;
		this.oldIdealDifference = oldIdealDifference;
		this.addCount = addCount;
		this.deleteCount = deleteCount;
	}

	public Set<V> getOldGroup() {
		return oldGroup;
	}

	public Set<V> getPrediction() {
		return prediction;
	}

	public Set<V> getIdeal() {
		return ideal;
	}

	public Integer getParticipant() {
		return participant;
	}

	public int getNumNewParticipants() {
		return numNewParticipants;
	}

	public int getAddCount() {
		return addCount;
	}

	public int getDeleteCount() {
		return deleteCount;
	}

	public void setParticipant(Integer participant) {
		this.participant = participant;
	}

	public void setNumNewParticipants(int numNewParticipants) {
		this.numNewParticipants = numNewParticipants;
	}
	
	public void setThreshold(double threshold){
		this.threshold = threshold;
	}
	
	public double getThreshold(){
		return threshold;
	}
	
	public int getOldIdealDifference(){
		return oldIdealDifference;
	}
	
}
