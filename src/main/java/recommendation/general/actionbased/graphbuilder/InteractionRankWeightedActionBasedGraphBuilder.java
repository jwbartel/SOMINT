package recommendation.general.actionbased.graphbuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import recommendation.general.actionbased.CollaborativeAction;
import recommendation.recipients.groupbased.GroupScorer;
import recommendation.recipients.groupbased.google.scoring.TopContactScore;

public class InteractionRankWeightedActionBasedGraphBuilder<CollaboratorType, ActionType extends CollaborativeAction<CollaboratorType>>
		extends WeightedActionBasedGraphBuilder<CollaboratorType, ActionType> {

	private final GroupScorer<CollaboratorType> scorer;
	
	public InteractionRankWeightedActionBasedGraphBuilder(long halfLife, double sentImportance) {
		scorer = new TopContactScore<>(sentImportance, halfLife);
	}
	
	private class Pair<V> {
		private final Set<V> entries = new HashSet<>();
		
		public Pair(V collaborator1, V collaborator2) {
			entries.add(collaborator1);
			entries.add(collaborator2);
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Pair)) {
				return false;
			}
			return this.entries.equals(((Pair) o).entries);
		}
	}
	
	
	@Override
	public WeightedGraph<CollaboratorType, DefaultEdge> addActionToGraph(
			WeightedGraph<CollaboratorType, DefaultEdge> graph,
			ActionType currentAction,
			Collection<ActionType> pastActions) {
		
		if (graph == null) {
			graph = new SimpleWeightedGraph<CollaboratorType,DefaultEdge>(DefaultWeightedEdge.class);
		}
        for(ActionType action : pastActions) {
        	
            Set<Pair<CollaboratorType>> seenCollaboratorPairs = new HashSet<>();
        	double actionScore = scorer.getInteractionRankScoreOfPastAction(currentAction, action);
        	
        	 for(CollaboratorType collaborator1 : action.getCollaborators()) {
        		 graph.addVertex(collaborator1);
        		 for (CollaboratorType collaborator2 : action.getCollaborators()) {
        			 if (!collaborator1.equals(collaborator2) && !graph.containsVertex(collaborator2)) {
        				 graph.addVertex(collaborator2);
        			 }
        			 Pair<CollaboratorType> pair = new Pair<>(collaborator1, collaborator2);
        			 if (!seenCollaboratorPairs.contains(pair)) {
        				 DefaultEdge edge = graph.getEdge(collaborator1, collaborator2);
        				 if (edge == null) {
        					 edge = new DefaultEdge();
        					 graph.addEdge(collaborator1, collaborator2, edge);
        					 graph.setEdgeWeight(edge, 0);
        				 }
        				 double currWeight = graph.getEdgeWeight(edge);
        				 graph.setEdgeWeight(edge, currWeight + actionScore);
        			 }
        		 }
             }
        }
        return graph;
	}

	

}
