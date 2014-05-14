package recommendation.recipients.groupbased.google.scoring;

import general.actionbased.CollaborativeAction;

import java.util.Collection;

import recommendation.recipients.groupbased.GroupScorer;

public class IntersectionWeightedScore<V> extends GroupScorer<V> {

	@Override
	public String getName() {
		return "IntersectiongWeightedScore";
	}

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

	public static <V> GroupScorerFactory<V> factory(Class<V> memberTypeClass) {
		
		return new GroupScorerFactory<V>() {

			@Override
			public GroupScorer<V> create(double wOut, double halfLife) {
				return new IntersectionWeightedScore<>(wOut, halfLife);
			}
		};
	}

}
