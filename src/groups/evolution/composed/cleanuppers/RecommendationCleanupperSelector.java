package groups.evolution.composed.cleanuppers;

public class RecommendationCleanupperSelector {

	@SuppressWarnings("rawtypes")
	static RecommendationCleanupperFactory factory = new SingleRecommenderEngineResultRecommendationCleanupperFactory<Integer>();
	
	@SuppressWarnings("rawtypes")
	public static void setFactory(RecommendationCleanupperFactory f){
		factory = f;
	}
	
	@SuppressWarnings("rawtypes")
	public static RecommendationCleanupper getCleanupper(){
		return factory.createRecommendationCleanupper();
	}
}
