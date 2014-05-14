package recommendation.groups.old.evolution;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import recommendation.groups.old.evolution.analysis.EvolutionStats;
import recommendation.groups.old.evolution.analysis.EvolutionStatsBuilder;
import recommendation.groups.old.evolution.predictions.choosers.MultiRecommenderEngineResultRecommendationChooserFactory;
import recommendation.groups.old.evolution.predictions.choosers.RecommendationChooserSelector;
import recommendation.groups.old.evolution.predictions.choosers.SingleRecommenderEngineResultRecommendationChooserFactory;
import recommendation.groups.old.evolution.predictions.lists.ExpectedScalingPredictionListMakerFactory;
import recommendation.groups.old.evolution.predictions.lists.JaccardCoefficientPredictionListMakerFactory;
import recommendation.groups.old.evolution.predictions.lists.PredictionListSelector;
import recommendation.groups.old.evolution.predictions.loading.HybridPredictionLoaderFactory;
import recommendation.groups.old.evolution.predictions.loading.PredictionLoaderSelector;
import recommendation.groups.old.evolution.predictions.oldchoosers.MultiPredictionMultiIdealPredictionChooserFactory;
import recommendation.groups.old.evolution.predictions.oldchoosers.MultiPredictionSingleIdealPredictionChooserFactory;
import recommendation.groups.old.evolution.predictions.oldchoosers.OldGroupAndPredictionPair;
import recommendation.groups.old.evolution.predictions.oldchoosers.PredictionChooserSelector;
import recommendation.groups.old.evolution.predictions.oldchoosers.SinglePredictionMultiIdealPredictionChooserFactory;
import recommendation.groups.old.evolution.predictions.oldchoosers.SinglePredictionSingleIdealPredictionChooserFactory;
import recommendation.groups.seedless.hybrid.IOFunctions;
import bus.tools.FileFinder;
import bus.tools.TestingConstants;


public class GroupMorphingModeler {
	
	static Map<Double, Integer> multipleMatchCounts = new HashMap<Double, Integer>();
	static Map<Double, Integer> coveredMultipleMatchCounts = new HashMap<Double, Integer>();

	static double threshold_increment = 1.0;

	protected static String newGroupsHeader = null;
	
	protected IOFunctions<Integer> ioHelp;
	
	Map<Set<Integer>, ArrayList<Set<Integer>>> mergedMappings = new HashMap<Set<Integer>, ArrayList<Set<Integer>>>();
	
	protected File newGroupsStatsFile = null;
	
	Collection<OldGroupAndPredictionPair<Integer>> usedPairings = new TreeSet<OldGroupAndPredictionPair<Integer>>(); //Tracks when an old group has been associated with a prediction 	
	Collection<Set<Integer>> usedOldGroups = new HashSet<Set<Integer>>();			//Tracks which of the modeled old groups have already been morphed
	Collection<Set<Integer>> usedPredictedGroups = new HashSet<Set<Integer>>();		//Tracks which of the predicted groups have been used to morph old groups
	Collection<Set<Integer>> usedIdealGroups = new HashSet<Set<Integer>>();			//Tracks which of the ideal groups have been reached through morphings

	Collection<Set<Integer>> oldGroups = new HashSet<Set<Integer>>();
	Map<Set<Integer>, ArrayList<Set<Integer>>> oldToIdealGroupsMap = new HashMap<Set<Integer>, ArrayList<Set<Integer>>>(); //What ideal groups old groups can morph into
	
	Collection<Set<Integer>> originalIdeals = new HashSet<Set<Integer>>();  //List of ideal groups before modeling starts.  Used to avoid contamination of the ideal groups
	
	double maxPredictionThreshold = -1;
	
	int[] manualCosts = null; //Holds counts for: {adds, deletes, splits}
	
	int totalOldGroups = 0;
	
	int automaticExpansions = 0;
	int algorithmRequiredAddCount = 0;
	int algorithmRequiredDeleteCount = 0;
	int algorithmRequiredSelectCount = 0;
	int algorithmRequiredAutomatedAdds = 0;
	
	
	static{
		
		//SELECT: initial source of predicted groups
		selectHybrid();
		
		//SELECT: algorithm to use for generating prediction lists
		selectExpectedScaling();
		//selectJaccardCoefficient();
		
		//SELECT: how predictions are choosen
		//selectSinglePredictionSingleIdealChoosing();
		selectSinglePredictionMultiIdealChoosing();
		//selectMultiPredictionSingleIdealChoosing();
		//selectMultiPredictionMultiIdealChoosing();
	}
	
	public static void selectHybrid(){
		PredictionLoaderSelector.setFactory(new HybridPredictionLoaderFactory());
		FileFinder.selectHybrid();
	}
	
