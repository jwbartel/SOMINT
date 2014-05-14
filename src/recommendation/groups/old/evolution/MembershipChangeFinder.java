package recommendation.groups.old.evolution;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import recommendation.groups.old.comparison.SeedFinder;


public class MembershipChangeFinder<V> extends SeedFinder<V>{
	
	
	public Set<V> getPseudoRandomNewIndividuals(Set<V> oldAndNewIndividuals, int numNew){

		Set<V> newMembers = super.findPsuedoRandomSeed(oldAndNewIndividuals, numNew);
		
		return newMembers;
	}
	
	public Set<V> getPseudoRandomNewIndividuals(Set<V> oldAndNewIndividuals, double percentNew){
		
		int oldAndNewMemberscount = oldAndNewIndividuals.size();
		int newMembersCount = (int) (percentNew * oldAndNewMemberscount);
		
		Set<V> newMembers = super.findPsuedoRandomSeed(oldAndNewIndividuals, newMembersCount);
		
		return newMembers;
	}
	
	
	public Set<V> getUnmaintainedGroup(Set<V> maintainedGroup, Set<V> newMembers){
		
		Set<V> unmaintainedGroup = new TreeSet<V>(maintainedGroup);
		unmaintainedGroup.removeAll(newMembers);
		return unmaintainedGroup;
		
	}
	
	public void saveUnmaintainedIndividuals(Set<V> oldAndNewIndividuals, double percentNew, File outFile){
		
		Set<V> newMembers = getPseudoRandomNewIndividuals(oldAndNewIndividuals, percentNew);
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
			for(V newMember: newMembers){
				out.write(newMember.toString());
				out.newLine();
			}
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	

}
