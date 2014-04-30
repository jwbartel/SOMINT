package groups.evolution.old;

import groups.evolution.GroupPredictionList;
import groups.evolution.predictions.oldchoosers.OldGroupAndPredictionPair;
import groups.evolution.predictions.oldchoosers.PredictionChooser;
import groups.evolution.predictions.oldchoosers.PredictionChooserSelector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;


public class GroupMaintainer<V> {
	static final int MAX_MERGES = 3;
	static final boolean REUSE_PREDICTED_GROUPS = false;
	
	public static final double IDEAL_MATCHING_THRESHOLD = 0;
	
	//ArrayList<Set<V>> usedOldGroups = new ArrayList<Set<V>>();
	//ArrayList<Set<V>> foundMaintanedGroups = new ArrayList<Set<V>>();
	//ArrayList<Set<V>> unchangedGroups = new ArrayList<Set<V>>();
	
	public static final int S_MORE_IMPORTANT = 0;
	public static final int D_MORE_IMPORTANT = 1;
	public static final int EUCLIDEAN_S_AND_D = 2;
	public static final int LCMA = 3;
	public static final int SUBSET = 4;
	
	ArrayList<GroupMorphingTuple<V>> tuples = new ArrayList<GroupMorphingTuple<V>>();
	
	static class HybridGroupMatch<V> implements Comparable<HybridGroupMatch<V>>{
		
		
		Double s;
		Double d;
		Double lcma;
		Set<V> group;
		int changes;
		int type;
		
		
		public HybridGroupMatch(Set<V> group, double s, double d, double lcma, int changes, int type){
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
			}else if(type == LCMA){
				return lcma.compareTo(arg0.lcma);
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
		
		public double getLCMA(){
			return LCMA;
		}
		
		public int getChanges(){
			return changes;
		}
	}
	
	static class AdjustedGroupMatch<V> implements Comparable<AdjustedGroupMatch<V>>{
		Set<V> newMembers;
		Set<V> originalGroup;
		Double percentDifference;
		Double expectedDifference;
		
		
		public AdjustedGroupMatch(Set<V> newMembers, Set<V> group, double percentDifference, double expectedDifference){
			this.newMembers = newMembers;
			this.originalGroup = group;
			this.percentDifference = percentDifference;
			this.expectedDifference = expectedDifference;
		}

		@Override
		public int compareTo(AdjustedGroupMatch<V> arg0) {
			int errorCompare = this.getObservedError().compareTo(arg0.getObservedError());
			if(errorCompare != 0) return errorCompare;
			
			int sizeCompare = new Integer(newMembers.size()).compareTo(arg0.newMembers.size());
			if(sizeCompare != 0) return sizeCompare;
			
			return newMembers.toString().compareTo(arg0.newMembers.toString());
		}
		
		private Double getObservedError(){
			return Math.abs(percentDifference - expectedDifference);
		}
		
	}
	
	static final double KELLI_AVERAGE_DELETION_RATE = .094;
	
	/*private GroupPredictionList<V> getPredictionList(Set<V> oldGroup, Collection<Set<V>> predictedGroups, Collection<Set<V>> usedPredictedGroups,
			Set<V> newIndividuals, double percentNew, double threshold){
		GroupPredictionList<V> predictionList = new GroupPredictionList<V>(oldGroup);
		
		for(Set<V> predictedGroup: predictedGroups){
			
			if(usedPredictedGroups.contains(predictedGroup)) continue;
			
			Set<V> existingPredictionMembers = new HashSet<V>(predictedGroup);
			existingPredictionMembers.removeAll(newIndividuals);
			
			//Step 3a: get the counts for adds, removes, and expected growth
			int newAdds = intersectionCount(predictedGroup, newIndividuals);
			int inserts = getSubtractionSize(existingPredictionMembers, oldGroup);
			int deletes = getSubtractionSize(oldGroup, existingPredictionMembers);
			
			//Step 3b: get the euclidean distance between the points and (0, 0, |f|/(1-p))
			double distance = getDistanceFromBest(inserts, deletes, newAdds, oldGroup.size(), percentNew);
			if(distance <= threshold){
				predictionList.addPrediction(predictedGroup);
			}
		}
		
		return predictionList;
		return new ExpectedScalingPredictionListMaker<V>().getPredictionList(oldGroup, predictedGroups, usedPredictedGroups, newIndividuals, percentNew, threshold);
	}*/
	
