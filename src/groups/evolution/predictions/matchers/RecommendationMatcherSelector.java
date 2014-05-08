package groups.evolution.predictions.matchers;

public class RecommendationMatcherSelector {

	@SuppressWarnings("rawtypes")
	static RecommendationMatcherFactory factory = new SingleRecommenderEngineResultRecommendationMatcherFactory<Integer>();
	
	@SuppressWarnings("rawtypes")
	public static void setFactory(RecommendationMatcherFactory f){
		factory = f;
	}
	
	@SuppressWarnings("rawtypes")
	public static RecommendationMatcher getChooser(){
		return factory.createPredictionChooser();
	}
}
