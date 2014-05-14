package recommendation.recipients.old.systemwide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.mail.MessagingException;

import recommendation.recipients.old.predictionchecking.SocialConnectionPredictionMaker;
import bus.accounts.Account;
import bus.data.structures.ComparableSet;
import bus.data.structures.DirectedEmailInteraction;
import bus.data.structures.EmailInteraction;
import bus.data.structures.groups.GoogleGroupTracker;
import bus.data.structures.groups.Group;

public class SystemwideSocialConnectionPredictionMaker extends SocialConnectionPredictionMaker {
		
		protected static String oldSender; 
	
		public SystemwideSocialConnectionPredictionMaker(String accountsFolder, String sender, Set<String> seed, Date currDate) throws IOException, MessagingException{
			super(accountsFolder, sender, seed, currDate);
			makePredictions(sender, seed, currDate);
		}
	
		public SystemwideSocialConnectionPredictionMaker(File accountsFolder, String sender, Set<String> seed, Date currDate) throws IOException, MessagingException{
			super(accountsFolder, sender, seed, currDate);
			makePredictions(sender, seed, currDate);
		}
	
		protected void makePredictions(Set<String> seed, Date currDate){
			//To reduce unnecessary computation from super class
		}
		
		protected void makePredictions(String sender, Set<String> seed, Date currDate) throws IOException{
			if(groupTracker == null){
				groupTracker = new GoogleGroupTracker((int)(totalMsgs*Account.TRAINING_RATIO));
				File groupListFile = new File(accountFolder, groups_list);
				groupTracker.load(groupListFile);	
			}
			
			if(oldAccountFolder == null || !oldAccountFolder.equals(accountFolder) || !oldDate.equals(currDate) || (oldSender == null && sender != null) || (oldSender != null && !oldSender.equals(sender))){
				buildGroupIRVals(sender, currDate);
				oldAccountFolder = accountFolder;
				oldDate = currDate;
				oldSender = sender;
			}
			
			if(predictIndividuals){
				makeIndividualPredictions(sender, seed);
			}else{
				makeGroupPredictions(sender, seed);
			}
			
			
		}
		
		protected void buildGroupIRVals(String sender, Date currDate) throws IOException{
			groupIRValues.clear();
			
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
				DirectedEmailInteraction interaction = (DirectedEmailInteraction) interactions.get(i);
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
			if(w_out == 1.0) return sentTotal+receivedTotal;
			return (w_out * sentTotal) + receivedTotal;
		}

}
