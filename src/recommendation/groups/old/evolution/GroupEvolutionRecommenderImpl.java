package recommendation.groups.old.evolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import recommendation.groups.old.evolution.predictions.lists.PredictionListSelector;
import recommendation.groups.old.evolution.predictions.oldchoosers.OldGroupAndPredictionPair;
import recommendation.groups.old.seedless.SeedlessGroupRecommender;
import recommendation.groups.old.seedless.kelli.HybridCliqueMerger;
import bus.tools.TestingConstants;

public class GroupEvolutionRecommenderImpl<V> implements GroupEvolutionRecommender<V> {
	
	private SeedlessGroupRecommender<V> recommenderEngine;

	@Override
	public Collection<RecommendedEvolution<V>> generateRecommendations(
			UndirectedGraph<V, DefaultEdge> socialGraph, Collection<Set<V>> oldGroups,
			Set<V> newMembers, Set<V> removedMembers,
			Collection<Entry<V, V>> removedEdges, Collection<Entry<V, V>> addedEdges) {
		
		oldGroups = (oldGroups == null)? new TreeSet<Set<V>>() : oldGroups;
		newMembers = (newMembers == null)? new TreeSet<V>() : newMembers;
		removedMembers = (removedMembers == null)? new TreeSet<V>() : removedMembers;
		removedEdges = (removedEdges == null)? new ArrayList<Entry<V,V>>() : removedEdges;
		addedEdges = (addedEdges == null)? new ArrayList<Entry<V,V>>() : addedEdges;
		
		
		//Tracks when an old group has been associated with a prediction
		Collection<OldGroupAndPredictionPair<V>> usedPairings = new HashSet<OldGroupAndPredictionPair<V>>(); 	
		
		//Tracks which of the old groups have already been evolved
		Collection<Set<V>> usedOldGroups = new HashSet<Set<V>>();
		
		//Tracks which of the predicted groups have been used to evolve old groups
		Collection<Set<V>> usedRecommenderEngineResults = new HashSet<Set<V>>();
		
		//Keeps track of recommendations found across all thresholds
		Collection<RecommendedEvolution<V>> allRecommendations = new ArrayList<RecommendedEvolution<V>>(); 
		
		recommenderEngine = new HybridCliqueMerger<V>(socialGraph);
		Collection<Set<V>> recommenderEngineResults = recommenderEngine.getRecommendations();
		
		int maxOldGroupSize = 0;
		for (Set<V> oldGroup : oldGroups) {
			maxOldGroupSize = Math.max(maxOldGroupSize, oldGroup.size());
		}
		double maxThreshold = maxOldGroupSize + newMembers.size() + removedMembers.size();
		
		double threshold = 0.0;
		int round = 1;
		
		double percentNew  = ((double) newMembers.size())/((double) socialGraph.edgeSet().size());
		
		while(true){
			
			if(usedRecommenderEngineResults.size() == recommenderEngineResults.size() || threshold > maxThreshold) break; 
			//If we have no more possible matches or we have surpassed the threshold, we should stop
			
			//Create the set of unused old groups
			Collection<Set<V>> unusedOldGroups = new HashSet<Set<V>>(oldGroups);
			unusedOldGroups.removeAll(usedOldGroups);
			
			//Create set of unused recommender engine results
			Collection<Set<V>> unusedRecommenderEngineResults = new HashSet<Set<V>>(recommenderEngineResults);
			unusedRecommenderEngineResults.remove(usedRecommenderEngineResults);

			//Find all possible matchings for this threshold
			Set<GroupPredictionList<V>> matchings = PredictionListSelector.getAllMatchings(unusedOldGroups, unusedRecommenderEngineResults, usedPairings, newMembers, percentNew, threshold);
						
			System.out.println("\tround "+round+"...\t"+unusedOldGroups.size()+" unused old groups,"+matchings.size()+" prediction lists");
			
			//Select from matchings to present recommendations to the user for this threshold
			EvolutionRecommendationSelector<V> recommender = new EvolutionRecommendationSelector<V>();
			Collection<RecommendedEvolution<V>> recommendations = recommender.selectRecommendationsForSingleThreshold(matchings, usedPairings, usedOldGroups, newMembers, usedRecommenderEngineResults);
			allRecommendations.addAll(recommendations);
			

			round++;
			threshold += TestingConstants.getThresholdIncrement();
		}
		
		return allRecommendations;
	}
	
	public static void main(String[] args) {
		GroupMorphingModeler.selectExpectedScaling();
		
		UndirectedGraph<Integer, DefaultEdge> socialGraph = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
		socialGraph.addVertex(1);
		socialGraph.addVertex(2);
		socialGraph.addVertex(3);
		socialGraph.addVertex(4);
		socialGraph.addVertex(5);
		socialGraph.addVertex(6);
		socialGraph.addEdge(1, 2);
		socialGraph.addEdge(1, 3);
		socialGraph.addEdge(1, 4);
		socialGraph.addEdge(1, 5);
		socialGraph.addEdge(1, 6);
		socialGraph.addEdge(2, 3);
		socialGraph.addEdge(2, 4);
		socialGraph.addEdge(2, 5);
		socialGraph.addEdge(2, 6);
		socialGraph.addEdge(3, 4);
		Set<Integer> newMembers = new TreeSet<Integer>();
		newMembers.add(5);
		
		Set<Integer> oldGroup = new TreeSet<Integer>();
		oldGroup.add(1);
		oldGroup.add(2);
		oldGroup.add(3);
		oldGroup.add(4);
		Collection<Set<Integer>> oldGroups = new ArrayList<Set<Integer>>();
		oldGroups.add(oldGroup);
		
		GroupEvolutionRecommender<Integer> recommender = new GroupEvolutionRecommenderImpl<Integer>();
		System.out.println(recommender.generateRecommendations(socialGraph, oldGroups, newMembers, null, null, null));
	}
}
