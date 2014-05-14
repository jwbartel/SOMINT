package recommendation.groups.old.evolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import recommendation.groups.old.evolution.predictions.choosers.RecommendationChooser;
import recommendation.groups.old.evolution.predictions.choosers.RecommendationChooserSelector;
import recommendation.groups.old.evolution.predictions.lists.PredictionListSelector;
import recommendation.groups.old.evolution.predictions.oldchoosers.OldGroupAndPredictionPair;
import bus.tools.TestingConstants;


//Selects recommendations based on a one-to-one mapping
//It would be good to include one-to-many, many-to-one, and many-to-many mappings here too
public class EvolutionRecommendationSelector<V> {


	
	private ArrayList<GroupPredictionList<V>> getSmallestPredictionLists(Set<GroupPredictionList<V>> predictionLists, Collection<Set<V>> usedOldGroups){
		ArrayList<GroupPredictionList<V>> toSelectFrom = new ArrayList<GroupPredictionList<V>>();
		int smallestListSize = -1;
		
		//Check for a prediction list of size 1
		for(GroupPredictionList<V> predictionList : predictionLists){
			
			if(usedOldGroups.contains(predictionList.getF())){
				//Ignored already expanded group
				continue;
			}
			
			int currentSize = predictionList.size();
			if(smallestListSize == -1 || smallestListSize == currentSize){
				toSelectFrom.add(predictionList);
			}else if(currentSize < smallestListSize){
				toSelectFrom.clear();
				smallestListSize = currentSize;
				toSelectFrom.add(predictionList);
			}
			
		}
		
		return toSelectFrom;
	}
	
	private Collection<GroupPredictionList<V>> getIntersectingLists(ArrayList<GroupPredictionList<V>> predictionLists){
		
		Set<GroupPredictionList<V>> intersectingLists = new HashSet<GroupPredictionList<V>>();
		
		for(int i=0; i<predictionLists.size(); i++){
			for(int j=i+1; j<predictionLists.size(); j++){
				
				GroupPredictionList<V> list1 = predictionLists.get(i);
				GroupPredictionList<V> list2 = predictionLists.get(j);
				
				if(intersectionExists(list1.getPredictions(), list2.getPredictions())){
					intersectingLists.add(list1);
					intersectingLists.add(list2);
				}
				
			}
		}
		
		return intersectingLists;
	}
	
	@SuppressWarnings("rawtypes")
	private boolean intersectionExists(Collection a, Collection b){
		if(a.size()>b.size()){
			return intersectionExists(b, a);
		}
		
		for(Object o:a){
			if(b.contains(o)){
				return true;
			}
		}
		
		return false;
		
	}
	

	
	private static double findMaxPredictionThreshold(Collection<Set<Integer>> oldGroups, Collection<Set<Integer>> predictedGroups, Collection<Set<Integer>> ideals, Collection<Integer> newMembers){
		
		double largestPredictionSize = -1;
		double largestIdealSize = -1;
		
		for(Set<Integer> oldGroup: oldGroups){
			for(Set<Integer> prediction: predictedGroups){
				
				Set<Integer>  merge = new TreeSet<Integer>(prediction);
				merge.retainAll(newMembers);
				merge.addAll(oldGroup);
				
				if(largestPredictionSize == -1 || largestPredictionSize < merge.size()){
					largestPredictionSize = merge.size();
				}
				
			}
		}
		if(ideals != null){
			for(Set<Integer> ideal: ideals){
				if(largestIdealSize  == -1 || largestIdealSize < ideal.size() ){
					largestIdealSize = ideal.size();
				}
			}
		}
		
		return Math.sqrt(Math.pow(largestPredictionSize, 2) + Math.pow(largestIdealSize, 2) + Math.pow(newMembers.size(), 2));
		
	}
	
	public static Collection<RecommendedEvolution<Integer>> selectRecommendationsAcrossAllThresholds(double percentNew, Set<Integer> newMembers,
			Collection<Set<Integer>> oldGroups, Collection<Set<Integer>> recommenderEngineResults){
		
		return selectRecommendationsAcrossAllThresholds(percentNew, newMembers, oldGroups, recommenderEngineResults, null, null);
	}
	
