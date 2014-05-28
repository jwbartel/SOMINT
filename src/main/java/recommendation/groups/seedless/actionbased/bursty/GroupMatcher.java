package recommendation.groups.seedless.actionbased.bursty;

import java.util.Set;

public interface GroupMatcher<Collaborator> {
	
	public boolean groupsMatch(Set<Collaborator> group1, Set<Collaborator> group2);
}
