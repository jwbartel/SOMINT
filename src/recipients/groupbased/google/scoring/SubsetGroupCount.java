package recipients.groupbased.google.scoring;

import general.actionbased.CollaborativeAction;

import java.util.Collection;

public class SubsetGroupCount<V> extends GroupScorer<V> {

	@Override
	public double score(Collection<V> group,
			CollaborativeAction<V> currentAction,
			Collection<CollaborativeAction<V>> pastGroupActions) {
		
		if (group.containsAll(currentAction.getCollaborators())) {
			return 1.0;
		} else {
			return 0.0;
		}
	}

	

}