	protected Set<V>  getNewMembersOfGroup(Set<V> group, Collection<V> newMembers){
		group = new TreeSet<V>(group);
		group.retainAll(newMembers);
		return group;
	}
	
	public static Set<Set> oldMembersWithMultipleMatchings = new HashSet<Set>();
	
	public int[] selectFromPredictionList(int participant, int totalOldLists, Set<GroupPredictionList<V>> matchings,
			Collection<V> newMembers, Collection<Set<V>> ideals, Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap,
			Collection<OldGroupAndPredictionPair<V>> usedPairings,
			Collection<Set<V>> usedIdealGroups, Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups){

		int expandedGroups = 0;
		int manualAddsRequired = 0;
		int manualDeletesRequired = 0;
		int selectCount = 0;
		int automatedAdds = 0;
		
		
		//Maybe need to allow predictions to be used multiple times
		//Need to allow ideals to be reached more than once
		
		//Four cases
		// 1: all disjoint prediction lists of length 1
		//			- definite predicted expansions, least amount of uncertainty
		//
		// 2: all prediction lists of length 1, but some overlap between lists
		//			- may mean predictions were split, so need to be reused
		//			- may mean one or more predictions are wrong and need to be ignored
		//			- need some way of testing whether predictions are appropriate (possibly by euclidean distances)
		//
		// 3: all predictions lists larger than 1, with a definite smallest list
		//			- may mean that single f_initial needs to be morphed into multiple f_primes
		//			- may mean one or more predictions are incorrectly associated and should be ignored
		//			- should not be a separate case from 4 when reusing predictions
		//
		// 4: all predictions lists larger than 1, with no definite smallest list
		//			- a hybridization of cases 2 and 3
		//			- do not attempt if no possible solution for both 2 & 3
		
		ArrayList<GroupPredictionList<V>> smallestPredictionLists = getSmallestPredictionLists(matchings, usedOldGroups);
		
		@SuppressWarnings("unchecked")
		PredictionChooser<V> chooser = PredictionChooserSelector.getChooser();

		boolean stopMorphing = false;
		while(smallestPredictionLists.size() > 0){
			//If no prediction lists, no need to try and select a best prediction
			System.out.print("\t"+smallestPredictionLists.size()+" smallest lists,");
			
			if(smallestPredictionLists.get(0).size() == 1){
				//Case 1 or 2
				
				for(GroupPredictionList<V> list: smallestPredictionLists){
					if(!usedOldGroups.contains(list.getF())){
						
					}
				}
				
				Collection<GroupPredictionList<V>> intersectingLists = getIntersectingLists(smallestPredictionLists);
				
				if(intersectingLists.size() == 0){
					//Case 1
					System.out.print("Case1");
					int[] stats = chooser.modelPredictionChoosingCase1(participant, smallestPredictionLists, newMembers, smallestPredictionLists, oldToIdealGroupsMap, usedPairings, usedOldGroups, usedPredictedGroups, usedIdealGroups);
						//modelPredictedExpansionsSelectionCase1(smallestPredictionLists, newMembers, predictionLists, oldToIdealGroupsMap, usedOldGroups, usedPredictedGroups, usedIdealGroups);
					expandedGroups += stats[0];
					manualAddsRequired += stats[1];
					manualDeletesRequired += stats[2];
					selectCount += stats[3];
					automatedAdds += stats[4];
					
				}else{
					//Case 2
					System.out.print("Case2");
					
					//ArrayList<GroupPredictionList<V>> disjointLists = new ArrayList<GroupPredictionList<V>>(smallestPredictionLists);
					//disjointLists.removeAll(intersectingLists);
					//if(disjointLists.size() == 0) break;
					int[] stats = chooser.modelPredictionChoosingCase2(participant, smallestPredictionLists, intersectingLists, newMembers, smallestPredictionLists, oldToIdealGroupsMap, usedPairings, usedOldGroups, usedPredictedGroups, usedIdealGroups); 
						//modelPredictedExpansionsSelectionCase1(disjointLists, newMembers, predictionLists, oldToIdealGroupsMap, usedOldGroups, usedPredictedGroups, usedIdealGroups);
					expandedGroups += stats[0];
					manualAddsRequired += stats[1];
					manualDeletesRequired += stats[2];
					selectCount += stats[3];
					automatedAdds += stats[4];
					
					if(stats[5] == 1) break;
					
					//Probably don't checkToStopMorphing
					//checkToStopMorphing = true;
					//break;
					//TODO
					
				}
				
			}else{
				//Case  3 or 4
				System.out.print("Case3 or Case4");
				
				for(GroupPredictionList<V> list: smallestPredictionLists){
					if(!usedOldGroups.contains(list.getF())){
						oldMembersWithMultipleMatchings.add(list.getF());
					}
				}
				
				/*if(smallestPredictionLists.size() == 1 && predictionLists.size() == 1){
					//Case 3 where only one predictionList exists
					int[] stats = modelPredictedExpansionsSelectionCase3(smallestPredictionLists.get(0), newMembers, predictionLists, oldToIdealGroupsMap, usedOldGroups, usedPredictedGroups, usedIdealGroups);
					expandedGroups += stats[0];
					manualAddsRequired += stats[1];
					manualDeletesRequired += stats[2];
					selectCount += stats[3];
					automatedAdds += stats[4];
				}else{

					int numOldUsed = usedOldGroups.size();
					int numOldPredicted = predictionLists.size();
					int coveredOldLists = usedOldGroups.size() + predictionLists.size();
					if(totalOldLists == coveredOldLists) {
						stopMorphing = true;
					}
					break;
					//TODO
				}*/
				
				int numOldUsed = usedOldGroups.size();
				int numOldPredicted = matchings.size();
				int coveredOldLists = usedOldGroups.size() + matchings.size();
				if(totalOldLists == coveredOldLists) {
					stopMorphing = true;
				}
				break;
			}
			
			smallestPredictionLists = getSmallestPredictionLists(matchings, usedOldGroups);
			
		}
		
		tuples.addAll(chooser.getTuples());
		//TODO:handle tuples
		
		int[] stats = new int[6];
		stats[0] = expandedGroups;
		stats[1] = manualAddsRequired;
		stats[2] = manualDeletesRequired;
		stats[3] = selectCount;
		stats[4] = automatedAdds;
		stats[5] = (stopMorphing)? 1: 0;
		return stats;
	}
	
