package recommendation.recipients.groupbased;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import data.representation.actionbased.CollaborativeAction;

public abstract class GroupScorer<V> {

	protected double wOut;
	protected double halfLife;
	
	public interface GroupScorerFactory<V> {
		public boolean takesWOutAndHalfLife();
		public GroupScorer<V> create();
		public GroupScorer<V> create(double wOut, double halfLife);
	}

	protected Collection<V> getGroupIntersection(Collection<V> group,
			CollaborativeAction<V> currentAction) {
		
		Set<V> intersection = new HashSet<>(group);
		intersection.retainAll(currentAction.getCollaborators());
		return intersection;
	}
	
	public double getInteractionRankScoreOfPastAction(CollaborativeAction<V> currentAction,
			CollaborativeAction<V> pastAction) {

		if (currentAction.getLastActiveDate() == null
				|| pastAction.getLastActiveDate() == null) {
			return 0.0;
		}

		long timeDifference = currentAction.getLastActiveDate().getTime()
				- pastAction.getLastActiveDate().getTime();
		double score = Math.pow(0.5, (double) timeDifference / halfLife);
		if (pastAction.wasSent()) {
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
	
	public abstract String getName();

	public abstract double score(Collection<V> group, CollaborativeAction<V> currentAction,
			Collection<CollaborativeAction<V>> pastGroupActions);
}
