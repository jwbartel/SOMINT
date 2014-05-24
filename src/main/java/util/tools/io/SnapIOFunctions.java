package util.tools.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import recommendation.groups.seedless.hybrid.IOFunctions;

public class SnapIOFunctions<V> extends IOFunctions<V> {

	public SnapIOFunctions(Class<V> genericClass) {
		super(genericClass);
	}

	public UndirectedGraph<V, DefaultEdge> createUIDGraph(String inputFile) {
		UndirectedGraph<V, DefaultEdge> g = new SimpleGraph<V, DefaultEdge>(DefaultEdge.class);
		int linesReadCount = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inputFile));
			String friendPair = in.readLine();

			while (friendPair != null) {
				String[] friends = friendPair.split(" ");
				V friend1 = parse(friends[0]);
				V friend2 = parse(friends[1]);
				
				g.addVertex(friend1);
				g.addVertex(friend2);
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

	@Override
	public Map<Set<V>, String> loadIdealGroupNames(String inputFile) {
		Map<Set<V>, String> groups = new HashMap<Set<V>, String>();

		int linesReadCount = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inputFile));
			String line = in.readLine();
			linesReadCount++;
			while (line != null) {

				Set<V> group = new TreeSet<V>();
				String[] parts = line.split("\t");
				
				String idealGroupName = parts[0];
				for (int i=1; i<parts.length; i++) {
					V member = parse(parts[i]);
					group.add(member);
				}
				groups.put(group, idealGroupName);
				line = in.readLine();
				linesReadCount++;
			}
			in.close();
		} catch (Exception e) {
			System.out.println("!!! fillIdealGroups, line:" + linesReadCount + ": "
					+ e.getMessage());
			e.printStackTrace();
		}

		return groups;
	}
	
	@Override
	public Collection<Set<V>> loadIdealGroups(String inputFile) {
		Collection<Set<V>> groups = new ArrayList<Set<V>>();

		int linesReadCount = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inputFile));
			String line = in.readLine();
			linesReadCount++;
			while (line != null) {
				Set<V> group = new HashSet<V>();
				
				String[] parts = line.split("\t");
				for (int i=1; i<parts.length; i++) {
					V member = parse(parts[i]);
					group.add(member);
				}
				
				groups.add(group);
				line = in.readLine();
				linesReadCount++;
			}
			in.close();
		} catch (Exception e) {
			System.out.println("!!! fillIdealGroups, line:" + linesReadCount + ": "
					+ e.getMessage());
			e.printStackTrace();
		}

		return groups;
	}

}
