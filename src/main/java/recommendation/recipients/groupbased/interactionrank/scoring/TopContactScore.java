package recommendation.recipients.groupbased.interactionrank.scoring;

import java.util.Collection;

import recommendation.general.actionbased.CollaborativeAction;
import recommendation.recipients.groupbased.GroupScorer;

public class TopContactScore<V> extends GroupScorer<V> {

	@Override
	public String getName() {
		return "TopContactScore";
	}

	public TopContactScore(double wOut, double halfLife) {
		this.wOut = wOut;
		this.halfLife = halfLife;
	}
	
	@Override
	public double score(Collection<V> group,
			CollaborativeAction<V> currentAction,
			Collection<CollaborativeAction<V>> pastGroupActions) {

		return getInteractionRank(currentAction, pastGroupActions);
	}

	public static <V> GroupScorerFactory<V> factory(Class<V> memberTypeClass) {
		
		return new GroupScorerFactory<V>() {

			@Override
			public GroupScorer<V> create(double wOut, double halfLife) {
				return new TopContactScore<>(wOut, halfLife);
			}
		};
	}

}