	private int[] modelPredictedExpansionsSelectionCase1(ArrayList<GroupPredictionList<V>> smallestPredictionLists, Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists, Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap, 
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups, Collection<Set<V>> usedIdeals){
		
		int expandedGroups = 0;
		int addCount = 0;
		int removeCount = 0;
		int selectCount = 0;
		int automatedAdds = 0;
	
		for(GroupPredictionList<V> predictionList : smallestPredictionLists){
			
			Set<V> oldGroup = predictionList.getF();
			if(usedOldGroups.contains(oldGroup) || predictionList.size() == 0){
				continue;
			}
			
			Set<V> prediction = predictionList.getPredictions().iterator().next();
			Set<V> membersToAdd = new TreeSet<V>(prediction);
			membersToAdd.retainAll(newMembers);
			
			ArrayList<Set<V>> possibleIdealExpansions = oldToIdealGroupsMap.get(oldGroup);
			Set<V> bestIdealExpansion = getBestIdealExpansion(oldGroup, membersToAdd, possibleIdealExpansions, usedIdeals);
			int[] addsAndRemoves = getAddsAndRemoves(oldGroup, membersToAdd, bestIdealExpansion);
			int adds = addsAndRemoves[0];
			int removes = addsAndRemoves[1];
			
			expandedGroups++;
			addCount += adds;
			removeCount += removes;
			automatedAdds += membersToAdd.size();
			removeSelection(oldGroup, prediction, bestIdealExpansion, oldToIdealGroupsMap, predictionLists, usedOldGroups, usedPredictedGroups, usedIdeals);
		
			
		}
		
		int[] stats = new int[5];
		stats[0] = expandedGroups;
		stats[1] = addCount;
		stats[2] = removeCount;
		stats[3] = selectCount;
		stats[4] = automatedAdds;
		return stats;
	}
	
