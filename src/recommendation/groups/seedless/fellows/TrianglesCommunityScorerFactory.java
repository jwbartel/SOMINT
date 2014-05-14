package recommendation.groups.seedless.fellows;

import recommendation.groups.seedless.CommunityScorer;
import recommendation.groups.seedless.CommunityScorerFactory;

public class TrianglesCommunityScorerFactory<V extends Comparable<V>> implements
		CommunityScorerFactory<V> {

	@Override
	public CommunityScorer<V> createCommunityScorer() {
		return new TrianglesCommunityScorer<V>();
	}

}
