package groups.evolution.evaluation;

import groups.evolution.composed.listmaker.ExpectedScalingPredictionListMakerFactory;
import groups.evolution.composed.listmaker.JaccardCoefficientPredictionListMakerFactory;
import groups.evolution.composed.listmaker.PredictionListSelector;
import groups.evolution.evaluation.synthetic.MembershipChangeFinder;
import groups.evolution.old.GroupMaintainer;
import groups.evolution.predictions.loading.HybridPredictionLoaderFactory;
import groups.evolution.predictions.loading.PredictionLoaderSelector;
import groups.seedless.kelli.IOFunctions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;

import bus.tools.TestingConstants;


public class MaintenanceModeler {
	
	IOFunctions<Integer> ioHelp;
	
	Map<Set<Integer>, ArrayList<Set<Integer>>> mergedMappings = new HashMap<Set<Integer>, ArrayList<Set<Integer>>>();
	
	File effortStatsFile = null;
	File newGroupsStatsFile = null;
	
	static double threshold_increment = 1.0;
	
	static{
		//Select initial source of predicted groups
		selectHybrid();
		
		//Select which algorithm to use for generating prediction lists
		selectExpectedScaling();
		//selectJaccardCoefficient();
	}
	
	static void selectHybrid(){
		PredictionLoaderSelector.setFactory(new HybridPredictionLoaderFactory());
	}
	
	static void selectExpectedScaling(){
		PredictionListSelector.setFactory(new ExpectedScalingPredictionListMakerFactory<Integer>());
		threshold_increment = 1.0;
	}
	
	static void selectJaccardCoefficient(){
		PredictionListSelector.setFactory(new JaccardCoefficientPredictionListMakerFactory<Integer>());
		threshold_increment = 0.01;
	}
	
	public void writeEffortData(String line) throws IOException{
		BufferedWriter	effortStatsOut = new BufferedWriter(new FileWriter(effortStatsFile, true));
		effortStatsOut.write(line);
		effortStatsOut.newLine();
		effortStatsOut.flush();
		effortStatsOut.close();
	}
	
	public void writeNewGroupsData(String line) throws IOException{
		BufferedWriter	newGroupsStatsOut = new BufferedWriter(new FileWriter(newGroupsStatsFile, true));
		newGroupsStatsOut.write(line);
		newGroupsStatsOut.newLine();
		newGroupsStatsOut.flush();
		newGroupsStatsOut.close();
	}
	
	static String effortHeader = null;
	static String newGroupsHeader = null;
	
