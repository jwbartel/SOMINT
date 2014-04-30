package groups.evolution.snap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import bus.tools.FileFinder;
import bus.tools.TestingConstants;
import groups.evolution.GroupMorphingModeler;
import groups.evolution.MembershipChangeFinder;
import groups.evolution.analysis.EvolutionStats;
import groups.evolution.predictions.loading.PredictionLoaderSelector;
import groups.evolution.predictions.loading.SnapHybridPredictionLoaderFactory;
import groups.seedless.kelli.IOFunctions;

public class SnapGroupMorphingModeler extends GroupMorphingModeler {
	
	
	public Collection<EvolutionStats> runRelativeScaledGroupMorphingForParticipant(int participant,  File newGroupsFile, boolean writeHeader) throws IOException{
		System.out.println("******************RELATIVE SCALED GROUP MORPHING FOR PARTICIPANT "+participant+"******************");
		
		Collection<EvolutionStats> collectedStats = new ArrayList<EvolutionStats>();
		
		newGroupsStatsFile = newGroupsFile;
		
		ioHelp = new SnapIOFunctions<Integer>(Integer.class);
		
		String idealFile = FileFinder.getSnapIdealFile(participant);		
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
		
		ioHelp = new SnapIOFunctions<Integer>(Integer.class);
		
				
		String oldGroupFileName = FileFinder.getSnapOldGroupFileName(participant, newIndividuals.size());
		ioHelp.printCliqueNamesToFile(oldGroupFileName, oldGroups);
		
		String newMembersFileName = FileFinder.getSnapNewMembersFileName(participant, percentNew);
		ArrayList<Set<Integer>> newMembersCollection = new ArrayList<Set<Integer>>();
		newMembersCollection.add(newIndividuals);
		ioHelp.printCliqueNamesToFile(newMembersFileName, newMembersCollection);

		String idealFile = FileFinder.getSnapIdealFile(participant);
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
	
	public static void runRelativeScaledGroupMorphingOnAllParticipants() throws IOException{
		
		Collection<EvolutionStats> allStats = new ArrayList<EvolutionStats>();
		
		File tupleFile = FileFinder.getSnapTupleFile();
		if(tupleFile.exists()){
			tupleFile.delete();
		}

		newGroupsHeader = "participants,user effort rate,error,percentage,starting groups count,used groups count,unused groups count";
		
		System.out.println("participant,percent new,num new,expected final morphings,old groups,manual adds,num expansions,adds required, deletes required,automatic adds,ideal morphings not reached,sum size of missed ideals");
				
		int[] participants = SnapTestingConstants.FACEBOOK_PARTICIPANTS;
		for(int i=0; i < participants.length; i++){
			int participant = participants[i];
			
					
			File newGroupsFile = FileFinder.getSnapEvolutionNewGroupsFile();
			
			GroupMorphingModeler modeler = new SnapGroupMorphingModeler();
			Collection<EvolutionStats> collectedStats = modeler.runRelativeScaledGroupMorphingForParticipant(participant, newGroupsFile, i==0);
			allStats.addAll(collectedStats);
		}

		System.out.print("Writing effort values...");
		File effortFile = FileFinder.getSnapEvolutionEffortFile();		
		if(allStats.size() > 0){
			if (!effortFile.getParentFile().exists()) {
				effortFile.getParentFile().mkdirs();
			}
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
	
	public static void main(String[] args) throws IOException{
		PredictionLoaderSelector.setFactory(new SnapHybridPredictionLoaderFactory());
		//TestingConstants.setThresholdIncrement(0.25);
		
		//runAllMaintenanceMatchingTests();
		//runAllMaintenanceMergingTests();
		
		//runAllMultipleMaintenanceMergingTests();
		
		//runAllMaintenanceMatchingToAdjustedPredictionsTests();
		runRelativeScaledGroupMorphingOnAllParticipants();
		
		
		//GroupMorphingModeler modeler = new GroupMorphingModeler();
		//modeler.modelPredictionListCreationWithDebugging(19, 0.1);
	}
}
