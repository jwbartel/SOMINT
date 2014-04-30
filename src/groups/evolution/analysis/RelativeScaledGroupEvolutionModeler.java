package groups.evolution.analysis;

import groups.evolution.GroupPredictionList;
import groups.evolution.MembershipChangeFinder;
import groups.evolution.EvolutionRecommendationSelector;
import groups.evolution.RecommendedEvolution;
import groups.evolution.old.GroupMaintainer;
import groups.evolution.old.GroupMorphingTuple;
import groups.evolution.predictions.lists.PredictionListSelector;
import groups.evolution.predictions.loading.PredictionLoaderSelector;
import groups.evolution.predictions.oldchoosers.OldGroupAndPredictionPair;
import groups.evolution.synthetic.SyntheticEvolutionDataGenerator;
import groups.seedless.kelli.IOFunctions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import bus.tools.FileFinder;
import bus.tools.TestingConstants;

public class RelativeScaledGroupEvolutionModeler {
	

	IOFunctions<Integer> ioHelp; //Helper for retrieving data stored in files
	
	Collection<OldGroupAndPredictionPair<Integer>> usedPairings = new TreeSet<OldGroupAndPredictionPair<Integer>>(); //Tracks when an old group has been associated with a prediction 	
	Collection<Set<Integer>> usedOldGroups = new HashSet<Set<Integer>>();			//Tracks which of the modeled old groups have already been morphed
	Collection<Set<Integer>> usedRecommenderEngineResults = new HashSet<Set<Integer>>();		//Tracks which of the predicted groups have been used to morph old groups
	Collection<Set<Integer>> usedIdealGroups = new HashSet<Set<Integer>>();			//Tracks which of the ideal groups have been reached through morphings


	Collection<Set<Integer>> oldGroups = new HashSet<Set<Integer>>(); //What the total available old groups are
	Map<Set<Integer>, ArrayList<Set<Integer>>> oldToIdealGroupsMap = new HashMap<Set<Integer>, ArrayList<Set<Integer>>>(); //What ideal groups old groups can morph into
	
	double maxPredictionThreshold = -1; //What is the maximum threshold we will iterate to before we stop offerring recommendations
	
	Collection<Set<Integer>> originalIdeals = new HashSet<Set<Integer>>();  //List of ideal groups before modeling starts.  Used to avoid contamination of the ideal groups
	
	
	int totalOldGroups = 0;
	
	int automaticExpansions = 0;
	int algorithmRequiredAddCount = 0;
	int algorithmRequiredDeleteCount = 0;
	int algorithmRequiredSelectCount = 0;
	int algorithmRequiredAutomatedAdds = 0;
	
	
	
	
	public void modelEvolution(int participant, Map<Set<Integer>, String> idealNames, Collection<Set<Integer>> recomenderEngineResults,
			double percentNew, int test) throws IOException{
		
		Set<Integer> newIndividuals = SyntheticEvolutionDataGenerator.loadNewMembership(participant, percentNew, test);
		runMyAlgorithm(participant, idealNames, recomenderEngineResults, newIndividuals, percentNew, test);
		
	}
	
	private void resetTrackedListsAndValues(){

		ioHelp = new IOFunctions<Integer>(Integer.class);
		
		usedPairings.clear();
		usedOldGroups.clear();
		usedRecommenderEngineResults.clear();
		usedIdealGroups.clear();
		
		oldGroups.clear();
		oldToIdealGroupsMap.clear();
		
		maxPredictionThreshold = -1;
		
		originalIdeals.clear();
		
		totalOldGroups = 0;
		
		automaticExpansions = 0;
		algorithmRequiredAddCount = 0;
		algorithmRequiredDeleteCount = 0;
		algorithmRequiredSelectCount = 0;
		algorithmRequiredAutomatedAdds = 0;
	}
	
	private void findMaxPredictionThreshold(Collection<Set<Integer>> predictedGroups, Collection<Set<Integer>> ideals, Collection<Integer> newMembers){
		
		double largestPredictionSize = -1;
		double largestIdealSize = -1;
		
		for(Set<Integer> oldGroup: oldGroups){
			for(Set<Integer> prediction: predictedGroups){
				
				Set<Integer>  merge = new TreeSet<Integer>(prediction);
				merge.retainAll(newMembers);
				merge.addAll(oldGroup);
				
				if(largestPredictionSize == -1 || largestPredictionSize < merge.size()){
					largestPredictionSize = merge.size();
				}
				
			}
		}
		
		for(Set<Integer> ideal: ideals){
			if(largestIdealSize  == -1 || largestIdealSize < ideal.size() ){
				largestIdealSize = ideal.size();
			}
		}
		
		maxPredictionThreshold = Math.sqrt(Math.pow(largestPredictionSize, 2) + Math.pow(largestIdealSize, 2) + Math.pow(newMembers.size(), 2));
		
	}
	
