package recommendation.groups.old.evolution.synthetic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import recommendation.groups.old.evolution.MembershipChangeFinder;
import recommendation.groups.seedless.hybrid.IOFunctions;
import bus.tools.FileFinder;
import bus.tools.TestingConstants;

public class SyntheticEvolutionDataGenerator {
	

	
	public static  Set<Integer> getAllIndividuals(Collection<Set<Integer>> groups){
		Set<Integer> individuals = new HashSet<Integer>();
		
		Iterator<Set<Integer>> groupsIter = groups.iterator();
		while(groupsIter.hasNext()){
			Set<Integer> group = groupsIter.next();
			
			individuals.addAll(group);
		}
		
		return individuals;
		
	}
	
	public static void saveNewMembershipForParticipant(int participant){
		IOFunctions<Integer> ioHelp = new IOFunctions<Integer>(Integer.class);
		String idNameMap = "data/Kelli/FriendshipData/2010Study/"+participant+"_People.txt";
		ioHelp.fillNamesAndIDs(idNameMap);
		
		String idealFile = "data/Jacob/Ideal/"+participant+"_ideal.txt";		
		Collection<Set<Integer>> ideals = ioHelp.loadIdealGroups(idealFile);
		
		Set<Integer> oldAndNewIndividuals = getAllIndividuals(ideals);
		MembershipChangeFinder<Integer> membershipChangeFinder = new MembershipChangeFinder<Integer>();
		
		double[] proportions = TestingConstants.GRAPH_GROWTH_PROPORTIONS;
		int numTests = TestingConstants.SYNTHETIC_TESTS_PER_PARTICIPANT;
		
		for (double percentNew: proportions){
			for(int test=0; test<numTests; test++){
				
				File outFile = FileFinder.getNewMembershipFile(participant, percentNew, test);
				File parent = outFile.getParentFile();
				if(!parent.exists()){
					parent.mkdirs();
				}
				membershipChangeFinder.saveUnmaintainedIndividuals(oldAndNewIndividuals, percentNew, outFile);
			}
		}
		
	}
	
	public static void saveAllNewMembership(){
		int[] participants = TestingConstants.PARTICIPANTS;
		for(int participant: participants){
			System.out.println("participant "+participant);
			saveNewMembershipForParticipant(participant);
		}
	}
	
	public static Set<Integer> loadNewMembership(int participant, double percentNew, int testNum){
		Set<Integer> newMembership = new TreeSet<Integer>();
		
		File newMembershipFile = FileFinder.getNewMembershipFile(participant, percentNew, testNum);
		try{
			BufferedReader in = new BufferedReader(new FileReader(newMembershipFile));
			String line = in.readLine();
			while (line != null){
				newMembership.add(Integer.parseInt(line));
				line = in.readLine();
			}
			in.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		return newMembership;
	}
	
	public static void generateAndSaveOldGroupsForTest(int participant, double percentNew, int testNum){
		
		IOFunctions<Integer> ioHelp = new IOFunctions<Integer>(Integer.class);
		
		//Load the id to name map
		String idNameMap = FileFinder.getFriendNameAndIdFileName(participant); //"data/Kelli/FriendshipData/2010Study/"+participant+"_People.txt";
		ioHelp.fillNamesAndIDs(idNameMap);
		
		//Get the list of ideals
		String idealFile = FileFinder.getIdealFile(participant);		
		Map<Set<Integer>, String> idealNames = ioHelp.loadIdealGroupNames(idealFile);
		Collection<Set<Integer>> ideals = idealNames.keySet();
		
		//Get the new individuals for this test
		Set<Integer> newIndividuals = loadNewMembership(participant, percentNew, testNum);
		

		MembershipChangeFinder<Integer> membershipChangeFinder = new MembershipChangeFinder<Integer>();
		
		Collection<Set<Integer>> oldGroups = new HashSet<Set<Integer>>();
		Map<Set<Integer>, ArrayList<String>> oldToIdealGroupsMap = new HashMap<Set<Integer>, ArrayList<String>>();
		
		for(Set<Integer> ideal:ideals){ 
			
			//For each f', find f' s.t. f = f' - new(f')
			Set<Integer> oldGroup = membershipChangeFinder.getUnmaintainedGroup(ideal, newIndividuals);
		
			//Ignore cases where the new group is not an expansion, but a new group
			if(oldGroup.size() == 0) continue;
			
			//Do not allow two copies of any f'
			if(!oldGroups.contains(oldGroup)){
				oldGroups.add(oldGroup);
				ArrayList<String> idealMappings = new ArrayList<String>();
				idealMappings.add(idealNames.get(ideal));
				oldToIdealGroupsMap.put(oldGroup, idealMappings);
			}else{
				ArrayList<String> idealMappings = oldToIdealGroupsMap.get(oldGroup);
				idealMappings.add(idealNames.get(ideal));
			}
			
		}
		
		File oldGroupToIdealFile = FileFinder.getOldGroupFile(participant, percentNew, testNum);
		File parent = oldGroupToIdealFile.getParentFile();
		if(!parent.exists()){
			parent.mkdirs();
		}
		ioHelp.printCliqueMappedToIdealsToFile(oldGroupToIdealFile.getAbsolutePath(), oldToIdealGroupsMap);
	}
	
	public static void generateAndSaveAllOldGroups(){
		int[] participants = TestingConstants.PARTICIPANTS;
		for(int participant: participants){
			System.out.println("participant "+participant);
			double[] proportions = TestingConstants.GRAPH_GROWTH_PROPORTIONS;
			int numTests = TestingConstants.SYNTHETIC_TESTS_PER_PARTICIPANT;
			
			for (double percentNew: proportions){
				for(int test=0; test<numTests; test++){
					generateAndSaveOldGroupsForTest(participant, percentNew, test);
				}
			}
		}
	}
	
	public static void main(String[] args){
		saveAllNewMembership();
		generateAndSaveAllOldGroups();
	}

}
