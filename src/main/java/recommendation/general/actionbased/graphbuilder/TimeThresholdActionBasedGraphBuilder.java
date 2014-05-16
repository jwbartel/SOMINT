package recommendation.general.actionbased.graphbuilder;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import recommendation.general.actionbased.CollaborativeAction;

public class TimeThresholdActionBasedGraphBuilder<Collaborator, Action extends CollaborativeAction<Collaborator>>
		implements ActionBasedGraphBuilder<Collaborator, Action> {

	private final long thresholdAge;
	
	public static <Collaborator, Action extends CollaborativeAction<Collaborator>> ActionBasedGraphBuilderFactory<Collaborator, Action> factory(Class<Collaborator> collaboratorClass, Class<Action> ActionClass) {
		return new ActionBasedGraphBuilderFactory<Collaborator, Action> () {

			@Override
			public boolean takesTime() {
				return true;
			}

			@Override
			public boolean takesScoredEdgeWithThreshold() {
				return false;
			}

			@Override
			public ActionBasedGraphBuilder<Collaborator, Action> create() {
				return null;
			}

			@Override
			public ActionBasedGraphBuilder<Collaborator, Action> create(long time) {
				return new TimeThresholdActionBasedGraphBuilder<>(time);
			}

			@Override
			public ActionBasedGraphBuilder<Collaborator, Action> create(long halfLife,
					double sentImportance, double threshold) {
				return null;
			}

		};
	}

	public TimeThresholdActionBasedGraphBuilder(long thresholdAge) {
		this.thresholdAge = thresholdAge;
	}
	
	public String getName() {
		return "Time Threshold";
	}

	@Override
	public Graph<Collaborator, DefaultEdge> addActionToGraph(
			Graph<Collaborator, DefaultEdge> graph,
			Action currentAction,
			Collection<Action> pastActions) {

		if (graph == null) {
			graph = new SimpleGraph<>(DefaultEdge.class);
		}
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(currentAction.getLastActiveDate().getTime() - thresholdAge);
		Date threshold = calendar.getTime();

		pastActions = new HashSet<>(pastActions);
		pastActions.add(currentAction);
		for (CollaborativeAction<Collaborator> action : pastActions) {
			if (action.getLastActiveDate().before(threshold))
				continue;
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
