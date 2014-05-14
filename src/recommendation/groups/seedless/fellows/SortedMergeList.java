package recommendation.groups.seedless.fellows;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class SortedMergeList<V> extends TreeSet<PossibleMerge<V>>{

	private static final long serialVersionUID = 1L;

	public PossibleMerge<V> peekTopMerge(){
		Iterator<PossibleMerge<V>> iter = super.iterator();
		if(iter.hasNext()){
			return iter.next();
		}else{
			return null;
		}
	}
	
	public PossibleMerge<V> removeTopMerge(){
		PossibleMerge<V> top = peekTopMerge();
		String str = top.toString();
		
		Set<PossibleMerge<V>> removedMerges = new TreeSet<PossibleMerge<V>>();
		
		Iterator<PossibleMerge<V>> iter = super.iterator();
		while(iter.hasNext()){
			PossibleMerge<V> otherMerge = iter.next();
			if(otherMerge.containsAtLeastOne(top.clique1, top.clique2)){
				removedMerges.add(otherMerge);
			}
		}
		
		super.removeAll(removedMerges);
		return top;
		
		
	}
	
	public boolean hasTop(){
		return  super.iterator().hasNext();
	}
}
