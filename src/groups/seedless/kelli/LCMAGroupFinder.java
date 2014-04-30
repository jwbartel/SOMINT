package groups.seedless.kelli;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class LCMAGroupFinder<V> {

	public static final String RESULTS_FOLDER = "data/Jacob/LCMA/";
	public static final String GROUP_FILE_MIDDLE = "_GroupsWithThreshold_";
	public static final String GROUP_FILE_END = ".txt";
	
	static class DegreeComparableVertex<V> implements Comparable<DegreeComparableVertex<V>>{
		V vertex;
		int degree;
		
		public DegreeComparableVertex(V vertex, int degree){
			this.vertex = vertex;
			this.degree =degree;
		}
		
		public int compareTo(DegreeComparableVertex<V> o) {
			return (new Integer(degree)).compareTo(o.degree);
		}
		
		
	}
	
	private UndirectedGraph<V, DefaultEdge> graph = null;
	private IOFunctions<V> ioHelp;
	String participantID;
	Class<V> genericClass;
	
	public LCMAGroupFinder(UndirectedGraph<V, DefaultEdge> graph, String participantID, Class<V> genericClass){
		this.graph = graph;
		this.participantID = participantID;
		this.ioHelp = new IOFunctions<V>(genericClass);
		this.genericClass = genericClass;
	}
	
	public void setIOHelp(IOFunctions<V> ioHelp){
		this.ioHelp = ioHelp;
	}
	
	public void findGroups(double threshold){
		
		Collection<Set<V>> localCliques = findLocalCliques();
		
		localCliques = preprocessCliques(localCliques);
		
		Collection<Set<V>> mergedGroups = mergeGroups(localCliques, threshold);
		
		String groupsFile = RESULTS_FOLDER + participantID + GROUP_FILE_MIDDLE + threshold + GROUP_FILE_END;
		ioHelp.printCliqueNamesToFile(groupsFile, mergedGroups);
	}
	
	public void findGroups(double threshold, Collection<Set<V>> localCliques){
		

		localCliques = deepCopy(localCliques);
		
		Collection<Set<V>> mergedGroups = mergeGroups(localCliques, threshold);
		
		String groupsFile = RESULTS_FOLDER + participantID + GROUP_FILE_MIDDLE + threshold + GROUP_FILE_END;
		ioHelp.printCliqueNamesToFile(groupsFile, mergedGroups);
	}
	
	
	protected Collection<Set<V>> deepCopy(Collection<Set<V>> cliques){
		
		Collection<Set<V>> copy = new ArrayList<Set<V>>();
		
		Iterator<Set<V>> iter = cliques.iterator();
		while(iter.hasNext()){
			Set<V> clique = iter.next();
			Set<V> cliqueCopy = new TreeSet<V>(clique);
			copy.add(cliqueCopy);
		}
		
		return copy;
		
	}
	
	protected Collection<Set<V>> preprocessCliques(Collection<Set<V>> cliques){
		
		System.out.println("~~~~~~~~~Preprocessing cliques~~~~~~~~~~~~");
		
		Collection<Set<V>> toRemove = new ArrayList<Set<V>>();
		
		Iterator<Set<V>> iter = cliques.iterator();
		while(iter.hasNext()){
			Set<V> clique = iter.next();
			if(clique.size() <= 2){
				toRemove.add(clique);
			}
		}
		
		cliques.removeAll(toRemove);

		System.out.println("cliques:"+cliques.size());
		return cliques;
		
	}
	
	protected Collection<Set<V>> mergeGroups(Collection<Set<V>> cliques, double threshold){
		

		System.out.println("~~~~~~~~~Merging groups~~~~~~~~~~~~");
		
		int passCount = 0;
		System.out.println("passCount:"+passCount+" groups:"+cliques.size());
		while(true){
			passCount++;
			Set<Set<V>> mergedGroups = new HashSet<Set<V>>();
			
			Iterator<Set<V>> outerIter = cliques.iterator();
			while(outerIter.hasNext()){
				Set<V> group1 = outerIter.next();				
				if(mergedGroups.contains(group1)){
					continue;
				}
				
				Iterator<Set<V>> innerIter = cliques.iterator();
				while(innerIter.hasNext()){
					Set<V> group2 = innerIter.next();
					if(group1.equals(group2) || mergedGroups.contains(group2)){
						continue;
					}
					
					Set<V> bigger, smaller;
					
					if(group1.size() >= group2.size()){
						bigger = group1;
						smaller = group2;
					}else{
						bigger = group2;
						smaller = group1;
					}
					
					double NA = NA(smaller, bigger);
					if(NA >= threshold){
						merge(bigger, smaller);
						mergedGroups.add(smaller);
						if(smaller == group1){
							break;
						}
					}
				}
			}

			System.out.println("passCount:"+passCount+" groups:"+cliques.size());
			
			if(mergedGroups.size() == 0){
				break;
			}
			
			cliques.removeAll(mergedGroups);
		}
		
		System.out.println("final groups:"+cliques.size());
		
		return cliques;
		
	}
	
	protected void merge(Set<V> bigger, Set<V> smaller){
		bigger.addAll(smaller);
	}
	
	public double NA(Set<V> s1, Set<V> s2){
		Set<V> intersection = new TreeSet<V>(s1);
		intersection.retainAll(s2);
		
		return ((double)(intersection.size() * intersection.size()))/(s1.size() * s2.size());
		
	}
	
	protected Collection<Set<V>> findLocalCliques(){
		
		System.out.println("~~~~~~~~~Finding Local Cliques~~~~~~~~~~~~");
		
		Collection<Set<V>> localCliques = new ArrayList<Set<V>>();
		
		Iterator<V> vertexIter = graph.vertexSet().iterator();
		while(vertexIter.hasNext()){
			V vertex = vertexIter.next();
			Set<V> neighborhood = getPrunedLocalNeighborhood(vertex);
			localCliques.add(neighborhood);
		}
		
		System.out.println("local cliques:"+localCliques.size());
		return localCliques;
	}
	
	protected Set<V> getPrunedLocalNeighborhood(V vertex){
		Set<V> neighborhood = new TreeSet<V>();
		neighborhood.add(vertex);
		Iterator<V> vertexIter = graph.vertexSet().iterator();
		
		//Put neighbors in a set
		while(vertexIter.hasNext()){
			V altVertex = vertexIter.next();
			if(!altVertex.equals(vertex) &&
					(graph.containsEdge(vertex, altVertex) || graph.containsEdge(altVertex, vertex))){
				
				neighborhood.add(altVertex);
			}
		}
		
		neighborhood  = pruneNeighborhood(neighborhood);
		return neighborhood;
		
	}
	
	protected Set<V> pruneNeighborhood(Set<V> neighborhood){
		while(true){
			if(neighborhood.size() == 0){
				return neighborhood;
			}
			
			DegreeComparableVertex<V> vertexAndDegree = minDegreeVertex(neighborhood);
			if(vertexAndDegree.degree == neighborhood.size()-1){
				return neighborhood;
			}
			neighborhood.remove(vertexAndDegree.vertex);
		}
	}
	
	protected DegreeComparableVertex<V> minDegreeVertex(Set<V> neighborhood){
		int minDegree = neighborhood.size()+1;
		V assocVertex = null;
		
		Iterator<V> iter = neighborhood.iterator();
		while(iter.hasNext()){
			V v = iter.next();
			int degree = degree(v, neighborhood);
			if(degree < minDegree){
				minDegree = degree;
				assocVertex = v;
			}
		}	
		
		return new DegreeComparableVertex<V>(assocVertex, minDegree);
	}
	
	protected int degree(V v1, Set<V> vertices){
		int degree = 0;
		
		Iterator<V> iter = vertices.iterator();
		while(iter.hasNext()){
			V v2 = iter.next();
			if(!v1.equals(v2) && 
					(graph.containsEdge(v1, v2) || graph.containsEdge(v2, v1))){
				degree++;
			}
		}
		return degree;
	}
	
	public static void main(String[] args){
		
		int[] participants =  {8, 10, 11, 12, 13, 14, 15, 16, 17,18, 19, 20, 21, 22, 23, 24, 25};
		
		double[] thresholds = {1.0, 0.95, 0.9, 0.85, 0.8, 0.75, 0.7, 0.6, 0.65, 0.6, 0.55, 0.5, 0.45, 0.4, 0.35, 0.3, 0.25, 0.2, 0.15, 0.1, 0.05, 0.0};
		
		for(int i=0; i<participants.length; i++){
							
			IOFunctions<Integer> ioHelp = new IOFunctions<Integer>(Integer.class);
			String participantID = ""+participants[i];//"12";//"25";
			System.out.println("\n\n\nParticipant: "+participantID);
		
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~SET UP~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			String inRelationships = "data/Kelli/FriendshipData/2010Study/"+participantID+"_MutualFriends.txt";
			String idNameMap = "data/Kelli/FriendshipData/2010Study/"+participantID+"_People.txt";
			System.out.println("created the UIDGraph");
			UndirectedGraph<Integer, DefaultEdge> UIDGraph = ioHelp.createUIDGraph(inRelationships);
			ioHelp.fillNames(idNameMap);
			

			LCMAGroupFinder<Integer> lcma = new LCMAGroupFinder<Integer>(UIDGraph, participantID, Integer.class);
			lcma.setIOHelp(ioHelp);
			
			Collection<Set<Integer>> localCliques = lcma.findLocalCliques();
			localCliques = lcma.preprocessCliques(localCliques);
			
			for(int thresholdPos=0; thresholdPos < thresholds.length ; thresholdPos++){
				double threshold = thresholds[thresholdPos];	
				

				System.out.println("~~~~~~~~~Threshold = "+threshold+"~~~~~~~~~~~~");
				
				lcma.findGroups(threshold, localCliques);
			}
		}
	}
}