	private int[] modelPredictedExpansionsSelectionCase3(GroupPredictionList<V> predictionList, Collection<V> newMembers,
			Collection<GroupPredictionList<V>> predictionLists, Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap, 
			Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups, Collection<Set<V>> usedIdeals){
		
		int expandedGroups = 0;
		int addCount = 0;
		int removeCount = 0;
		int selectCount = 0;
		int automatedAdds = 0;
		
		Set<V> oldGroup = predictionList.getF();
		if(!usedOldGroups.contains(oldGroup) && predictionList.size() != 0){
			
			for(Set<V> prediction: predictionList.getPredictions()){

				Set<V> membersToAdd = new TreeSet<V>(prediction);
				membersToAdd.retainAll(newMembers);
				
				ArrayList<Set<V>> possibleIdealExpansions = oldToIdealGroupsMap.get(oldGroup);
				Set<V> bestIdealExpansion = getBestIdealExpansion(oldGroup, membersToAdd, possibleIdealExpansions, usedIdeals);
				
				int[] addsAndRemoves = getAddsAndRemoves(oldGroup, membersToAdd, bestIdealExpansion);
				int adds = addsAndRemoves[0];
				int removes = addsAndRemoves[1];
				
				expandedGroups++;
				addCount += adds;
				removeCount += removes;
				automatedAdds += membersToAdd.size();
				removeSelection(oldGroup, prediction, bestIdealExpansion, oldToIdealGroupsMap, predictionLists, usedOldGroups, usedPredictedGroups, usedIdeals);
			}
			
		}
		int[] stats = new int[5];
		stats[0] = expandedGroups;
		stats[1] = addCount;
		stats[2] = removeCount;
		stats[3] = selectCount;
		stats[4] = automatedAdds;
		return stats;
	}
	
	private Set<V> getBestIdealExpansion(Set<V> oldGroup, Set<V> membersToAdd, Collection<Set<V>> possibleIdealExpansions,
			Collection<Set<V>> usedIdeals){
		
		Set<V> bestIdealExpansion = null;
		int costBestIdealExpansion = -1;
		
		for(Set<V> possibleIdealExpansion : possibleIdealExpansions){
			
			int[] addsAndRemoves = getAddsAndRemoves(oldGroup, membersToAdd, possibleIdealExpansion);
			int adds = addsAndRemoves[0];
			int removes = addsAndRemoves[1];
			int cost = adds + removes;
			

			if(costBestIdealExpansion == -1 ||
					(cost < costBestIdealExpansion && (usedIdeals.contains(bestIdealExpansion) || !usedIdeals.contains(bestIdealExpansion) || costBestIdealExpansion > cost + IDEAL_MATCHING_THRESHOLD)) ||
					(cost >= costBestIdealExpansion - IDEAL_MATCHING_THRESHOLD && usedIdeals.contains(bestIdealExpansion) && !usedIdeals.contains(possibleIdealExpansion))){
				bestIdealExpansion = possibleIdealExpansion;
				costBestIdealExpansion = cost;
			}
		}
		
		return bestIdealExpansion;
		
	}
	
	private Collection<GroupPredictionList<V>> getIntersectingLists(ArrayList<GroupPredictionList<V>> predictionLists){
		
		Set<GroupPredictionList<V>> intersectingLists = new HashSet<GroupPredictionList<V>>();
		
		for(int i=0; i<predictionLists.size(); i++){
			for(int j=i+1; j<predictionLists.size(); j++){
				
				GroupPredictionList<V> list1 = predictionLists.get(i);
				GroupPredictionList<V> list2 = predictionLists.get(j);
				
				if(intersectionExists(list1.getPredictions(), list2.getPredictions())){
					intersectingLists.add(list1);
					intersectingLists.add(list2);
				}
				
			}
		}
		
		return intersectingLists;
	}
	
	private ArrayList<GroupPredictionList<V>> getSmallestPredictionLists(Set<GroupPredictionList<V>> predictionLists, Collection<Set<V>> usedOldGroups){
		ArrayList<GroupPredictionList<V>> toSelectFrom = new ArrayList<GroupPredictionList<V>>();
		int smallestListSize = -1;
		
		//Check for a prediction list of size 1
		for(GroupPredictionList<V> predictionList : predictionLists){
			
			if(usedOldGroups.contains(predictionList.getF())){
				//Ignored already expanded group
				continue;
			}
			
			int currentSize = predictionList.size();
			if(smallestListSize == -1 || smallestListSize == currentSize){
				toSelectFrom.add(predictionList);
			}else if(currentSize < smallestListSize){
				toSelectFrom.clear();
				smallestListSize = currentSize;
				toSelectFrom.add(predictionList);
			}
			
		}
		
		return toSelectFrom;
	}
	
