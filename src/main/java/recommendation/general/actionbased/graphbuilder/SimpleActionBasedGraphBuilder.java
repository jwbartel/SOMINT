package recommendation.general.actionbased.graphbuilder;

import java.util.Collection;
import java.util.HashSet;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import recommendation.general.actionbased.CollaborativeAction;

public class SimpleActionBasedGraphBuilder<CollaboratorType, ActionType extends CollaborativeAction<CollaboratorType>>
		implements ActionBasedGraphBuilder<CollaboratorType, ActionType> {

	@Override
	public Graph<CollaboratorType, DefaultEdge> addActionToGraph(
			Graph<CollaboratorType, DefaultEdge> graph,
			ActionType currentAction,
			Collection<ActionType> pastActions) {

		if (graph == null) {
			graph = new SimpleGraph<>(DefaultEdge.class);
		}

		pastActions = new HashSet<>(pastActions);
		pastActions.add(currentAction);
		for (CollaborativeAction<CollaboratorType> action : pastActions) {
			for (CollaboratorType collaborator : action.getCollaborators()) {
				if (graph.containsVertex(collaborator)) {
					continue;
				}
				graph.addVertex(collaborator);
				for (CollaboratorType collaborator2 : action.getCollaborators()) {
					if (!collaborator2.equals(collaborator)) {
						if (!graph.containsVertex(collaborator2)) {
							graph.addVertex(collaborator2);
						}
						if (!graph.containsEdge(collaborator, collaborator2)) {
							graph.addEdge(collaborator, collaborator2);
						}
					}
				}
			}
		}
		return graph;
	}

}
