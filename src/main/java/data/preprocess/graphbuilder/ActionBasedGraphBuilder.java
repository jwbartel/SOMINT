package data.preprocess.graphbuilder;

import java.util.Collection;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

import data.representation.actionbased.CollaborativeAction;

public interface ActionBasedGraphBuilder<Collaborator, Action extends CollaborativeAction<Collaborator>> {

	public String getName();
	
	public Graph<Collaborator, DefaultEdge> addActionToGraph(
			Graph<Collaborator, DefaultEdge> graph,
			Action currentAction,
			Collection<Action> pastActions);
}