	private int[] getExpandingCosts(Set<V> oldGroup, Set<V> prediction, Set<V> ideal, Collection<V> newMembers){
		
		Set<V> membersToAdd = new TreeSet<V>(prediction);
		
		int[] addsAndRemoves = getAddsAndRemoves(oldGroup, membersToAdd, ideal);
		return addsAndRemoves;
	}
	
	
	
	private void removeSelection(Set<V> usedOldGroup, Set<V> usedPrediction, Set<V> usedIdeal, Map<Set<V>, ArrayList<Set<V>>> oldToIdealGroupsMap,
			Collection<GroupPredictionList<V>> predictionLists, Collection<Set<V>> usedOldGroups, Collection<Set<V>> usedPredictedGroups, Collection<Set<V>> usedIdealGroups){
		
		usedOldGroups.add(usedOldGroup);
		usedPredictedGroups.add(usedPrediction);
		usedIdealGroups.add(usedIdeal);
		
		if(!REUSE_PREDICTED_GROUPS){
			//Clean up prediction lists
			Set<GroupPredictionList<V>> toRemove = new TreeSet<GroupPredictionList<V>>();
			for(GroupPredictionList<V> predictionList: predictionLists){
				
				if(predictionList.getF().equals(usedOldGroup)){
					//This already made all predicted expansions for this group
					toRemove.add(predictionList);
					continue;
				}
				
				predictionList.removePrediction(usedPrediction);
				if(predictionList.size() == 0){
					toRemove.add(predictionList);
				}
				
			}
			predictionLists.removeAll(toRemove);
		}
		
	}
	
	/*private double getDistanceFromBest(int inserts, int deletes, int newAdds, int oldGroupSize, double percentNew){
		double expectedInserts = 0;
		double expectedDeletes = 0;
		double expectedNewAdds = Math.round((((percentNew)/(1.0-percentNew))*oldGroupSize));
		
		double retVal = Math.pow(((double) inserts) - expectedInserts, 2.0);
		retVal += Math.pow(((double) deletes) - expectedDeletes, 2.0);
		retVal += Math.pow(((double) newAdds) - expectedNewAdds, 2.0);
		
		retVal = Math.sqrt(retVal);
		return retVal;
	}*/
	
	@SuppressWarnings("rawtypes")
	private boolean intersectionExists(Collection a, Collection b){
		if(a.size()>b.size()){
			return intersectionExists(b, a);
		}
		
		for(Object o:a){
			if(b.contains(o)){
				return true;
			}
		}
		
		return false;
		
	}
	
	private int intersectionCount(Set<V> a, Set<V> b){ 
		if(a.size()>b.size()){
			return intersectionCount(b, a);
		}
		
		int count = 0;
		for(V v:a){
			if(b.contains(v)){
				count++;
			}
		}
		return count;
	}
	
	/*private int getSubtractionSize(Set<V> a, Set<V> b){
		int count = 0;
		for(V v:a){
			if(!b.contains(v)){
				count++;
			}
		}
		return count;
	}*/
	
