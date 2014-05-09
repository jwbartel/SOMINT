package recipients.groupbased.google.scoring;

import general.actionbased.CollaborativeAction;

import java.util.Collection;

public class SubsetGroupScore<V> extends GroupScorer<V> {

	public SubsetGroupScore(double wOut, double halfLife) {
		this.wOut = wOut;
		this.halfLife = halfLife;
	}
	
	@Override
	public double score(Collection<V> group,
			CollaborativeAction<V> currentAction,
			Collection<CollaborativeAction<V>> pastGroupActions) {
		
		if (group.containsAll(currentAction.getCollaborators())) {
			return getInteractionRank(currentAction, pastGroupActions);
		} else {
			return 0.0;
		}
	}

	

}