	/*public static void runAllRelativeScaledGroupMorphing() throws IOException{
		
		effortHeader = "participants,user effort rate,error,percentage,intended size,manual effort,list size,depth of least effort,least effort";
		newGroupsHeader = "participants,user effort rate,error,percentage,starting groups count,used groups count,unused groups count";
		

		System.out.println("participant,percent new,num new,expected final morphings,old groups,manual adds,num expansions,adds required, deletes required,automatic adds,ideal morphings not reached");
				
		int[] participants = {10,12, 13, 16, 17, 19, 21, 22, 23, 24, 25};
		for(int i=0; i < participants.length; i++){
			int participant = participants[i];
			
					
			File effortFile = new File("data/Jacob/Stats/relativeScaledMaintenance/adjustedMatching/data/efforts.csv");
			File newGroupsFile = new File("data/Jacob/Stats/relativeScaledMaintenance/adjustedMatching/data/new groups.csv");
				
			MaintenanceModeler modeler = new MaintenanceModeler();
			modeler.runRelativeScaledGroupMorphing(participant, effortFile, newGroupsFile, true);
		}
		
	}
	
	public void runRelativeScaledGroupMorphing(int participant, File effortFile, File newGroupsFile, boolean writeHeader) throws IOException{
		
		
		effortStatsFile = effortFile;
		newGroupsStatsFile = newGroupsFile;
		
		if(writeHeader){
			
			
			if(!effortFile.getParentFile().exists()){
				effortFile.getParentFile().mkdirs();
			}
			
			if(effortFile.exists()){
				effortFile.delete();
			}
			if(newGroupsFile.exists()){
				newGroupsStatsFile.delete();
			}
			
			writeEffortData(effortHeader);
			writeNewGroupsData(newGroupsHeader);
		}
		
		
		
		ioHelp = new IOFunctions<Integer>(Integer.class);
		String idNameMap = "data/Kelli/FriendshipData/2010Study/"+participant+"_People.txt";
		ioHelp.fillNamesAndIDs(idNameMap);
		
		String idealFile = "data/Jacob/Ideal/"+participant+"_ideal.txt";		
		Collection<Set<Integer>> ideals = ioHelp.loadIdealGroups(idealFile);
		
		Collection<Set<Integer>> predictedGroups = PredictionLoaderSelector.loadPredictions(participant);
		
		Set<Integer> oldAndNewIndividuals = getAllIndividuals(ideals);
		MembershipChangeFinder<Integer> maintenanceFinder = new MembershipChangeFinder<Integer>();
		
		int maxPredictionSize = -1;
		for(Set<Integer> predictedGroup : predictedGroups){
			if(predictedGroup.size() > maxPredictionSize){
				maxPredictionSize = predictedGroup.size();
			}
		}
		
		double[] percentages = {0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.10};//, 0.20, 0.30, 0.40, 0.50, 0.6, 0.7, 0.8, 0.9};

		for(int percentagesPos=0; percentagesPos<percentages.length; percentagesPos++){
			mergedMappings.clear();
			
			double percentage = percentages[percentagesPos];
			int manualAddCost = 0;
			int manualDeleteCost = 0;
			int manualSplitCost = 0;

			Collection<Set<Integer>> usedOldGroups = new HashSet<Set<Integer>>();
			Collection<Set<Integer>> usedPredictedGroups = new HashSet<Set<Integer>>();
			Collection<Set<Integer>> usedIdealGroups = new HashSet<Set<Integer>>();
			
			Collection<Set<Integer>> oldGroups = new HashSet<Set<Integer>>();
			Map<Set<Integer>, ArrayList<Set<Integer>>> oldToIdealGroupsMap = new HashMap<Set<Integer>, ArrayList<Set<Integer>>>();
			
			
						
			// START OF MY ALGORITHM!!!!
			
			//Step 1: Pseudo-randomly select p*|F'| individuals from F'
			Set<Integer> newIndividuals = maintenanceFinder.getPseudoRandomNewIndividuals(oldAndNewIndividuals, percentage);
			
			
			Collection<Set<Integer>> originalIdeals = new HashSet<Set<Integer>>(ideals);
			
			for(Set<Integer> ideal:ideals){ 
				
				//Step 2a: for each f', find f' s.t. f = f' - new(f')
				Set<Integer> oldGroup = maintenanceFinder.getUnmaintainedGroup(ideal, newIndividuals);
			
				//Step 2c: Ignore cases where the new group is not an expansion, but a new group
				if(oldGroup.size() == 0) continue;
				
				manualAddCost += (ideal.size() - oldGroup.size());
				
				//Step 2b: If there are two copies of any f, remove the duplicate
				if(!oldGroups.contains(oldGroup)){
					oldGroups.add(oldGroup);
					ArrayList<Set<Integer>> idealMappings = new ArrayList<Set<Integer>>();
					idealMappings.add(ideal);
					oldToIdealGroupsMap.put(oldGroup, idealMappings);
				}else{
					ArrayList<Set<Integer>> idealMappings = oldToIdealGroupsMap.get(oldGroup);
					idealMappings.add(ideal);
					manualSplitCost++;
				}
				
			}
			
			String prefix = ""+participant+","+percentage+","+newIndividuals.size()+","+originalIdeals.size()+","+oldToIdealGroupsMap.keySet().size()+",";
			String newGroupData = prefix+","+predictedGroups.size();
			
			int totalOldGroups = oldGroups.size();
			
			int automaticExpansions = 0;
			int addCount = 0;
			int deleteCount = 0;
			int selectCount = 0;
			int automatedAdds = 0;
			
			double threshold = 0.0;
			int rounds = 0;
			while(true){

				if(usedIdealGroups.size() == originalIdeals.size()) break;
				//if(usedPredictedGroups.size() == predictedGroups.size() || threshold > maxPredictionSize) break;
				
				
				Set<GroupPredictionList<Integer>> predictionLists = new TreeSet<GroupPredictionList<Integer>>();
				
				GroupMaintainer<Integer> maintainer = new GroupMaintainer<Integer>();
				
				//Step 3 find all recommendation lists
				for(Set<Integer> oldGroup : oldToIdealGroupsMap.keySet()){ 
					
					if(usedOldGroups.contains(oldGroup)) continue;
					GroupPredictionList<Integer> predictionList = PredictionListSelector.getPredictionList(oldGroup, predictedGroups, usedPredictedGroups, newIndividuals, percentage, threshold);
					
					if(predictionList.size() > 0){
						predictionLists.add(predictionList);
					}
				}
				
				int[] stats = maintainer.selectFromPredictionList(totalOldGroups, predictionLists, newIndividuals, ideals, oldToIdealGroupsMap, usedIdealGroups, usedOldGroups, usedPredictedGroups);
				automaticExpansions += stats[0];
				addCount += stats[1];
				deleteCount += stats[2];
				selectCount += stats[3];
				automatedAdds += stats[4];
				
				boolean stopMorphing = stats[5] == 1;
				if(stopMorphing) break;
				
				rounds++;
				threshold += threshold_increment;
			}
			
			Collection<Set<Integer>> unreachedIdeals = new HashSet<Set<Integer>>(originalIdeals);
			unreachedIdeals.removeAll(usedIdealGroups);
			
			Collection<Set<Integer>> unusedPredictions = new HashSet<Set<Integer>>(predictedGroups);
			unusedPredictions.removeAll(usedPredictedGroups);
			
			
			
			int unpredictedIdeals = unreachedIdeals.size();
			
			System.out.println(prefix+manualAddCost+","+automaticExpansions+","+addCount+","+deleteCount+","+automatedAdds+","+unpredictedIdeals);
		}
		
	}*/
	
