package groups.evolution.analysis;

import groups.seedless.analysis.GroupPredictionStats;

public class EvolutionStats extends GroupPredictionStats {
	
	double percentNew;
	int numNewMembers;
	
	int expectedIdeals;
	int numOldGroups;
	
	int manualSplits;
	
	int groupsExpanded;
	int selections;
	int automatedAdds;
	
	int unchangedMissedIdeals;
	
	public EvolutionStats(int participant, double percentNew, int numNewMembers){
		super(participant);
		this.percentNew = percentNew;
		this.numNewMembers = numNewMembers;
	}
	
	public String getHeader(){
		return "participants," +
				"percent new," +
				"num new individuals," +
				"num expected ideals," +
				"num old groups," +
				"manual adds," +
				"manual deletions," +
				"manual splits," +
				"morphings made by algorithm," +
				"additions after algorithm," +
				"deletions after algorithm," +
				"adds during algorithm," +
				"unreached ideal morphings," +
				"unchanged and unreached ideal morphings,";
	}
	
	public String toString(){
		return "" +
				getParticipant() + "," +
				getPercentNew() + "," +
				getNumNewMembers() + "," +
				getExpectedIdeals() + "," +
				getNumOldGroups() + "," +
				getManualAdds() + "," +
				getManualDeletions() + "," +
				getManualSplits() + "," +
				getGroupsExpanded() + "," +
				getAdditions() + "," +
				getDeletions() + "," +
				getAutomatedAdds() + "," +
				getMissedIdeals() + "," +
				getUnchangedMissedIdeals() + ",";
	}
	
	public double getPercentNew(){
		return percentNew;
	}
	
	public int getNumNewMembers(){
		return numNewMembers;
	}

	public int getExpectedIdeals() {
		return expectedIdeals;
	}

	public void setExpectedIdeals(int expectedIdeals) {
		this.expectedIdeals = expectedIdeals;
	}

	public int getNumOldGroups() {
		return numOldGroups;
	}

	public void setNumOldGroups(int numOldGroups) {
		this.numOldGroups = numOldGroups;
	}

	public int getManualSplits() {
		return manualSplits;
	}

	public void setManualSplits(int manualSplits) {
		this.manualSplits = manualSplits;
	}

	public int getGroupsExpanded() {
		return groupsExpanded;
	}

	public void setGroupsExpanded(int groupsExpanded) {
		this.groupsExpanded = groupsExpanded;
	}

	public int getSelections() {
		return selections;
	}

	public void setSelections(int selections) {
		this.selections = selections;
	}

	public int getAutomatedAdds() {
		return automatedAdds;
	}

	public void setAutomatedAdds(int automatedAdds) {
		this.automatedAdds = automatedAdds;
	}
	
	public int getUnchangedMissedIdeals() {
		return unchangedMissedIdeals;
	}
	
	public void setUnchangedMissedIdeals(int unchangedMissedIdeals) {
		this.unchangedMissedIdeals = unchangedMissedIdeals;
	}
}
