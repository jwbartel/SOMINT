package groups.seedless.hybrid;

import groups.seedless.SeedlessGroupRecommender;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import bus.tools.FileAndMemoryBasedBronKerboschCliqueFinder;
import bus.tools.FileFinder;

public class HybridCliqueMerger<V> implements SeedlessGroupRecommender<V> {

	public static final int LARGE_GROUP_MIN_SIZE = 50;

	double S = 0.0, D = 1.0;
	protected UndirectedGraph<V, DefaultEdge> graph = null;
	protected final Collection<Set<V>> precomputedMaximalCliques;
	protected IOFunctions<V> ioHelp;
	int participantID;
	Class<V> genericClass;

	public HybridCliqueMerger(UndirectedGraph<V, DefaultEdge> graph) {
		this.graph = graph;
		precomputedMaximalCliques = null;
	}
	
	public HybridCliqueMerger(UndirectedGraph<V, DefaultEdge> graph, Collection<Set<V>> maximalCliques) {
		this.graph = graph;
		this.precomputedMaximalCliques = maximalCliques;
	}

	public HybridCliqueMerger(UndirectedGraph<V, DefaultEdge> graph, int participantID,
			Class<V> genericClass) {
		this.graph = graph;
		this.participantID = participantID;
		this.ioHelp = new IOFunctions<V>(genericClass);
		this.genericClass = genericClass;
		precomputedMaximalCliques = null;
	}
	
	@Override
	public String getTypeOfRecommender() {
		return "hybrid";
	}

	public void setIOHelp(IOFunctions<V> ioHelp) {
		this.ioHelp = ioHelp;
	}

	public void setParticipantID(int id) {
		this.participantID = id;
	}

	public void setSAndD(double s, double d) {
		this.S = s;
		this.D = d;
	}

	@Override
	public Collection<Set<V>> getRecommendations() {
		if (precomputedMaximalCliques == null) {
			return findNetworksAndSubgroups();
		} else {
			return findNetworksAndSubgroups(precomputedMaximalCliques);
		}
	}

	public Collection<Set<V>> findNetworksAndSubgroups() {
		return findNetworksAndSubgroups(getMaximalCliques());
	}

	public Collection<Set<V>> findNetworksAndSubgroups(IOFunctions<V> ioHelp) {
		File maximalCliquesFile = new File(ioHelp.getSubStepsFolder(), "maximal_cliques");
		return findNetworksAndSubgroups(getMaximalCliques(graph, maximalCliquesFile));
	}

	public Collection<Set<V>> findNetworksAndSubgroups(Collection<Set<V>> cliques) {
		// Finds networks and subcliques using the default S and D values
		return findNetworksAndSubgroups(cliques, 0.9, 0.35, 1.0, 0.15);
	}

	public Collection<Set<V>> findNetworksAndSubgroups(Collection<Set<V>> cliques, double networkS,
			double networkD, double subgroupS, double subgroupD) {
		setSAndD(networkS, networkD);
		Collection<Set<V>> networks = findNetworks(cliques);
		setSAndD(subgroupS, subgroupD);
		Collection<Set<V>> subgroups = findSubgroups(networks);

		Collection<Set<V>> networksAndSubgroups = new HashSet<Set<V>>();
		networksAndSubgroups.addAll(networks);
		networksAndSubgroups.addAll(subgroups);
		return networksAndSubgroups;
	}

