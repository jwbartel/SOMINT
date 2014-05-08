package groups.evolution.composed;

import groups.evolution.GroupPredictionList;
import groups.evolution.composed.cleanuppers.RecommendationCleanupperFactory;
import groups.evolution.composed.cleanuppers.SingleRecommenderEngineResultRecommendationCleanupperFactory;
import groups.evolution.composed.listmaker.ExpectedScalingPredictionListMakerFactory;
import groups.evolution.composed.listmaker.PredictionListMakerFactory;
import groups.evolution.predictions.oldchoosers.OldGroupAndPredictionPair;
import groups.evolution.recommendations.RecommendedGroupChangeEvolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import bus.tools.TestingConstants;

//Selects recommendations based on a one-to-one mapping
//It would be good to include one-to-many, many-to-one, and many-to-many mappings here too
public class EvolutionRecommendationMatcher<V> {
	private RecommendationCleanupperFactory<V> cleanupperFactory;
	private PredictionListMakerFactory<V> listMakerFactory;
	
	public EvolutionRecommendationMatcher () {
		init(null, null);
	}
	
	public EvolutionRecommendationMatcher (RecommendationCleanupperFactory<V> cleanupperFactory,
			PredictionListMakerFactory<V> listMakerFactory) {
		init(cleanupperFactory, listMakerFactory);
	}
	
	private void init(RecommendationCleanupperFactory<V> cleanupperFactory,
			PredictionListMakerFactory<V> listMakerFactory) {
		if (cleanupperFactory != null) {
			this.cleanupperFactory = cleanupperFactory;
		} else {
			this.cleanupperFactory = new SingleRecommenderEngineResultRecommendationCleanupperFactory<>();
		}
		
		if (listMakerFactory != null) {
			this.listMakerFactory = listMakerFactory;
		} else {
			this.listMakerFactory = new ExpectedScalingPredictionListMakerFactory<>();
		}
	}

	private ArrayList<GroupPredictionList<V>> getSmallestPredictionLists(
			Set<GroupPredictionList<V>> predictionLists,
			Collection<Set<V>> usedOldGroups) {
		ArrayList<GroupPredictionList<V>> toSelectFrom = new ArrayList<GroupPredictionList<V>>();
		int smallestListSize = -1;

		// Check for a prediction list of size 1
		for (GroupPredictionList<V> predictionList : predictionLists) {

			if (usedOldGroups.contains(predictionList.getF())) {
				// Ignored already expanded group
				continue;
			}

			int currentSize = predictionList.size();
			if (smallestListSize == -1 || smallestListSize == currentSize) {
				toSelectFrom.add(predictionList);
			} else if (currentSize < smallestListSize) {
				toSelectFrom.clear();
				smallestListSize = currentSize;
				toSelectFrom.add(predictionList);
			}

		}

		return toSelectFrom;
	}

	private Collection<GroupPredictionList<V>> getIntersectingLists(
			ArrayList<GroupPredictionList<V>> predictionLists) {

		Set<GroupPredictionList<V>> intersectingLists = new HashSet<GroupPredictionList<V>>();

		for (int i = 0; i < predictionLists.size(); i++) {
			for (int j = i + 1; j < predictionLists.size(); j++) {

				GroupPredictionList<V> list1 = predictionLists.get(i);
				GroupPredictionList<V> list2 = predictionLists.get(j);

				if (intersectionExists(list1.getPredictions(),
						list2.getPredictions())) {
					intersectingLists.add(list1);
					intersectingLists.add(list2);
				}

			}
		}

		return intersectingLists;
	}

	@SuppressWarnings("rawtypes")
	private boolean intersectionExists(Collection a, Collection b) {
		if (a.size() > b.size()) {
			return intersectionExists(b, a);
		}

		for (Object o : a) {
			if (b.contains(o)) {
				return true;
			}
		}

		return false;

	}

	private double findMaxPredictionThreshold(
			Collection<Set<V>> oldGroups,
			Collection<Set<V>> predictedGroups,
			Collection<Set<V>> ideals, Collection<V> newMembers) {

		double largestPredictionSize = -1;
		double largestIdealSize = -1;

		for (Set<V> oldGroup : oldGroups) {
			for (Set<V> prediction : predictedGroups) {

				Set<V> merge = new TreeSet<V>(prediction);
				merge.retainAll(newMembers);
				merge.addAll(oldGroup);

				if (largestPredictionSize == -1
						|| largestPredictionSize < merge.size()) {
					largestPredictionSize = merge.size();
				}

			}
		}
		if (ideals != null) {
			for (Set<V> ideal : ideals) {
				if (largestIdealSize == -1 || largestIdealSize < ideal.size()) {
					largestIdealSize = ideal.size();
				}
			}
		}

		return Math.sqrt(Math.pow(largestPredictionSize, 2)
				+ Math.pow(largestIdealSize, 2)
				+ Math.pow(newMembers.size(), 2));

	}