	public String[] findMaitenanceByMatchingAdjustedPredictions(double percentNew, double deletionRate, double error, 
			Set<V> oldGroup, Set<V> newIndividuals, Collection<Set<V>> filteredGuessedGroups, Set<V> intendedGroup,
			Map<Set<V>, ArrayList<Set<V>>> mergedMappings, Collection<Set<V>> usedPredictions){
		
		int manualEffort = intendedGroup.size() - oldGroup.size();
		
		
		//Calculate the thresholds
		double percentDifferenceExpected = percentNew/(1.0-percentNew);
		double expectedEffort = percentDifferenceExpected*deletionRate;
		
		double minDifference = percentDifferenceExpected - expectedEffort;
		minDifference = minDifference - minDifference*error;
		
		double maxDifference = percentDifferenceExpected + expectedEffort;
		maxDifference = maxDifference + maxDifference*expectedEffort;
		
		filteredGuessedGroups = new ArrayList<Set<V>>(filteredGuessedGroups);
		
		Collection<AdjustedGroupMatch<V>> matchedGroups = new TreeSet<AdjustedGroupMatch<V>>();
		
		//Sort out groups that don't match 
		for(Set<V> group:filteredGuessedGroups){
			
			Set<V> shrunkGroup = new TreeSet<V>(group);
			shrunkGroup.retainAll(newIndividuals);
			
			double percentToAdd = ((double) shrunkGroup.size())/oldGroup.size();
			
			if(minDifference <= percentToAdd && maxDifference >= percentToAdd){
				matchedGroups.add(new AdjustedGroupMatch<V>(shrunkGroup, group, percentToAdd, percentDifferenceExpected));
			}	
			
		}
		
		int listSize = matchedGroups.size();
		int depthOfBest = -1;
		int changesForBest = -1;
		AdjustedGroupMatch<V> bestMatch = null;
		
		int depth=0;
		Iterator<AdjustedGroupMatch<V>> matchIter = matchedGroups.iterator();
		while(matchIter.hasNext()){
			AdjustedGroupMatch<V> match = matchIter.next();
			int changes = getNecessaryChanges(oldGroup, match.newMembers, intendedGroup);
			
			if(changes != -1 && (changesForBest == -1 || changesForBest > changes) ){
				depthOfBest = depth;
				changesForBest = changes;
				bestMatch = match;
			}
			
			depth++;
		}
		
		if(bestMatch != null){
			usedPredictions.add(bestMatch.originalGroup);
			ArrayList<Set<V>> mapping = mergedMappings.get(bestMatch.originalGroup);
			if(mapping != null){
				usedPredictions.addAll(mapping);
			}
		}
		
		String[] stats = new String[3];
		stats[0] = ""+listSize;
		stats[1] = ""+depthOfBest;
		stats[2] = ""+changesForBest;
		return stats;
		
	}
	
	
	
	public String[] findMaintenanceByMatching(double s_threshold, double d_threshold, Set<V> seed, Collection<Set<V>> guessedGroups, Set<V> intendedGroup){
		
		int manualEffort = intendedGroup.size() - seed.size();
		/*if(manualEffort == 0){
			unchangedGroups.add(intendedGroup);
		}*/
		
		guessedGroups = new ArrayList<Set<V>>(guessedGroups);
		
		
		double maxSGroupS = -1;
		double maxSGroupD = -1;
		int maxSGroupChanges = -1;
		
		double minDGroupS = -1;
		double minDGroupD = -1;
		int minDGroupChanges = -1;

		double minEuclidean = -1;
		double minEuclideanS = -1;
		double minEuclideanD = -1;
		int minEuclideanChanges = -1;
		
		double maxLCMA = -1;
		int maxLCMAChanges  = -1;
		
		double maxSubsetRatio = -1;
		int maxSubsetRatioChanges = -1;
		
		Iterator<Set<V>> groupsIter = guessedGroups.iterator();
		while(groupsIter.hasNext()){
			Set<V> group = groupsIter.next();
			
			double[] scores = mergeScores(seed, group);
			double s = scores[0];
			double d = scores[1];
			double euclidean = scores[2];
			double lcma = scores[3];
			double subsetRatio = scores[4];

			if(s == 0.0 || d == 1.0 || lcma == 0.0) continue;
			int changes = -1;//getNecessaryChanges(seed, group, intendedGroup);
			
			if((s >= s_threshold && d <= d_threshold )&& (maxSGroupS == -1 || maxSGroupS < s || (maxSGroupS == s && maxSGroupD > d)) ){
				if(changes == -1) changes = getNecessaryChanges(seed, group, intendedGroup);
				maxSGroupS = s;
				maxSGroupD = d;
				maxSGroupChanges = changes;
			}
			if((s >= s_threshold && d <= d_threshold )&&  (minDGroupD == -1 || minDGroupD > d || (minDGroupD == d && minDGroupS < s))){
				if(changes == -1) changes = getNecessaryChanges(seed, group, intendedGroup);
				minDGroupS = s;
				minDGroupD = d;
				minDGroupChanges = changes;
			}
			if((s >= s_threshold && d <= d_threshold )&& (minEuclidean == -1 || minEuclidean > euclidean) ){
				if(changes == -1) changes = getNecessaryChanges(seed, group, intendedGroup);
				minEuclidean = euclidean;
				minEuclideanS = s;
				minEuclideanD = d;
				minEuclideanChanges = changes;
			}
			if(maxLCMA == -1 || maxLCMA < lcma){
				if(changes == -1) changes = getNecessaryChanges(seed, group, intendedGroup);
				maxLCMA = lcma;
				maxLCMAChanges = changes;
			}
			if(subsetRatio > 0.0 && (maxSubsetRatio == -1 || maxSubsetRatio < subsetRatio)){
				if(changes == -1) changes = getNecessaryChanges(seed, group, intendedGroup);
				maxSubsetRatio = subsetRatio;
				maxSubsetRatioChanges = changes;
			}
		}
		
		
		
		String[] stats = new String[7];

		stats[0] = "" + intendedGroup.size();
		stats[1] = "" + manualEffort;
		stats[2] = "" + maxSGroupChanges;
		stats[3] = "" + minDGroupChanges;
		stats[4] = "" + minEuclideanChanges;
		stats[5] = "" + maxLCMAChanges;
		stats[6] = "" + maxSubsetRatioChanges;
		
		return stats;
	}

	
	static final double MAX_EUCLIDEAN = Math.sqrt(2.0);
	