	public static Collection<RecommendedEvolution<Integer>> selectRecommendationsAcrossAllThresholds(double percentNew, Set<Integer> newMembers,
			Collection<Set<Integer>> oldGroups, Collection<Set<Integer>> recommenderEngineResults, 
			Collection<Set<Integer>> ideals, Map<Set<Integer>, ArrayList<Set<Integer>>> oldToIdealGroupsMap){
		
		
		Collection<OldGroupAndPredictionPair<Integer>> usedPairings = new TreeSet<OldGroupAndPredictionPair<Integer>>(); //Tracks when an old group has been associated with a prediction 	
		Collection<Set<Integer>> usedOldGroups = new HashSet<Set<Integer>>();			//Tracks which of the modeled old groups have already been morphed
		Collection<Set<Integer>> usedRecommenderEngineResults = new HashSet<Set<Integer>>();		//Tracks which of the predicted groups have been used to morph old groups
		
		Collection<RecommendedEvolution<Integer>> allRecommendations = new ArrayList<RecommendedEvolution<Integer>>(); //Keeps track of recommendations found across all thresholds
		
		double maxThreshold = findMaxPredictionThreshold(oldGroups, recommenderEngineResults, ideals, newMembers);
		
		double threshold = 0.0;
		int round = 1;
		
		while(true){
			
			if(usedRecommenderEngineResults.size() == recommenderEngineResults.size() || threshold > maxThreshold) break; 
			//If we have no more possible matches or we have surpassed the threshold, we should stop
			
			//Create the set of unused old groups
			Collection<Set<Integer>> unusedOldGroups = new HashSet<Set<Integer>>(oldGroups);
			unusedOldGroups.removeAll(usedOldGroups);
			
			//Create set of unused recommender engine results
			Collection<Set<Integer>> unusedRecommenderEngineResults = new HashSet<Set<Integer>>(recommenderEngineResults);
			unusedRecommenderEngineResults.remove(usedRecommenderEngineResults);

			//Find all possible matchings for this threshold
			Set<GroupPredictionList<Integer>> matchings = PredictionListSelector.getAllMatchings(unusedOldGroups, unusedRecommenderEngineResults, usedPairings, newMembers, percentNew, threshold);
						
			System.out.println("\tround "+round+"...\t"+matchings.size()+" prediction lists");
			
			//Select from matchings to present recommendations to the user for this threshold
			if (matchings.size() > 0) {
				EvolutionRecommendationSelector<Integer> recommender = new EvolutionRecommendationSelector<Integer>();
				Collection<RecommendedEvolution<Integer>> recommendations = recommender.selectRecommendationsForSingleThreshold(matchings, oldToIdealGroupsMap, usedPairings, usedOldGroups, newMembers, usedRecommenderEngineResults);
				allRecommendations.addAll(recommendations);
			}

			round++;
			threshold += TestingConstants.getThresholdIncrement();
		}
		
		return allRecommendations;
	}
	
