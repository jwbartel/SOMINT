package groups.seedless.fellows;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.UndirectedGraph;

public class Triangle<V extends Comparable<V>> implements Comparable<Triangle<V>>{

	@SuppressWarnings("rawtypes")
	static UndirectedGraph graph;
	Set<V> vertices = new TreeSet<V>();
	
	public Triangle(V v1, V v2, V v3){
		vertices.add(v1);
		vertices.add(v2);
		vertices.add(v3);
		if(vertices.size() != 3) throw new RuntimeException("Triangles must have three vertices.");
	}
	
	public Triangle(Set<V> vertices){
		if(vertices.size() != 3) throw new RuntimeException("Triangles must have three vertices.");
		this.vertices.addAll(vertices);
	}
	
	public Set<V> getVertices(){
		return new TreeSet<V>(vertices);
	}
	
	public boolean equals(Triangle<V> t){
		return vertices.size() == t.vertices.size() && vertices.containsAll(t.vertices);
	}
	
	public boolean contains(V vertex){
		return vertices.contains(vertex);
	}

	@Override
	public int compareTo(Triangle<V> t) {
		int pos =0;
		for(V v1: vertices){
			
			Iterator<V> iter = t.vertices.iterator();
			for(int i=0; i<pos; i++){
				iter.next();
			}
			V v2 = iter.next();
			
			int compareVal = v1.compareTo(v2);
			if(compareVal != 0) return compareVal;
			pos++;
		}
		
		if(this.equals(t)) return 0;
		return -1;
	}
	
	@SuppressWarnings("unchecked")
	public boolean exists(){
		Iterator<V> iter = vertices.iterator();
		V v1 = iter.next();
		V v2 = iter.next();
		V v3 = iter.next();
		
		try{
			return graph.containsEdge(v1, v2) && graph.containsEdge(v2, v3) && graph.containsEdge(v3, v1);
		}catch(NullPointerException e){
			throw e;
		}
	}
	
	public static void setGraph(UndirectedGraph g){
		graph = g;
	}
}
