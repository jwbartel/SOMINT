package groups.evolution.fullrecommendation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import groups.evolution.GroupEvolutionRecommender;
import groups.evolution.recommendations.RecommendedEvolution;
import groups.evolution.recommendations.RecommendedGroupCreationEvolution;
import groups.seedless.SeedlessGroupRecommender;
import groups.seedless.SeedlessGroupRecommenderFactory;

public class FullRecommendationGroupEvolutionRecommender<V> implements
		GroupEvolutionRecommender<V> {

	@Override
	public String getTypeOfRecommender() {
		return "full recommendation";
	}

	private final SeedlessGroupRecommenderFactory<V> recommenderEngineFactory;
	private final Collection<Set<V>> prefetchedSeedlessRecommendations;

	public FullRecommendationGroupEvolutionRecommender(
			SeedlessGroupRecommenderFactory<V> recommenderEngineFactory) {
		this.recommenderEngineFactory = recommenderEngineFactory;
		this.prefetchedSeedlessRecommendations = null;
	}

	public FullRecommendationGroupEvolutionRecommender(
			SeedlessGroupRecommenderFactory<V> recommenderEngineFactory,
			Collection<Set<V>> prefetchedSeedlessRecommendations) {
		this.recommenderEngineFactory = recommenderEngineFactory;
		this.prefetchedSeedlessRecommendations = prefetchedSeedlessRecommendations;
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
			UndirectedGraph<V, DefaultEdge> socialGraph,
			Collection<Set<V>> maximalCliques, Collection<Set<V>> oldGroups,
			Set<V> newMembers, Set<V> removedMembers,
			Collection<Entry<V, V>> removedEdges,
			Collection<Entry<V, V>> addedEdges) {

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
		
		Collection<RecommendedEvolution<V>> allRecommendations = new ArrayList<RecommendedEvolution<V>>(); 
		for (Set<V> recommenderEngineResult : recommenderEngineResults) {
			allRecommendations.add(new RecommendedGroupCreationEvolution<>(recommenderEngineResult));
		}
		return allRecommendations;
	}

}
