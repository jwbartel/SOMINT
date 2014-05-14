package recommendation.recipients.predictionchecking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.mail.MessagingException;

import bus.accounts.Account;
import bus.data.structures.ComparableSet;
import bus.data.structures.DirectedEmailInteraction;
import bus.data.structures.EmailInteraction;
import bus.data.structures.groups.GoogleGroupTracker;
import bus.data.structures.groups.Group;

public class TimeAndDirectionPredictionMaker extends PredictionMaker{
	
	Date date;
	
	protected Map<ComparableSet<String>, Double> groupIRValues = new TreeMap<ComparableSet<String>, Double>();
	protected Map<String, Double> individualIRValues = new TreeMap<String, Double>();

	public TimeAndDirectionPredictionMaker(){
		
	}
	
	public TimeAndDirectionPredictionMaker(File accountFolder, Date currDate) throws IOException, MessagingException {
		super(accountFolder);
		this.date = currDate;
		makePredictions(currDate);
	}
	
	public TimeAndDirectionPredictionMaker(String accountFolder, Date currDate) throws IOException, MessagingException {
		super(accountFolder);
		this.date = currDate;
		makePredictions(currDate);
	}
	
	protected void makePredictions(Date currDate) throws IOException{

		if(groupTracker == null){
			groupTracker = new GoogleGroupTracker((int)(totalMsgs*Account.TRAINING_RATIO));
			File groupListFile = new File(accountFolder, groups_list);
			groupTracker.load(groupListFile);	
		}
		
		if(predictIndividuals){
			makeIndividualPredictions(currDate);
		}else{
			makeGroupPredictions(currDate);
		}
	}
	
	protected void makeIndividualPredictions(Date currDate) throws IOException{
		if(individualMsgs == null){
			File individualsListFile = new File(accountFolder, individuals_list);
			loadIndividualMsgList(individualsListFile);
		}
		
		buildIndividualIRVals(currDate);
	}
	
	protected void makeGroupPredictions(Date currDate) throws IOException{
		
		buildGroupIRVals(currDate);
	}
	
	private void buildIndividualIRVals(Date currDate) throws IOException{
		individualIRValues.clear();
		
		Iterator<String> individuals = individualMsgs.keySet().iterator();
		while(individuals.hasNext()){
			String individual = individuals.next();
			ArrayList<EmailInteraction> interactions = individualMsgs.get(individual);
			
			double ir = getIR(interactions, currDate);
		
			individualIRValues.put(individual, ir);
		}
	}
	
	protected void buildGroupIRVals(Date currDate) throws IOException{
		Iterator<Group> groups = groupTracker.getAllGroups().iterator();
		while(groups.hasNext()){
			Group group = groups.next();
			ComparableSet<String> members = new ComparableSet<String>(group.getMembers());
			ArrayList<EmailInteraction> interactions = group.getInteractions();
			
			double ir = getIR(interactions, currDate);
			
			groupIRValues.put(members, ir);
			
		}
	}
	
	protected double getIR(ArrayList<EmailInteraction> interactions, Date currDate) throws IOException{
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
			
			boolean wasReceived = ((DirectedEmailInteraction) interaction).wasReceived();
			
			if(wasReceived){
				receivedTotal += val;
			}else{
				sentTotal += val;
			}
		}
		
		return (w_out * sentTotal) + receivedTotal;
	}

	public Map<String, Double> getIndividualPredictions() {
		return individualIRValues;
	}

	public Map<ComparableSet<String>, Double> getGroupPredictions() {
		return groupIRValues;
	}
	
	public Date getDate(){
		return date;
	}

}
