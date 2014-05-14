package kelli.mergeAttempts.incremental;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class ATrace {
	int smallerCliqueSize;
	int largerCliqueSize;
	Set<Integer> smallerClique;
	Set<Integer> largerClique;
	int smallerCliqueIndex;
	int largerCliqueIndex;
	int mergeNumber;
	int comparisonNumber;
	Set<Integer> smallerCliquePointer;
	Set<Integer> largerCliquePointer;
	
	public ATrace(int _smallerCliqueSize,
	int _largerCliqueSize,
	Set<Integer> _smallerClique,
	Set<Integer> _largerClique, 
	int _smallerCliqueIndex, int _largerCliqueIndex, int _mergeNumber, int _comparisonNumber){
		smallerCliqueSize = _smallerCliqueSize;
		largerCliqueSize = _largerCliqueSize;
		smallerClique = new HashSet<Integer>(_smallerClique);
		smallerCliquePointer = _smallerClique;
		largerClique = new HashSet<Integer>(_largerClique);
		largerCliquePointer = _largerClique;
		smallerCliqueIndex = _smallerCliqueIndex;
		largerCliqueIndex = _largerCliqueIndex;
		mergeNumber = _mergeNumber;
		comparisonNumber = _comparisonNumber;
	}
	
	public int getSmallerCliqueSize() {
		return smallerCliqueSize;
	}
	public void setSmallerCliqueSize(int smallerCliqueSize) {
		this.smallerCliqueSize = smallerCliqueSize;
	}
	public int getLargerCliqueSize() {
		return largerCliqueSize;
	}
	public void setLargerCliqueSize(int largerCliqueSize) {
		this.largerCliqueSize = largerCliqueSize;
	}
	public Set<Integer> getSmallerClique() {
		return smallerClique;
	}
	public void setSmallerClique(Set<Integer> smallerClique) {
		this.smallerClique = smallerClique;
	}
	public Set<Integer> getLargerClique() {
		return largerClique;
	}
	public void setLargerClique(Set<Integer> largerClique) {
		this.largerClique = largerClique;
	}
	public String toString(){
		return "merge: "+mergeNumber+" comparison: "+comparisonNumber+" smallCliqueIndex: "+smallerCliqueIndex+" largeCliqueIndex"+largerCliqueIndex
		+"small: "+smallerCliqueSize+"    large:"+largerCliqueSize+" \n smaller:"+smallerClique+" \n Larger:"+largerClique;
	}
	public boolean equals(ATrace o2){
		return getSmallerClique().equals(o2.getSmallerClique()) &&
				getLargerClique().equals(o2.getLargerClique());
	}
	public Vector <Set<Integer>> getHistory(int cliqueIndex, Vector<ATrace> trace){
		Vector<Set<Integer>> history = new Vector<Set<Integer>>();
		
		for(ATrace t: trace){
			if(t.largerCliqueIndex == cliqueIndex){
				history.add(t.largerClique);
			} 
		}
		return history;
	}
	public static String compareTraces(Vector<ATrace> trace1, Vector<ATrace> trace2){
		int size = Math.min(trace1.size(), trace2.size());
		StringBuffer reVal = new StringBuffer();
		boolean printed = false;
		for (int i = 0; i< size; i++){
			if (!trace1.get(i).equals(trace2.get(i))){
				String s = "\n index: "+i+" trace1: "+trace1.get(i)+"   trace2:"+trace2.get(i);
				if(!printed) {
					System.out.println(s);
					printed = true;
				}
				reVal.append(s);
			}
		}
		int largeSize = Math.max(trace1.size(), trace2.size());
		Vector<ATrace> trace;
		String name = "trace1:";
		if(trace1.size() == largeSize){
			trace = trace1;
		} else{
			trace = trace2;
			name = "trace2:";
		}
		
		for (int i = size; i< largeSize; i++){
				String s = "\n index: "+i+name+trace.get(i);
				System.out.println(s);
				reVal.append(s);
		}
		return reVal.toString();
	}
}
