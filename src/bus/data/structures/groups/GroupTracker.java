package bus.data.structures.groups;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import bus.data.structures.ComparableSet;

public interface GroupTracker {

	public void foundGroup(ComparableSet<String> addresses);

	public void foundMsgAddresses(Set<String> addresses, String msgLocation, Date date);

	public Set<Group> findMatchingGroups(Set<String> addresses);
	public Set<Group> getAllGroups();

	public void save(File dest) throws IOException;
	public void load(File src) throws IOException;
	
	public Map<String, Double> getNormalizedAddressWeights();
	
	public Date getEarliestDate();
	
	public void clear();
	
}