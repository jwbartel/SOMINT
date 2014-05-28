package recommendation.groups.seedless.actionbased.bursty;

import java.util.HashSet;
import java.util.Set;

public class RelativeEditsThresholdMatcher<Collaborator> implements GroupMatcher<Collaborator> {

	private final double threshold;
	
	public RelativeEditsThresholdMatcher(double threshold) {
		this.threshold = threshold;
	}
	
	@Override
	public boolean groupsMatch(Set<Collaborator> group1,
			Set<Collaborator> group2) {
		
		Set<Collaborator> intersect = new HashSet<Collaborator>(group1);
		intersect.retainAll(group2);
		
		double score = Double.POSITIVE_INFINITY;
		if(group1.size() != 0 && group2.size() != 0) {
			score = ((double) intersect.size()) / Math.min(group1.size(), group2.size());
		}
		return score < threshold;
	}

}
