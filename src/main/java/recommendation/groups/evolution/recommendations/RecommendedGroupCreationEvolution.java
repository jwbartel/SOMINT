package recommendation.groups.evolution.recommendations;

import java.util.Set;

public class RecommendedGroupCreationEvolution<V> implements RecommendedEvolution<V> {

	final Set<V> recommenderEngineResult;
	
	public RecommendedGroupCreationEvolution(Set<V> recommenderEngineResult) {
		this.recommenderEngineResult = recommenderEngineResult;
	}
	
	@Override
	public Set<V> getRecommenderEngineResult() {
		return recommenderEngineResult;
	}

}
