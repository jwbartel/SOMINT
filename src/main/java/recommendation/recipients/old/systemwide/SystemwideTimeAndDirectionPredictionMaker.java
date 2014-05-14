package recommendation.recipients.old.systemwide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.mail.MessagingException;

import recommendation.recipients.old.predictionchecking.TimeAndDirectionPredictionMaker;
import bus.accounts.Account;
import bus.accounts.FileNameByOS;
import bus.data.structures.AddressLists;
import bus.data.structures.ComparableSet;
import bus.data.structures.EmailInteraction;
import bus.data.structures.groups.GoogleGroupTracker;
import bus.data.structures.groups.Group;

public class SystemwideTimeAndDirectionPredictionMaker extends TimeAndDirectionPredictionMaker{
	
	public SystemwideTimeAndDirectionPredictionMaker(String accountsFolder, String sender, Date currDate) throws IOException, MessagingException{
		super(accountsFolder, currDate);
		makePredictions(sender, currDate);
	}
	
	public SystemwideTimeAndDirectionPredictionMaker(File accountsFolder, String sender, Date currDate) throws IOException, MessagingException{
		super(accountsFolder, currDate);
		makePredictions(sender, currDate);
	}
	
	protected void makePredictions(Date currDate){
		//To reduce unnecessary computation from super class
	}
	
	protected void makePredictions(String sender, Date currDate) throws IOException{
		if(groupTracker == null){
			groupTracker = new GoogleGroupTracker((int)(totalMsgs*Account.TRAINING_RATIO));
			File groupListFile = new File(accountFolder, groups_list);
			groupTracker.load(groupListFile);	
		}
		
		if(predictIndividuals){
			makeIndividualPredictions(sender, currDate);
		}else{
			makeGroupPredictions(sender, currDate);
		}
	}
	
	protected void makeIndividualPredictions(String sender, Date currDate) throws IOException{
		if(individualMsgs == null){
			File individualsListFile = new File(accountFolder, individuals_list);
			loadIndividualMsgList(individualsListFile);
		}
		
		buildIndividualIRVals(sender, currDate);
	}
	
	protected void makeGroupPredictions(String sender, Date currDate) throws IOException{

		buildGroupIRVals(sender, currDate);
	}
	
	private void buildIndividualIRVals(String sender, Date currDate) throws IOException{
		individualIRValues.clear();
		
		Iterator<String> individuals = individualMsgs.keySet().iterator();
		while(individuals.hasNext()){
			String individual = individuals.next();
			ArrayList<EmailInteraction> interactions = individualMsgs.get(individual);
			
			double ir = getIR(interactions,  sender, currDate);
		
			individualIRValues.put(individual, ir);
		}
	}
	
	protected void buildGroupIRVals(String sender, Date currDate) throws IOException{

		Iterator<Group> groups = groupTracker.getAllGroups().iterator();
		while(groups.hasNext()){
			Group group = groups.next();
			ComparableSet<String> members = new ComparableSet<String>(group.getMembers());
			ArrayList<EmailInteraction> interactions = group.getInteractions();
			
			double ir = getIR(interactions, sender, currDate);
			
			groupIRValues.put(members, ir);
			
		}
	}
	
	protected double getIR(ArrayList<EmailInteraction> interactions, String sender, Date currDate) throws IOException{

		double sentTotal = 0.0;
		double receivedTotal = 0.0;
		
		for(int i=0; i<interactions.size(); i++){
			EmailInteraction interaction = interactions.get(i);
			double val = 0.0;
			
			if(isAged){
				if(!useHalfLives){
					
					long currDateLong = currDate.getTime();
					long oldestDate = groupTracker.getEarliestDate().getTime();					
					long interactionDate = interaction.getDate().getTime();
					
					val = ((double) (interactionDate - oldestDate))/((double) currDateLong - oldestDate);
					
				}else{
					
					long currDateLong = currDate.getTime();	
					long interactionDate = interaction.getDate().getTime();
					
					double exponent = ((double) currDateLong - interactionDate)/half_life;
					val = Math.pow(0.5, exponent);
					
				}
			}else{
				val = 1.0;
			}
			
			String interactionSender = Account.getSender(interaction.getEmailLocation());
			boolean wasReceived = (sender == null && interactionSender == null) || (sender != null && sender.equals(interactionSender));
			//boolean wasReceived = ((DirectedEmailInteraction) interaction).wasReceived();
			
			if(wasReceived){
				receivedTotal += val;
			}else{
				sentTotal += val;
			}
		}
		
		return (w_out * sentTotal) + receivedTotal;
	}
	
	public static void main(String[] args) throws IOException{
		String accountsFolder = "C:\\Users\\Jacob\\Documents\\My Dropbox\\Sample Enron Accounts";
		File msgList = new File(accountsFolder, Account.ALL_MSGS_ADAPTED);
		
		BufferedReader in = new BufferedReader(new FileReader(msgList));
		String line = in.readLine();
		line = in.readLine();
		while(line != null){
			String currMessage = FileNameByOS.getMappedFileName(line);
			File addressFile = new File(currMessage+Account.ADDR_FILE_SUFFIX);
			if(!addressFile.exists()){
				Account.saveAddresses(new File(currMessage), addressFile);
			}
			
			AddressLists addressLists = new AddressLists(addressFile);
			ArrayList<String> from = addressLists.getFrom();
			if(from.size() != 1){
				System.out.println(/*from.toString() + "\t" +*/ line);
				//Account.saveAddresses(new File(currMessage), addressFile);
			}
			line = in.readLine();
			while(line != null && line.startsWith("\t")){
				line = in.readLine();
			}
			//TODO: run for ubuntu machine
			
		}
	}
}
