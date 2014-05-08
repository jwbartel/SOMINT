package groups.seedless.hybrid;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class IOFunctions<V> {

	Class<V> genericClass;

	private boolean storeSubSteps = false;
	private boolean storeIdsAsSubsteps = true;
	private String subStepsFolder = null;
	
	private final TreeMap<V, String> uidNames = new TreeMap<V, String>();
	private final TreeMap<String, V> nameUIDs = new TreeMap<String, V>();
	private final HashMap<V, Boolean> uidInClique = new HashMap<V, Boolean>();
	public UndirectedGraph<V, DefaultEdge> UIDGraph = null;

	public IOFunctions(Class<V> genericClass) {
		this.genericClass = genericClass;
	}

	public void setStoreSubSteps(boolean storeSubSteps) {
		this.storeSubSteps = storeSubSteps;
	}

	public boolean getStoreSubSteps() {
		return storeSubSteps;
	}
	
	public boolean getStoreIdsAsSubsteps() {
		return storeIdsAsSubsteps;
	}

	public void setStoreIdsAsSubsteps(boolean storeIdsAsSubsteps) {
		this.storeIdsAsSubsteps = storeIdsAsSubsteps;
	}

	public void setSubStepsFolder(String subStepsFolder) {
		this.subStepsFolder = subStepsFolder;
	}
	
	public String getSubStepsFolder() {
		return subStepsFolder;
	}

	public UndirectedGraph<V, DefaultEdge> createUIDGraph(String inputFile) {
		UndirectedGraph<V, DefaultEdge> g = new SimpleGraph<V, DefaultEdge>(DefaultEdge.class);
		int linesReadCount = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inputFile));
			String friendPair = in.readLine();
			V friend1;
			V friend2;
			int parsingSpace = -1;
			// in.available() returns 0 if the file does not have more lines.
			while (friendPair != null) {
				linesReadCount++;
				parsingSpace = friendPair.indexOf(' ');
				friend1 = parse(friendPair.substring(0, parsingSpace));
				friendPair = friendPair.substring(parsingSpace + 1);
				parsingSpace = friendPair.indexOf(' ');
				if (parsingSpace != -1)
					friend2 = parse(friendPair.substring(0, parsingSpace));
				else
					friend2 = parse(friendPair);
				g.addVertex(friend1);
				uidInClique.put(friend1, false);
				g.addVertex(friend2);
				uidInClique.put(friend2, false);
				g.addEdge(friend1, friend2);
				friendPair = in.readLine();
			}
			// dispose all the resources after using them.
			in.close();
		} catch (Exception e) {
			System.out
					.println("!!! CreateUIDGraph, line:" + linesReadCount + ": " + e.getMessage());
		}
		UIDGraph = g;
		return g;
	}

	@SuppressWarnings("unchecked")
	protected V parse(String line) {

		Object value = null;

		if (genericClass.equals(Integer.class)) {
			value = Integer.parseInt(line);
		} else if (genericClass.equals(Double.class)) {
			value = Double.parseDouble(line);
		} else if (genericClass.equals(Long.class)) {
			value = Long.parseLong(line);
		} else if (genericClass.equals(String.class)) {
			value = line;
		}

		return (V) value;
	}

	public void printCliquesToFile(String outputFileName, Collection<Set<V>> cliques) {
		File outputFile = new File(outputFileName);
		if (!outputFile.getParentFile().exists()) {
			outputFile.getParentFile().mkdirs();
		}
		int cliqueCount = 1;
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(outputFileName));
			Iterator<Set<V>> collIter = cliques.iterator();
			Iterator<V> uidIter;
			Set<V> currClique;
			Set<DefaultEdge> edgeSet;
			HashSet<V> friendSet = null;
			V currUID;
			int connectionLevel = 0;
			int averageConnectionLevel = 0;
			HashMap<V, Boolean> uidNOTinClique = new HashMap<V, Boolean>(uidInClique);
			while (collIter.hasNext()) {
				pw.println("Clique: " + cliqueCount);
				currClique = collIter.next();
				pw.println("clique size: " + currClique.size());
				uidIter = currClique.iterator();
				while (uidIter.hasNext()) {
					currUID = uidIter.next();
					if (uidNames.containsKey(currUID)) {
						edgeSet = UIDGraph.edgesOf(currUID);
						friendSet = new HashSet<V>();
						for (DefaultEdge edge : edgeSet) {
							V source = UIDGraph.getEdgeSource(edge);
							if (friendSet.contains(source) || currUID == source) {
								source = UIDGraph.getEdgeTarget(edge);
							}
							friendSet.add(source);
						}
						for (V friend : friendSet) {
							if (currClique.contains(friend)) {
								connectionLevel++;
							}
						}
						averageConnectionLevel = averageConnectionLevel + connectionLevel;
						pw.println(uidNames.get(currUID) + "  ~ " + connectionLevel
								+ " in this clique out of " + friendSet.size()
								+ " total mutual friends");
						uidNOTinClique.put(currUID, true);
					}
					connectionLevel = 0;
				}
				pw.println("average connection is " + averageConnectionLevel / currClique.size());
				pw.println();
				averageConnectionLevel = 0;
				cliqueCount++;
			}
			pw.println("Friends Not Grouped:");
			int coverageCount = 0;
			for (V uid : uidNOTinClique.keySet()) {
				if (uidNOTinClique.get(uid)) {
					coverageCount++;
				} else {
					if (uidNames.containsKey(uid))
						pw.println(uidNames.get(uid));
				}
			}
			System.out.println("Coverage: " + coverageCount + " out of " + uidNOTinClique.size()
					+ " friends.");
			System.out.println("Results can be found in: " + outputFileName);
			pw.close();
		} catch (Exception e) {
			System.out.println("!!! Problem in PrintCliquesToFile: " + e.getMessage());
			System.exit(0);
		}
	}

	public void printCliqueIDsToFile(String outputFile, Collection<Set<V>> cliques) {
		int cliqueCount = 1;
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
			Iterator<Set<V>> cliqueIter = cliques.iterator();
			Iterator<V> uidIter;
			Set<V> currClique;
			V currUID;
			while (cliqueIter.hasNext()) {
				pw.println("Clique: " + cliqueCount);
				currClique = cliqueIter.next();
				pw.println("clique size: " + currClique.size());
				uidIter = currClique.iterator();
				while (uidIter.hasNext()) {
					currUID = uidIter.next();
					pw.println(currUID);
				}
				pw.println();
				cliqueCount++;
			}
			System.out.println("Results can be found in: " + outputFile);
			pw.close();
		} catch (Exception e) {
			System.out.println("!!! Problem in PrintCliquesToFile: " + e.getMessage());
			System.exit(0);
		}
	}

	public void printCliqueNamesToFile(String outputFileName, Collection<Set<V>> networkCliques) {

		File outputFile = new File(outputFileName);
		if (!outputFile.getParentFile().exists()) {
			outputFile.getParentFile().mkdirs();
		}
		int cliqueCount = 1;
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(outputFileName));
			Collection<Set<String>> cliques = alphabetizeCliques(networkCliques);
			Iterator<Set<String>> collIter = cliques.iterator();
			Iterator<String> uidIter;
			Set<String> currClique;
			String currUID;
			while (collIter.hasNext()) {
				pw.println("Clique: " + cliqueCount);
				currClique = collIter.next();
				pw.println("clique size: " + currClique.size());
				uidIter = currClique.iterator();
				while (uidIter.hasNext()) {
					currUID = uidIter.next();
					pw.println(currUID);
				}
				pw.println();
				cliqueCount++;
			}
			System.out.println("Results can be found in: " + outputFileName);
			pw.close();
		} catch (Exception e) {
			System.out.println("!!! Problem in PrintCliquesToFile: " + e.getMessage());
			System.exit(0);
		}
	}

	public void printCliqueMappedToIdealsToFile(String outputFile,
			Map<Set<V>, ArrayList<String>> mappings) {
		int cliqueCount = 1;
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
			Iterator<Set<V>> cliqueIter = mappings.keySet().iterator();
			Iterator<V> uidIter;
			Set<V> currClique;
			V currUID;
			while (cliqueIter.hasNext()) {
				pw.println("Clique: " + cliqueCount);
				currClique = cliqueIter.next();
				pw.println("clique size: " + currClique.size());
				uidIter = currClique.iterator();
				while (uidIter.hasNext()) {
					currUID = uidIter.next();
					pw.println(currUID);
				}
				ArrayList<String> idealNames = mappings.get(currClique);
				for (String idealName : idealNames) {
					pw.println("\t" + idealName);
				}
				pw.println();
				cliqueCount++;
			}
			System.out.println("Results can be found in: " + outputFile);
			pw.close();
		} catch (Exception e) {
			System.out.println("!!! Problem in CliqueMappedToIdealsToFile: " + e.getMessage());
			System.exit(0);
		}
	}

	private Collection<Set<String>> alphabetizeCliques(Collection<Set<V>> networkCliques) {
		Collection<Set<String>> cliques = new ArrayList<Set<String>>();
		Set<String> cliqueNames;
		Iterator<Set<V>> cliqueIter = networkCliques.iterator();
		Set<V> currClique;
		Iterator<V> uidIter;
		V currUID;
		while (cliqueIter.hasNext()) {
			currClique = cliqueIter.next();
			uidIter = currClique.iterator();
			cliqueNames = new TreeSet<String>();
			while (uidIter.hasNext()) {
				currUID = uidIter.next();
				if (uidNames.containsKey(currUID)) {
					cliqueNames.add(uidNames.get(currUID));
				}
			}
			cliques.add(cliqueNames);
		}
		return cliques;
	}

	public Collection<Set<V>> loadGroups(String inputFile) {
		Collection<Set<V>> groups = new ArrayList<Set<V>>();

		int linesReadCount = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inputFile));
			String line = in.readLine();
			while (line != null) {
				line = in.readLine();

				if (line != null) {
					int count = Integer.parseInt(line.substring("clique size: ".length()));
					Set<V> group = new TreeSet<V>();
					for (int i = 0; i < count; i++) {
						String name = in.readLine();
						if (name != null) {
							V uid = nameUIDs.get(name);
							if (uid == null) {
								System.out.println("ERROR: null");
							}
							group.add(uid);
						}
					}
					groups.add(group);
					in.readLine();
					line = in.readLine();
				}
			}

		} catch (Exception e) {
			System.out.println("!!! fillNamesAndIDs, line:" + linesReadCount + ": "
					+ e.getMessage());
			e.printStackTrace();
		}

		return groups;
	}

	public Map<Set<V>, ArrayList<String>> loadCliqueMappedToIdeals(String inputFile) {
		Map<Set<V>, ArrayList<String>> groupsToIdeals = new HashMap<Set<V>, ArrayList<String>>();

		int linesReadCount = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inputFile));
			String line = in.readLine();
			while (line != null) {
				line = in.readLine();

				if (line != null) {
					int count = Integer.parseInt(line.substring("clique size: ".length()));
					Set<V> group = new TreeSet<V>();
					for (int i = 0; i < count; i++) {
						String uidStr = in.readLine();
						if (uidStr != null) {
							V uid = parse(uidStr);
							if (uid == null) {
								System.out.println("ERROR: null");
							}
							group.add(uid);
						}
					}
					ArrayList<String> idealNames = new ArrayList<String>();
					line = in.readLine();
					while (line != null && line.startsWith("\t")) {
						idealNames.add(line.substring(1));
						line = in.readLine();
					}
					groupsToIdeals.put(group, idealNames);
					line = in.readLine();
				}
			}

		} catch (Exception e) {
			System.out.println("!!! fillNamesAndIDs, line:" + linesReadCount + ": "
					+ e.getMessage());
			e.printStackTrace();
		}

		return groupsToIdeals;
	}
	
	public Map<Set<V>, String> loadGroupIDs(String prefix, String inputFile) {
		Map<Set<V>, String> groups = new HashMap<Set<V>, String>();

		int linesReadCount = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inputFile));
			String line = in.readLine();
			while (line != null) {
				String groupName = prefix + line.substring("Clique: ".length());
				line = in.readLine();

				if (line != null) {
					int count = Integer.parseInt(line.substring("clique size: ".length()));
					Set<V> group = new TreeSet<V>();
					Set<String> groupNames = new TreeSet<String>();
					for (int i = 0; i < count; i++) {
						String uidName = in.readLine();
						V uid = parse(uidName);
						group.add(uid);
					}
					groups.put(group, groupName);
					in.readLine();
					line = in.readLine();
				}
			}

		} catch (Exception e) {
			System.out.println("!!! fillNamesAndIDs, line:" + linesReadCount + ": "
					+ e.getMessage());
			e.printStackTrace();
		}

		return groups;
	}

	public Map<Set<V>, String> loadGroupNames(String prefix, String inputFile) {
		Map<Set<V>, String> groups = new HashMap<Set<V>, String>();

		int linesReadCount = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inputFile));
			String line = in.readLine();
			while (line != null) {
				String groupName = prefix + line.substring("Clique: ".length());
				line = in.readLine();

				if (line != null) {
					int count = Integer.parseInt(line.substring("clique size: ".length()));
					Set<V> group = new TreeSet<V>();
					Set<String> groupNames = new TreeSet<String>();
					for (int i = 0; i < count; i++) {
						String name = in.readLine();
						if (name != null) {
							groupNames.add(name);
							V uid = nameUIDs.get(name);
							if (uid == null) {
								System.out.println("ERROR: NULL!!");
							}
							group.add(uid);
						}
					}
					groups.put(group, groupName);
					in.readLine();
					line = in.readLine();
				}
			}

		} catch (Exception e) {
			System.out.println("!!! fillNamesAndIDs, line:" + linesReadCount + ": "
					+ e.getMessage());
			e.printStackTrace();
		}

		return groups;
	}

	public Collection<Set<V>> loadIdealGroups(String inputFile) {
		Collection<Set<V>> groups = new ArrayList<Set<V>>();

		int linesReadCount = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inputFile));
			String line = in.readLine();
			linesReadCount++;
			while (line != null) {
				line = in.readLine();
				linesReadCount++;

				Set<V> group = new TreeSet<V>();
				while (line != null && !line.equals("")) {

					String name = line;
					char lastChar = name.charAt(name.length() - 1);
					if (lastChar == ' ' || lastChar == '\t')
						name = name.substring(0, name.length() - 1);
					V uid = nameUIDs.get(name);
					if (uid == null) {
						int x = 0;
					}
					group.add(uid);
					line = in.readLine();
					linesReadCount++;
				}
				groups.add(group);
				line = in.readLine();
				linesReadCount++;
			}

		} catch (Exception e) {
			System.out.println("!!! fillIdealGroups, line:" + linesReadCount + ": "
					+ e.getMessage());
			e.printStackTrace();
		}

		return groups;
	}

	public Map<Set<V>, String> loadIdealGroupNames(String inputFile) {
		Map<Set<V>, String> groups = new HashMap<Set<V>, String>();

		int linesReadCount = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inputFile));
			String line = in.readLine();
			linesReadCount++;
			String idealGroupName;
			while (line != null) {
				idealGroupName = line;
				line = in.readLine();
				linesReadCount++;

				Set<V> group = new TreeSet<V>();
				while (line != null && !line.equals("")) {

					String name = line;
					char lastChar = name.charAt(name.length() - 1);
					if (lastChar == ' ' || lastChar == '\t')
						name = name.substring(0, name.length() - 1);
					V uid = nameUIDs.get(name);
					if (uid == null) {
						int x = 0;
					}
					group.add(uid);
					line = in.readLine();
					linesReadCount++;
				}
				groups.put(group, idealGroupName);
				line = in.readLine();
				linesReadCount++;
			}

		} catch (Exception e) {
			System.out.println("!!! fillIdealGroups, line:" + linesReadCount + ": "
					+ e.getMessage());
			e.printStackTrace();
		}

		return groups;
	}

	public void fillNamesAndIDs(String inputNames) {
		int linesReadCount = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inputNames));
			String friendName = in.readLine();
			V friendUID;
			int parsingComma;
			// in.available() returns 0 if the file does not have more lines.
			while (friendName != null) {

				linesReadCount++;
				parsingComma = friendName.indexOf(',');
				friendUID = parse(friendName.substring(0, parsingComma));
				friendName = friendName.substring(parsingComma + 2);
				nameUIDs.put(friendName, friendUID);
				uidNames.put(friendUID, friendName);
				friendName = in.readLine();
			}
			in.close();
		} catch (Exception e) {
			System.out.println("!!! fillNamesAndIDs, line:" + linesReadCount + ": "
					+ e.getMessage());
		}
	}

	public void fillIDs(String inputNames) {
		int linesReadCount = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inputNames));
			String friendName = in.readLine();
			V friendUID;
			int parsingComma;
			// in.available() returns 0 if the file does not have more lines.
			while (friendName != null) {
				linesReadCount++;
				parsingComma = friendName.indexOf(',');
				friendUID = parse(friendName.substring(0, parsingComma));
				friendName = friendName.substring(parsingComma + 2);
				nameUIDs.put(friendName, friendUID);
				friendName = in.readLine();
			}
			in.close();
		} catch (Exception e) {
			System.out.println("!!! fillIDs, line:" + linesReadCount + ": " + e.getMessage());
		}
	}

	public void fillNames(String inputNames) {
		int linesReadCount = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inputNames));
			String friendName = in.readLine();
			V friendUID;
			int parsingComma;
			// in.available() returns 0 if the file does not have more lines.
			while (friendName != null) {
				linesReadCount++;
				parsingComma = friendName.indexOf(',');
				friendUID = parse(friendName.substring(0, parsingComma));
				friendName = friendName.substring(parsingComma + 2);
				uidNames.put(friendUID, friendName);
				friendName = in.readLine();
			}
			in.close();
		} catch (Exception e) {
			System.out.println("!!! fillNames, line:" + linesReadCount + ": " + e.getMessage());
		}
	}

	public Collection<Set<V>> loadCliqueIDs(String inputFile) {
		ArrayList<Set<V>> retVal = new ArrayList<Set<V>>();
		int linesReadCount = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inputFile));
			String line = in.readLine();

			while (line != null) {
				linesReadCount++;

				line = in.readLine();
				linesReadCount++;
				line = in.readLine();
				linesReadCount++;
				Set<V> clique = new TreeSet<V>();
				while (line != null && !line.equals("")) {
					V uid = parse(line);
					clique.add(uid);
					line = in.readLine();
					linesReadCount++;
				}
				retVal.add(clique);

				line = in.readLine();
				linesReadCount++;
			}
			in.close();
		} catch (Exception e) {
			System.out.println("!!! fillNames, line:" + linesReadCount + ": " + e.getMessage());
		}
		return retVal;
	}

}
