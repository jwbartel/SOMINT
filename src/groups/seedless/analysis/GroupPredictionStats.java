package groups.seedless.analysis;

public class GroupPredictionStats {
	int participant;

	int manualAdds;
	int manualDeletions;

	int additions;
	int deletions;
	int missedIdeals;
	
	public GroupPredictionStats(int participant){
		this.participant = participant;
	}
	
	public int getParticipant(){
		return participant;
	}

	public int getManualAdds() {
		return manualAdds;
	}

	public void setManualAdds(int manualAdds) {
		this.manualAdds = manualAdds;
	}

	public int getManualDeletions() {
		return manualDeletions;
	}

	public void setManualDeletions(int manualDeletions) {
		this.manualDeletions = manualDeletions;
	}

	public int getAdditions() {
		return additions;
	}

	public void setAdditions(int additions) {
		this.additions = additions;
	}

	public int getDeletions() {
		return deletions;
	}

	public void setDeletions(int deletions) {
		this.deletions = deletions;
	}

	public int getMissedIdeals() {
		return missedIdeals;
	}

	public void setMissedIdeals(int missedIdeals) {
		this.missedIdeals = missedIdeals;
	}
	
	
}