	public static void selectExpectedScaling(){
		PredictionListSelector.setFactory(new ExpectedScalingPredictionListMakerFactory<Integer>());
		threshold_increment = 1.0;
		FileFinder.selectExpectedScaling();
	}
	
	public static void selectJaccardCoefficient(){
		PredictionListSelector.setFactory(new JaccardCoefficientPredictionListMakerFactory<Integer>());
		threshold_increment = 0.01;
		FileFinder.selectJaccardCoefficient();
	}
	
	public static void selectSinglePredictionSingleIdealChoosing(){
		PredictionChooserSelector.setFactory(new SinglePredictionSingleIdealPredictionChooserFactory<Integer>());
		RecommendationChooserSelector.setFactory(new SingleRecommenderEngineResultRecommendationChooserFactory<Integer>());
		FileFinder.selectSinglePredictionSingleIdealChoosing();
	}
	
	public static void selectSinglePredictionMultiIdealChoosing(){
		PredictionChooserSelector.setFactory(new SinglePredictionMultiIdealPredictionChooserFactory<Integer>());
		RecommendationChooserSelector.setFactory(new SingleRecommenderEngineResultRecommendationChooserFactory<Integer>());
		FileFinder.selectSinglePredictionMultiIdealChoosing();
	}
	
	public static void selectMultiPredictionSingleIdealChoosing(){
		PredictionChooserSelector.setFactory(new MultiPredictionSingleIdealPredictionChooserFactory<Integer>());
		RecommendationChooserSelector.setFactory(new MultiRecommenderEngineResultRecommendationChooserFactory<Integer>());
		FileFinder.selectMultiPredictionSingleIdealChoosing();
	}
	
	public static void selectMultiPredictionMultiIdealChoosing(){
		PredictionChooserSelector.setFactory(new MultiPredictionMultiIdealPredictionChooserFactory<Integer>());
		RecommendationChooserSelector.setFactory(new MultiRecommenderEngineResultRecommendationChooserFactory<Integer>());
		FileFinder.selectMultiPredictionMultiIdealChoosing();
	}
	
	
	public static void runRelativeScaledGroupMorphingOnAllParticipants() throws IOException{
		
		Collection<EvolutionStats> allStats = new ArrayList<EvolutionStats>();
		
		File tupleFile = FileFinder.getTupleFile();
		if(tupleFile.exists()){
			tupleFile.delete();
		}

		newGroupsHeader = "participants,user effort rate,error,percentage,starting groups count,used groups count,unused groups count";
		
		System.out.println("participant,percent new,num new,expected final morphings,old groups,manual adds,num expansions,adds required, deletes required,automatic adds,ideal morphings not reached,sum size of missed ideals");
				
		int[] participants = TestingConstants.PARTICIPANTS;
		for(int i=0; i < participants.length; i++){
			int participant = participants[i];
			
					
			File newGroupsFile = FileFinder.getEvolutionNewGroupsFile();
			
			GroupMorphingModeler modeler = new GroupMorphingModeler();
			Collection<EvolutionStats> collectedStats = modeler.runRelativeScaledGroupMorphingForParticipant(participant, newGroupsFile, i==0);
			allStats.addAll(collectedStats);
		}

		System.out.print("Writing effort values...");
		File effortFile = FileFinder.getEvolutionEffortFile();		
		if(allStats.size() > 0){
			BufferedWriter out = new BufferedWriter(new FileWriter(effortFile));
			
			boolean wroteHeader = false;
			for(EvolutionStats stats: allStats){
				if(!wroteHeader){
					out.write(stats.getHeader());
					out.newLine();
					wroteHeader = true;
				}
				out.write(stats.toString());
				out.newLine();
			}
			out.close();
		}
		System.out.println("done.");
		
		//writeMultipleMergeCounts(FileFinder.getMultipleMatchesFileName());
	}
	
	static void writeMultipleMergeCounts(String dest) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		
		out.write("Old groups with multiple matches");
		out.newLine();
		out.write("Percent new, Multiple Matches");
		out.newLine();
		for(Double percentage: multipleMatchCounts.keySet()){
			out.write(""+percentage+','+multipleMatchCounts.get(percentage));
			out.newLine();
		}
		out.newLine();
		out.newLine();
		
