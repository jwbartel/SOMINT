package recommendation.groups.seedless.actionbased;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import recommendation.groups.seedless.SeedlessGroupRecommender;
import recommendation.groups.seedless.SeedlessGroupRecommenderFactory;
import data.preprocess.graphbuilder.ActionBasedGraphBuilder;
import data.representation.actionbased.CollaborativeAction;

public class GraphFormingActionBasedSeedlessGroupRecommender<CollaboratorType> implements
	ActionBasedSeedlessGroupRecommender<CollaboratorType>{

	boolean tryRebuildingGraph = true;
	private UndirectedGraph<CollaboratorType, DefaultEdge> graph;
	private Collection<Set<CollaboratorType>> recommendations;
	
	private final SeedlessGroupRecommenderFactory<CollaboratorType> recommenderFactory;
	private final ActionBasedGraphBuilder<CollaboratorType, CollaborativeAction<CollaboratorType>> graphBuilder;
	private final Collection<CollaborativeAction<CollaboratorType>> pastActions = new ArrayList<>();
	private CollaborativeAction<CollaboratorType> mostRecentAction = null;

	public GraphFormingActionBasedSeedlessGroupRecommender(
			SeedlessGroupRecommenderFactory<CollaboratorType> recommenderFactory,
			ActionBasedGraphBuilder<CollaboratorType, CollaborativeAction<CollaboratorType>> graphBuilder) {
		this.recommenderFactory = recommenderFactory;
		this.graphBuilder = graphBuilder;
	}

	@Override
	public void addPastAction(CollaborativeAction<CollaboratorType> action) {
		pastActions.add(action);
		tryRebuildingGraph = true;
		if (mostRecentAction == null
				|| mostRecentAction.getLastActiveDate().before(action.getLastActiveDate())) {
			mostRecentAction = action;
		}
	}

	@Override
	public Collection<CollaborativeAction<CollaboratorType>> getPastActions() {
		List<CollaborativeAction<CollaboratorType>> retVal = new ArrayList<>(pastActions);
		Collections.sort(retVal);
		return retVal;
	}
	
	private UndirectedGraph<CollaboratorType, DefaultEdge> buildGraph() {
		if (mostRecentAction == null) {
			return new SimpleGraph<>(DefaultEdge.class);
		}
		return graphBuilder.addActionToGraph(null,
				mostRecentAction, getPastActions());
//		Graph<CollaboratorType, DefaultEdge> graph = graphBuilder.addActionToGraph(null,
//				mostRecentAction, getPastActions());
//		UndirectedGraph<CollaboratorType, DefaultEdge> undirectedGraph = new SimpleGraph<>(
//				DefaultEdge.class);
//		for (CollaboratorType collaborator : graph.vertexSet()) {
//			undirectedGraph.addVertex(collaborator);
//		}
//		for (DefaultEdge edge : graph.edgeSet()) {
//			CollaboratorType source = graph.getEdgeSource(edge);
//			CollaboratorType target = graph.getEdgeTarget(edge);
//			undirectedGraph.addEdge(source, target);
//		}
//		return undirectedGraph;
	}
	
	private boolean graphsAreDifferent(UndirectedGraph<CollaboratorType, DefaultEdge> oldGraph,
			UndirectedGraph<CollaboratorType, DefaultEdge> newGraph) {
		if (oldGraph == null || newGraph == null) {
			return oldGraph != null || newGraph != null;
		}
		if (!(oldGraph.vertexSet().containsAll(newGraph.vertexSet()))) {
			return true;
		}
		
		for(DefaultEdge edge : oldGraph.edgeSet()) {
			CollaboratorType src = oldGraph.getEdgeSource(edge);
			CollaboratorType tgt = oldGraph.getEdgeTarget(edge);
			if (!newGraph.containsEdge(src, tgt)) {
				return true;
			}
		}
		
		for(DefaultEdge edge : newGraph.edgeSet()) {
			CollaboratorType src = newGraph.getEdgeSource(edge);
			CollaboratorType tgt = newGraph.getEdgeTarget(edge);
			if (!oldGraph.containsEdge(src, tgt)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Collection<Set<CollaboratorType>> getRecommendations() {
		if (tryRebuildingGraph) {
			UndirectedGraph<CollaboratorType, DefaultEdge> newGraph = buildGraph();
			if (graphsAreDifferent(graph, newGraph)) {
				graph = newGraph;
				System.out.println("Finding groups in graph with "
						+ graph.vertexSet().size() + " vertices and "
						+ graph.edgeSet().size() + " edges.");
				SeedlessGroupRecommender<CollaboratorType> recommender = recommenderFactory.create(graph);
				recommendations = recommender.getRecommendations();
			}
			System.out.println("Graphs are the same. Reusing past found groups");
		}
		return recommendations;
	}

	@Override
	public String getTypeOfRecommender() {
		return "graph-forming action-based seedless";
	}

	public UndirectedGraph<CollaboratorType, DefaultEdge> getGraph() {
		if (graph == null) {
			graph = buildGraph();
		}
		return graph;
	}
}
