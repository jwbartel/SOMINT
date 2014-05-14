package recommendation.groups.old.seedless.fellows;

import recommendation.groups.old.seedless.CommunityScorer;
import recommendation.groups.old.seedless.CommunityScorerFactory;

public class ClaudetCommunityScorerFactory<V> implements CommunityScorerFactory<V> {

	
	public CommunityScorer<V> createCommunityScorer() {
		return new ClaudetCommunityScorer<V>();
	}

}
