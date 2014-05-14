package recommendation.recipients.groupbased.google.scoring;

import java.util.Collection;

import recommendation.general.actionbased.CollaborativeAction;
import recommendation.recipients.groupbased.GroupScorer;

public class SubsetWeightedScore<V> extends GroupScorer<V> {

	@Override
	public String getName() {
		return "SubsetWeightedScore";
	}

	public SubsetWeightedScore(double wOut, double halfLife) {
		this.wOut = wOut;
		this.halfLife = halfLife;
	}
	
	@Override
	public double score(Collection<V> group,
			CollaborativeAction<V> currentAction,
			Collection<CollaborativeAction<V>> pastGroupActions) {

		if (group.containsAll(currentAction.getCollaborators())) {
			return ((double) currentAction.getCollaborators().size() / group
					.size())
					* getInteractionRank(currentAction, pastGroupActions);
		} else {
			return 0.0;
		}
	}

	public static <V> GroupScorerFactory<V> factory(Class<V> memberTypeClass) {
		
		return new GroupScorerFactory<V>() {

			@Override
			public GroupScorer<V> create(double wOut, double halfLife) {
				return new SubsetWeightedScore<>(wOut, halfLife);
			}
		};
	}

}
