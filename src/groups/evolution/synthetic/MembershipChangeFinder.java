package groups.evolution.synthetic;

import groups.comparison.SeedFinder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


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
	
	public Map<Set<V>, Collection<Set<V>>> getUnmaintainedToMaintainedGroups(Collection<Set<V>> maintainedGroups, Set<V> newMembers) {
		
		Map<Set<V>, Collection<Set<V>>> retVal = new HashMap<Set<V>, Collection<Set<V>>>();
		
		
		for(Set<V> maintainedGroup : maintainedGroups) {
			Set<V> unmaintainedGroup = getUnmaintainedGroup(maintainedGroup, newMembers);
			if(unmaintainedGroup.size() == 0) {
				continue;
			}
			Collection<Set<V>> mappings = retVal.get(unmaintainedGroup);
			if (mappings == null) {
				mappings = new ArrayList<>();
				retVal.put(unmaintainedGroup, mappings);
			}
			mappings.add(maintainedGroup);
		}
		
		return retVal;
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
