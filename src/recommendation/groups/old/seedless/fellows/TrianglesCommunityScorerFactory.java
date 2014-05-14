package recommendation.groups.old.seedless.fellows;

import recommendation.groups.old.seedless.CommunityScorer;
import recommendation.groups.old.seedless.CommunityScorerFactory;

public class TrianglesCommunityScorerFactory<V extends Comparable<V>> implements
		CommunityScorerFactory<V> {

	@Override
	public CommunityScorer<V> createCommunityScorer() {
		return new TrianglesCommunityScorer<V>();
	}

}