	protected void preprocessCliques(Collection<Set<V>> cliques) {

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

	public Collection<Set<V>> findNetworks(Collection<Set<V>> cliques) {

		preprocessCliques(cliques);

		System.out.println("~~~~~~~~~~~~Find Networks~~~~~~~~~~~~~~~~~~");

		Collection<Set<V>> networkCliques = makeMerges(cliques, 0);

		if (ioHelp != null && ioHelp.getStoreSubSteps()) {
			String outputLargeGroupFile = FileFinder.getHybridNetworksFileName(ioHelp, participantID);
			if (ioHelp.getStoreIdsAsSubsteps()) {
				ioHelp.printCliqueIDsToFile(outputLargeGroupFile, networkCliques);
			} else {
				ioHelp.printCliqueNamesToFile(outputLargeGroupFile, networkCliques);
			}
		}

		return networkCliques;
	}

	public Collection<Set<V>> findSubgroups(Collection<Set<V>> networks) {

		UndirectedGraph<V, DefaultEdge> subGraph = findSubgraph(networks);

		System.out.println("~~~~~~~~~~~~~Find subgroups~~~~~~~~~~~~~");
		Collection<Set<V>> subgroupCliques;
		if (ioHelp != null && ioHelp.getStoreSubSteps()) {
			File maximalCliqueFile = FileFinder.getHybridMaximalCliquesFile(ioHelp, participantID);
			subgroupCliques = getMaximalCliques(subGraph, maximalCliqueFile);
		} else {
			subgroupCliques = getMaximalCliques(subGraph);
		}

		int passNumber = 1;
		System.out.println("Finding first level merges...");
		subgroupCliques = getFirstLevelMerges(subgroupCliques);

		System.out.println("pass " + passNumber + " cliques.size: " + subgroupCliques.size());
		passNumber++;

		subgroupCliques = makeMerges(subgroupCliques, passNumber);

		System.out.println("subgroups size: " + subgroupCliques.size());

		if (ioHelp != null && ioHelp.getStoreSubSteps()) {
			String outputFile = FileFinder.getHybridSubcliquesFileName(ioHelp, participantID);
			ioHelp.printCliqueIDsToFile(outputFile, subgroupCliques);
		}

		return subgroupCliques;

	}

	private Collection<Set<V>> getFirstLevelMerges(Collection<Set<V>> subgroupCliques) {
		Collection<Set<V>> removedCliques = new HashSet<Set<V>>();// ArrayList<Set<V>>();
		Collection<Set<V>> mergedCliques = new HashSet<Set<V>>();// ArrayList<Set<V>>();
		long start = System.currentTimeMillis();

		for (Set<V> outerC : subgroupCliques) {
			if (!removedCliques.contains(outerC)) {

				for (Set<V> innerC : subgroupCliques) {
					if (!removedCliques.contains(innerC)) {
						if (!outerC.equals(innerC)) {

							Set<V> bigger, smaller;
							if (outerC.size() >= innerC.size()) {
								bigger = outerC;
								smaller = innerC;
							} else {
								bigger = innerC;
								smaller = outerC;
							}

							if (shouldMerge(bigger, smaller)) {
								mergedCliques.remove(bigger);
								mergedCliques.remove(smaller);

								boolean reremoveBigger = removedCliques.remove(bigger);

								bigger.addAll(smaller);
								mergedCliques.add(bigger);

								if (reremoveBigger) {
									removedCliques.add(bigger);
								}

								removedCliques.add(smaller);

							}
						}
					}
				}
			}
		}

		long end = System.currentTimeMillis();
		System.out.println("finshed in " + (end - start) + "ms");

		return mergedCliques;
	}

	public Collection<Set<V>> getMaximalCliques() {
		return getMaximalCliques(graph, null);
	}

	public Collection<Set<V>> getMaximalCliques(UndirectedGraph<V, DefaultEdge> graph) {
		return getMaximalCliques(graph, null);
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
			BronKerboschCliqueFinder<V, DefaultEdge> BKcliqueFind = new FileAndMemoryBasedBronKerboschCliqueFinder<V, DefaultEdge>(
					graph);
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

	public boolean merged(Set<V> a, Set<V> b) {

		if (shouldMerge(a, b)) {
			a.addAll(b);
			return true;
		}
		return false;
	}

	private boolean shouldMerge(Set<V> a, Set<V> b) {

		int intersectionCount = 0, differenceCount = 0;

		for (V member : b) {
			if (a.contains(member))
				intersectionCount++;
			else {
				differenceCount++;
			}
		}

		double percentSame = ((double) intersectionCount) / b.size();
		double percentDiff = ((double) differenceCount) / b.size();

		return percentSame >= S || percentDiff <= D;

	}

	public Collection<Set<V>> makeMerges(Collection<Set<V>> origCliques, int initialPassNumber) {

		ArrayList<Set<V>> cliques = new ArrayList<Set<V>>(origCliques);

		int compareCount = 0;
		int passNumber = initialPassNumber;
		// ioHelp.printCliqueNamesToFile("data/Jacob/temp/my_pass"+passNumber+".txt",
		// cliques);
		System.out.println("pass " + passNumber + " cliques.size: " + cliques.size());

		while (true) {
			boolean mergeHappened = false;
			Set<Set<V>> mergedCliques = new HashSet<Set<V>>();

			for (Set<V> outerC : cliques) {
				if (mergedCliques.contains(outerC))
					continue;
				for (Set<V> innerC : cliques) {
					if (innerC.equals(outerC) || mergedCliques.contains(innerC)) {
						continue;
					}

					Set<V> bigger, smaller;
					if (outerC.size() >= innerC.size()) {
						bigger = outerC;
						smaller = innerC;
					} else {
						bigger = innerC;
						smaller = outerC;
					}

					boolean merged = false;
					if (shouldMerge(bigger, smaller)) {
						boolean biggerWasMerged = mergedCliques.remove(bigger);
						bigger.addAll(smaller);
						if (biggerWasMerged)
							mergedCliques.add(bigger);

						mergedCliques.add(smaller);
						mergeHappened = true;
						merged = true;
						if (smaller == outerC) {
							compareCount++;
							// outerC was merged and can no longer be merged
							// with others
							break;
						}
					}
					if (passNumber == -1) {
						if (merged) {
							System.out.println("" + compareCount + ":merged");
						} else {
							System.out.println("" + compareCount + ":not merged");
						}
					}
					compareCount++;
				}
			}

			if (!mergeHappened)
				break;
			cliques.removeAll(mergedCliques);
			passNumber++;

			// ioHelp.printCliqueNamesToFile("data/Jacob/temp/my_pass"+passNumber+".txt",
			// cliques);
			System.out.println("pass " + passNumber + " cliques.size: " + cliques.size());
		}

		return cliques;
	}

	protected UndirectedGraph<V, DefaultEdge> findSubgraph(Collection<Set<V>> networks) {

		UndirectedGraph<V, DefaultEdge> subGraph = new SimpleGraph<V, DefaultEdge>(
				DefaultEdge.class);
		Set<V> vSet = graph.vertexSet();
		Set<DefaultEdge> eSet;
		V source, target;
		Set<V> largeGroups = findLargeGroupMembers(networks);

		System.out.println("~~~~~~~~~~~Find subgraph~~~~~~~~~~~~~~");
		for (V v : vSet) {
			if (largeGroups.contains(v)) {
				subGraph.addVertex(v);
			}
		}
		for (V v : vSet) {
			if (largeGroups.contains(v)) {
				eSet = graph.edgesOf(v);
				for (DefaultEdge e : eSet) {
					source = graph.getEdgeSource(e);
					target = graph.getEdgeTarget(e);
					if (largeGroups.contains(source) && largeGroups.contains(target)) {
						subGraph.addEdge(source, target);
					}
				}
			}
		}
		System.out.println("Vertices: " + subGraph.vertexSet().size());
		System.out.println("Edges: " + subGraph.edgeSet().size());
		return subGraph;
	}

	protected Set<V> findLargeGroupMembers(Collection<Set<V>> networks) {

		System.out.println("~~~~~~~~~~Find large group Members~~~~~~~~~~~~~~~~");

		Iterator<Set<V>> cliqueIter = networks.iterator();
		Set<V> currClique;
		Set<V> largeGroups = new TreeSet<V>();
		while (cliqueIter.hasNext()) {
			currClique = cliqueIter.next();
			if (currClique.size() >= LARGE_GROUP_MIN_SIZE) {
				Iterator<V> memberIter = currClique.iterator();
				while (memberIter.hasNext()) {
					V member = memberIter.next();
					largeGroups.add(member);
				}
			}
		}
		System.out.println("" + largeGroups.size() + " members of large groups");
		return largeGroups;
	}

	public static void main(String[] args) {

		int[] participants = { 12 };// {25};//{22, 23, 24, 25};//{ 13, 16, 17,
									// 18};/*{8, 10, 12, 13, 16, 17, 19, 21, 22,
									// 23, 24, 25};*/

		for (int i = 0; i < participants.length; i++) {
			IOFunctions<Integer> ioHelp = new IOFunctions<Integer>(Integer.class);
			String participantID = "" + participants[i];// "12";//"25";
			System.out.println("\n\n\nParticipant: " + participantID);

			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~SET UP~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			String inRelationships = "data/Kelli/FriendshipData/2010Study/" + participantID
					+ "_MutualFriends.txt";
			String idNameMap = "data/Kelli/FriendshipData/2010Study/" + participantID
					+ "_People.txt";
			System.out.println("created the UIDGraph");
			UndirectedGraph<Integer, DefaultEdge> UIDGraph = ioHelp.createUIDGraph(inRelationships);
			// ioHelp.fillNames(idNameMap);

			HybridCliqueMerger<Integer> hybrid = new HybridCliqueMerger<Integer>(UIDGraph);// ,
																							// participantID,
																							// Integer.class);
			// hybrid.setIOHelp(ioHelp);

			// File maximalCliqueFile = new
			// File("data/Kelli/Cliques/MaximalCliques/"+participantID+"_MaximalCliques.txt");
			// Collection<Set<Integer>> cliques =
			// hybrid.getMaximalCliques(UIDGraph, maximalCliqueFile);

			// hybrid.findNetworksAndSubgroups(cliques, 0.9, 0.35, 1.0, 0.15);
			Collection<Set<Integer>> groups = hybrid.findNetworksAndSubgroups();
		}
	}
}