	private Set<Integer> getIdeal(String idealName, Map<Set<Integer>, String> idealNames){
		for(Entry<Set<Integer>, String> entry: idealNames.entrySet()){
			if(entry.getValue().equals(idealName)){
				return entry.getKey();
			}
		}
		return null;
	}
	
	/*
	 * Actions: populates ultimately what old groups can morph to
	 * Returns: the manual costs ({adds, deletes, splits}) of generating the ideals
	 * 
	 */
	private int[] populateOldToIdealGroupsMap(Map<Set<Integer>, String> idealNames, int participant, double percentNew, int test){
		int manualAddCost = 0;
		int manualDeleteCost = 0;
		int manualSplitCost = 0;
		
		MembershipChangeFinder<Integer> membershipChangeFinder = new MembershipChangeFinder<Integer>();
		File oldGroupToIdealFile = FileFinder.getOldGroupFile(participant, percentNew, test);
		Map<Set<Integer>, ArrayList<String>> oldGroupToIdealNames = ioHelp.loadCliqueMappedToIdeals(oldGroupToIdealFile.getPath());
		
		for(Set<Integer> oldGroup: oldGroupToIdealNames.keySet()){ 
			
		
			//Ignore cases where the new group is not an expansion, but a new group
			if(oldGroup.size() == 0) continue;
			oldGroups.add(oldGroup);
			ArrayList<Set<Integer>> idealMappings = new ArrayList<Set<Integer>>();
			
			ArrayList<String> idealNameMappings = oldGroupToIdealNames.get(oldGroup);
			
			//Add a cost for splitting for each additional group the user would have to split off the old one
			if(idealNameMappings.size() > 1){
				manualSplitCost += idealNameMappings.size() - 1;
			}
			
			//Count the manual cost for adding members to reach each ideal
			for(String idealName: idealNameMappings){
				
				Set<Integer> ideal = getIdeal(idealName, idealNames);
				if(ideal == null) continue;

				idealMappings.add(ideal);
				
				manualAddCost += (ideal.size() - oldGroup.size());
				
			}
			

			oldToIdealGroupsMap.put(oldGroup, idealMappings);
			
		}
		
		int[] manualCosts = {manualAddCost, manualDeleteCost, manualSplitCost};
		return manualCosts;
	}
	
