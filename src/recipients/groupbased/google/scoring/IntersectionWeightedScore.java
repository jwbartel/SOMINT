package recipients.groupbased.google.scoring;

import general.actionbased.CollaborativeAction;

import java.util.Collection;

public class IntersectionWeightedScore<V> extends GroupScorer<V> {

	public IntersectionWeightedScore(double wOut, double halfLife) {
		this.wOut = wOut;
		this.halfLife = halfLife;
	}
	
	@Override
	public double score(Collection<V> group,
			CollaborativeAction<V> currentAction,
			Collection<CollaborativeAction<V>> pastGroupActions) {

		Collection<V> intersection = getGroupIntersection(group, currentAction);
		if (intersection.size() > 0) {
			return intersection.size()
					* getInteractionRank(currentAction, pastGroupActions);
		} else {
			return 0.0;
		}
	}

}
