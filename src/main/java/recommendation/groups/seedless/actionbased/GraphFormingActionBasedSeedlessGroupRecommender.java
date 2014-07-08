package recommendation.groups.seedless.actionbased;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import data.preprocess.graphbuilder.ActionBasedGraphBuilder;
import data.representation.actionbased.CollaborativeAction;
import recommendation.groups.seedless.SeedlessGroupRecommender;
import recommendation.groups.seedless.SeedlessGroupRecommenderFactory;

public class GraphFormingActionBasedSeedlessGroupRecommender<CollaboratorType> implements
	ActionBasedSeedlessGroupRecommender<CollaboratorType>{

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
		if (mostRecentAction == null
				|| mostRecentAction.getLastActiveDate().before(action.getLastActiveDate())) {
			mostRecentAction = action;
		}
	}

	@Override
	public Collection<CollaborativeAction<CollaboratorType>> getPastActions() {
		return new ArrayList<>(pastActions);
	}
	
	private UndirectedGraph<CollaboratorType, DefaultEdge> buildGraph() {
		if (mostRecentAction == null) {
			return new SimpleGraph<>(DefaultEdge.class);
		}
		Graph<CollaboratorType, DefaultEdge> graph = graphBuilder.addActionToGraph(null,
				mostRecentAction, getPastActions());
		UndirectedGraph<CollaboratorType, DefaultEdge> undirectedGraph = new SimpleGraph<>(
				DefaultEdge.class);
		for (CollaboratorType collaborator : graph.vertexSet()) {
			undirectedGraph.addVertex(collaborator);
		}
		for (DefaultEdge edge : graph.edgeSet()) {
			CollaboratorType source = graph.getEdgeSource(edge);
			CollaboratorType target = graph.getEdgeTarget(edge);
			undirectedGraph.addEdge(source, target);
		}
		return undirectedGraph;
	}

	@Override
	public Collection<Set<CollaboratorType>> getRecommendations() {
		UndirectedGraph<CollaboratorType, DefaultEdge> graph = buildGraph();
		System.out.println("Vertices: "+graph.vertexSet().size());
		System.out.println("Edges:" + graph.edgeSet().size());
		SeedlessGroupRecommender<CollaboratorType> recommender = recommenderFactory.create(graph);
		return recommender.getRecommendations();
	}

	@Override
	public String getTypeOfRecommender() {
		return "graph-forming action-based seedless";
	}

}
