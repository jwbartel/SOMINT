package data.preprocess.graphbuilder;

import java.util.Collection;
import java.util.HashSet;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import data.representation.actionbased.CollaborativeAction;

public class SimpleActionBasedGraphBuilder<Collaborator, Action extends CollaborativeAction<Collaborator>>
		implements ActionBasedGraphBuilder<Collaborator, Action> {

	public static <Collaborator, Action extends CollaborativeAction<Collaborator>> ActionBasedGraphBuilderFactory<Collaborator, Action> factory(Class<Collaborator> collaboratorClass, Class<Action> ActionClass) {
		return new ActionBasedGraphBuilderFactory<Collaborator, Action>() {

			@Override
			public boolean takesTime() {
				return false;
			}

			@Override
			public boolean takesScoredEdgeWithThreshold() {
				return false;
			}

			@Override
			public ActionBasedGraphBuilder<Collaborator, Action> create() {
				return new SimpleActionBasedGraphBuilder<>();
			}

			@Override
			public ActionBasedGraphBuilder<Collaborator, Action> create(
					long time) {
				return null;
			}

			@Override
			public ActionBasedGraphBuilder<Collaborator, Action> create(
					long halfLife, double sentImportance, double threshold) {
				return null;
			}

		};
	}
	
	public String getName() {
		return "Simple";
	}
	
	@Override
	public Graph<Collaborator, DefaultEdge> addActionToGraph(
			Graph<Collaborator, DefaultEdge> graph,
			Action currentAction,
			Collection<Action> pastActions) {

		if (graph == null) {
			graph = new SimpleGraph<>(DefaultEdge.class);
		}

		pastActions = new HashSet<>(pastActions);
		pastActions.add(currentAction);
		for (CollaborativeAction<Collaborator> action : pastActions) {
			for (Collaborator collaborator : action.getCollaborators()) {
				if (graph.containsVertex(collaborator)) {
					continue;
				}
				graph.addVertex(collaborator);
				for (Collaborator collaborator2 : action.getCollaborators()) {
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
