package bus.thunderbird;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import bus.data.structures.DirectedEmailInteraction;
import bus.data.structures.groups.GoogleGroupTracker;
import bus.thunderbird.structures.ThunderbirdAddressParser;


public class Trainer {
	
	static GoogleGroupTracker tracker = new GoogleGroupTracker();
	static Map<String, ArrayList<DirectedEmailInteraction>> individualInteractions = new TreeMap<String, ArrayList<DirectedEmailInteraction>>();
	
	static Set<String> addressSet = new TreeSet<String>();
	static Set<String> accountsOwners = new TreeSet<String>();
	
	
	public void setAccountsOwners(Set<String> accountsOwners){
		Trainer.accountsOwners = accountsOwners;
	}
	
	public Trainer(){
		
	}
	
	public static GoogleGroupTracker getGroups(){
		return tracker;
	}
	
	public static Map<String, ArrayList<DirectedEmailInteraction>> getIndividuals(){
		return individualInteractions;
	}
	
	public void trainMessage(String date, String from, String to, String cc, String bcc){
		ThunderbirdAddressParser fromParser = new ThunderbirdAddressParser(from);
		String[] fromAddress = fromParser.getAddresses();
		//if(fromAddress.length == 0) throw new RuntimeException("No from");
		
		ThunderbirdAddressParser sendParser = new ThunderbirdAddressParser();
		sendParser.add(to);
		sendParser.add(cc);
		sendParser.add(bcc);
		String[] outAddresses = sendParser.getAddresses();
		//if(outAddresses.length == 0) throw new RuntimeException("No out");

		
		Date dateObj = new Date(Long.parseLong(date));
		boolean wasReceived = wasReceived(fromAddress);
		
		DirectedEmailInteraction interaction = new DirectedEmailInteraction(null, dateObj, wasReceived);
		//System.out.println("Interaction :"+interaction);
		
	
		try{
		Set<String> group = new TreeSet<String>();
		for(int i=0; i<fromAddress.length; i++){
			group.add(fromAddress[i]);
			//System.out.println("Training for from:"+fromAddress[i]);
			trainIndividualInteraction(fromAddress[i], interaction);
		}
		for(int i=0; i<outAddresses.length; i++){
			group.add(outAddresses[i]);
			//System.out.println("Training for out:"+outAddresses[i]);
			trainIndividualInteraction(outAddresses[i], interaction);
		}
		tracker.foundMsgAddresses(group, null, dateObj, wasReceived);
		}catch(NullPointerException e){
			e.printStackTrace();
			throw e;
		}
	}
	
	public static void load (String groupFile, String individualFile) throws IOException{
		load(new File(groupFile), new File(individualFile));
	}
	
	public static void load (File groupFile, File individualFile) throws IOException{
		System.out.println("Loading past interactions");
		
		loadInviduals(individualFile);
		System.out.println("Individuals loaded");
		tracker.load(groupFile);
		System.out.println("Groups loaded");
		
	}
	
	protected static void loadInviduals(File file) throws IOException{
		individualInteractions.clear();
		
		BufferedReader in = new BufferedReader(new FileReader(file));
		String line = in.readLine();
		String address = null;
		ArrayList<DirectedEmailInteraction> interactions = new ArrayList<DirectedEmailInteraction>();
		while(line != null){
			if(line.charAt(0) =='\t'){
				//individual address
				if(interactions.size() > 0 && address != null){
					individualInteractions.put(address, interactions);
				}
				
				address = line.substring(1);
				interactions = new ArrayList<DirectedEmailInteraction>();
			}else{
				
				int splitPt = line.indexOf(' ');
				long date = Long.parseLong(line.substring(0, splitPt));
				line = line.substring(splitPt+1);
				
				splitPt = line.indexOf(' ');
				boolean wasReceived;
				String loc;
				if(splitPt == -1){
					wasReceived = Boolean.parseBoolean(line);
					loc = "";
				}else{
					wasReceived = Boolean.parseBoolean(line.substring(0, splitPt));
					loc = line.substring(splitPt+1);
				}
				
				DirectedEmailInteraction interaction = new DirectedEmailInteraction("", new Date(date), wasReceived);
				interactions.add(interaction);
			}
			line = in.readLine();
		}
	}
	
