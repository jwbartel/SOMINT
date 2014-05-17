package data.preprocess.graphbuilder;

import data.representation.actionbased.CollaborativeAction;

public interface ActionBasedGraphBuilderFactory <Collaborator, Action extends CollaborativeAction<Collaborator>> {

	public boolean takesTime();
	public boolean takesScoredEdgeWithThreshold();
	
	public ActionBasedGraphBuilder<Collaborator, Action> create();
	public ActionBasedGraphBuilder<Collaborator, Action> create(long time);
	public ActionBasedGraphBuilder<Collaborator, Action> create(long halfLife,
			double sentImportance, double threshold);
	
}
