package recommendation.general.actionbased.graphbuilder;

import java.util.Collection;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import recommendation.general.actionbased.CollaborativeAction;

public interface ActionBasedGraphBuilder<CollaboratorType, ActionType extends CollaborativeAction<CollaboratorType>> {

	public Graph<CollaboratorType, DefaultEdge> addActionToGraph(
			Graph<CollaboratorType, DefaultEdge> graph,
			ActionType currentAction,
			Collection<ActionType> pastActions);
}
