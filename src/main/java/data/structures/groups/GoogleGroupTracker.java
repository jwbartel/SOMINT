package data.structures.groups;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
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

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import data.structures.ComparableSet;
import data.structures.DirectedEmailInteraction;
import data.structures.EmailInteraction;
import data.structures.ScoreUpdater;

public class GoogleGroupTracker extends StrictGroupTracker {
	
	public static final int INTERSECTION_GROUP_COUNT = 0;
	public static final int INTERSECTION_GROUP_SCORE = 1;
	public static final int INTERSECTION_WEIGHTED_SCORE = 2;
	public static final int TOP_CONTACT_SCORE = 3;
	
	public static final int NUM_SCHEMES = 4;
	
	private static int SCORE_UPDATER_SCHEME = 0;
	
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
	
	Map<ComparableSet<String>, GoogleGroup> groups = new TreeMap<ComparableSet<String>, GoogleGroup>();

	public static void setScoreUpdaterScheme(int scheme){
		SCORE_UPDATER_SCHEME = scheme;
	}
	
	public GoogleGroupTracker(){
	}
	
	public GoogleGroupTracker(int totalMsgs){
		super(totalMsgs);
	}
	
	public Set<Group> findMatchingGroups(Set<String> addresses, Date currDate) throws IOException{
		Set<Group> toReturn = new TreeSet<Group>();
		
		Iterator<ComparableSet<String>> sets = groups.keySet().iterator();
		while(sets.hasNext()){
			GoogleGroup group = groups.get(sets.next());
			
			double weight = 0;
			if(SCORE_UPDATER_SCHEME == INTERSECTION_GROUP_COUNT){
				weight = ScoreUpdater.intersectingGroupCount(addresses, group);
			}else if(SCORE_UPDATER_SCHEME == INTERSECTION_GROUP_SCORE){
				weight = ScoreUpdater.intersectionGroupScore(currDate, addresses, group);
			}else if(SCORE_UPDATER_SCHEME == INTERSECTION_WEIGHTED_SCORE){
				weight = ScoreUpdater.intersectionWeightedScore(currDate, addresses, group);
			}else if(SCORE_UPDATER_SCHEME == TOP_CONTACT_SCORE){
				weight = ScoreUpdater.topContactScore(currDate, addresses, group);
			}
			
			if(weight != 0){
				Group scoredGroup = new Group(group.getMembers());
				scoredGroup.setWeight(weight);
				toReturn.add(scoredGroup);
			}
		}
		
		return toReturn;
	}
	
	
	public void foundMsgAddresses(Set<String> addresses, String msgLocation, Date date, boolean wasReceived){
		/*if(date!=null){
			if(earliestDate == null || date.compareTo(earliestDate)<0){
				earliestDate = date;
			}
		}*/
		
		if(msgLocation == null) msgLocation = "";
		
		ComparableSet<String> comparableSet = new ComparableSet<String>(addresses);
		boolean exists = false;
		
		Iterator<ComparableSet<String>> sets = groups.keySet().iterator();
		while(sets.hasNext()){
			ComparableSet<String> set = sets.next();
			if(set.size() <= comparableSet.size() && comparableSet.containsAll(set)){
				if(set.size() == comparableSet.size()){
					exists = true;
				}
				
				GoogleGroup group = groups.get(set);
				group.foundInteraction(msgLocation, date, wasReceived);
			}
		}
		
		if(!exists){
			GoogleGroup group = new GoogleGroup(comparableSet);
			group.foundInteraction(msgLocation, date, wasReceived);
			groups.put(comparableSet, group);
		}
	}
	
	public void save(File dest) throws IOException{

		System.out.println("saved tracker");
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		
		Iterator<ComparableSet<String>> sets = groups.keySet().iterator();
		while(sets.hasNext()){
			GoogleGroup group = groups.get(sets.next());
			
			Iterator<String> addresses = group.getMembers().iterator();
			while(addresses.hasNext()){
				String address = addresses.next();
				out.write(address);
				out.newLine();
			}
			
			
			ArrayList<EmailInteraction> interactions = group.interactions;
			for(int i=0; i<interactions.size(); i++){
				DirectedEmailInteraction interaction = (DirectedEmailInteraction) interactions.get(i);
				out.write("\t"+interaction.wasReceived()+"\t"+dateFormat.format(interaction.getDate())+"\t"+interaction.getEmailLocation());
				out.newLine();
			}
		}
		
		out.flush();
		out.close();
			
	}
	
	
	
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
			
			GoogleGroup group = new GoogleGroup(addresses);
			while(line != null && line.charAt(0)=='\t'){
				line = line.substring(1);
				int tabIndex = line.indexOf('\t');
				
				boolean wasReceived = Boolean.parseBoolean(line.substring(0,tabIndex));
				line = line.substring(tabIndex+1);
				
				tabIndex = line.indexOf('\t');
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
				
				group.foundInteraction(msgLoc, date, wasReceived);
				line = in.readLine();
			}
			
			groups.put(addresses, group);
		}
		in.close();
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
	
	protected Date getMessageDate(String msgFile) throws IOException{
		if(msgFile == null){
			return null;
		}
		
		StringBuffer msgBuffer = null;
		BufferedReader in = new BufferedReader(new FileReader(msgFile));
		String line = in.readLine();
		while(line != null){
			
			if(msgBuffer == null){
				msgBuffer = new StringBuffer(line);
			}else{
				msgBuffer.append("\n");
				msgBuffer.append(line);
			}
			
			line = in.readLine();
		}
		in.close();
		
		String msg = msgBuffer.toString();
		Session session = Session.getDefaultInstance(System.getProperties());
		
		Date msgDate;
		try{
			MimeMessage message = new MimeMessage(session, new ByteArrayInputStream(msg.getBytes()));
			
			msgDate = message.getSentDate();
			if(msgDate == null){
				msgDate = message.getReceivedDate();
			}
		}catch (MessagingException e) {
			return null;
		}
		
		return msgDate;
		
	}
	
	public void clear(){
		super.clear();
		groups.clear();
	}
}