		out.write("Old groups with multiple matches that were covered");
		out.newLine();
		out.write("Percent new, Multiple Matches");
		out.newLine();
		for(Double percentage: coveredMultipleMatchCounts.keySet()){
			out.write(""+percentage+','+coveredMultipleMatchCounts.get(percentage));
			out.newLine();
		}
		out.flush();
		out.close();
	}
	
	public Collection<EvolutionStats> runRelativeScaledGroupMorphingForParticipant(int participant,  File newGroupsFile, boolean writeHeader) throws IOException{
		System.out.println("******************RELATIVE SCALED GROUP MORPHING FOR PARTICIPANT "+participant+"******************");
		
		Collection<EvolutionStats> collectedStats = new ArrayList<EvolutionStats>();
		
		newGroupsStatsFile = newGroupsFile;
		
		ioHelp = new IOFunctions<Integer>(Integer.class);
		String idNameMap = FileFinder.getFriendNameAndIdFileName(participant);
		ioHelp.fillNamesAndIDs(idNameMap);
		
		String idealFile = FileFinder.getIdealFile(participant);		
		Collection<Set<Integer>> ideals = ioHelp.loadIdealGroups(idealFile);
		
		Collection<Set<Integer>> predictedGroups = PredictionLoaderSelector.loadPredictions(participant);
		
		Set<Integer> oldAndNewIndividuals = getAllIndividuals(ideals);
		MembershipChangeFinder<Integer> membershipChangeFinder = new MembershipChangeFinder<Integer>();
		
		double[] percentages = TestingConstants.GRAPH_GROWTH_PROPORTIONS;
		DecimalFormat format = new DecimalFormat("00");
		
		for(int percentagePos = 0; percentagePos < percentages.length; percentagePos++){
			
			double percentage = percentages[percentagePos];
			
			Set<Integer> newIndividuals = membershipChangeFinder.getPseudoRandomNewIndividuals(oldAndNewIndividuals, percentage);
			
			System.out.print("Printing intended evolution data...");
			Map<Set<Integer>, ArrayList<Set<Integer>>> oldToIdeals = populateOldToIdealGroupsMap(ideals, newIndividuals);
			Collection<Set<Integer>> oldGroups = oldToIdeals.keySet();
			writeIntendedEvolutionData(participant, oldGroups, ideals, oldToIdeals, predictedGroups, newIndividuals, percentage);
			System.out.println("done.");
			
			System.out.print("Modeling with "+format.format(percentage*100)+"% new members...");

			EvolutionStats stats = runMyAlgorithm(participant, oldGroups, ideals, oldToIdeals, predictedGroups, newIndividuals, percentage);
			collectedStats.add(stats);

			System.out.println();
		}
		
		
		
		System.out.println("\n");
		
		return collectedStats;
	}
	
	protected void writeIntendedEvolutionData(int participant, Collection<Set<Integer>> oldGroups, Collection<Set<Integer>> ideals, 
			Map<Set<Integer>, ArrayList<Set<Integer>>> oldToIdealGroups,
			Collection<Set<Integer>> predictedGroups, Set<Integer> newIndividuals, double percentNew) throws IOException{
		
		ioHelp = new IOFunctions<Integer>(Integer.class);
		
		String idNameMap = FileFinder.getFriendNameAndIdFileName(participant);
		ioHelp.fillNamesAndIDs(idNameMap);
		
		String oldGroupFileName = FileFinder.getOldGroupFileName(participant, newIndividuals.size());
		ioHelp.printCliqueNamesToFile(oldGroupFileName, oldGroups);
		
		String newMembersFileName = FileFinder.getNewMembersFileName(participant, percentNew);
		ArrayList<Set<Integer>> newMembersCollection = new ArrayList<Set<Integer>>();
		newMembersCollection.add(newIndividuals);
		ioHelp.printCliqueNamesToFile(newMembersFileName, newMembersCollection);

		String idealFile = FileFinder.getIdealFile(participant);
		Map<Set<Integer>, String> oldGroupNames = ioHelp.loadGroupNames("old group-", oldGroupFileName);
		Map<Set<Integer>, String> idealNames = ioHelp.loadIdealGroupNames(idealFile);
		
		File oldToIdealFile = FileFinder.getOldToIdealFile(participant, newIndividuals.size());
		if(!oldToIdealFile.getParentFile().exists()){
			oldToIdealFile.getParentFile().mkdirs();
		}
		BufferedWriter out = new BufferedWriter(new FileWriter(oldToIdealFile));
		for(Set<Integer> oldGroup: oldToIdealGroups.keySet()){
			String oldGroupName = oldGroupNames.get(oldGroup);
			
			out.write(oldGroupName+"-> [");
			
			ArrayList<Set<Integer>> idealMappings = oldToIdealGroups.get(oldGroup);
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
		out.close();
	}
	
	public EvolutionStats computeStats(int participant, Set<Integer> newIndividuals, double percentNew, 
			Collection<Set<Integer>> ideals, Collection<RecommendedEvolution<Integer>> recommendations){
		
			EvolutionStatsBuilder<Integer> builder = new EvolutionStatsBuilder<Integer>();
			EvolutionStats stats = new EvolutionStats(participant, percentNew, newIndividuals.size());
			
			System.out.print("\tComputing manual costs...");
			Map<Set<Integer>, ArrayList<Set<Integer>>> oldToIdealGroupsMap = builder.ComputeManualStats(ideals, newIndividuals, stats);
			System.out.println("done.");
			
			System.out.print("\tComputing recommendation costs...");
			builder.computeEvolutionStats(newIndividuals, ideals, recommendations, oldToIdealGroupsMap, stats);
			System.out.println("done.");
			
			return stats;
			
	}	
	
	public EvolutionStats runMyAlgorithm(int participant, Collection<Set<Integer>> oldGroups, Collection<Set<Integer>> ideals, 
			Map<Set<Integer>, ArrayList<Set<Integer>>> oldToIdeals,
			Collection<Set<Integer>> predictedGroups, Set<Integer> newIndividuals, double percentNew) throws IOException{
		
		mergedMappings.clear();
				
		//String prefix = ""+participant+","+percentNew+","+newIndividuals.size()+","+ideals.size()+","+oldToIdealGroupsMap.keySet().size()+",";
		//String newGroupData = prefix+","+predictedGroups.size();
		
		int totalOldGroups = oldGroups.size();
		
		System.out.print("\t"+totalOldGroups+" old,"+predictedGroups.size()+" predicted,"+ideals.size()+" ideal");
		
		Collection<RecommendedEvolution<Integer>> recommendations = EvolutionRecommendationSelector.selectRecommendationsAcrossAllThresholds(percentNew, newIndividuals, oldGroups, predictedGroups);
		
		
		System.out.println("Computing stats...");
		EvolutionStats stats = computeStats(participant, newIndividuals, percentNew, ideals, recommendations);
		System.out.println("done.");
		
		return stats;
	}
	
	/*
	 * Actions: populates ultimately what old groups can morph to
	 * Returns: a map of old groups to their intended ideals
	 * 
	 */
	protected Map<Set<Integer>, ArrayList<Set<Integer>>> populateOldToIdealGroupsMap(Collection<Set<Integer>> ideals, Set<Integer> newIndividuals){
		Map<Set<Integer>, ArrayList<Set<Integer>>> oldToIdealGroups = new HashMap<Set<Integer>, ArrayList<Set<Integer>>>();
		
		MembershipChangeFinder<Integer> membershipChangeFinder = new MembershipChangeFinder<Integer>();
		
		for(Set<Integer> ideal:ideals){ 
			
			Set<Integer> oldGroup = membershipChangeFinder.getUnmaintainedGroup(ideal, newIndividuals);
		
			if(oldGroup.size() == 0) continue;
			
			if(!oldToIdealGroups.entrySet().contains(oldGroup)){
				ArrayList<Set<Integer>> idealMappings = new ArrayList<Set<Integer>>();
				idealMappings.add(ideal);
				oldToIdealGroups.put(oldGroup, idealMappings);
			}else{
				ArrayList<Set<Integer>> idealMappings = oldToIdealGroups.get(oldGroup);
				idealMappings.add(ideal);
			}
			
		}
		
		return oldToIdealGroups;
	}
	
	public static  Set<Integer> getAllIndividuals(Collection<Set<Integer>> groups){
		Set<Integer> individuals = new HashSet<Integer>();
		
		Iterator<Set<Integer>> groupsIter = groups.iterator();
		while(groupsIter.hasNext()){
			Set<Integer> group = groupsIter.next();
			
			individuals.addAll(group);
		}
		
		return individuals;
		
	}
	
	public void writeNewGroupsData(String line) throws IOException{
		BufferedWriter	newGroupsStatsOut = new BufferedWriter(new FileWriter(newGroupsStatsFile, true));
		newGroupsStatsOut.write(line);
		newGroupsStatsOut.newLine();
		newGroupsStatsOut.flush();
		newGroupsStatsOut.close();
	}
	
	public static void main(String[] args) throws IOException{
		
		//runAllMaintenanceMatchingTests();
		//runAllMaintenanceMergingTests();
		
		//runAllMultipleMaintenanceMergingTests();
		
		//runAllMaintenanceMatchingToAdjustedPredictionsTests();
		runRelativeScaledGroupMorphingOnAllParticipants();
		
		
		//GroupMorphingModeler modeler = new GroupMorphingModeler();
		//modeler.modelPredictionListCreationWithDebugging(19, 0.1);
	}
	
	
}