	public String[] findMultipleMaintenanceByMerging(double s_threshold, double d_threshold, UndirectedGraph<V, DefaultEdge> graph,
			Set<V> seed, Collection<Set<V>> cliques, Set<V> intendedGroup, int type){
		cliques = new HashSet<Set<V>>(cliques);
		
		Collection<Set<V>> merges = new ArrayList<Set<V>>();
		
		Iterator<Set<V>> firstPassIter = cliques.iterator();
		while(firstPassIter.hasNext()){
			Set<V> clique = firstPassIter.next();
			
			double[] mergeScores = mergeScores(seed, clique);
			double s = mergeScores[0];
			double d = mergeScores[1];
			double euclidean = mergeScores[2];
			double lcma = mergeScores[3];
			
			boolean shouldMerge = false;
			if(type == S_MORE_IMPORTANT){
				shouldMerge = s >= s_threshold;
			}else if(type == D_MORE_IMPORTANT){
				shouldMerge = d <= d_threshold;
			}else if(type == EUCLIDEAN_S_AND_D){
				shouldMerge = euclidean < MAX_EUCLIDEAN;
			}else if(type == LCMA){
				shouldMerge = lcma > 0;
			}else if(type == SUBSET){
				shouldMerge = clique.containsAll(seed);
			}
			
			if(shouldMerge){
				Set<V> merge = new TreeSet<V>(seed);
				merge.addAll(clique);
				merges.add(merge);
			}
			
		}
		
		HybridCliqueMerger<V> merger = new HybridCliqueMerger<V>(graph);
		Collection<Set<V>> hybridMergedCliques = merger.findNetworksAndSubgroups(merges, 0.9, 0.35, 1.0, 0.15);
		
		return findMaintenanceByMatching(s_threshold, d_threshold, seed, hybridMergedCliques, intendedGroup);
		
	}
	
