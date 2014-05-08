package groups.evolution.composed.listmaker;

import groups.evolution.GroupPredictionList;
import groups.evolution.predictions.oldchoosers.OldGroupAndPredictionPair;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class ExpectedScalingPredictionListMaker<V> extends
		PredictionListMaker<V> {

	@Override
	public GroupPredictionList<V> getPredictionList(Set<V> oldGroup, Collection<Set<V>> unusedRecommenderEngineResults,
			Collection<OldGroupAndPredictionPair<V>> usedPairings, Set<V> newIndividuals, double percentNew, double threshold){
		
		GroupPredictionList<V> predictionList = new GroupPredictionList<V>(oldGroup);
		
		for(Set<V> recommenderEngineResult: unusedRecommenderEngineResults){
						
			OldGroupAndPredictionPair<V> currPair = new OldGroupAndPredictionPair<V>(oldGroup, recommenderEngineResult);
			if(usedPairings.contains(currPair)) continue;  //If we've already matched a pair don't recommend them again
			
			Set<V> existingMembersInResult = new HashSet<V>(recommenderEngineResult);
			existingMembersInResult.removeAll(newIndividuals);
			
			//Get the counts for adds, removes, and expected growth
			int newAdds = intersectionCount(recommenderEngineResult, newIndividuals);
			int inserts = getSubtractionSize(existingMembersInResult, oldGroup);
			int deletes = getSubtractionSize(oldGroup, existingMembersInResult);
			
			//Get the euclidean distance between the points and (0, 0, |f|/(1-p))
			double distance = getDistanceFromBest(inserts, deletes, newAdds, oldGroup.size(), percentNew);
			if(distance <= threshold){
				predictionList.addPrediction(recommenderEngineResult);
			}
		}
		
		return predictionList;
	}
	
	protected int intersectionCount(Set<V> a, Set<V> b){ 
		if(a.size()>b.size()){
			return intersectionCount(b, a);
		}
		
		Set<V> intersection = new TreeSet<V>(a);
		intersection.retainAll(b);
		
		return intersection.size();
	}
	
	protected int getSubtractionSize(Set<V> a, Set<V> b){
		Set<V> subtraction = new TreeSet<V>(a);
		subtraction.removeAll(b);
		
		return subtraction.size();
	}
	
	protected double getDistanceFromBest(int inserts, int deletes, int newAdds, int oldGroupSize, double percentNew){
		double expectedInserts = 0;
		double expectedDeletes = 0;
		double expectedNewAdds = Math.round((((percentNew)/(1.0-percentNew))*oldGroupSize));
		
		double retVal = Math.pow(((double) inserts) - expectedInserts, 2.0);
		retVal += Math.pow(((double) deletes) - expectedDeletes, 2.0);
		retVal += Math.pow(((double) newAdds) - expectedNewAdds, 2.0);
		
		retVal = Math.sqrt(retVal);
		return retVal;
	}

	@Override
	public GroupPredictionList<V> getPredictionList(Set<V> oldGroup,
			String oldGroupName, Collection<Set<V>> unusedRecommenderEngineResults,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Set<V> newIndividuals, double percentNew, double threshold,
			Map<Set<V>, String> predictionNames) {


		GroupPredictionList<V> predictionList = new GroupPredictionList<V>(oldGroup, oldGroupName, predictionNames);
		
		for(Set<V> recommenderEngineResult: unusedRecommenderEngineResults){
			String predictedGroupName = predictionNames.get(recommenderEngineResult);
			
			OldGroupAndPredictionPair<V> currPair = new OldGroupAndPredictionPair<V>(oldGroup, recommenderEngineResult);
			if(usedPairings.contains(currPair)) continue;
			
			Set<V> existingMembersInResult = new HashSet<V>(recommenderEngineResult);
			existingMembersInResult.removeAll(newIndividuals);
			
			//Step 3a: get the counts for adds, removes, and expected growth
			if(oldGroupName.equals("old group-5") && threshold == 4.0){
				System.out.println("reached");
			}
			
			int newAdds = intersectionCount(recommenderEngineResult, newIndividuals);
			int inserts = getSubtractionSize(existingMembersInResult, oldGroup);
			int deletes = getSubtractionSize(oldGroup, existingMembersInResult);
			
			//Step 3b: get the euclidean distance between the points and (0, 0, |f|/(1-p))
			double distance = getDistanceFromBest(inserts, deletes, newAdds, oldGroup.size(), percentNew);
			if(distance <= threshold){
				predictionList.addPrediction(recommenderEngineResult);
			}
		}
		
		return predictionList;
	}

}