	public static void runAllMaintenanceMatchingToAdjustedPredictionsTests() throws IOException{
		
		double[] deletionRates = {0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.10, 0.20, 0.30, 0.40, 0.50, 0.60, 0.70, 0.80, 0.90};
		double[] errors = {0.0, 0.05, 0.10, 0.15, 0.20, 0.25, 0.30, 0.35, 0.40, 0.45, 0.50, 0.55, 0.60, 0.65, 0.70, 0.75, 0.80, 0.85, 0.90, 0.95, 1.00};;
		
		
		effortHeader = "participants,user effort rate,error,percentage,intended size,manual effort,list size,depth of least effort,least effort";
		newGroupsHeader = "participants,user effort rate,error,percentage,starting groups count,used groups count,unused groups count";
		
		
		/*for(int deletionPos=0; deletionPos < deletionRates.length; deletionPos++){
			double deletionRate = deletionRates[deletionPos];
			
			for(int errorPos=0; errorPos < errors.length; errorPos++){
				double error = errors[errorPos];
				
				File effortFile = new File("data/Jacob/Stats/maintenance/adjustedMatching/data/efforts "+deletionRate+"deletion "+error+"error.csv");
				System.out.println(effortFile.getAbsolutePath());
				File newGroupsFile = new File("data/Jacob/Stats/maintenance/adjustedMatching/data/new groups "+deletionRate+"deletion "+error+"error.csv");
				
				if(!effortFile.getParentFile().exists()){
					effortFile.getParentFile().mkdirs();
				}

				BufferedWriter out = new BufferedWriter(new FileWriter(effortFile));
				out.write(effortHeader);
				out.newLine();
				out.flush();
				out.close();
				
				out = new BufferedWriter(new FileWriter(newGroupsFile));
				out.write(newGroupsHeader);
				out.newLine();
				out.flush();
				out.close();
			}
		}*/
		
		
		int[] participants = {10,12,13,16, 17, 19, 21, 22, 23, 24, 25};
		for(int i=0; i < participants.length; i++){
			int participant = participants[i];
			
			for(int deletionPos=0; deletionPos < deletionRates.length; deletionPos++){
				double deletionRate = deletionRates[deletionPos];
				
				for(int errorPos=0; errorPos < errors.length; errorPos++){
					double error = errors[errorPos];
					
					boolean needsHeader = (i==0);
					
					File effortFile = new File("data/Jacob/Stats/maintenance/adjustedMatching/data/efforts "+deletionRate+"deletion "+error+"effort .csv");
					File newGroupsFile = new File("data/Jacob/Stats/maintenance/adjustedMatching/data/new groups "+deletionRate+"deletion "+error+"effort .csv");
					
					MaintenanceModeler modeler = new MaintenanceModeler();
					modeler.runMaintenanceMatchingToAdjustedPredictionsTests(participant, deletionRate, error, effortFile, newGroupsFile, needsHeader);
				}
				
			}
			
		}
		
	}
	
