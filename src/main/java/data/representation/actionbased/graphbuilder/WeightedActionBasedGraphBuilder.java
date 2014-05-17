package data.representation.actionbased.graphbuilder;

import java.util.Collection;

import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultEdge;

import data.representation.actionbased.CollaborativeAction;

public abstract class WeightedActionBasedGraphBuilder<Collaborator, Action extends CollaborativeAction<Collaborator>>
		implements ActionBasedGraphBuilder<Collaborator, Action> {

	public abstract WeightedGraph<Collaborator, DefaultEdge> addActionToWeightedGraph(
			WeightedGraph<Collaborator, DefaultEdge> graph, Action currentAction,
			Collection<Action> pastActions);

	@Override
	public Graph<Collaborator, DefaultEdge> addActionToGraph(
			Graph<Collaborator, DefaultEdge> graph,
			Action currentAction,
			Collection<Action> pastActions) {
		if (graph == null || graph instanceof WeightedGraph) {
			return addActionToWeightedGraph((WeightedGraph<Collaborator, DefaultEdge>) graph,
					currentAction, pastActions);
		}
		return null;
	}

}
