package recipients.groupbased.google.scoring;

import general.actionbased.CollaborativeAction;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public abstract class GroupScorer<V> {

	protected double wOut;
	protected double halfLife;

	protected Collection<V> getGroupIntersection(Collection<V> group,
			CollaborativeAction<V> currentAction) {
		
		Set<V> intersection = new HashSet<>(group);
		intersection.retainAll(currentAction.getCollaborators());
		return intersection;
	}
	
	private double getInteractionRankScoreOfPastAction(CollaborativeAction<V> currentAction,
			CollaborativeAction<V> pastAction) {

		if (currentAction.getCreators() == null
				|| pastAction.getCreators() == null
				|| currentAction.getCreators().size() != 1
				|| pastAction.getCreators().size() != 1
				|| currentAction.getLastActiveDate() == null
				|| pastAction.getLastActiveDate() == null) {
			return 0.0;
		}

		long timeDifference = currentAction.getLastActiveDate().getTime()
				- pastAction.getLastActiveDate().getTime();
		double score = Math.pow(0.5, (double) timeDifference / halfLife);
		V currentActionCreator = currentAction.getCreators().iterator().next();
		V pastActionCreator = pastAction.getCreators().iterator().next();
		if (currentActionCreator.equals(pastActionCreator)) {
			score *= wOut;
		}
		return score;
	}
	
	protected double getInteractionRank(CollaborativeAction<V> currentAction,
			Collection<CollaborativeAction<V>> pastGroupActions) {
		
		double score = 0.0;
		for (CollaborativeAction<V> pastAction : pastGroupActions) {
			score += getInteractionRankScoreOfPastAction(currentAction, pastAction);
		}
		return score;
	}

	public abstract double score(Collection<V> group, CollaborativeAction<V> currentAction,
			Collection<CollaborativeAction<V>> pastGroupActions);
}