	public void runMaintenanceMatchingToAdjustedPredictionsTests(int participant, double deletionRate, double error, File effortFile, File newGroupsFile, boolean writeHeader) throws IOException{
		
		System.out.println(""+participant+","+deletionRate+","+error);
		
		effortStatsFile = effortFile;
		newGroupsStatsFile = newGroupsFile;
		
		if(writeHeader){
			
			
			if(!effortFile.getParentFile().exists()){
				effortFile.getParentFile().mkdirs();
			}
			
			if(effortFile.exists()){
				effortFile.delete();
			}
			if(newGroupsFile.exists()){
				newGroupsStatsFile.delete();
			}
			
			writeEffortData(effortHeader);
			writeNewGroupsData(newGroupsHeader);
		}
		
		
		
		ioHelp = new IOFunctions<Integer>(Integer.class);
		String idNameMap = "data/Kelli/FriendshipData/2010Study/"+participant+"_People.txt";
		ioHelp.fillNamesAndIDs(idNameMap);
		
		String idealFile = "data/Jacob/Ideal/"+participant+"_ideal.txt";		
		Collection<Set<Integer>> ideals = ioHelp.loadIdealGroups(idealFile);
	
		String subcliquesFile = "data/Jacob/Hybrid/"+participant+"_Subcliques.txt";
		Collection<Set<Integer>> subcliques = ioHelp.loadGroups(subcliquesFile);
		
		String networksFile = "data/Jacob/Hybrid/"+participant+"_LargeGroups.txt";
		Collection<Set<Integer>> networks = ioHelp.loadGroups(networksFile);
		
		Collection<Set<Integer>> groups = new HashSet<Set<Integer>>(subcliques);
		groups.addAll(networks);
		
		Set<Integer> individuals = getAllIndividuals(ideals);
		MembershipChangeFinder<Integer> maintenanceFinder = new MembershipChangeFinder<Integer>();
		
		double[] percentages = {0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.10, 0.20, 0.30, 0.40, 0.50, 0.6, 0.7, 0.8, 0.9};		
		for(int percentagesPos=0; percentagesPos<percentages.length; percentagesPos++){
			mergedMappings.clear();
			
			double percentage = percentages[percentagesPos]; 

			
			/*if(percentage == 0.9){
				System.out.println("reached");
			}*/
			
			Set<Integer> newIndividuals = maintenanceFinder.getPseudoRandomNewIndividuals(individuals, percentage);
			
			groups = filterGuessedGroups(groups, newIndividuals);
			Collection<Set<Integer>> mergedGroups = getSingleLevelMerges(groups);
			
			Collection<Set<Integer>> usedGroups = new HashSet<Set<Integer>>();
			
			
			String prefix = ""+participant+","+deletionRate+","+error+","+percentage;
			String newGroupData = prefix+","+mergedGroups.size();
			
			for(Set<Integer> ideal:ideals){ 
				
				Set<Integer> oldGroup = maintenanceFinder.getUnmaintainedGroup(ideal, newIndividuals);
				int manualEffort = ideal.size() - oldGroup.size();
				
				/*if(percentage == 0.9){
					System.out.println("reached");
				}*/
				String effortData = prefix +","+ideal.size()+","+manualEffort ; 
				
				GroupMaintainer<Integer> maintainer = new GroupMaintainer<Integer>();
				String[] stats = maintainer.findMaitenanceByMatchingAdjustedPredictions(percentage, deletionRate, error, oldGroup, newIndividuals, mergedGroups, ideal, mergedMappings, usedGroups);
				for(int i=0; i<stats.length; i++){
					effortData += ","+stats[i];
				}
				
				writeEffortData(effortData);
				//System.out.println(effortData);
			}
			
			newGroupData += ","+usedGroups.size();
			
			mergedGroups.removeAll(usedGroups);
			newGroupData += ","+mergedGroups.size();
			
			writeNewGroupsData(newGroupData);
			
		}
		
	}
	
