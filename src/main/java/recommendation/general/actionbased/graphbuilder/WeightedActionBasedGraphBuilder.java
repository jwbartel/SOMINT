package recommendation.general.actionbased.graphbuilder;

import java.util.Collection;

import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultEdge;

import recommendation.general.actionbased.CollaborativeAction;

public abstract class WeightedActionBasedGraphBuilder<CollaboratorType, ActionType extends CollaborativeAction<CollaboratorType>>
		implements ActionBasedGraphBuilder<CollaboratorType, ActionType> {

	public abstract WeightedGraph<CollaboratorType, DefaultEdge> addActionToGraph(
			WeightedGraph<CollaboratorType, DefaultEdge> graph, ActionType currentAction,
			Collection<ActionType> pastActions);

	@Override
	public Graph<CollaboratorType, DefaultEdge> addActionToGraph(
			Graph<CollaboratorType, DefaultEdge> graph,
			ActionType currentAction,
			Collection<ActionType> pastActions) {
		if (graph instanceof WeightedGraph) {
			return addActionToGraph((WeightedGraph<CollaboratorType, DefaultEdge>) graph,
					currentAction, pastActions);
		}
		return null;
	}

}