	public Collection<RecommendedGroupChangeEvolution<V>> selectRecommendationsAcrossAllThresholds(
			double percentNew, Set<V> newMembers,
			Collection<Set<V>> oldGroups,
			Collection<Set<V>> recommenderEngineResults) {

		return selectRecommendationsAcrossAllThresholds(percentNew, newMembers,
				oldGroups, recommenderEngineResults, null);
	}

	public Collection<RecommendedGroupChangeEvolution<V>> selectRecommendationsAcrossAllThresholds(
			double percentNew, Set<V> newMembers,
			Collection<Set<V>> oldGroups,
			Collection<Set<V>> recommenderEngineResults,
			Collection<Set<V>> ideals) {

		// Tracks when an old group has been associated with a prediction
		Collection<OldGroupAndPredictionPair<V>> usedPairings = new TreeSet<OldGroupAndPredictionPair<V>>();

		// Tracks which of the modeled old groups have already been morphed into a prediction
		Collection<Set<V>> usedOldGroups = new HashSet<Set<V>>();
		
		// Tracks which of the predicted groups have been used to morph old
		// groups
		Collection<Set<V>> usedRecommenderEngineResults = new HashSet<Set<V>>();
		
		// Keeps track of recommendations found across all thresholds
		Collection<RecommendedGroupChangeEvolution<V>> allRecommendations = new ArrayList<RecommendedGroupChangeEvolution<V>>(); 

		double maxThreshold = findMaxPredictionThreshold(oldGroups,
				recommenderEngineResults, ideals, newMembers);

		double threshold = 0.0;
		int round = 1;

		while (true) {

			if (usedRecommenderEngineResults.size() == recommenderEngineResults
					.size() || threshold > maxThreshold)
				break;
			// If we have no more possible matches or we have surpassed the
			// threshold, we should stop

			// Create the set of unused old groups
			Collection<Set<V>> unusedOldGroups = new HashSet<Set<V>>(
					oldGroups);
			unusedOldGroups.removeAll(usedOldGroups);

			// Create set of unused recommender engine results
			Collection<Set<V>> unusedRecommenderEngineResults = new HashSet<Set<V>>(
					recommenderEngineResults);
			unusedRecommenderEngineResults.remove(usedRecommenderEngineResults);

			// Find all possible matchings for this threshold
			Set<GroupPredictionList<V>> matchings = listMakerFactory.getPredictionListMaker()
					.getAllMatchings(unusedOldGroups, unusedRecommenderEngineResults, usedPairings,
							newMembers, percentNew, threshold);

			System.out.println("\tround " + round + "...\t" + matchings.size()
					+ " prediction lists");

			// Select from matchings to present recommendations to the user for
			// this threshold
			if (matchings.size() > 0) {
				
				Collection<RecommendedGroupChangeEvolution<V>> recommendations = selectRecommendationsForSingleThreshold(
						matchings, usedPairings, usedOldGroups, newMembers,
						usedRecommenderEngineResults);
				allRecommendations.addAll(recommendations);
			}

			round++;
			threshold += TestingConstants.getThresholdIncrement();
		}

		return allRecommendations;
	}