	public Collection<Set<Integer>> filterGuessedGroups(Collection<Set<Integer>> guessedGroups, Set<Integer> newIndividuals){
		
		Collection<Set<Integer>> filteredGroups = new ArrayList<Set<Integer>>();
		
		for(Set<Integer> group: guessedGroups){
			
			boolean intersectionExists = false;
			for(Integer individual:group){
				if(newIndividuals.contains(individual)){
					intersectionExists = true;
					break;
				}
			}
			
			if(intersectionExists && !filteredGroups.contains(group)){
				filteredGroups.add(group);
			}
		}
		
		return filteredGroups;
	}
	
	
	private Collection<Set<Integer>> getSingleLevelMerges(Collection<Set<Integer>> groups){
		Collection<Set<Integer>> merges = new HashSet<Set<Integer>>();
		
		for(Set<Integer> group1:groups){
			for(Set<Integer> group2:groups){
				if(group1.equals(group2) || group1.containsAll(group2) || group2.containsAll(group1)) continue;
				
				Set<Integer> merge = new TreeSet<Integer>(group1);
				merge.addAll(group2);
				
				ArrayList<Set<Integer>> mappings = mergedMappings.get(merge);
				if(mappings == null){
					mappings = new ArrayList<Set<Integer>>();
					mergedMappings.put(merge, mappings);
				}
				mappings.add(group1);
				mappings.add(group2);
				
				merges.add(merge);
				merges.add(group1);
				merges.add(group2);
			}
		}
		
		return merges;
		
	}
	
	
	public static void runAllMaintenanceMatchingTests(){
		double[][] weights = new double[1][2];
		//weights[0][0] = 0.9; weights[0][1] = 0.35;
		weights[0][0] = 1.0; weights[0][1] = 0.15;
		
		MaintenanceModeler modeler = new MaintenanceModeler();
		

		//Header for matching
		System.out.println("participant,percentage new,ideal group size,manual effort,max s group changes, min d group changes, min euclidean changes,lcma changes,subset ratio changes");
		
		int[] participants = {10,12,13,16, 17, 19, 21, 22, 23, 24, 25};
		for(int i=0; i < participants.length; i++){
			int participant = participants[i];
			
		
			modeler.runMaintenanceMatchingTests(participant, weights);
		}
	}
	
