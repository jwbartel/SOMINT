package recommendation.general.actionbased.graphbuilder;

import java.util.Collection;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import recommendation.general.actionbased.CollaborativeAction;

public interface ActionBasedGraphBuilder<RecipientType, ActionType extends CollaborativeAction<RecipientType>> {

	public Graph<RecipientType, DefaultEdge> addActionToGraph(
			Graph<RecipientType, DefaultEdge> graph,
			ActionType currentAction,
			Collection<ActionType> pastActions);
}
