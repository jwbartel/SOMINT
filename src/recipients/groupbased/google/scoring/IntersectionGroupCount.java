package recipients.groupbased.google.scoring;

import general.actionbased.CollaborativeAction;

import java.util.Collection;

public class IntersectionGroupCount<V> extends GroupScorer<V> {

	@Override
	public String getName() {
		return "IntersectiongGroupCount";
	}
	
	@Override
	public double score(Collection<V> group,
			CollaborativeAction<V> currentAction,
			Collection<CollaborativeAction<V>> pastGroupActions) {

		Collection<V> intersection = getGroupIntersection(group, currentAction);
		if (intersection.size() > 0) {
			return 1.0;
		} else {
			return 0.0;
		}
	}

	public static <V> GroupScorerFactory<V> factory(Class<V> memberTypeClass) {
		
		return new GroupScorerFactory<V>() {

			@Override
			public GroupScorer<V> create(double wOut, double halfLife) {
				return new IntersectionGroupCount<>();
			}
		};
	}

}
