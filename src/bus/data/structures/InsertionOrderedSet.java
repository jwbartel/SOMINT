package bus.data.structures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class InsertionOrderedSet<E> implements Set<E> {
	ArrayList<E> contents = new ArrayList<E>();
	
	public boolean add(E e) {
		if(!contains(e)){
			return contents.add(e);
		}
		return false;
	}

	public boolean addAll(Collection<? extends E> c) {
		boolean changed = false;
		Iterator<? extends E> iter = c.iterator();
		while(iter.hasNext()){
			changed = changed || add(iter.next());
		}
		return changed;
	}

	public void clear() {
		contents.clear();		
	}

	public boolean contains(Object o) {
		for(int i=0; i<contents.size(); i++){
			if(contents.get(i).equals(o)){
				return true;
			}
		}
		return false;
	}

	public boolean containsAll(Collection<?> c) {
		Iterator<?> iter = c.iterator();
		while(iter.hasNext()){
			if(!contains(iter.next())){
				return false;
			}
		}
		return true;
	}

	public boolean isEmpty() {
		return contents.isEmpty();
	}

	public Iterator<E> iterator() {
		return contents.iterator();
	}

	public boolean remove(Object o) {
		return contents.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return false;
	}

	public int size() {
		return contents.size();
	}

	public Object[] toArray() {
		return contents.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return contents.toArray(a);
	}

	
}
