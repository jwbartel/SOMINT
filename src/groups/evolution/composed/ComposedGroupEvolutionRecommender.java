package groups.evolution.composed;

import groups.evolution.GroupEvolutionRecommender;
import groups.evolution.GroupPredictionList;
import groups.evolution.predictions.lists.PredictionListSelector;
import groups.evolution.predictions.oldchoosers.OldGroupAndPredictionPair;
import groups.evolution.recommendations.RecommendedEvolution;
import groups.evolution.recommendations.RecommendedGroupChangeEvolution;
import groups.evolution.recommendations.RecommendedGroupCreationEvolution;
import groups.seedless.SeedlessGroupRecommender;
import groups.seedless.SeedlessGroupRecommenderFactory;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class ComposedGroupEvolutionRecommender<V> implements GroupEvolutionRecommender<V> {
	
	private static final double DEFAULT_THRESHOLD_INCREMENT = 1.0;

	private final SeedlessGroupRecommenderFactory<V> recommenderEngineFactory;
	private final Collection<Set<V>> prefetchedSeedlessRecommendations;
	private final double thresholdIncrement;
	
	public ComposedGroupEvolutionRecommender(SeedlessGroupRecommenderFactory<V> recommenderEngineFactory) {
		this.recommenderEngineFactory = recommenderEngineFactory;
		this.prefetchedSeedlessRecommendations = null;
		this.thresholdIncrement = DEFAULT_THRESHOLD_INCREMENT;
	}
	
	public ComposedGroupEvolutionRecommender(SeedlessGroupRecommenderFactory<V> recommenderEngineFactory,
			Collection<Set<V>> prefetchedSeedlessRecommendations) {
		this.recommenderEngineFactory = recommenderEngineFactory;
		this.prefetchedSeedlessRecommendations = prefetchedSeedlessRecommendations;
		this.thresholdIncrement = DEFAULT_THRESHOLD_INCREMENT;
	}
	
	public ComposedGroupEvolutionRecommender(SeedlessGroupRecommenderFactory<V> recommenderEngineFactory, double thresholdIncrement) {
		this.recommenderEngineFactory = recommenderEngineFactory;
		this.prefetchedSeedlessRecommendations = null;
		this.thresholdIncrement = thresholdIncrement;
	}
	
	@Override
	public String getTypeOfRecommender() {
		return "composed";
	}
	
	@Override
	public Collection<RecommendedEvolution<V>> generateRecommendations(
			UndirectedGraph<V, DefaultEdge> oldSocialGraph,
			UndirectedGraph<V, DefaultEdge> socialGraph, Collection<Set<V>> oldGroups) {

		return generateRecommendations(oldSocialGraph, null, socialGraph, oldGroups);
	}
	
	@Override
	public Collection<RecommendedEvolution<V>> generateRecommendations(
			UndirectedGraph<V, DefaultEdge> oldSocialGraph, Collection<Set<V>> maximalCliques,
			UndirectedGraph<V, DefaultEdge> socialGraph, Collection<Set<V>> oldGroups) {

		Set<V> newMembers = new HashSet<V>(socialGraph.vertexSet());
		newMembers.removeAll(oldSocialGraph.vertexSet());

		Set<V> removedMembers = new HashSet<V>(oldSocialGraph.vertexSet());
		newMembers.removeAll(socialGraph.vertexSet());

		Collection<DefaultEdge> removedEdgeObjs = new HashSet<DefaultEdge>(oldSocialGraph.edgeSet());
		removedEdgeObjs.removeAll(socialGraph.edgeSet());
		Collection<Entry<V, V>> removedEdges = new HashSet<Entry<V, V>>();
		for (DefaultEdge edge : removedEdgeObjs) {
			removedEdges.add(new SimpleEntry<>(
					oldSocialGraph.getEdgeSource(edge),
					oldSocialGraph.getEdgeTarget(edge)));
		}

		Collection<DefaultEdge> addedEdgeObjs = new HashSet<DefaultEdge>(socialGraph.edgeSet());
		addedEdgeObjs.removeAll(oldSocialGraph.edgeSet());
		Collection<Entry<V, V>> addedEdges = new HashSet<Entry<V, V>>();
		for (DefaultEdge edge : addedEdgeObjs) {
			addedEdges.add(new SimpleEntry<>(
					socialGraph.getEdgeSource(edge),
					socialGraph.getEdgeTarget(edge)));
		}

		return generateRecommendations(socialGraph, oldGroups, newMembers, removedMembers,
				removedEdges, addedEdges);
	}

	@Override
	public Collection<RecommendedEvolution<V>> generateRecommendations(
			UndirectedGraph<V, DefaultEdge> socialGraph, Collection<Set<V>> oldGroups,
			Set<V> newMembers, Set<V> removedMembers, Collection<Entry<V, V>> removedEdges,
			Collection<Entry<V, V>> addedEdges) {
		return generateRecommendations(socialGraph, null, oldGroups, newMembers, removedMembers,
				removedEdges, addedEdges);
	}

	@Override
	public Collection<RecommendedEvolution<V>> generateRecommendations(
			UndirectedGraph<V, DefaultEdge> socialGraph, Collection<Set<V>> maximalCliques,
			Collection<Set<V>> oldGroups, Set<V> newMembers, Set<V> removedMembers,
			Collection<Entry<V, V>> removedEdges, Collection<Entry<V, V>> addedEdges) {
		
		// Fix any null arguments
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
		
		Collection<Set<V>> recommenderEngineResults;
		if (prefetchedSeedlessRecommendations != null) {
			recommenderEngineResults = new HashSet<>(prefetchedSeedlessRecommendations);
		} else {
			SeedlessGroupRecommender<V> recommenderEngine;
			if (maximalCliques != null) {
				recommenderEngine = recommenderEngineFactory.create(socialGraph, maximalCliques);
			} else {
				recommenderEngine = recommenderEngineFactory.create(socialGraph);
			}
			recommenderEngineResults = recommenderEngine.getRecommendations();
		}
		
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
			EvolutionRecommendationMatcher<V> recommender = new EvolutionRecommendationMatcher<V>();
			Collection<RecommendedGroupChangeEvolution<V>> recommendations = recommender.selectRecommendationsForSingleThreshold(matchings, usedPairings, usedOldGroups, newMembers, usedRecommenderEngineResults);
			allRecommendations.addAll(recommendations);
			

			round++;
			threshold += thresholdIncrement;
		}
		
		if (usedRecommenderEngineResults.size() != recommenderEngineResults.size()) {
			Collection<Set<V>> unusedRecommenderEngineResults = new HashSet<>(recommenderEngineResults);
			unusedRecommenderEngineResults.removeAll(usedRecommenderEngineResults);
			for (Set<V> unusedRecommenderEngineResult : unusedRecommenderEngineResults) {
				allRecommendations.add(new RecommendedGroupCreationEvolution<>(unusedRecommenderEngineResult));
			}
		}
		
		return allRecommendations;
	}

}
