package bus.data.structures.groups;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import bus.data.structures.EmailInteraction;

public class Group implements Comparable<Group>{
	protected Set<String> addresses = new TreeSet<String>();
	double weight;
	protected Date date;
	
	ArrayList<EmailInteraction> interactions = new ArrayList<EmailInteraction>();
	
	public Set<String> getMembers(){
		return addresses;
	}
	
	public void clear(){
		addresses.clear();
		weight = 0;
	}
	
	public Group(Set<String> addresses){		
		this.addresses = addresses;
		weight = 1;
	}
	
	public boolean isSubsetOf(Set<String> addresses){
		if(this.addresses.size() <= addresses.size()){
			Iterator<String> iter = this.addresses.iterator();
			while(iter.hasNext()){
				String address = iter.next();
				if(!addresses.contains(address)){
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	public void increment(){
		weight++;
	}
	
	public String toString(){
		return addresses.toString();
	}

	public int compareTo(Group arg0) {
		
		if(this.weight != arg0.weight){
			double value =  this.weight - arg0.weight;
			if(value<0){
				return -1;
			}else if(value>0){
				return 1;
			}else{
				return 0;
			}
		}else{
			return this.addresses.toString().compareTo(arg0.addresses.toString());
		}
	}
	
	public double getWeight(){
		return weight;
	}
	
	public void setMostRecentDate(Date date){
		this.date = date;
	}
	
	public Date getDate(){
		return date;
	}
	
	public void setWeight(double weight){
		this.weight = weight;
	}
	
	public void foundInteraction(String loc, Date date){
		interactions.add(new EmailInteraction(loc, date));
	}
	
	public ArrayList<EmailInteraction> getInteractions(){
		ArrayList<EmailInteraction> toReturn = new ArrayList<EmailInteraction>();
		
		for(int i=0; i<interactions.size(); i++){
			EmailInteraction interaction = interactions.get(i);
			EmailInteraction toAdd = new EmailInteraction(interaction.getEmailLocation(), interaction.getDate());
			toReturn.add(toAdd);
		}
		
		return toReturn;
	}
}