	public Collection<RecommendedEvolution<V>> selectRecommendationsForSingleThreshold(Set<GroupPredictionList<V>> matchings, Collection<OldGroupAndPredictionPair<V>> usedPairings, Collection<Set<V>> usedOldGroups, Set<V> newMembers, Collection<Set<V>> usedRecommenderEngineResults){

		//Maybe need to allow predictions to be used multiple times
		//Maybe need to allow ideals to be reached more than once

		//Four cases
		// 1: all disjoint prediction lists of length 1
		//			- definite predicted expansions, least amount of uncertainty
		//
		// 2: all prediction lists of length 1, but some overlap between lists
		//			- may mean predictions were split, so need to be reused
		//			- may mean one or more predictions are wrong and need to be ignored
		//			- need some way of testing whether predictions are appropriate (possibly by euclidean distances)
		//
		// 3: all predictions lists larger than 1, with a definite smallest list
		//			- may mean that single f_initial needs to be morphed into multiple f_primes
		//			- may mean one or more predictions are incorrectly associated and should be ignored
		//			- should not be a separate case from 4 when reusing predictions
		//
		// 4: all predictions lists larger than 1, with no definite smallest list
		//			- a hybridization of cases 2 and 3
		//			- do not attempt if no possible solution for both 2 & 3

		ArrayList<GroupPredictionList<V>> smallestPredictionLists = getSmallestPredictionLists(matchings, usedOldGroups);

		@SuppressWarnings("unchecked")
		RecommendationChooser<V> chooser = RecommendationChooserSelector.getChooser();
		Collection<RecommendedEvolution<V>> recommendations = new TreeSet<RecommendedEvolution<V>>();

		//boolean stopMorphing = false;
		while(smallestPredictionLists.size() > 0){
			System.out.print("\t"+smallestPredictionLists.size()+" smallest lists,");

			if(smallestPredictionLists.get(0).size() == 1){
				//Case 1 or 2

				for(GroupPredictionList<V> list: smallestPredictionLists){
					if(!usedOldGroups.contains(list.getF())){

					}
				}

				Collection<GroupPredictionList<V>> intersectingLists = getIntersectingLists(smallestPredictionLists);

				if(intersectingLists.size() == 0){
					//Case 1
					System.out.print("Case1");
					Collection<RecommendedEvolution<V>> currentIterationRecommendations = chooser.modelPredictionChoosingCase1(smallestPredictionLists, newMembers, matchings, usedPairings, usedOldGroups, usedRecommenderEngineResults);
					recommendations.addAll(currentIterationRecommendations);

				}else{
					//Case 2
					System.out.print("Case2");
					Collection<RecommendedEvolution<V>> currentIterationRecommendations = chooser.modelPredictionChoosingCase2(smallestPredictionLists, intersectingLists, newMembers, smallestPredictionLists, usedPairings, usedOldGroups, usedRecommenderEngineResults);
					recommendations.addAll(currentIterationRecommendations);

				}
				
			}else{
				//Case  3 or 4
				System.out.print("Case3 or Case4");
				
				/*for(GroupPredictionList<V> list: smallestPredictionLists){
					if(!usedOldGroups.contains(list.getF())){
						oldMembersWithMultipleMatchings.add(list.getF());
					}
				}*/
				
				/*if(smallestPredictionLists.size() == 1 && predictionLists.size() == 1){
					//Case 3 where only one predictionList exists
					int[] stats = modelPredictedExpansionsSelectionCase3(smallestPredictionLists.get(0), newMembers, predictionLists, oldToIdealGroupsMap, usedOldGroups, usedPredictedGroups, usedIdealGroups);
					expandedGroups += stats[0];
					manualAddsRequired += stats[1];
					manualDeletesRequired += stats[2];
					selectCount += stats[3];
					automatedAdds += stats[4];
				}else{

					int numOldUsed = usedOldGroups.size();
					int numOldPredicted = predictionLists.size();
					int coveredOldLists = usedOldGroups.size() + predictionLists.size();
					if(totalOldLists == coveredOldLists) {
						stopMorphing = true;
					}
					break;
					//TODO
				}*/
				
				/*int numOldUsed = usedOldGroups.size();
				int numOldPredicted = matchings.size();
				int coveredOldLists = usedOldGroups.size() + matchings.size();
				if(totalOldLists == coveredOldLists) {
					stopMorphing = true;
				}*/
				break;
			}
			
			smallestPredictionLists = getSmallestPredictionLists(matchings, usedOldGroups);
		}
		return recommendations;
	}
	