	public void runMaintenanceMatchingTests(int participant, double[][] weights){
		
		ioHelp = new IOFunctions<Integer>(Integer.class);
		String idNameMap = "data/Kelli/FriendshipData/2010Study/"+participant+"_People.txt";
		ioHelp.fillNamesAndIDs(idNameMap);
		
		String idealFile = "data/Jacob/Ideal/"+participant+"_ideal.txt";		
		Collection<Set<Integer>> ideals = ioHelp.loadIdealGroups(idealFile);
	
		String subcliquesFile = "data/Jacob/Hybrid/"+participant+"_Subcliques.txt";
		Collection<Set<Integer>> subcliques = ioHelp.loadGroups(subcliquesFile);
		
		String networksFile = "data/Jacob/Hybrid/"+participant+"_LargeGroups.txt";
		Collection<Set<Integer>> networks = ioHelp.loadGroups(networksFile);
		
		Collection<Set<Integer>> groups = new HashSet<Set<Integer>>(subcliques);
		groups.addAll(networks);
		
		Set<Integer> individuals = getAllIndividuals(ideals);
		MembershipChangeFinder<Integer> maintenanceFinder = new MembershipChangeFinder<Integer>();
		
		
		double[] percentages = TestingConstants.GRAPH_GROWTH_PROPORTIONS;
		for(int percentagesPos=0; percentagesPos<percentages.length; percentagesPos++){
			double percentage = percentages[percentagesPos]; 
			
			Set<Integer> newIndividuals = maintenanceFinder.getPseudoRandomNewIndividuals(individuals, percentage);
			
			GroupMaintainer<Integer> maintainer = new GroupMaintainer<Integer>();
			
			for(int weightsPos = 0; weightsPos < weights.length; weightsPos++){
				double[] weightVals = weights[weightsPos];
				double s_threshold = weightVals[0];
				double d_threshold = weightVals[1];
				

				Iterator<Set<Integer>> idealsIter = ideals.iterator();
				while(idealsIter.hasNext()){
					Set<Integer> ideal = idealsIter.next();
					
					Set<Integer> oldGroup = maintenanceFinder.getUnmaintainedGroup(ideal, newIndividuals);
					String[] stats = maintainer.findMaintenanceByMatching(s_threshold, d_threshold, oldGroup, groups, ideal);
					
					System.out.print(""+participant+","+percentage);
					for(int i=0; i<stats.length; i++){
						System.out.print(","+stats[i]);
					}
					System.out.println();
				}
			
			}
		}
	}
	
	public static void runAllMultipleMaintenanceMergingTests(){
		double[][] weights = new double[1][2];
		weights[0][0] = 0.9; weights[0][1] = 0.35;
		//weights[1][0] = 1.0; weights[1][1] = 0.15;
		
		MaintenanceModeler modeler = new MaintenanceModeler();
		

		//Header for matching
		System.out.println("participant,percentage new,s threshold,d threshold,ideal group size,manual effort,max s group changes, min d group changes, min euclidean changes,lcma changes,subset ratio changes");
		
		int[] participants = {10,12,13,16, 17, 19, 21, 22, 23, 24, 25};
		for(int i=0; i < participants.length; i++){
			int participant = participants[i];
			
		
			modeler.runMultipleMaintenanceMergingTests(participant, weights);
		}
	}
	