	private void modelPredicionListCreationAndSelection(int participant, Collection<Set<Integer>> ideals, Collection<Set<Integer>> recommenderEngineResults,
			Set<Integer> newIndividuals, double percentNew){
		
		//Initialize the threshold and round
		double threshold = 0.0;
		int round = 1;
		
		System.out.print("\tRunning models for creating and selecting prediction lists...");
		ArrayList<GroupMorphingTuple<Integer>> tuples = new ArrayList<GroupMorphingTuple<Integer>>();
		
		Collection<RecommendedEvolution<Integer>> recommendations = EvolutionRecommendationSelector.selectRecommendationsAcrossAllThresholds(percentNew, newIndividuals, oldGroups, recommenderEngineResults, ideals, oldToIdealGroupsMap);
		
		/*while(true){
			int remainingIdeals = originalIdeals.size() - usedIdealGroups.size();
			
			if(remainingIdeals == 0) break;  //The user should stop asking for recommendations if he sees all the ideals he wants 
			
			if(usedRecommenderEngineResults.size() == recommenderEngineResults.size() || threshold > maxPredictionThreshold) break; 
			//If we have no more possible matches or we have surpassed the threshold, we should stop
			
			//Create the set of unused old groups
			Collection<Set<Integer>> unusedOldGroups = new HashSet<Set<Integer>>(oldGroups);
			unusedOldGroups.removeAll(usedOldGroups);
			
			//Create set of unused recommender engine results
			Collection<Set<Integer>> unusedRecommenderEngineResults = new HashSet<Set<Integer>>(recommenderEngineResults);
			unusedRecommenderEngineResults.remove(usedRecommenderEngineResults);

			//Find all possible matchings for this threshold
			Set<GroupPredictionList<Integer>> matchings = PredictionListSelector.getAllMatchings(unusedOldGroups, unusedRecommenderEngineResults, usedPairings, newIndividuals, percentNew, threshold);
			
			System.out.println();
			System.out.print("\tround "+round+"...\t"+remainingIdeals+" remaining ideals,"+matchings.size()+" prediction lists");
			
			//Select from matchings to present recommendations to the user
			EvolutionRecommendationSelector<Integer> selector = new EvolutionRecommendationSelector<Integer>();
			Collection<RecommendedEvolution<Integer>> recommendations = selector.selectRecommendationsForSingleThreshold(matchings, oldToIdealGroupsMap, usedPairings, unusedOldGroups, newIndividuals, usedRecommenderEngineResults, usedIdealGroups);
			
			//TODO: add the selected recommendations to the set of presented recommendations
			
			
			
			
			
			
			GroupMaintainer<Integer> maintainer = new GroupMaintainer<Integer>();
			
			int[] stats = maintainer.selectFromPredictionList(participant, totalOldGroups, matchings, newIndividuals, ideals, oldToIdealGroupsMap, usedPairings, usedIdealGroups, usedOldGroups, usedRecommenderEngineResults);
				//maintainer.selectFromPredictionList(totalOldGroups, predictionLists, newIndividuals, ideals, oldToIdealGroupsMap, usedIdealGroups, usedOldGroups, usedPredictedGroups);
			automaticExpansions += stats[0];
			algorithmRequiredAddCount += stats[1];
			algorithmRequiredDeleteCount += stats[2];
			algorithmRequiredSelectCount += stats[3];
			algorithmRequiredAutomatedAdds += stats[4];
			
			tuples.addAll(maintainer.getTuples());
			for(GroupMorphingTuple<Integer> tuple : tuples){
				tuple.setParticipant(participant);
				tuple.setNumNewParticipants(newIndividuals.size());
				tuple.setThreshold(threshold);
			}
			
			boolean stopMorphing = stats[5] == 1;
			if(stopMorphing) break;

			round++;
			threshold += TestingConstants.THRESHOLD_INCREMENT;
		}*/
		
		/*Integer oldMulitpleMatchCount = multipleMatchCounts.get(percentNew);
		if(oldMulitpleMatchCount == null){
			oldMulitpleMatchCount = 0;
		}
		oldMulitpleMatchCount += GroupMaintainer.oldMembersWithMultipleMatchings.size();
		multipleMatchCounts.put(percentNew, oldMulitpleMatchCount);
		
		Collection<Set<Integer>> coveredMultipleMatchedGroups = new HashSet<Set<Integer>>(usedOldGroups);
		coveredMultipleMatchedGroups.retainAll(GroupMaintainer.oldMembersWithMultipleMatchings);
		Integer coveredCount = coveredMultipleMatchCounts.get(percentNew);
		if(coveredCount == null){
			coveredCount = 0;
		}
		coveredMultipleMatchCounts.put(percentNew, coveredCount + coveredMultipleMatchedGroups.size());
		GroupMaintainer.oldMembersWithMultipleMatchings.clear();

		ioHelp = new IOFunctions<Integer>(Integer.class);

		String idNameMap = "data/Kelli/FriendshipData/2010Study/"+participant+"_People.txt";
		ioHelp.fillNamesAndIDs(idNameMap);
		
		String oldGroupFileName = oldGroupFilePrefix;
		oldGroupFileName += "participant_"+participant;
		oldGroupFileName += " "+"newIndividuals_"+newIndividuals.size();
		oldGroupFileName += " "+oldGroupFileSuffix;
		
		String newMembersFileName = newMembersFilePrefix;
		newMembersFileName += "participant_"+participant;
		newMembersFileName += " "+"newIndividuals_"+newIndividuals.size();
		newMembersFileName += " "+newMembersFileSuffix;
		ArrayList<Set<Integer>> newMembersCollection = new ArrayList<Set<Integer>>();
		newMembersCollection.add(newIndividuals);
		ioHelp.printCliqueNamesToFile(newMembersFileName, newMembersCollection);
		
		
		String idealFile = "data/Jacob/Ideal/"+participant+"_ideal.txt";
		Map<Set<Integer>, String> oldGroupNames = ioHelp.loadGroupNames("old group-", oldGroupFileName);
		Map<Set<Integer>, String> idealNames = ioHelp.loadIdealGroupNames(idealFile);
		
		Map<Set<Integer>, String> predictedGroupNames = PredictionLoaderSelector.loadPredictionNames(participant);
		
		try{
			String tupleFileName = tupleFilePrefix + tupleFileSuffix;
			BufferedWriter out = new BufferedWriter(new FileWriter(tupleFileName, true));
			for(GroupMorphingTuple<Integer> tuple: tuples){
				String oldGroupName = oldGroupNames.get(tuple.getOldGroup());
				String predictedGroupName = predictedGroupNames.get(tuple.getPrediction());
				String idealName = idealNames.get(tuple.getIdeal());
				
				out.write(""+tuple.getParticipant()+","+tuple.getNumNewParticipants()+","+percentNew+","+oldGroupName+","+predictedGroupName+","+idealName+","+tuple.getThreshold()+","+tuple.getOldIdealDifference()+","+tuple.getAddCount()+","+tuple.getDeleteCount());
				out.newLine();
			}
			out.flush();
			out.close();
		}catch(IOException e){
			e.printStackTrace();
		}*/
		//TODO: save tuple names to file
	}
	