	public Collection<RecommendedEvolution<V>> selectRecommendationsForSingleThreshold(Set<GroupPredictionList<V>> matchings, Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap, Collection<OldGroupAndPredictionPair<V>> usedPairings, Collection<Set<V>> usedOldGroups, Set<V> newMembers, Collection<Set<V>> usedRecommenderEngineResults){

		//Maybe need to allow predictions to be used multiple times
		//Maybe need to allow ideals to be reached more than once

		//Four cases
		// 1: all disjoint prediction lists of length 1
		//			- definite predicted expansions, least amount of uncertainty
		//
		// 2: all prediction lists of length 1, but some overlap between lists
		//			- may mean predictions were split, so need to be reused
		//			- may mean one or more predictions are wrong and need to be ignored
		//			- need some way of testing whether predictions are appropriate (possibly by euclidean distances)
		//
		// 3: all predictions lists larger than 1, with a definite smallest list
		//			- may mean that single f_initial needs to be morphed into multiple f_primes
		//			- may mean one or more predictions are incorrectly associated and should be ignored
		//			- should not be a separate case from 4 when reusing predictions
		//
		// 4: all predictions lists larger than 1, with no definite smallest list
		//			- a hybridization of cases 2 and 3
		//			- do not attempt if no possible solution for both 2 & 3

		ArrayList<GroupPredictionList<V>> smallestPredictionLists = getSmallestPredictionLists(matchings, usedOldGroups);

		@SuppressWarnings("unchecked")
		RecommendationChooser<V> chooser = RecommendationChooserSelector.getChooser();
		Collection<RecommendedEvolution<V>> recommendations = new TreeSet<RecommendedEvolution<V>>();

		//boolean stopMorphing = false;
		while(smallestPredictionLists.size() > 0){
			System.out.print("\t"+smallestPredictionLists.size()+" smallest lists,");

			if(smallestPredictionLists.get(0).size() == 1){
				//Case 1 or 2

				for(GroupPredictionList<V> list: smallestPredictionLists){
					if(!usedOldGroups.contains(list.getF())){

					}
				}

				Collection<GroupPredictionList<V>> intersectingLists = getIntersectingLists(smallestPredictionLists);

				if(intersectingLists.size() == 0){
					//Case 1
					System.out.print("Case1");
					Collection<RecommendedEvolution<V>> currentIterationRecommendations = chooser.modelPredictionChoosingCase1(smallestPredictionLists, newMembers, matchings, usedPairings, usedOldGroups, usedRecommenderEngineResults);
					recommendations.addAll(currentIterationRecommendations);

				}else{
					//Case 2
					System.out.print("Case2");
					Collection<RecommendedEvolution<V>> currentIterationRecommendations = chooser.modelPredictionChoosingCase2(smallestPredictionLists, intersectingLists, newMembers, smallestPredictionLists, usedPairings, usedOldGroups, usedRecommenderEngineResults);
					if (currentIterationRecommendations != null) {
						recommendations.addAll(currentIterationRecommendations);
					} else {
						System.out.println("No non-intersecting lists");
						break;
						// Not enough certainty to predict growth at this point.
						// TODO: figure out a way to address uncertainty
					}

				}
				
			}else{
				//Case  3 or 4
				System.out.print("Case3 or Case4");
				
				/*for(GroupPredictionList<V> list: smallestPredictionLists){
					if(!usedOldGroups.contains(list.getF())){
						oldMembersWithMultipleMatchings.add(list.getF());
					}
				}*/
				
				/*if(smallestPredictionLists.size() == 1 && predictionLists.size() == 1){
					//Case 3 where only one predictionList exists
					int[] stats = modelPredictedExpansionsSelectionCase3(smallestPredictionLists.get(0), newMembers, predictionLists, oldToIdealGroupsMap, usedOldGroups, usedPredictedGroups, usedIdealGroups);
					expandedGroups += stats[0];
					manualAddsRequired += stats[1];
					manualDeletesRequired += stats[2];
					selectCount += stats[3];
					automatedAdds += stats[4];
				}else{

					int numOldUsed = usedOldGroups.size();
					int numOldPredicted = predictionLists.size();
					int coveredOldLists = usedOldGroups.size() + predictionLists.size();
					if(totalOldLists == coveredOldLists) {
						stopMorphing = true;
					}
					break;
					//TODO
				}*/
				
				/*int numOldUsed = usedOldGroups.size();
				int numOldPredicted = matchings.size();
				int coveredOldLists = usedOldGroups.size() + matchings.size();
				if(totalOldLists == coveredOldLists) {
					stopMorphing = true;
				}*/
				break;
			}
			
			smallestPredictionLists = getSmallestPredictionLists(matchings, usedOldGroups);
		}
		return recommendations;
	}
}
