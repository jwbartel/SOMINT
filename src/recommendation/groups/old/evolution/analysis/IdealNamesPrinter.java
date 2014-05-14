package recommendation.groups.old.evolution.analysis;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import recommendation.groups.seedless.hybrid.IOFunctions;


public class IdealNamesPrinter {

	public void printIdealNamesForAllParticipants() throws IOException{
		int[] participants = {10,12, 13, 16, 17, 19, 21, 22, 23, 24, 25};
		for(int i=0; i<participants.length; i++){
			printSplitIdeals(participants[i]);
		}
	}

	private void printSplitIdeals(int participant) {
		IOFunctions<Integer> ioHelp = new IOFunctions<Integer>(Integer.class);
		String idNameMap = "data/Kelli/FriendshipData/2010Study/"+participant+"_People.txt";
		ioHelp.fillNamesAndIDs(idNameMap);
		
		String idealFile = "data/Jacob/Ideal/"+participant+"_ideal.txt";
		Map<Set<Integer>, String> idealNames = ioHelp.loadIdealGroupNames(idealFile);
		
		
		//System.out.println("Participant "+participant+"");
		Set<Integer> allMembers = new TreeSet<Integer>();
		for(Set<Integer> ideal: idealNames.keySet()){
			allMembers.addAll(ideal);
		}
		int totalSize = allMembers.size();
		
		for(Set<Integer> ideal: idealNames.keySet()){
			
			String name = idealNames.get(ideal);
			double relativeSize = ((double) ideal.size())/(double) totalSize;
			System.out.println(""+participant+","+name+","+ideal.size()+","+relativeSize);
			
		}
		
	}
	
	public static void main(String[] args) throws IOException{
		
		IdealNamesPrinter printer = new IdealNamesPrinter();
		printer.printIdealNamesForAllParticipants();
	}
}
