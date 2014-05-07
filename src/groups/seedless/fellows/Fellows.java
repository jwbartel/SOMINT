package groups.seedless.fellows;

import groups.seedless.SeedlessGroupRecommender;
import groups.seedless.kelli.IOFunctions;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;

public class Fellows<V extends Comparable<V>> implements
		SeedlessGroupRecommender<V> {

	public static final String RESULTS_FOLDER = "data/Jacob/Fellows/";

	public static final String GROUPS_FILE_SUFFIX = "_Groups.txt";

	private Vector<V> vertexList = new Vector<V>();
	private UndirectedGraph<V, DefaultEdge> UIDGraph = null;
	private String inFileName;
	private HashMap<V, String> uidNames = new HashMap<V, String>();
	private IOFunctions<V> ioHelp;
	String participantID;

	boolean isGreedy = false;
	double threshold = -1;
	Class<V> genericClass;

	Map<Set<V>, Double> cohesionValues = new HashMap<Set<V>, Double>();
	Set<Triangle<V>> triangles;

	public Fellows(UndirectedGraph<V, DefaultEdge> ugraph) {
		UIDGraph = ugraph;
	}

	public Fellows(UndirectedGraph<V, DefaultEdge> ugraph, String id,
			boolean greedy, Class<V> genericClass) {
		init(ugraph, id, greedy, -1, genericClass);
	}

	public Fellows(UndirectedGraph<V, DefaultEdge> ugraph, String id,
			boolean greedy, double threshold, Class<V> genericClass) {
		init(ugraph, id, greedy, threshold, genericClass);
	}

	public Fellows(UndirectedGraph<V, DefaultEdge> ugraph, String id,
			Class<V> genericClass) {
		init(ugraph, id, false, -1, genericClass);
	}

	public Fellows(UndirectedGraph<V, DefaultEdge> ugraph, String id,
			double threshold, Class<V> genericClass) {
		init(ugraph, id, false, threshold, genericClass);
	}

	protected void init(UndirectedGraph<V, DefaultEdge> ugraph, String id,
			boolean greedy, double threshold, Class<V> genericClass) {
		UIDGraph = ugraph;
		participantID = id;
		isGreedy = greedy;
		this.threshold = threshold;
		this.genericClass = genericClass;
	}

	public void setIOHelp(IOFunctions<V> ioHelp) {
		this.ioHelp = ioHelp;
	}

	private void doSetup() {
		System.out.println("~~~~~~~~~~~~~SET UP~~~~~~~~~~~~~~~~");
		inFileName = "data/Kelli/FriendshipData/" + participantID
				+ "_MutualFriends.txt";
		String idNameMap = "data/Kelli/FriendshipData/" + participantID
				+ "_People.txt";
		// String idNameMap = "data/Kelli/FriendshipData/PeopleNames.txt";
		// inFileName =
		// "data/Kelli/FriendshipData/"+participant+"FriendOfFriends.txt";
		System.out.println("created the UIDGraph");
		UIDGraph = ioHelp.createUIDGraph(inFileName);
		ioHelp.fillNames(idNameMap);
	}

	@Override
	public Collection<Set<V>> getRecommendations() {
		return greedyFindGroups();
	}

	private V breakTies(Set<V> group, Collection<V> optimalNodes) {

		if (optimalNodes.size() == 1) {
			return optimalNodes.iterator().next();
		}

		V optimalNode = null;
		int optimalInternalTriangles = -1;
		int optimalOutgoingTriangles = -1;

		for (V candidate : optimalNodes) {
			Set<V> tempGroup = new TreeSet<>(group);
			tempGroup.add(candidate);
			int[] triangleCounts = sortTriangles(tempGroup, triangles);

			boolean shouldSet = false;
			if (optimalInternalTriangles < triangleCounts[0]) {
				shouldSet = true;
			} else if (optimalInternalTriangles < triangleCounts[0]
					&& optimalOutgoingTriangles < triangleCounts[1]) {
				shouldSet = true;
			}

			if (shouldSet) {
				optimalNode = candidate;
				optimalInternalTriangles = triangleCounts[0];
				optimalOutgoingTriangles = triangleCounts[1];
			}
		}
		return optimalNode;
	}

	private Set<V> getCandidateNodes(Set<V> group) {
		Set<V> candidateNodes = new HashSet<V>();
		for (V groupMember : group) {
			Collection<DefaultEdge> edges = UIDGraph.edgesOf(groupMember);
			for (DefaultEdge edge : edges) {
				V v1 = UIDGraph.getEdgeSource(edge);
				if (!group.contains(v1)) {
					candidateNodes.add(v1);
				}
				V v2 = UIDGraph.getEdgeTarget(edge);
				if (!group.contains(v2)) {
					candidateNodes.add(v2);
				}
			}
		}
		return candidateNodes;
	}

	private void greedilyGrowGroup(Set<V> group) {

		while (true) {

			// All nodes not in the group
			Set<V> otherNodes = getCandidateNodes(group);

			double currCohesion = getCohesion(group);
			Collection<V> optimalNodes = new ArrayList<V>();
			double optimalNextCohesion = 0;

			// Find the node that increases the cohesion
			for (V otherNode : otherNodes) {
				Set<V> tempGroup = new TreeSet<>(group);
				tempGroup.add(otherNode);

				double nextCohesion = getCohesion(tempGroup);
				if (nextCohesion < currCohesion
						|| nextCohesion < optimalNextCohesion) {
					continue;
				} else {
					if (nextCohesion > optimalNextCohesion) {
						optimalNodes = new ArrayList<V>();
						optimalNextCohesion = nextCohesion;
					}
					optimalNodes.add(otherNode);
				}
			}

			if (optimalNodes.size() == 0) {
				break;
			}

			group.add(breakTies(group, optimalNodes));
		}
	}

	private Collection<Set<V>> greedyFindGroups() {

		triangles = getTriangles(UIDGraph);

		System.out.println("~~~~~~~~~~~~~~Greedy Finding Groups~~~~~~~~~~~~~~");

		long start, elapsedTime;
		float elapsedTimeMin;
		start = System.currentTimeMillis();

		Collection<Set<V>> groups = new HashSet<Set<V>>();
		Set<V> ungroupedNodes = new TreeSet<V>(UIDGraph.vertexSet());
		while (ungroupedNodes.size() > 0) {

			Set<V> group = new TreeSet<V>();
			group.add(ungroupedNodes.iterator().next());
			greedilyGrowGroup(group);

			groups.add(group);
			ungroupedNodes.removeAll(group);
			System.out.println("Found group number " + groups.size() + ", "
					+ ungroupedNodes.size() + " ungrouped nodes remaining");
		}

		elapsedTime = System.currentTimeMillis() - start;
		elapsedTimeMin = elapsedTime / 1000 / 60;

		System.out.println("Completed in " + elapsedTimeMin + " minutes");

		return groups;
	}

	public void findGroups(Collection<Set<V>> cliques) {

		preprocessCliques(cliques);

		triangles = getTriangles(UIDGraph);

		System.out.println("~~~~~~~~~~~~~~Finding Groups~~~~~~~~~~~~~~");

		long start, elapsedTime;
		float elapsedTimeMin;
		start = System.currentTimeMillis();

		cliques = makeMerges(cliques, triangles);
		System.out.println("final groups: " + cliques.size());
		String outputFile = RESULTS_FOLDER + participantID + GROUPS_FILE_SUFFIX;
		ioHelp.printCliqueNamesToFile(outputFile, cliques);
	}

	private void preprocessCliques(Collection<Set<V>> cliques) {

		System.out.println("~~~~~~~~~Preprocess cliques~~~~~~~~~~~~");
		System.out.println("cliques.size = " + cliques.size());
		System.out.println("removing all cliques <= size 2");
		Iterator<Set<V>> cliqueIter = cliques.iterator();
		Set<V> currClique;
		while (cliqueIter.hasNext()) {
			currClique = cliqueIter.next();
			if (currClique.size() <= 2)
				cliqueIter.remove();
		}
		System.out.println("cliques.size = " + cliques.size());
	}

	private Collection<Set<V>> makeMerges(Collection<Set<V>> cliques,
			Set<Triangle<V>> triangles) {
		// TODO: merge cliques using greedy algorithm

		System.out.println("~~~~~~~~~~~~~~Merging Cliques~~~~~~~~~~~~~~");

		System.out.println("Performing greedy merges");
		if (threshold >= 0.0)
			System.out.println("With threshold=" + threshold);

		ArrayList<Set<V>> cliquesInList = new ArrayList<Set<V>>(cliques);

		int passNumber = 0;
		System.out.println("pass " + passNumber + " cliques.size: "
				+ cliquesInList.size());
		while (true) {

			SortedMergeList<V> mergeList = new SortedMergeList<V>();

			for (int c1 = 0; c1 < cliquesInList.size(); c1++) {
				Set<V> clique1 = cliquesInList.get(c1);
				double c1Cohesion = getCohesion(clique1);

				for (int c2 = c1 + 1; c2 < cliquesInList.size(); c2++) {
					Set<V> clique2 = cliquesInList.get(c2);
					double c2Cohesion = getCohesion(clique2);
					if (clique2.equals(clique1)) {
						continue;
					}

					Set<V> merge = new TreeSet<V>(clique1);
					merge.addAll(clique2);

					if (cliques.contains(merge))
						continue;

					double cohesion = getCohesion(merge);

					if (threshold <= cohesion
							&& (!isGreedy || (c1Cohesion <= cohesion && c2Cohesion <= cohesion))) {

						mergeList.add(new PossibleMerge<V>(clique1, clique2,
								cohesion));
					}

				}
			}

			boolean mergeOccurred = false;
			while (mergeList.hasTop()) {
				mergeOccurred = true;
				PossibleMerge<V> top = mergeList.removeTopMerge();

				Set<V> merge = top.getClique1();
				merge.addAll(top.getClique2());

				cliquesInList.remove(top.getClique2());
			}

			passNumber++;
			System.out.println("pass " + passNumber + " cliques.size: "
					+ cliquesInList.size());

			if (!mergeOccurred) {
				return cliquesInList;
			}

		}

	}

	private int[] sortTriangles(Set<V> group, Set<Triangle<V>> triangles) {
		// Returns the number of inner triangles and the number of outbound
		// triangles given a set of vertices and a set of triangles

		int[] retVal = new int[2]; // {triangles in group, triangles outbound
									// from group}
		Iterator<Triangle<V>> trianglesIter = triangles.iterator();
		while (trianglesIter.hasNext()) {
			Triangle<V> triangle = trianglesIter.next();
			Set<V> elements = triangle.getVertices();

			if (group.containsAll(elements)) {
				retVal[0]++;
			} else {
				elements.removeAll(group);
				if (elements.size() == 1) {
					retVal[1]++;
				}
			}
		}
		return retVal;

	}

	private Set<Triangle<V>> getTriangles(
			UndirectedGraph<V, DefaultEdge> UIDGraph) {
		System.out.println("~~~~~~~~~~~~~Finding Triangles~~~~~~~~~~~~~~~");

		Set<Triangle<V>> toReturn = new HashSet<Triangle<V>>();

		ArrayList<V> vertices = new ArrayList<V>(UIDGraph.vertexSet());
		for (int i = 0; i < vertices.size(); i++) {

			V v1 = vertices.get(i);
			for (int j = i + 1; j < vertices.size(); j++) {
				V v2 = vertices.get(j);
				if (UIDGraph.containsEdge(v1, v2)
						|| UIDGraph.containsEdge(v2, v1)) {
					// Only check for a third vertex if the first two are
					// connected
					for (int k = j + 1; k < vertices.size(); k++) {
						V v3 = vertices.get(k);
						if ((UIDGraph.containsEdge(v1, v3) || UIDGraph
								.containsEdge(v3, v1))
								&& (UIDGraph.containsEdge(v2, v3) || UIDGraph
										.containsEdge(v3, v2))) {
							toReturn.add(new Triangle<V>(v1, v2, v3));
						}
					}
				}
			}
		}

		System.out.println("total triangles: " + toReturn.size());

		return toReturn;
	}

	public double getCohesion(Set<V> clique) {
		Double cohesion = null; // cohesionValues.get(clique);
		if (cohesion == null) {

			int[] counts = sortTriangles(clique, triangles);
			cohesion = cohesion(clique.size(), counts[0], counts[1]);
			if (cohesion.equals(Double.NaN)) {
				cohesion = 0.0;
			}
			// cohesionValues.put(clique, cohesion);
		}

		return cohesion;
	}

	private double cohesion(int groupSize, int containedTriangles,
			int outboundTriangles) {

		double density = ((double) containedTriangles)
				/ binomialCoefficient(groupSize, 3);
		double isolation = ((double) containedTriangles)
				/ ((double) (containedTriangles + outboundTriangles));

		return density * isolation;
	}

	private double binomialCoefficient(int n, int k) {
		if (k == 0)
			return 1;
		if (n == 0)
			return 0;

		return binomialCoefficient(n - 1, k - 1)
				+ binomialCoefficient(n - 1, k);
	}

	public Collection<Set<V>> getMaximalCliques(
			UndirectedGraph<V, DefaultEdge> graph, boolean useFile) {

		System.out.println("~~~~~~~~~~Find All Maximal Cliques~~~~~~~~~~");
		long start, elapsedTime, getAllTime;
		// BronKerboschCliqueFinder<Integer, DefaultEdge> BKcliqueFind = new
		// BronKerboschCliqueFinder<Integer, DefaultEdge>(UIDGraph);
		// status message
		System.out.println("running getAllMaximalCliques");
		start = System.currentTimeMillis();

		File maximalCliqueFile = new File("data/Kelli/Cliques/MaximalCliques/"
				+ participantID + "_MaximalCliques.txt");
		Collection<Set<V>> maximalCliques;
		if (!useFile || !maximalCliqueFile.exists()) {
			BronKerboschCliqueFinder<V, DefaultEdge> BKcliqueFind = new BronKerboschCliqueFinder<V, DefaultEdge>(
					graph);
			maximalCliques = BKcliqueFind.getAllMaximalCliques();
			if (useFile) {
				ioHelp.printCliqueIDsToFile(maximalCliqueFile.getPath(),
						maximalCliques);
				System.out.println("Can be found in "
						+ maximalCliqueFile.getPath());
			}
		} else {
			System.out.println("Loading cliques from file "
					+ maximalCliqueFile.getPath());
			maximalCliques = ioHelp.loadCliqueIDs(maximalCliqueFile.getPath());
		}

		// compareCliques();
		getAllTime = System.currentTimeMillis() - start;
		float elapsedTimeMin = getAllTime / (60 * 1000F);
		// status message
		System.out.println("found AllMaximalCliques (" + maximalCliques.size()
				+ ") in " + elapsedTimeMin + " minutes");
		elapsedTime = System.currentTimeMillis() - start;
		elapsedTimeMin = elapsedTime / (60 * 1000F);
		// status message: all done :)
		System.out.println("this stage done in " + elapsedTimeMin
				+ " minutes !!!");

		return maximalCliques;
	}

	public static void main(String[] args) {

		int[] participants = { 10, 12, 13, 16, 17, 19, 21, 22, 23, 24, 25 };

		for (int i = 0; i < participants.length; i++) {
			IOFunctions<Integer> ioHelp = new IOFunctions<Integer>(
					Integer.class);
			String participantID = "" + participants[i];// "12";//"25";
			System.out.println("\n\n\nParticipant: " + participantID);

			System.out
					.println("~~~~~~~~~~~~~~~~~~~~~~~SET UP~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			String inRelationships = "data/Kelli/FriendshipData/2010Study/"
					+ participantID + "_MutualFriends.txt";
			String idNameMap = "data/Kelli/FriendshipData/2010Study/"
					+ participantID + "_People.txt";
			System.out.println("created the UIDGraph");
			UndirectedGraph<Integer, DefaultEdge> UIDGraph = ioHelp
					.createUIDGraph(inRelationships);
			ioHelp.fillNames(idNameMap);

			Fellows<Integer> fellows = new Fellows<Integer>(UIDGraph,
					participantID, true, Integer.class);
			fellows.setIOHelp(ioHelp);

			Collection<Set<Integer>> cliques = fellows.getMaximalCliques(
					UIDGraph, true);

			fellows.findGroups(cliques);
		}
	}
}
