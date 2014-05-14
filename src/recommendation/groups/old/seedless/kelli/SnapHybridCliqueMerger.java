package recommendation.groups.old.seedless.kelli;

import java.io.File;
import java.util.Collection;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;

import recommendation.groups.old.evolution.snap.SnapTestingConstants;
import recommendation.groups.seedless.hybrid.IOFunctions;
import bus.tools.FileAndMemoryBasedBronKerboschCliqueFinder;
import bus.tools.FileFinder;
import bus.tools.io.IntegerValueParser;
import bus.tools.io.SnapIOFunctions;
import bus.tools.io.ValueParser;

public class SnapHybridCliqueMerger<V> extends HybridCliqueMerger<V>{

	private final ValueParser<V> parser;

	public SnapHybridCliqueMerger(UndirectedGraph<V, DefaultEdge> graph, ValueParser<V> parser) {
		super(graph);
		this.parser = parser;
	}

	public SnapHybridCliqueMerger(UndirectedGraph<V, DefaultEdge> graph, int participantID,
			Class<V> genericClass, ValueParser<V> parser) {
		super(graph, participantID, genericClass);
		this.parser = parser;
	}

	@Override
	public Collection<Set<V>> findNetworks(Collection<Set<V>> cliques) {

		preprocessCliques(cliques);

		System.out.println("~~~~~~~~~~~~Find Networks~~~~~~~~~~~~~~~~~~");

		Collection<Set<V>> networkCliques = makeMerges(cliques, 0);

		if (ioHelp != null && ioHelp.getStoreSubSteps()) {
			String outputLargeGroupFile = FileFinder.getHybridNetworksFileName(ioHelp, participantID);
			ioHelp.printCliqueIDsToFile(outputLargeGroupFile, networkCliques);
		}

		return networkCliques;
	}
	
	public Collection<Set<V>> getMaximalCliques(UndirectedGraph<V, DefaultEdge> graph,
			File maximalCliqueFile) {

		System.out.println("~~~~~~~~~~Find All Maximal Cliques~~~~~~~~~~");
		long start, elapsedTime, getAllTime;
		// BronKerboschCliqueFinder<Integer, DefaultEdge> BKcliqueFind = new
		// BronKerboschCliqueFinder<Integer, DefaultEdge>(UIDGraph);
		// status message
		System.out.println("running getAllMaximalCliques");
		start = System.currentTimeMillis();

		Collection<Set<V>> maximalCliques;
		if (maximalCliqueFile == null || !maximalCliqueFile.exists()) {
			BronKerboschCliqueFinder<V, DefaultEdge> BKcliqueFind = new FileAndMemoryBasedBronKerboschCliqueFinder<V, DefaultEdge>(graph, parser);
			maximalCliques = BKcliqueFind.getAllMaximalCliques();
			if (maximalCliqueFile != null) {
				ioHelp.printCliqueIDsToFile(maximalCliqueFile.getPath(), maximalCliques);
				System.out.println("Can be found in " + maximalCliqueFile.getPath());
			}
		} else {
			System.out.println("Loading cliques from file " + maximalCliqueFile.getPath());
			maximalCliques = ioHelp.loadCliqueIDs(maximalCliqueFile.getPath());
		}

		// compareCliques();
		getAllTime = System.currentTimeMillis() - start;
		float elapsedTimeMin = getAllTime / (60 * 1000F);
		// status message
		System.out.println("found AllMaximalCliques (" + maximalCliques.size() + ") in "
				+ elapsedTimeMin + " minutes");
		elapsedTime = System.currentTimeMillis() - start;
		elapsedTimeMin = elapsedTime / (60 * 1000F);
		// status message: all done :)
		System.out.println("this stage done in " + elapsedTimeMin + " minutes !!!");

		return maximalCliques;
	}
	
	public static void main(String[] args) {
		int[] participants = SnapTestingConstants.FACEBOOK_PARTICIPANTS;

		for (int i = 0; i < participants.length; i++) {
			IOFunctions<Integer> ioHelp = new SnapIOFunctions<Integer>(Integer.class);			
			String participantID = "" + participants[i];// "12";//"25";
			System.out.println("\n\n\nParticipant: " + participantID);

			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~SET UP~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			ioHelp.setSubStepsFolder("data/Stanford_snap/facebook/substeps/"+participantID+"/");
			File subStepsFolder = new File(ioHelp.getSubStepsFolder());
			if(!subStepsFolder.exists()) {
				subStepsFolder.mkdirs();
			}
			
			String inRelationships = "data/Stanford_snap/facebook/"+participantID+".edges";
			System.out.println("created the UIDGraph");
			UndirectedGraph<Integer, DefaultEdge> UIDGraph = ioHelp.createUIDGraph(inRelationships);
			System.out.println("Edges:"+UIDGraph.edgeSet().size());
			System.out.println("Vertices:"+UIDGraph.vertexSet().size());

			HybridCliqueMerger<Integer> hybrid = new SnapHybridCliqueMerger<Integer>(UIDGraph, new IntegerValueParser());
			hybrid.setIOHelp(ioHelp);
			Collection<Set<Integer>> groups = hybrid.findNetworksAndSubgroups(ioHelp);

			String predictionsFile = "data/Stanford_snap/facebook/Predicted groups/"+participantID+".edges";
			ioHelp.printCliqueIDsToFile(predictionsFile, groups);
		}
	}
}