	public String[] findMaintenaceByMerging(double s_threshold, double d_threshold, Set<V> seed, Collection<Set<V>> cliques, Set<V> intendedGroup, int type){
		cliques = new HashSet<Set<V>>(cliques);
		
		
		Set<V> merge = new TreeSet<V>(seed);
			
		int pass = 0;
		while(true){
			if(pass >= MAX_MERGES) break;
			
			double currMaxS = -1;
			double currMinD = -1;
			double currMinEuclidean = -1;
			double currMaxLCMA = -1;
			
			Set<V> bestGroup = null;
			
			Collection<Set<V>> removedCliques = new ArrayList<Set<V>>();
			
			Iterator<Set<V>> iter = cliques.iterator(); 
			while(iter.hasNext()){
				
				Set<V> clique = iter.next();
				
				if(merge.equals(clique) || merge.containsAll(clique)){
					removedCliques.add(clique);
					continue;
				}
				double[] mergeScores = mergeScores(merge, clique);
				double s = mergeScores[0];
				double d = mergeScores[1];
				double euclidean = mergeScores[2];
				double lcma = mergeScores[3];
				
				if(type != LCMA && s < s_threshold && d > d_threshold) continue;
				
				if(type == S_MORE_IMPORTANT){
					if(currMaxS == -1 || currMaxS < s || (currMaxS == s && currMinD > d)){
						currMaxS = s;
						currMinD = d;
						bestGroup = clique;
					}
				}else if(type == D_MORE_IMPORTANT){
					if(currMinD == -1 || currMinD > d || (currMinD == d && currMaxS < s)){
						currMaxS = s;
						currMinD = d;
						bestGroup = clique;
					}
				}else if(type == EUCLIDEAN_S_AND_D){
					if(currMinEuclidean == -1 || currMinEuclidean < euclidean){
						currMinEuclidean = euclidean;
						bestGroup = clique;
					}
				}else if(type == LCMA){
					if(currMaxLCMA == -1 || currMaxLCMA < lcma){
						currMaxLCMA = lcma;
						bestGroup = clique;
					}
				}
				
				
			}
			
			boolean foundMerge = false;
			if(type == S_MORE_IMPORTANT && currMaxS != -1){
				foundMerge = true;
			}else if(type == D_MORE_IMPORTANT && currMinD != -1){
				foundMerge = true;
			}else if(type == EUCLIDEAN_S_AND_D && currMinEuclidean != -1){
				foundMerge = true;
			}else if(type == LCMA && currMaxLCMA == -1){
				foundMerge = true;
			}
			
			if(foundMerge && bestGroup != null){
				pass++;


				merge.addAll(bestGroup);
				
				cliques.removeAll(removedCliques);
				
			}else{
				break;
			}
		}
		
		int necessaryChanges = getNecessaryChanges(new TreeSet<V>(), merge, intendedGroup);
		
		String[] stats = new String[1];
		stats[0] = "" + necessaryChanges;
		
		return stats;
	}
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static int[] getAddsAndRemoves(Set seed, Set group, Set ideal){
		int adds = 0;
		int removes = 0;
		
		if(group == null || ideal == null){
			int[] vals = {-1,-1};
			return vals;
		}
		
		Set tempGroup = new TreeSet(group);
		tempGroup.addAll(seed);
		
		boolean intersection = false;
		Iterator groupIter = tempGroup.iterator();
		while(groupIter.hasNext()){
			Object o = groupIter.next();
			if(!ideal.contains(o)){
				removes++;
			}else{
				intersection= true;
			}
		}
		
		if(!intersection){
			int[] vals = {-1,-1};
			return vals;
		}
		
		Iterator  idealIter = ideal.iterator();
		while(idealIter.hasNext()){
			Object o = idealIter.next();
			if(!tempGroup.contains(o)){
				adds++;
			}
		}
		
		int[] vals = {adds,removes};
		return vals;
	}
	
	protected int getNecessaryChanges(Set<V> seed, Set<V> group, Set<V> ideal){
		int[] vals = getAddsAndRemoves(seed, group, ideal);
		if(vals[0]==-1){
			return -1;
		}else{
			return vals[0]+vals[1];
		}
		
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
		boolean subsetRelationship = b.containsAll(a);
		
		double percentSame = ((double) intersectionCount)/b.size();
		double percentDiff = ((double) differenceCount)/b.size();
		double euclidean = getEuclideanDistance(percentSame, percentDiff);
		double lcma = NA(intersectionCount, a, b);
		double subsetRatio = 0.0;
		if(subsetRelationship){
			subsetRatio = ((double) a.size())/((double)b.size());
		}
		
		double[] scores = {percentSame, percentDiff, euclidean, lcma, subsetRatio};
		
		return scores;
	}
	
	public Double getEuclideanDistance(double s, double d){
		return Math.sqrt(Math.pow(1-s, 2.0)+Math.pow(d, 2.0));
	}
	
	public double NA(int intersectionCount, Set<V> s1, Set<V> s2){
		
		return ((double)(intersectionCount * intersectionCount))/(s1.size() * s2.size());
		
	}
	
	public ArrayList<GroupMorphingTuple<V>> getTuples(){
		return tuples;
	}
}
