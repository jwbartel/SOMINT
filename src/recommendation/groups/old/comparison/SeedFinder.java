package recommendation.groups.old.comparison;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class SeedFinder<V> {
	
	Random rand = new Random();
	
	public Set<V> findPsuedoRandomSeed(Set<V> group, int seedSizeLimit){
		
		Set<V> seed = new TreeSet<V>();
		Set<V> groupCopy = new TreeSet<V>(group);
		
		while(seed.size() < seedSizeLimit && groupCopy.size() != 0){
			int pos = rand.nextInt(groupCopy.size());
			
			Iterator<V> iter = groupCopy.iterator();
			for(int i=0; i<pos; i++){
				iter.next();
			}
			V element = iter.next();
			seed.add(element);
			groupCopy.remove(element);
		}
		
		return seed;
	}
	
	public static void main(String[] args){
		
		SeedFinder<Integer> seedFinder = new SeedFinder<Integer>();
		Set<Integer> group = new TreeSet<Integer>();
		for(int i=0; i<1000; i++){
			group.add(i);
		}
		
		int seedSize = 4;
		System.out.println(seedFinder.findPsuedoRandomSeed(group, seedSize));
		System.out.println(seedFinder.findPsuedoRandomSeed(group, seedSize));
		System.out.println(seedFinder.findPsuedoRandomSeed(group, seedSize));
		
		
	}
}
