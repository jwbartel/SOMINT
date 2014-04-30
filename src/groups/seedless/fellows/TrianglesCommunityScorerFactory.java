package groups.seedless.fellows;

import groups.seedless.CommunityScorer;
import groups.seedless.CommunityScorerFactory;

public class TrianglesCommunityScorerFactory<V extends Comparable<V>> implements
		CommunityScorerFactory<V> {

	@Override
	public CommunityScorer<V> createCommunityScorer() {
		return new TrianglesCommunityScorer<V>();
	}

}
