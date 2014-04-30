package groups.seedless.fellows;

import groups.seedless.CommunityScorer;
import groups.seedless.CommunityScorerFactory;

public class ClaudetCommunityScorerFactory<V> implements CommunityScorerFactory<V> {

	
	public CommunityScorer<V> createCommunityScorer() {
		return new ClaudetCommunityScorer<V>();
	}

}