	public Collection<RecommendedGroupChangeEvolution<V>> selectRecommendationsForSingleThreshold(
			Set<GroupPredictionList<V>> matchings,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Set<V> newMembers,
			Collection<Set<V>> usedRecommenderEngineResults) {

		// Maybe need to allow predictions to be used multiple times
		// Maybe need to allow ideals to be reached more than once

		// Four cases
		// 1: all disjoint prediction lists of length 1
		// - definite predicted expansions, least amount of uncertainty
		//
		// 2: all prediction lists of length 1, but some overlap between lists
		// - may mean predictions were split, so need to be reused
		// - may mean one or more predictions are wrong and need to be ignored
		// - need some way of testing whether predictions are appropriate
		// (possibly by euclidean distances)
		//
		// 3: all predictions lists larger than 1, with a definite smallest list
		// - may mean that single f_initial needs to be morphed into multiple
		// f_primes
		// - may mean one or more predictions are incorrectly associated and
		// should be ignored
		// - should not be a separate case from 4 when reusing predictions
		//
		// 4: all predictions lists larger than 1, with no definite smallest
		// list
		// - a hybridization of cases 2 and 3
		// - do not attempt if no possible solution for both 2 & 3

		ArrayList<GroupPredictionList<V>> smallestPredictionLists = getSmallestPredictionLists(
				matchings, usedOldGroups);

		Collection<RecommendedGroupChangeEvolution<V>> recommendations = new TreeSet<RecommendedGroupChangeEvolution<V>>();

		// boolean stopMorphing = false;
		while (smallestPredictionLists.size() > 0) {
			System.out.print("\t" + smallestPredictionLists.size()
					+ " smallest lists,");

			Collection<RecommendedGroupChangeEvolution<V>> currentIterationRecommendations;
			if (smallestPredictionLists.get(0).size() == 1) {
				// Case 1 or 2

				Collection<GroupPredictionList<V>> intersectingLists = getIntersectingLists(smallestPredictionLists);

				if (intersectingLists.size() == 0) {
					// Case 1
					System.out.print("Case1");
					currentIterationRecommendations = modelPredictionChoosingCase1(
							smallestPredictionLists, newMembers, matchings,
							usedPairings, usedOldGroups,
							usedRecommenderEngineResults);

				} else {
					// Case 2
					System.out.print("Case2");
					currentIterationRecommendations = modelPredictionChoosingCase2(
							smallestPredictionLists, intersectingLists,
							newMembers, smallestPredictionLists, usedPairings,
							usedOldGroups, usedRecommenderEngineResults);

				}

			} else {
				// Case 3 or 4
				System.out.print("Case3 or Case4");
				currentIterationRecommendations = null;
				// TODO: handle case 3 or 4

				break;
			}

			if (currentIterationRecommendations != null
					&& currentIterationRecommendations.size() > 0) {
				recommendations.addAll(currentIterationRecommendations);
			} else {
				break;
			}
			smallestPredictionLists = getSmallestPredictionLists(matchings,
					usedOldGroups);
		}
		return recommendations;
	}

	public Collection<RecommendedGroupChangeEvolution<V>> selectRecommendationsForSingleThreshold(
			Set<GroupPredictionList<V>> matchings,
			Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups, Set<V> newMembers,
			Collection<Set<V>> usedRecommenderEngineResults) {

		// Maybe need to allow predictions to be used multiple times
		// Maybe need to allow ideals to be reached more than once

		// Four cases
		// 1: all disjoint prediction lists of length 1
		// - definite predicted expansions, least amount of uncertainty
		//
		// 2: all prediction lists of length 1, but some overlap between lists
		// - may mean predictions were split, so need to be reused
		// - may mean one or more predictions are wrong and need to be ignored
		// - need some way of testing whether predictions are appropriate
		// (possibly by euclidean distances)
		//
		// 3: all predictions lists larger than 1, with a definite smallest list
		// - may mean that single f_initial needs to be morphed into multiple
		// f_primes
		// - may mean one or more predictions are incorrectly associated and
		// should be ignored
		// - should not be a separate case from 4 when reusing predictions
		//
		// 4: all predictions lists larger than 1, with no definite smallest
		// list
		// - a hybridization of cases 2 and 3
		// - do not attempt if no possible solution for both 2 & 3

		ArrayList<GroupPredictionList<V>> smallestPredictionLists = getSmallestPredictionLists(
				matchings, usedOldGroups);

		Collection<RecommendedGroupChangeEvolution<V>> recommendations = new TreeSet<RecommendedGroupChangeEvolution<V>>();

		// boolean stopMorphing = false;
		while (smallestPredictionLists.size() > 0) {
			System.out.print("\t" + smallestPredictionLists.size()
					+ " smallest lists,");

			Collection<RecommendedGroupChangeEvolution<V>> currentIterationRecommendations;
			if (smallestPredictionLists.get(0).size() == 1) {
				// Case 1 or 2

				Collection<GroupPredictionList<V>> intersectingLists = getIntersectingLists(smallestPredictionLists);

				if (intersectingLists.size() == 0) {
					// Case 1
					System.out.print("Case1");
					currentIterationRecommendations = modelPredictionChoosingCase1(
							smallestPredictionLists, newMembers, matchings,
							usedPairings, usedOldGroups,
							usedRecommenderEngineResults);

				} else {
					// Case 2
					System.out.print("Case2");
					currentIterationRecommendations = modelPredictionChoosingCase2(
							smallestPredictionLists, intersectingLists,
							newMembers, smallestPredictionLists, usedPairings,
							usedOldGroups, usedRecommenderEngineResults);
				}

			} else {
				// Case 3 or 4
				System.out.print("Case3 or Case4");
				currentIterationRecommendations = null;
			}

			if (currentIterationRecommendations != null) {
				recommendations.addAll(currentIterationRecommendations);
			} else {
				break;
			}

			smallestPredictionLists = getSmallestPredictionLists(matchings,
					usedOldGroups);
		}
		return recommendations;
	}

