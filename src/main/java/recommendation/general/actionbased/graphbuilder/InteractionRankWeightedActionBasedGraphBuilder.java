package recommendation.general.actionbased.graphbuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.WeightedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import recommendation.general.actionbased.CollaborativeAction;
import recommendation.recipients.groupbased.GroupScorer;
import recommendation.recipients.groupbased.interactionrank.scoring.TopContactScore;

public class InteractionRankWeightedActionBasedGraphBuilder<CollaboratorType, ActionType extends CollaborativeAction<CollaboratorType>>
		extends WeightedActionBasedGraphBuilder<CollaboratorType, ActionType> {

	private final GroupScorer<CollaboratorType> scorer;
	private final double edgeThreshold;
	
	public static <Collaborator, Action extends CollaborativeAction<Collaborator>> ActionBasedGraphBuilderFactory<Collaborator, Action> factory(Class<Collaborator> collaboratorClass, Class<Action> ActionClass) {
		return new ActionBasedGraphBuilderFactory<Collaborator, Action>() {

			@Override
			public boolean takesTime() {
				return false;
			}

			@Override
			public boolean takesScoredEdgeWithThreshold() {
				return true;
			}

			@Override
			public ActionBasedGraphBuilder<Collaborator, Action> create() {
				return null;
			}

			@Override
			public ActionBasedGraphBuilder<Collaborator, Action> create(
					long time) {
				return null;
			}

			@Override
			public ActionBasedGraphBuilder<Collaborator, Action> create(
					long halfLife, double sentImportance, double threshold) {
				return new InteractionRankWeightedActionBasedGraphBuilder<>(
						halfLife, sentImportance, threshold);
			}

		};
	}
	
	public InteractionRankWeightedActionBasedGraphBuilder(long halfLife, double sentImportance, double edgeThreshold) {
		scorer = new TopContactScore<>(sentImportance, halfLife);
		this.edgeThreshold = edgeThreshold;
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
	
	public String getName() {
		return "Interaction Rank";
	}
	
	
	@Override
	public WeightedGraph<CollaboratorType, DefaultEdge> addActionToWeightedGraph(
			WeightedGraph<CollaboratorType, DefaultEdge> graph,
			ActionType currentAction,
			Collection<ActionType> pastActions) {
		
		if (graph == null) {
			graph = new SimpleWeightedGraph<CollaboratorType,DefaultEdge>(DefaultWeightedEdge.class);
		}
		pastActions = new HashSet<>(pastActions);
		pastActions.add(currentAction);
        for(ActionType action : pastActions) {
        	
            Set<Pair<CollaboratorType>> seenCollaboratorPairs = new HashSet<>();
        	double actionScore = scorer.getInteractionRankScoreOfPastAction(currentAction, action);
        	if (actionScore == 0.0) {
        		continue;
        	}

        	for(CollaboratorType collaborator1 : action.getCollaborators()) {
        		if (graph.containsVertex(collaborator1)) {
        			continue;
        		}
        		graph.addVertex(collaborator1);
        		for (CollaboratorType collaborator2 : action.getCollaborators()) {
        			if (!collaborator1.equals(collaborator2)) {
        				if(!graph.containsVertex(collaborator2)) {
        					graph.addVertex(collaborator2);
        				}
        				Pair<CollaboratorType> pair = new Pair<>(collaborator1, collaborator2);
        				if (!seenCollaboratorPairs.contains(pair)) {
        					DefaultEdge edge = graph.getEdge(collaborator1, collaborator2);
        					if (edge == null) {
        						edge = graph.addEdge(collaborator1, collaborator2);
        						graph.setEdgeWeight(edge, 0);
        					}
        					double currWeight = graph.getEdgeWeight(edge);
        					graph.setEdgeWeight(edge, currWeight + actionScore);
        				}
        			}
        		}
        	}
        }
        return graph;
	}

	@Override
	public Graph<CollaboratorType, DefaultEdge> addActionToGraph(
			Graph<CollaboratorType, DefaultEdge> graph,
			ActionType currentAction,
			Collection<ActionType> pastActions) {
		if (graph == null || graph instanceof WeightedGraph) {
			graph = addActionToWeightedGraph(
					(WeightedGraph<CollaboratorType, DefaultEdge>) graph, currentAction,
					pastActions);
			
			Set<CollaboratorType> allVertices = graph.vertexSet();
	        for (CollaboratorType vertex : allVertices) {
	            for (CollaboratorType vertex2: allVertices){
	                DefaultEdge edge = graph.getEdge(vertex, vertex2);
					if (graph.getEdgeWeight(edge) < edgeThreshold) {
						graph.removeEdge(edge);
					}
	            }
	        }
	        return graph;
		}
		return null;
	}

}
