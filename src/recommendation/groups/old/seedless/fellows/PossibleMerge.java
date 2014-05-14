package recommendation.groups.old.seedless.fellows;

import java.util.Set;

public class PossibleMerge<V> implements Comparable<PossibleMerge<V>>{
	Set<V> clique1;
	Set<V> clique2;
	double cohesion;
	
	public PossibleMerge(Set<V> clique1, Set<V> clique2, double cohesion){
		this.clique1 = clique1;
		this.clique2 = clique2;
		this.cohesion = cohesion;
	}
	
	public Set<V> getClique1(){
		return clique1;
	}
	
	public Set<V> getClique2(){
		return clique2;
	}
	
	protected boolean[][] equalsGraph(Set<V> cliqueR0, Set<V> cliqueR1, Set<V> cliqueC0, Set<V> cliqueC1){
		boolean[][] equals = new boolean[2][2];
		
		equals[0][0] = cliqueR0.equals(cliqueC0);
		equals[0][1] = cliqueR0.equals(cliqueC1);
		equals[1][0] = cliqueR1.equals(cliqueC0);
		equals[1][1] = cliqueR1.equals(cliqueC1);
		
		return equals;
	}
	
	protected int compare(Set<V> s1, Set<V> s2, boolean areEqual){
		if(areEqual) return 0;
		/*if(s1.size() != s2.size()){
			return (new Integer(s1.size())).compareTo(s2.size());
		}*/
		
		return s1.toString().compareTo(s2.toString());
	}

	@Override
	public int compareTo(PossibleMerge<V> arg0) {
		
		int cohesionCompare = -1*(new Double(cohesion)).compareTo(arg0.cohesion);
		if(cohesionCompare != 0){
			return cohesionCompare;
		}
		
		Set<V> r0 = clique1, r1= clique2, c0 = arg0.clique1, c1 = arg0.clique2;
		boolean[][] equals = equalsGraph(r0, r1, c0, c1);
		int clique1Compare, clique2Compare;
		
		int otherCompare;
		if(equals[0][1] || equals[1][0]){
			otherCompare = 0;
			clique1Compare = compare(r0, c1, equals[0][1]);
		}else{
			otherCompare = 1;
			clique1Compare = compare(r0, c0, equals[0][0]);		
		}
		
		if(clique1Compare != 0){
			return clique1Compare;
		}
		
		if(otherCompare == 0){
			clique2Compare = compare(r1, c0, equals[1][0]);
		}else{
			clique2Compare = compare(r1, c1, equals[1][1]);
		}
		
		return clique1Compare;
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof PossibleMerge){
			return compareTo((PossibleMerge) o) == 0;
		}
		return false;
	}
	
	public boolean containsAtLeastOne(Set<V> c1, Set<V> c2){
		return clique1.equals(c1) || clique1.equals(c2) || clique2.equals(c1) || clique2.equals(c2);
	}
	
	public String toString(){
		return ""+cohesion+" clique1:"+clique1.size()+" clique2:"+clique2.size();
	}
}