	private void runMultipleMaintenanceMergingTests(int participant, double[][] weights){
		ioHelp = new IOFunctions<Integer>(Integer.class);
		String idNameMap = "data/Kelli/FriendshipData/2010Study/"+participant+"_People.txt";
		ioHelp.fillNamesAndIDs(idNameMap);
				
		String idealFile = "data/Jacob/Ideal/"+participant+"_ideal.txt";		
		Collection<Set<Integer>> ideals = ioHelp.loadIdealGroups(idealFile);
		
		String inRelationships = "data/Kelli/FriendshipData/2010Study/"+participant+"_MutualFriends.txt";
		UndirectedGraph<Integer, DefaultEdge> graph = ioHelp.createUIDGraph(inRelationships);		
		
		File maximalCliqueFile = new File("data/Kelli/Cliques/MaximalCliques/"+participant+"_MaximalCliques.txt");
		Collection<Set<Integer>> maximalCliques = getMaximalCliques(graph, maximalCliqueFile);
		
		Set<Integer> individuals = getAllIndividuals(ideals);
		MembershipChangeFinder<Integer> maintenanceFinder = new MembershipChangeFinder<Integer>();
		
		double[] percentages = {0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.10, 0.20, 0.30, 0.40, 0.50, 0.6, 0.7, 0.8, 0.9};
		for(int percentagesPos=0; percentagesPos<percentages.length; percentagesPos++){
			double percentage = percentages[percentagesPos];
			
			
			Set<Integer> newIndividuals = maintenanceFinder.getPseudoRandomNewIndividuals(individuals, percentage);
			
			for(int weightsPos = 0; weightsPos < weights.length; weightsPos++){
				double[] weightVals = weights[weightsPos];
				double s_threshold = weightVals[0];
				double d_threshold = weightVals[1];
				
				for(Set<Integer> ideal: ideals){
					
					Set<Integer> seed = maintenanceFinder.getUnmaintainedGroup(ideal, newIndividuals);
					int manualEffort = ideal.size() - seed.size();
					int idealSize = ideal.size();
					
					boolean printedPrefix = false;
					for(int type=0; type<3; type++){
						
						if(!printedPrefix){
							System.out.print(""+participant+","+percentage+","+weightVals[0]+","+weightVals[1]+","+manualEffort+","+idealSize);
							printedPrefix = true;
						}
						
						
						GroupMaintainer<Integer> maintainer = new GroupMaintainer<Integer>(); 
						String[] results = maintainer.findMultipleMaintenanceByMerging(s_threshold, d_threshold, graph, seed, maximalCliques, ideal, type);
						for(int i=0; i<results.length; i++){
							System.out.print(","+results[i]);
						};
					}
					System.out.println();
					
					
				}
				
			}
			
		}
	}
	
	public static void runAllMaintenanceMergingTests(){
		double[][] weights = new double[2][2];
		weights[0][0] = 0.9; weights[0][1] = 0.35;
		weights[1][0] = 1.0; weights[1][1] = 0.15;
		
		System.out.print("participant,percentage new,s threshold,d threshold,manual effort,ideal size,");
		System.out.print("changes with max s,changes with min d,changes with euclidean,changes with lcma");
		System.out.println();
		
		MaintenanceModeler modeler = new MaintenanceModeler();
		int[] participants = {10, 12, 13,16, 17, 19, 21, 22, 23, 24, 25};
		for(int i=0; i < participants.length; i++){
			int participant = participants[i];
			

			modeler.runMaintenanceMergingTests(participant, weights);
		}
		
	}
	
