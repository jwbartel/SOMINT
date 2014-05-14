package recommendation.groups.old.seedless;

public class CommunityScorerSelector {

	static CommunityScorerFactory<Integer> factory;
	
	public static void setFactory(CommunityScorerFactory<Integer> f){
		factory = f;
	}
	
	public static CommunityScorer<Integer> createCommunityScorer(){
		return factory.createCommunityScorer();
	}
}
