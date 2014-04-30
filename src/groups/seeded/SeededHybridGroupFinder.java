package groups.seeded;

import groups.seeded.SeededLCMAGroupFinder.LCMAGroupMatch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;



public class SeededHybridGroupFinder<V> {
	
	public static final int S_MORE_IMPORTANT = 0;
	public static final int D_MORE_IMPORTANT = 1;
	public static final int EUCLIDEAN_S_AND_D = 2;
	
	static class HybridGroupMatch<V> implements Comparable<HybridGroupMatch<V>>{
		Double s;
		Double d;
		Set<V> group;
		int changes;
		int type;
		
		
		public HybridGroupMatch(Set<V> group, double s, double d, int changes, int type){
			this.group = group;
			this.s = s;
			this.d = d;
			this.changes = changes;
			this.type = type;
		}

		
		public int compareTo(HybridGroupMatch<V> arg0) {
			int sCompare = -1*s.compareTo(arg0.s);
			int dCompare = d.compareTo(arg0.d);
			
			if(type == S_MORE_IMPORTANT){
				if(sCompare == 0){
					return dCompare;
				}else{
					return sCompare;
				}
			}else if(type == D_MORE_IMPORTANT){
				if(dCompare == 0){
					return sCompare;
				}else{
					return dCompare;
				}
			}else if(type == EUCLIDEAN_S_AND_D){
				return getEuclideanDistance().compareTo(arg0.getEuclideanDistance());
			}else{
				return 0;
			}
		}
		
		public Set<V> getGroup(){
			return group;
		}
		
		public double getS(){
			return s;
		}
		
		public double getD(){
			return d;
		}
		
		public Double getEuclideanDistance(){
			return Math.sqrt(Math.pow(1-s, 2.0)+Math.pow(d, 2.0));
		}
		
		public int getChanges(){
			return changes;
		}
	}
	
	
	public String[] findGroupByMatching(Set<V> seed, Collection<Set<V>> guessedGroups, Set<V> intendedGroup, int type){
		
		guessedGroups = new ArrayList<Set<V>>(guessedGroups);
		
		TreeSet<HybridGroupMatch<V>> matches = new TreeSet<HybridGroupMatch<V>>();
		
		Iterator<Set<V>> groupsIter = guessedGroups.iterator();
		while(groupsIter.hasNext()){
			Set<V> group = groupsIter.next();
			
			double[] scores = mergeScores(seed, group);
			if(scores[0] == 0.0 && scores[1] == 1.0) continue;
			int changes = getNecessaryChanges(group, intendedGroup);
			
			matches.add(new HybridGroupMatch<V>(group, scores[0], scores[1], changes, type));
		}
		
		int exactMatchDepth = -1;
		
		int leastChanges = -1;
		double leastChangesS = -1;
		double leastChangesD = -1;
		int leastChangesPass = 0;
		
		double maxSGroupS = -1;
		double maxSGroupD = -1;
		int maxSGroupChanges = -1;
		int maxSPass = 0;
		
		double minDGroupS = -1;
		double minDGroupD = -1;
		int minDGroupChanges = -1;
		int minDPass = 0;

		double minEuclidean = -1;
		double minEuclideanS = -1;
		double minEuclideanD = -1;
		int minEuclideanChanges = -1;
		int minEuclideanPass = 0;
		
		int depth = 0;
		Iterator<HybridGroupMatch<V>> matchesIter = matches.iterator();
		while(matchesIter.hasNext()){
			HybridGroupMatch<V> match = matchesIter.next();
			double euclidean = match.getEuclideanDistance();
			
			if(match.getGroup().equals(intendedGroup)){
				exactMatchDepth = depth;
			}
			if(maxSGroupS == -1 || maxSGroupS < match.getS()){
				maxSGroupS = match.getS();
				maxSGroupD = match.getD();
				maxSGroupChanges = match.getChanges();
				maxSPass = depth;
			}
			if(minDGroupD == -1 || minDGroupD > match.getD()){
				minDGroupS = match.getS();
				minDGroupD = match.getD();
				minDGroupChanges = match.getChanges();
				minDPass = depth;
			}
			if(maxSGroupS == -1 || maxSGroupS < match.getS()){
				maxSGroupS = match.getS();
				maxSGroupD = match.getD();
				maxSGroupChanges = match.getChanges();
				maxSPass = depth;
			}
			if(minEuclidean == -1 || minEuclidean > euclidean){
				minEuclidean = euclidean;
				minEuclideanS = match.getS();
				minEuclideanD = match.getD();
				minEuclideanChanges = match.getChanges();
				minEuclideanPass = depth;
			}
			
			if(match.getChanges() != -1 && (leastChanges == -1 || leastChanges > match.getChanges())){
				leastChanges = match.getChanges();
				leastChangesPass = depth;
				leastChangesS = match.getS();
				leastChangesD = match.getD();
			}
			
			depth++;
		}
		
		String[] stats = new String[18];
		stats[0] = "" + exactMatchDepth;

		
		stats[1] = "" + maxSGroupS;
		stats[2] = "" + maxSGroupD;
		stats[3] = "" + maxSPass;
		stats[4] = "" + maxSGroupChanges;
		
		stats[5] = "" + minDGroupS;
		stats[6] = "" + minDGroupD;
		stats[7] = "" + minDPass;
		stats[8] = "" + minDGroupChanges;
		
		stats[9] = "" + minEuclideanS;
		stats[10] = "" + minEuclideanD;
		stats[11] = "" + minEuclideanPass;
		stats[12] = "" + minEuclideanChanges;
		
		stats[13] = "" + leastChangesS;
		stats[14] = "" + leastChangesD;
		stats[15] = "" + leastChangesPass;
		stats[16] = "" + leastChanges;
		
		stats[17] = "" + depth; //groups that are checked
		
		return stats;
	}

