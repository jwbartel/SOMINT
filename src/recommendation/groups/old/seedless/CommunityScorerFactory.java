package recommendation.groups.old.seedless;

public interface CommunityScorerFactory<V> {

	public CommunityScorer<V> createCommunityScorer();
}
