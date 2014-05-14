package recommendation.groups.seeded;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class SeededLCMAGroupFinder<V> {
	
	/*double threshold = 0.0;
	
	public void setThreshold(double threshold){
		this.threshold =threshold;
	}*/
	
	static class LCMAGroupMatch<V> implements Comparable<LCMAGroupMatch<V>>{
		Double NAScore;
		Set<V> group;
		int changes;
		
		public LCMAGroupMatch(Set<V> group, double NAScore, int changes){
			this.group = group;
			this.NAScore = NAScore;
			this.changes = changes;
		}

		
		public int compareTo(LCMAGroupMatch<V> arg0) {
			return -1 * NAScore.compareTo(arg0.NAScore);
		}
		
		public Set<V> getGroup(){
			return group;
		}
		
		public double getNA(){
			return NAScore;
		}
		
		public int getChanges(){
			return changes;
		}
	}
	
	public String[] findGroupByMatching(Set<V> seed, Collection<Set<V>> guessedGroups, Set<V> intendedGroup){
		guessedGroups = new ArrayList<Set<V>>(guessedGroups);
		
		TreeSet<LCMAGroupMatch<V>> matches = new TreeSet<SeededLCMAGroupFinder.LCMAGroupMatch<V>>();
		
		Iterator<Set<V>> groupsIter = guessedGroups.iterator();
		while(groupsIter.hasNext()){
			Set<V> group = groupsIter.next();
			
			double NA = NA(seed, group);
			if(NA == 0.0) continue;
			int changes = getNecessaryChanges(group, intendedGroup);
			
			matches.add(new LCMAGroupMatch<V>(group, NA, changes));
		}
		
		int exactMatchDepth = -1;
		
		double bestLCMA = -1;
		int bestLCMADepth = -1;
		int bestLCMAChanges = -1;
		
		int leastChanges = -1;
		int leastChangesDepth = -1;
		double leastChangesLCMA = -1;
		
		
		
		int depth = 0;
		Iterator<LCMAGroupMatch<V>> matchesIter = matches.iterator();
		while(matchesIter.hasNext()){
			LCMAGroupMatch<V> match = matchesIter.next();
			
			if(match.getGroup().equals(intendedGroup)){
				exactMatchDepth = depth;
			}
			if(bestLCMA == -1 || bestLCMA < match.getNA()){
				bestLCMA = match.getNA();
				bestLCMADepth = depth;
				bestLCMAChanges = match.getChanges();
			}
			
			if(match.getChanges() != -1 && (leastChanges == -1 || leastChanges > match.getChanges())){
				leastChanges = match.getChanges();
				leastChangesDepth = depth;
				leastChangesLCMA = match.getNA();
			}
			
			depth++;
		}
		
		String[] stats = new String[8];
		stats[0] = "" + exactMatchDepth;

		
		stats[1] = "" + bestLCMA;
		stats[2] = "" + bestLCMADepth;
		stats[3] = "" + bestLCMAChanges;

		
		stats[4] = "" + leastChangesLCMA;
		stats[5] = "" + leastChangesDepth;
		stats[6] = "" + leastChanges;
		
		stats[7] = "" + depth; //groups that are checked
		
		return stats;
		
	}
	
	public String[] findGroupByMerging(double threshold, Set<V> seed, Collection<Set<V>> cliques, Set<V> intendedGroup){
		cliques = new HashSet<Set<V>>(cliques);
		Set<V> merge = new TreeSet<V>(seed);
		
		Set<V> leastChangesMerge = null;
		Integer leastChanges = null;
		int leastChangesPass = 0;
		
		Set<V> closestLCMAMerge = null;
		Double closestLCMAMergeNA = null;
		int closestLCMAPass = 0;
		
		int pass = 0;
		while(true){
			
			Set<V> bestMatch = null;
			Double bestScore = null;
			
			Collection<Set<V>> removedCliques = new ArrayList<Set<V>>();
			
			Iterator<Set<V>> iter = cliques.iterator(); 
			while(iter.hasNext()){
				
				Set<V> clique = iter.next();
				
				if(merge.equals(clique) || merge.containsAll(clique)){
					removedCliques.add(clique);
					continue;
				}
				double NA = NA(merge, clique);
				
				if(NA > threshold && (bestScore == null || bestScore <  NA)){
					bestScore = NA;
					bestMatch = clique;
				}
				
			}
			
			cliques.removeAll(removedCliques);
			
			if(bestScore != null && bestMatch != null){
				pass++;
				merge.addAll(bestMatch);
				cliques.remove(bestMatch);
				//System.out.println("pass:"+pass+" merge size:"+merge.size());
				
				double NA = NA(merge, intendedGroup);
				if(closestLCMAMergeNA == null || closestLCMAMergeNA < NA){
					closestLCMAMergeNA = NA;
					closestLCMAMerge = new TreeSet<V>(merge);
					closestLCMAPass = pass;
				}
				
				int changes = getNecessaryChanges(merge, intendedGroup);
				if(leastChanges == null || leastChanges < changes){
					leastChanges = changes;
					leastChangesMerge = new TreeSet<V>(merge);
					leastChangesPass = pass;
				}
				
				if(merge.equals(intendedGroup)){
					//System.out.println("Found match at pass:"+pass+"!!!!!!!!!!!!!!!!!!");
				}
				
				if(merge.size() >= intendedGroup.size()*2){
					break;
				}
				
				
			}else{
				break;
			}
		}
		
		
		
		String[] stats = new String[10];
		stats[0] = ""+intendedGroup.size(); //size of the intended group

		stats[1] = "";
		if(leastChangesMerge != null){
			stats[1] += NA(leastChangesMerge, intendedGroup); //lcma score of merge with least necessary changes
		}
		stats[2] = ""+leastChangesPass; //pass that created the merge with least necessary changes
		stats[3] = ""+leastChanges; //changes merge with least necessary changes
		
		stats[4] = ""+closestLCMAMergeNA; //lcma of closest scored merge
		stats[5] = ""+closestLCMAPass; //pass of closes LCMA score
		stats[6] = "";
		if(closestLCMAMerge != null){
			stats[6] += getNecessaryChanges(closestLCMAMerge, intendedGroup); //necessary changes for closes LCMA group
		}
		
		stats[7] = ""+NA(merge, intendedGroup); //lcma of final group
		stats[8] = ""+pass;//total passes;
		stats[9] = ""+getNecessaryChanges(closestLCMAMerge, intendedGroup); //necessary changes for final merge
		
		return stats;
		
	}
	
	public double NA(Set<V> s1, Set<V> s2){
		Set<V> intersection = new TreeSet<V>(s1);
		intersection.retainAll(s2);
		
		return ((double)(intersection.size() * intersection.size()))/(s1.size() * s2.size());
		
	}
	
	protected int getNecessaryChanges(Set<V> group, Set<V> ideal){
		int adds = 0;
		int removes = 0;
		
		if(group == null || ideal == null){
			return -1;
		}
		
		boolean intersection = false;
		Iterator<V> groupIter = group.iterator();
		while(groupIter.hasNext()){
			V v = groupIter.next();
			if(!ideal.contains(v)){
				removes++;
			}else{
				intersection= true;
			}
		}
		
		if(!intersection) return -1;
		
		Iterator<V>  idealIter = ideal.iterator();
		while(idealIter.hasNext()){
			V v = idealIter.next();
			if(!group.contains(v)){
				adds++;
			}
		}
		
		return adds+removes;
		
	}
}
