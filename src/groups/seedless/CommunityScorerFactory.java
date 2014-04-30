package groups.seedless;

public interface CommunityScorerFactory<V> {

	public CommunityScorer<V> createCommunityScorer();
}
