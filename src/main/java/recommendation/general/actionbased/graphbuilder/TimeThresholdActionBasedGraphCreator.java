package recommendation.general.actionbased.graphbuilder;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import recommendation.general.actionbased.CollaborativeAction;

public class TimeThresholdActionBasedGraphCreator<RecipientType> implements
		ActionBasedGraphBuilder<RecipientType> {
	
	private final long thresholdAge;
	
	public TimeThresholdActionBasedGraphCreator(long thresholdAge) {
		this.thresholdAge = thresholdAge;
	}

	@Override
	public Graph<RecipientType, DefaultEdge> addActionToGraph(
			CollaborativeAction<RecipientType> currentAction,
			Collection<CollaborativeAction<RecipientType>> pastActions) {
		
		UndirectedGraph<RecipientType, DefaultEdge> graph = new SimpleGraph<RecipientType,DefaultEdge>(DefaultEdge.class);
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(currentAction.getLastActiveDate().getTime()-thresholdAge);
        Date threshold = calendar.getTime();

        for(CollaborativeAction<RecipientType> action : pastActions) {
            if(action.getLastActiveDate().before(threshold)) continue;
            for(RecipientType collaborator : action.getCollaborators()) {
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
