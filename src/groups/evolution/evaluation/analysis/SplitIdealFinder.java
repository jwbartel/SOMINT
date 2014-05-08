package groups.evolution.evaluation.analysis;

import groups.evolution.evaluation.GroupMorphingModeler;
import groups.seedless.hybrid.IOFunctions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.swt.widgets.Tree;


public class SplitIdealFinder {

	public void printSplitIdeals(int participant, int newMembers, String type) throws IOException{
		
		String prefix = "data/Jacob/Stats/relativeScaledMaintenance/adjustedMatching/old to ideal/";
		
		File file = new File(prefix+"participant_"+participant+" newIndividuals_"+newMembers+" "+type+".txt");
		if(file.exists()){
			boolean printHeader = true;
			boolean printed = false;
			
			IOFunctions<Integer> ioHelp = new IOFunctions<Integer>(Integer.class);
			String idNameMap = "data/Kelli/FriendshipData/2010Study/"+participant+"_People.txt";
			ioHelp.fillNamesAndIDs(idNameMap);
			
			String idealFile = "data/Jacob/Ideal/"+participant+"_ideal.txt";		
			Collection<Set<Integer>> ideals = ioHelp.loadIdealGroups(idealFile);
			Map<Set<Integer>, String> idealNames = ioHelp.loadIdealGroupNames(idealFile);
			
			Set<Integer> oldAndNewIndividuals = GroupMorphingModeler.getAllIndividuals(ideals);
			
			int oldAndNewMembersCount = oldAndNewIndividuals.size();
			
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line = in.readLine();
			while(line != null){
				
				String finalIdealsStr = line.substring(line.indexOf("[")+1, line.length()-1);
				String[] finalIdeals = finalIdealsStr.split(","); 
				
				int percentNew = (int) Math.round(100.0 * ((double) newMembers )/((double) oldAndNewMembersCount) );
				
				if(finalIdeals.length > 1){
					
					ArrayList<String> finalIdealNames = new ArrayList<String>();
					ArrayList<Set<Integer>> finalIdealSets = new ArrayList<Set<Integer>>();
					
					for(int i=0; i<finalIdeals.length; i++){
						String idealName = finalIdeals[i];
						Set<Integer> idealSet = getIdealGroup(idealName, idealNames);
						finalIdealNames.add(idealName);
						finalIdealSets.add(idealSet);
						
					}
					
					int intersection = findIntersection(finalIdealSets);
					String output = ""+participant+','+percentNew+","+intersection;
					for(int i=0; i < finalIdealNames.size(); i++){
						output+= ","+finalIdealNames.get(i)+","+finalIdealSets.get(i).size();
					}
					
					System.out.println(output);
					printed = true;
				}
				
				line = in.readLine();
			}
			
		}
		
	}
	
	Set<Integer> getIdealGroup(String name, Map<Set<Integer>, String> idealNames){
		for(Set<Integer> set: idealNames.keySet()){
			String mappedName = idealNames.get(set);
			if(name.equals(mappedName)){
				return set;
			}
		}
		return null;
	}
	
	int findIntersection(ArrayList<Set<Integer>> idealSets){
		Set<Integer> intersection = new TreeSet<Integer>(idealSets.get(0));
		for(int i=1; i<idealSets.size(); i++){
			intersection.retainAll(idealSets.get(i));
		}
		return intersection.size();
	}
	
	public void printSplitIdeals(int participant) throws IOException{
		String[] types = {"singlePrediction singleIdeal", "singlePrediction multiIdeal", "multiPrediction singleIdeal", "multiPrediction multiIdeal"};
		int maxNewMembers = 800;
		for(int newMembers = 1; newMembers <= maxNewMembers; newMembers++ ){
			for(int i=0; i<types.length; i++){
				printSplitIdeals(participant, newMembers, types[i]);
			}
		}
	}
	
	public void printSplitIdealsForAllParticipants() throws IOException{
		int[] participants = {10,12, 13, 16, 17, 19, 21, 22, 23, 24, 25};
		for(int i=0; i<participants.length; i++){
			printSplitIdeals(participants[i]);
		}
	}
	
	public static void main(String[] args) throws IOException{
		
		SplitIdealFinder finder = new SplitIdealFinder();
		finder.printSplitIdealsForAllParticipants();
		
	}
}
