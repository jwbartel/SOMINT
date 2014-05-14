package recommendation.general.actionbased.graphbuilder;

import java.util.Collection;

import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import recommendation.general.actionbased.CollaborativeAction;

public class SimpleActionBasedGraphBuilder<RecipientType> implements ActionBasedGraphBuilder<RecipientType> {

	@Override
	public Graph<RecipientType, DefaultEdge> addActionToGraph(
			CollaborativeAction<RecipientType> currentAction,
			Collection<CollaborativeAction<RecipientType>> pastActions) {
		
		UndirectedGraph<RecipientType, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        for(CollaborativeAction<RecipientType> action : pastActions) {
			for (RecipientType collaborator : action.getCollaborators()) {
				graph.addVertex(collaborator);
				for (RecipientType collaborator2 : action.getCollaborators()) {
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
