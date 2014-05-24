package data.structures.groups;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import data.structures.ComparableSet;
import data.structures.EmailInteraction;



public class StrictGroupTracker implements GroupTracker {
	
	protected Date earliestDate = null;
	int totalMsgs = 0;
	
	public Date getEarliestDate(){
		return earliestDate;
	}
	
	public StrictGroupTracker(){
	}
	
	public StrictGroupTracker(int totalMsgs){
		this.totalMsgs = totalMsgs;
	}
	
	
	Map<ComparableSet<String>, Group> groups = new TreeMap<ComparableSet<String>, Group>();
	
	public void foundGroup(ComparableSet<String> addresses){
		Group group = groups.get(addresses);
		if(group == null){
			group = new Group(addresses);
			groups.put(addresses, group);
		}else{
			group.increment();
		}
	}
	
	public void foundMsgAddresses(Set<String> addresses, String msgLocation, Date date){
		if(date!=null){
			if(earliestDate == null || date.compareTo(earliestDate)<0){
				earliestDate = date;
			}
		}
		ComparableSet<String> compSet = new ComparableSet<String>(addresses);
		Set<ComparableSet<String>> keySet = groups.keySet();
		Iterator<ComparableSet<String>> sets = keySet.iterator();
		boolean exists = keySet.contains(compSet);
		int maxWeight = 1;
		while(sets.hasNext()){
			ComparableSet<String> set = sets.next();
			if(set.size() <= compSet.size()){
				if(compSet.containsAll(set)){
					Group group = groups.get(set);
					group.increment();
					
					if(date != null){
						Date oldDate = group.getDate();
						if(oldDate==null || date.compareTo(oldDate)>0){
							group.setMostRecentDate(date);
						}
					}
					group.foundInteraction(msgLocation, date);
				}
			}else{
				if(!exists){
					int weight = (int) groups.get(set).getWeight();
					if(weight > maxWeight){
						maxWeight = weight;
					}
				}
			}
		}
		if(!exists){
			Group group = new Group(compSet);
			group.setWeight(maxWeight);
			group.setMostRecentDate(date);
			group.foundInteraction(msgLocation, date);
			groups.put(compSet, group);
		}
	}
	
	public Set<Group> findMatchingGroups(Set<String> addresses){
		Set<Group> toReturn = new TreeSet<Group>();
		
		Iterator<ComparableSet<String>> knownSets = groups.keySet().iterator();
		while(knownSets.hasNext()){
			Set<String> knownSet = knownSets.next();
			if(knownSet.size()<=addresses.size()){
				continue;
			}
			
			Iterator<String> addrs = addresses.iterator();
			boolean isSubset = true;
			while(addrs.hasNext()){
				String addr = addrs.next();
				if(!knownSet.contains(addr)){
					isSubset = false;
					break;
				}
			}
			if(isSubset){
				toReturn.add(groups.get(knownSet));
			}
		}
		
		return toReturn;
	}
	
	public void save(File dest) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		
		Iterator<ComparableSet<String>> sets = groups.keySet().iterator();
		while(sets.hasNext()){
			Group group = groups.get(sets.next());
			
			Iterator<String> addresses = group.getMembers().iterator();
			while(addresses.hasNext()){
				String address = addresses.next();
				out.write(address);
				out.newLine();
			}
			
			
			ArrayList<EmailInteraction> interactions = group.interactions;
			for(int i=0; i<interactions.size(); i++){
				EmailInteraction interaction = interactions.get(i);
				out.write("\t"+dateFormat.format(interaction.getDate())+"\t"+interaction.getEmailLocation());
				out.newLine();
			}
		}
		
		out.flush();
		out.close();
			
	}
	
	public static SimpleDateFormat dateFormat = new SimpleDateFormat();
	
	public void load(File src) throws IOException{
		Set<String> seenMsgs = new TreeSet<String>();
		
		groups.clear();
		BufferedReader in = new BufferedReader(new FileReader(src));
		
		String line = in.readLine();
		while(line != null){
			ComparableSet<String> addresses = new ComparableSet<String>();
			while(line != null && line.charAt(0) !='\t'){
				addresses.add(line);
				line = in.readLine();
			}
			
			Group group = new Group(addresses);
			while(line != null && line.charAt(0)=='\t'){
				line = line.substring(1);
				int tabIndex = line.indexOf('\t');
				Date date;
				try {
					date = dateFormat.parse(line.substring(0,tabIndex));
				} catch (ParseException e) {
					throw new IOException("Error parsing date: "+ line.substring(0,tabIndex));
				}
				String msgLoc = line.substring(tabIndex+1);

				if(!seenMsgs.contains(msgLoc)){
					seenMsgs.add(msgLoc);
					
					if(earliestDate == null || earliestDate.after(date)){
						earliestDate = date;
					}
				}
				
				group.foundInteraction(msgLoc, date);
				line = in.readLine();
			}
			
			groups.put(addresses, group);
		}
		in.close();
	}
	
	/*public static void main(String[] args){
		GroupTracker gt = new StrictGroupTracker(0);
		ComparableSet<String> s1 = new ComparableSet<String>();
		s1.add("A");
		s1.add("B");
		s1.add("C");
		gt.foundMsgAddresses(s1);
		
		ComparableSet<String> s2 = new ComparableSet<String>();
		s2.add("B");
		s2.add("C");
		System.out.println(gt.findMatchingGroups(s2));
	}*/
	
	public void print(){
		Iterator<ComparableSet<String>> iter = groups.keySet().iterator();
		while(iter.hasNext()){
			ComparableSet<String> next = iter.next();
			System.out.println(groups.get(next).getWeight());
			System.out.println("\t"+next.toString());
		}
	}

	
	public Map<String, Double> getNormalizedAddressWeights() {
		Map<String, Double> toReturn = new TreeMap<String, Double>();
		
		Iterator<ComparableSet<String>> sets = groups.keySet().iterator();
		while(sets.hasNext()){
			ComparableSet<String> currSet = sets.next();
			int currWeight = (int) groups.get(currSet).getWeight();
			
			Iterator<String> addresses = currSet.iterator();
			while(addresses.hasNext()){
				String address = addresses.next();
				double normalizedWeight = ((double) currWeight)/totalMsgs;
				
				if(!toReturn.containsKey(address)){
					toReturn.put(address, normalizedWeight);
				}else{
					toReturn.put(address, toReturn.get(address)+normalizedWeight);
				}
			}
		}
		
		return toReturn;
	}
	
	public Set<Group> getAllGroups(){
		Set<Group> toReturn = new TreeSet<Group>();
		
		Iterator<ComparableSet<String>> sets = groups.keySet().iterator();
		while(sets.hasNext()){
			ComparableSet<String> set = sets.next();
			toReturn.add(groups.get(set));
		}
		
		return toReturn;
	}
	
	public void clear(){
		groups.clear();
	}
}