	public String[] findGroupByMerging(double s_threshold, double d_threshold, Set<V> seed, Collection<Set<V>> cliques, Set<V> intendedGroup, boolean sMoreImportant){
		cliques = new HashSet<Set<V>>(cliques);
		Set<V> merge = new TreeSet<V>(seed);
		
		Set<V> leastChangesMerge = null;
		Integer leastChanges = null;
		Double leastChangesS = null;
		Double leastChangesD = null;
		int leastChangesPass = 0;
		
		Set<V> closestSMerge = null;
		Double closestSMergeS = null;
		Double closestSMergeD = null;
		Integer closestSMergeChanges = null;
		int closestSPass = 0;
		
		Set<V> closestDMerge = null;
		Double closestDMergeS = null;
		Double closestDMergeD = null;
		Integer closestDMergeChanges = null;
		int closestDPass = 0;
		

		double[] currentScore = mergeScores(merge, intendedGroup);
		int currentChanges = getNecessaryChanges(merge, intendedGroup);	
		
		int pass = 0;
		while(true){
			Set<V> bestMatch = null;
			Double bestS = null;
			Double bestD = null;
			

			Collection<Set<V>> removedCliques = new ArrayList<Set<V>>();
			
			Iterator<Set<V>> iter = cliques.iterator(); 
			while(iter.hasNext()){
				
				Set<V> clique = iter.next();
				
				if(merge.equals(clique) || merge.containsAll(clique)){
					removedCliques.add(clique);
					continue;
				}
				double[] mergeScores = mergeScores(merge, clique);
				
				if(mergeScores[0] >= s_threshold || mergeScores[1] <= d_threshold){
					
					boolean isBest = false;
					if(bestS == null || bestD == null){
						isBest = true;
					}else if(sMoreImportant){
						//S outranks D
						
						if(bestS > mergeScores[0] || (bestS == mergeScores[0] && bestD < mergeScores[1]) ){
							isBest = true;
						}
						
					}else{
						//D outranks S
						if(bestD < mergeScores[1] || (bestD == mergeScores[1] && bestS > mergeScores[0]) ){
							isBest = true;
						}
						
					}
					
					if(isBest){
						bestS = mergeScores[0];
						bestD = mergeScores[1];
						bestMatch = clique;
					}
				}
				
			}
			
			cliques.removeAll(removedCliques);
			
			if(bestS != null && bestD != null && bestMatch != null){
				pass++;
				merge.addAll(bestMatch);
				cliques.remove(bestMatch);			
				if(closestSMerge == null || closestSMergeS < currentScore[0]){
					closestSMergeS = currentScore[0];
					closestSMergeD = currentScore[1];
					closestSMerge = new TreeSet<V>(merge);
					closestSPass = pass;
					closestSMergeChanges = currentChanges;
				}
				
				if(closestDMerge == null || closestDMergeS > currentScore[1]){
					closestDMergeS = currentScore[0];
					closestDMergeD = currentScore[1];
					closestDMerge = new TreeSet<V>(merge);
					closestDPass = pass;
					closestDMergeChanges = currentChanges;
				}
				
				if(leastChangesMerge == null || leastChanges > currentChanges){
					leastChanges = currentChanges;
					leastChangesS = currentScore[0];
					leastChangesD = currentScore[1];
					leastChangesMerge = new TreeSet<V>(merge);
					leastChangesPass = pass;
				}
				
				if(merge.size() >= intendedGroup.size()*2){
					break;
				}
				
			}else{
				break;
			}
		}
		
		String[] stats = new String[13];
		stats[0] = ""+intendedGroup.size(); //size of the intended group

		
		stats[1] = "" + leastChangesS; //S score of merge with least necessary changes
		stats[2] = "" + leastChangesD; //D score of merge with least necessary changes
		stats[3] = "" + leastChangesPass; //pass that created the merge with least necessary changes
		stats[4] = "" + leastChanges; //changes merge with least necessary changes
		
		
		stats[5] = "" + closestSMergeS;
		stats[6] = "" + closestSMergeD;
		stats[7] = "" + closestSPass;
		stats[8] = "" + closestSMergeChanges;

		
		stats[9] = "" + closestDMergeS;
		stats[10] = "" + closestDMergeD;
		stats[11] = "" + closestDPass;
		stats[12] = "" + closestDMergeChanges;

		
		stats[9] = "" + currentScore[0];
		stats[10] = "" + currentScore[1];
		stats[11] = "" + pass;
		stats[12] = "" + currentChanges;
		
		return stats;
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
	
	
	public double[] mergeScores(Set<V> a, Set<V> b){
		if(b.size() > a.size()){
			return mergeScores(b, a);
		}
		
		int intersectionCount=0, differenceCount=0;
		
		for (V member: b){
			if(a.contains(member))
				intersectionCount++;
			else{
				differenceCount++;
			}
		}
		
		double percentSame = ((double) intersectionCount)/b.size();
		double percentDiff = ((double) differenceCount)/b.size();
		
		double[] scores = {percentSame, percentDiff};
		
		return scores;
	}
}
