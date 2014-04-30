package bus.data.structures;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

public class ComparableSet<E> extends TreeSet<E> implements Comparable<E>{
	private static final long serialVersionUID = 1L;
	
	public ComparableSet(){
		super();
	}
	
	public ComparableSet(Collection<? extends E> c){
		super(c);
	}
	
	public ComparableSet(Comparator<? super E> comparator){
		super(comparator);
	}
	
	public ComparableSet(SortedSet<E> s){
		super(s);
	}

	public int compareTo(Object arg0) {
		if(!(arg0 instanceof ComparableSet<?>)){
			return 0;
		}
		
		return this.toString().compareTo(arg0.toString());
	}
	
	/*public boolean equals(Object o){
		if(! (o instanceof Set) ){
			return false;
		}
		
		@SuppressWarnings("unchecked")
		ComparableSet<E> setObj = (ComparableSet<E>) o;
		if(setObj.size() != this.size()){
			return false;
		}
		
		return setObj.containsAll(this) && this.containsAll(setObj);
		
	}*/
	
}