	private void runMyAlgorithm(int participant, Map<Set<Integer>, String> idealNames, Collection<Set<Integer>> recommenderEngineResults,
			Set<Integer> newIndividuals, double percentNew, int test) throws IOException{
		
		Collection<Set<Integer>> ideals = idealNames.keySet();
		
		resetTrackedListsAndValues();
		findMaxPredictionThreshold(recommenderEngineResults, ideals, newIndividuals);
		
		originalIdeals.addAll(ideals);
		int[] manualCosts = populateOldToIdealGroupsMap(idealNames, participant, percentNew, test);

		String idNameMap = FileFinder.getFriendNameAndIdFileName(participant); 
		ioHelp.fillNamesAndIDs(idNameMap);

		totalOldGroups = oldGroups.size();

		//TODO: Check if this is necessary.  I don't think so because I now save this when generating data 
		/*Map<Set<Integer>, String> oldGroupNames = ioHelp.loadGroupNames("old group-", oldGroupFileName);
		
		BufferedWriter out = new BufferedWriter(new FileWriter(oldToIdealFileName));
		for(Set<Integer> oldGroup: oldToIdealGroupsMap.keySet()){
			String oldGroupName = oldGroupNames.get(oldGroup);
			
			out.write(oldGroupName+"-> [");
			
			ArrayList<Set<Integer>> idealMappings = oldToIdealGroupsMap.get(oldGroup);
			for(int i=0; i<idealMappings.size(); i++){
				Set<Integer> ideal = idealMappings.get(i);
				String idealName = idealNames.get(ideal);
				if(i>0) out.write(",");
				out.write(idealName);
			}
			
			out.write("]");
			out.newLine();
			
		}
		out.flush();
		out.close();*/
		
		
		//Should not be printing out data here, should be done elsewhere
		//I'm just keeping this code to remember what was being stored previously
		/*String prefix = ""+participant+","+percentNew+","+newIndividuals.size()+","+originalIdeals.size()+","+oldToIdealGroupsMap.keySet().size()+",";
		String newGroupData = prefix+","+recommenderEngineResults.size();
		System.out.print("\t"+totalOldGroups+" old,"+recommenderEngineResults.size()+" predicted,"+ideals.size()+" ideal");*/
		
		
		modelPredicionListCreationAndSelection(participant, ideals, recommenderEngineResults, newIndividuals, percentNew);
		
		Collection<Set<Integer>> unreachedIdeals = new HashSet<Set<Integer>>(originalIdeals);
		unreachedIdeals.removeAll(usedIdealGroups);
		
		Collection<Set<Integer>> unusedRecommenderEngineResults = new HashSet<Set<Integer>>(recommenderEngineResults);
		unusedRecommenderEngineResults.removeAll(usedRecommenderEngineResults);
		
		int unpredictedIdeals = unreachedIdeals.size();
		
		int sumUnpredictedIdealsSize = 0;
		for(Set<Integer> ideal : unreachedIdeals){
			sumUnpredictedIdealsSize += ideal.size();
		}
		
		//Again we should not be printing data here, just returning
		//Retained only to make sure things are consistent as I update code
		//writeEffortData(prefix+manualCosts[0]+","+automaticExpansions+","+algorithmRequiredAddCount+","+algorithmRequiredDeleteCount+","+algorithmRequiredAutomatedAdds+","+unpredictedIdeals+","+sumUnpredictedIdealsSize);
		
	}
}
