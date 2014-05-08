package groups.comparison;

import groups.seedless.hybrid.IOFunctions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class ResultComparator<V>{
	
	public int[] compareGroups(Collection<Set<V>> groups, Collection<Set<V>> ideals){
		ArrayList<Set<V>> groupsCopy = removeDuplicates(groups);
		ArrayList<Set<V>> idealsCopy = removeDuplicates(ideals);
		
		int numGroups = groupsCopy.size();
		int sumGroupSizes = 0;
		for(int i=0; i<groupsCopy.size(); i++){
			sumGroupSizes += groupsCopy.get(i).size();
		}
		
		int sumIdealSizes = 0;
		for(int i=0; i<idealsCopy.size(); i++){
			sumIdealSizes+= idealsCopy.get(i).size();
		}
		
		Collection<Set<V>> usedIdealGroups = new ArrayList<Set<V>>();
				
		Collection<Set<V>> exactMatches = getExactMatches(groupsCopy, idealsCopy);
		groupsCopy.removeAll(exactMatches);
		usedIdealGroups.addAll(exactMatches);
		
		int repeats = 0;
		
		int totalChanges = 0;
		ArrayList<Set<V>> needChanges = new ArrayList<Set<V>>();
		
		ArrayList<Set<V>> unmatchable = new ArrayList<Set<V>>();
		
		Iterator<Set<V>> groupsIter = groupsCopy.iterator();
		while(groupsIter.hasNext()){
			
			Set<V> group = groupsIter.next();
			int necessaryChanges = -1;
			Set<V> matchingIdeal = null;
			
			Iterator<Set<V>> idealsIter = idealsCopy.iterator();
			while(idealsIter.hasNext()){
				Set<V> ideal = idealsIter.next();
				
				int changes = getNecessaryChanges(group, ideal);
				if(changes != -1){
					if(necessaryChanges == -1 || necessaryChanges > changes){
						necessaryChanges = changes;
						matchingIdeal = ideal;
					}
				}
			}
			
			if(necessaryChanges == -1){
				unmatchable.add(group);
			}else{
				needChanges.add(group);
				totalChanges += necessaryChanges;
				if(usedIdealGroups.contains(matchingIdeal)){
					repeats++;
				}
				usedIdealGroups.add(matchingIdeal);
			}
		}
		
		idealsCopy.removeAll(usedIdealGroups);
		int totalUnaddressedSizes = 0;
		Iterator<Set<V>> idealsIter = idealsCopy.iterator();
		while(idealsIter.hasNext()){
			Set<V> ideal = idealsIter.next();
			totalUnaddressedSizes += ideal.size();
		}
		
		int[] stats = new int[10];
		stats[0] = numGroups; //groups predicted
		stats[1] = sumGroupSizes; //sum of the sizes of predicted groups
		stats[2] = sumIdealSizes; //sum of the sizes of the ideal groups
		stats[3] = exactMatches.size(); //exactly correct predictions
		stats[4] = needChanges.size(); //predictions that need modifications
		stats[5] = totalChanges; //total modifications needed across all groups
		stats[6] = unmatchable.size();  //totally incorrect predictions
		stats[7] = repeats;  //total predictions that repeat a previously predicted ideal group
		stats[8] = idealsCopy.size();  //ideal groups that were not addressed by correct or partially correct predictions
		stats[9] = totalUnaddressedSizes; //sum(unaddressed ideal group sizes)
		return stats;
	}
	
	protected int getNecessaryChanges(Set<V> group, Set<V> ideal){
		int adds = 0;
		int removes = 0;
		
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
	
	
	protected Collection<Set<V>> getExactMatches(ArrayList<Set<V>> groups, ArrayList<Set<V>> ideals){
		ArrayList<Set<V>> matches = new ArrayList<Set<V>>();
		
		for(int i=0; i<groups.size(); i++){
			Set<V> group = groups.get(i);
			if(ideals.contains(group)){
				matches.add(group);
			}
			
		}
		
		return matches;
		
	}
		
	public ArrayList<Set<V>> removeDuplicates(Collection<Set<V>> groups){
		ArrayList<Set<V>> groupsCopy = new ArrayList<Set<V>>();
		
		Iterator<Set<V>> iter = groups.iterator();
		while(iter.hasNext()){
			Set<V> group = iter.next();
			if(!groupsCopy.contains(group)){
				groupsCopy.add(group);
			}
		}
		return groupsCopy;
	}
	
	public static void main(String[] args){
		
		ResultComparator<Integer> comparator = new ResultComparator<Integer>();
		
		File idealFolder = new File("data\\Jacob\\Ideal");
		File[] idealFiles = idealFolder.listFiles();
		Arrays.sort(idealFiles);
		
		System.out.println("participant, threshold, predicted groups, sum of group sizes, sum of ideal sizes, exact predictions, predictions needing adds or removes, total needed adds or removes, uncorrectable predictions, repeated predicted ideals, unpredicted ideal groups, sum of unaddressed ideal group sizes");
		for(int i=0; i<idealFiles.length; i++){
			File idealFile = idealFiles[i];
			Integer participant = Integer.parseInt(idealFile.getName().substring(0, idealFile.getName().indexOf('_')));
			
			String idNameMap = "data/Kelli/FriendshipData/2010Study/"+participant+"_People.txt";
			
			IOFunctions<Integer> ioHelp = new IOFunctions<Integer>(Integer.class);
			ioHelp.fillNamesAndIDs(idNameMap);
			
			Collection<Set<Integer>> ideals = ioHelp.loadIdealGroups(idealFile.getAbsolutePath());
			
			double[] lcmaThresholds = {1.0, 0.95, 0.9, 0.85, 0.8, 0.75, 0.7, 0.6, 0.65, 0.6, 0.55, 0.5, 0.45, 0.4, 0.35, 0.3, 0.25, 0.2, 0.15, 0.1, 0.05, 0.0};
			for(int thresholdPos=0; thresholdPos < lcmaThresholds.length; thresholdPos++){
				double threshold = lcmaThresholds[thresholdPos];
				
				
				String groupsFile = "data/Jacob/LCMA/"+participant+"_GroupsWithThreshold_"+threshold+".txt";
				Collection<Set<Integer>> groups = ioHelp.loadGroups(groupsFile);
				int[] stats = comparator.compareGroups(groups, ideals);
				
				System.out.print(""+participant+","+threshold);
				for(int s=0; s<stats.length; s++){
					System.out.print(","+stats[s]);
				}
				System.out.println();
				
			}
		}
		
	}
	
}
