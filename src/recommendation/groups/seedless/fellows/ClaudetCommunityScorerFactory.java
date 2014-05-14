package recommendation.groups.seedless.fellows;

import recommendation.groups.seedless.CommunityScorer;
import recommendation.groups.seedless.CommunityScorerFactory;

public class ClaudetCommunityScorerFactory<V> implements CommunityScorerFactory<V> {

	
	public CommunityScorer<V> createCommunityScorer() {
		return new ClaudetCommunityScorer<V>();
	}

}