	protected void trainIndividualInteraction(String address, DirectedEmailInteraction interaction){
		
		ArrayList<DirectedEmailInteraction> oldInteractions = individualInteractions.get(address);
		if(oldInteractions == null){
			oldInteractions = new ArrayList<DirectedEmailInteraction>();
			individualInteractions.put(address, oldInteractions);
		}
		if(!oldInteractions.contains(interaction)){
			oldInteractions.add(interaction);
		}
	}
	
	protected boolean wasReceived(String[] fromAddress){
		for(int i=0; i<fromAddress.length; i++){
			String email = fromAddress[i];
			if(email == null || email.length() == 0){
				continue;
			}
			int pos = email.indexOf('+');
			if(pos >= 0){
				email = email.substring(0, pos)+email.substring(email.indexOf('@'));
			}
			if(accountsOwners.contains(email)){
				return false;
			}
		}
		
		return true;
	}
	
	protected static void saveIndividuals(File file) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		
		Iterator<Entry<String, ArrayList<DirectedEmailInteraction>>> entries = individualInteractions.entrySet().iterator();
		while(entries.hasNext()){
			Entry<String, ArrayList<DirectedEmailInteraction>> entry = entries.next();
			
			String individual = entry.getKey();
			out.write("\t"+individual);
			out.newLine();
			
			ArrayList<DirectedEmailInteraction> interactions = entry.getValue();
			for(int i=0; i<interactions.size(); i++){
				out.write(""+interactions.get(i));
				out.newLine();
			}
		}
		out.flush();
		out.close();
	}
	
	public static void save(String groupFile, String individualFile) throws IOException{
		save(new File(groupFile), new File(individualFile));
	}
	
	public static void save (File groupFile, File individualFile) throws IOException{
		System.out.println("Saving groups & individuals");
		tracker.save(groupFile);
		saveIndividuals(individualFile);
	}
	
	public static void main(String[] args) throws Exception{
		accountsOwners.add("bartel.jacob@gmail.com");
		
		Trainer trainer = new Trainer();
		
		File folder = new File("C:\\hierarchical_email_predictions");//"/home/bartizzi/Research/thunderbird add-ons/thunderbird data");
		
		File[] files = folder.listFiles();
		for(int i=0; i<files.length; i++){
			if(!files[i].getName().equals("LogInbox.txt") && !files[i].getName().equals("LogSent.txt") ) continue;
			BufferedReader in = new BufferedReader(new FileReader( files[i]));
			
			String line = in.readLine();
			while(line != null){
			
				if(files[i].getName().equals("LogSent.txt")){
					int x = 0;
					x = x + 1;
				}
				
				//line = "1196359338,JASutton@ehs.unc.edu (Department of Environment, Health and Safety):::\"EHS Notifications\" <ehs_notifications@listserv.unc.edu>:::::::::REQUIRED ENVIRONMENT, HEALTH & SAFETY ORIENTATION TRAINING";
				
				String origLine = line;
				try{
					
					int splitPt = line.indexOf(',');
					
					String date = (line.substring(0, splitPt));
					line = line.substring(splitPt+1);
					
					splitPt = line.indexOf(":::");
					String from = line.substring(0, splitPt);
					line = line.substring(splitPt+3);
					
					splitPt = line.indexOf(":::");
					String to = line.substring(0, splitPt);
					line = line.substring(splitPt+3);
					
					splitPt = line.indexOf(":::");
					String cc = line.substring(0, splitPt);
					line = line.substring(splitPt+3);
					
					splitPt = line.indexOf(":::");
					String bcc = line.substring(0, splitPt);
					line = line.substring(splitPt+3);
					
					trainer.trainMessage(date, from, to, cc, bcc);
					
					//if(true) return;
					
				}catch(Exception e){
					System.out.println(files[i].getName()+" - "+origLine);
					throw e;
				}

				
				line = in.readLine();
			}
		}
		
		tracker.save(new File("C:\\hierarchical_email_predictions\\groups2"));
		
		
		/*Iterator<String> addressIter = addressSet.iterator();
		while(addressIter.hasNext()){
			System.out.println(addressIter.next());
		}*/
	}
}