	private void runMaintenanceMergingTests(int participant, double[][] weights){
		ioHelp = new IOFunctions<Integer>(Integer.class);
		String idNameMap = "data/Kelli/FriendshipData/2010Study/"+participant+"_People.txt";
		ioHelp.fillNamesAndIDs(idNameMap);
		
		String idealFile = "data/Jacob/Ideal/"+participant+"_ideal.txt";		
		Collection<Set<Integer>> ideals = ioHelp.loadIdealGroups(idealFile);
		
		String inRelationships = "data/Kelli/FriendshipData/2010Study/"+participant+"_MutualFriends.txt";
		UndirectedGraph<Integer, DefaultEdge> graph = ioHelp.createUIDGraph(inRelationships);		
		
		File maximalCliqueFile = new File("data/Kelli/Cliques/MaximalCliques/"+participant+"_MaximalCliques.txt");
		Collection<Set<Integer>> maximalCliques = getMaximalCliques(graph, maximalCliqueFile);
		
		Set<Integer> individuals = getAllIndividuals(ideals);
		MembershipChangeFinder<Integer> maintenanceFinder = new MembershipChangeFinder<Integer>();
		
		double[] percentages = {0.01, 0.02, 0.03, 0.04, 0.05, 0.06, 0.07, 0.08, 0.09, 0.10, 0.20, 0.30, 0.40, 0.50, 0.6, 0.7, 0.8, 0.9};
		for(int percentagesPos=0; percentagesPos<percentages.length; percentagesPos++){
			double percentage = percentages[percentagesPos];
			
			
			Set<Integer> newIndividuals = maintenanceFinder.getPseudoRandomNewIndividuals(individuals, percentage);
			
			for(int weightsPos = 0; weightsPos < weights.length; weightsPos++){
				double[] weightVals = weights[weightsPos];
				double s_threshold = weightVals[0];
				double d_threshold = weightVals[1];
				
				for(Set<Integer> ideal: ideals){
					
					Set<Integer> seed = maintenanceFinder.getUnmaintainedGroup(ideal, newIndividuals);
					int manualEffort = ideal.size() - seed.size();
					int idealSize = ideal.size();
					
					boolean printedPrefix = false;
					for(int type=0; type<4; type++){
						
						if(!printedPrefix){
							System.out.print(""+participant+","+percentage+","+weightVals[0]+","+weightVals[1]+","+manualEffort+","+idealSize);
							printedPrefix = true;
						}
						
						
						GroupMaintainer<Integer> maintainer = new GroupMaintainer<Integer>(); 
						String[] results = maintainer.findMaintenaceByMerging(s_threshold, d_threshold, seed, maximalCliques, ideal, type);
						for(int i=0; i<results.length; i++){
							System.out.print(","+results[i]);
						};
					}
					System.out.println();
					
					
				}
				
			}
			
		}
	}
	
	public Collection<Set<Integer>> getMaximalCliques(UndirectedGraph<Integer, DefaultEdge> graph, File maximalCliqueFile){
		
		Collection<Set<Integer>> maximalCliques;
		if(maximalCliqueFile == null || !maximalCliqueFile.exists()){		
			BronKerboschCliqueFinder<Integer, DefaultEdge> BKcliqueFind = new BronKerboschCliqueFinder<Integer, DefaultEdge>(graph);
			maximalCliques = BKcliqueFind.getAllMaximalCliques();
			if(maximalCliqueFile != null){
				ioHelp.printCliqueIDsToFile(maximalCliqueFile.getPath(), maximalCliques);
				System.out.println("Can be found in "+maximalCliqueFile.getPath());
			}
		}else{
			//System.out.println("Loading cliques from file "+maximalCliqueFile.getPath());
			maximalCliques = ioHelp.loadCliqueIDs(maximalCliqueFile.getPath());
		}
		

		//compareCliques();
		//status message
		//status message: all done :)

		return maximalCliques;
	}
	
	public Set<Integer> getAllIndividuals(Collection<Set<Integer>> groups){
		Set<Integer> individuals = new HashSet<Integer>();
		
		Iterator<Set<Integer>> groupsIter = groups.iterator();
		while(groupsIter.hasNext()){
			Set<Integer> group = groupsIter.next();
			
			individuals.addAll(group);
		}
		
		return individuals;
		
	}
	
	public static void main(String[] args) throws IOException{
		
		//runAllMaintenanceMatchingTests();
		runAllMaintenanceMergingTests();
		
		//runAllMultipleMaintenanceMergingTests();
		
		//runAllMaintenanceMatchingToAdjustedPredictionsTests();
		//runAllRelativeScaledGroupMorphing();
	}
}