	public Collection<RecommendedGroupChangeEvolution<V>> modelPredictionChoosingCase1(
			ArrayList<GroupPredictionList<V>> smallestPredictionLists,
			Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups,
			Collection<Set<V>> usedPredictedGroups) {

		Collection<RecommendedGroupChangeEvolution<V>> recommendations = new TreeSet<RecommendedGroupChangeEvolution<V>>();

		for (int i = 0; i < smallestPredictionLists.size(); i++) {

			GroupPredictionList<V> predictionList = smallestPredictionLists
					.get(i);

			Set<V> oldGroup = predictionList.getF();
			if (usedOldGroups.contains(oldGroup) || predictionList.size() == 0) {
				continue;
			}

			Set<V> recommenderEngineResult = predictionList.getPredictions()
					.iterator().next();
			RecommendedGroupChangeEvolution<V> recommendedEvolution = new RecommendedGroupChangeEvolution<V>(
					oldGroup, recommenderEngineResult, newMembers);
			recommendations.add(recommendedEvolution);

			cleanupperFactory.createRecommendationCleanupper().removeSelection(oldGroup,
					recommenderEngineResult, predictionLists, usedPairings, usedOldGroups,
					usedPredictedGroups);

		}

		return recommendations;
	}

	public Collection<RecommendedGroupChangeEvolution<V>> modelPredictionChoosingCase2(
			ArrayList<GroupPredictionList<V>> smallestPredictionLists,
			Collection<GroupPredictionList<V>> intersectingLists,
			Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups,
			Collection<Set<V>> usedPredictedGroups) {

		ArrayList<GroupPredictionList<V>> disjointLists = new ArrayList<GroupPredictionList<V>>(
				smallestPredictionLists);
		disjointLists.removeAll(intersectingLists);
		if (disjointLists.size() != 0) {
			return modelPredictionChoosingCase1(disjointLists, newMembers,
					predictionLists, usedPairings, usedOldGroups,
					usedPredictedGroups);
		} else {
			return null;
		}

		// TODO:handle non-disjoint lists
	}

	public Collection<RecommendedGroupChangeEvolution<V>> modelPredictionChoosingCase3(
			GroupPredictionList<V> predictionList, Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists,
			Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups,
			Collection<Set<V>> usedPredictedGroups,
			Collection<Set<V>> usedIdeals) {

		/*
		 * Set<V> oldGroup = predictionList.getF();
		 * if(!usedOldGroups.contains(oldGroup) && predictionList.size() != 0){
		 * 
		 * for(Set<V> prediction: predictionList.getPredictions()){
		 * 
		 * Set<V> membersToAdd = new TreeSet<V>(prediction);
		 * membersToAdd.retainAll(newMembers);
		 * 
		 * ArrayList<Set<V>> possibleIdealExpansions =
		 * oldToIdealGroupsMap.get(oldGroup); Set<V> bestIdealExpansion =
		 * getBestIdealExpansion(oldGroup, membersToAdd,
		 * possibleIdealExpansions, usedIdeals);
		 * 
		 * 
		 * }
		 * 
		 * }
		 */
		return null;
	}

	public Collection<RecommendedGroupChangeEvolution<V>> modelPredictionChoosingCase4(
			ArrayList<GroupPredictionList<V>> smallestPredictionLists,
			Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists,
			Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedOldGroups,
			Collection<Set<V>> usedPredictedGroups,
			Collection<Set<V>> usedIdeals) {
		// TODO Auto-generated method stub
		return null;
	}
}
