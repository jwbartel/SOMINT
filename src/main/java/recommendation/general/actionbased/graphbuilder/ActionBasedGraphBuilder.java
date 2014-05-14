package recommendation.general.actionbased.graphbuilder;

import java.util.Collection;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import recommendation.general.actionbased.CollaborativeAction;

public interface ActionBasedGraphBuilder<RecipientType> {

	public Graph<RecipientType, DefaultEdge> addActionToGraph(
			CollaborativeAction<RecipientType> currentAction,
			Collection<CollaborativeAction<RecipientType>> pastActions);
}
