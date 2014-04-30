package recipients.predictionchecking.hierarchical;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.mail.MessagingException;

import recipients.predictionchecking.TimeAndDirectionPredictionMaker;

import bus.accounts.Account;
import bus.data.structures.ComparableSet;
import bus.data.structures.EmailInteraction;
import bus.data.structures.groups.GoogleGroupTracker;
import bus.data.structures.groups.Group;

public class HierarchicalTimeAndDirectionPredictionMaker extends TimeAndDirectionPredictionMaker {

	public HierarchicalTimeAndDirectionPredictionMaker(){
		
	}
	
	public HierarchicalTimeAndDirectionPredictionMaker(File accountFolder, Date currDate) throws IOException,MessagingException {
		super(accountFolder, currDate);
	}
	
	public HierarchicalTimeAndDirectionPredictionMaker(String accountFolder, Date currDate) throws IOException,MessagingException {
		super(accountFolder, currDate);
	}
	
	protected void makePredictions(Date currDate) throws IOException{

		if(groupTracker == null){
			groupTracker = new GoogleGroupTracker((int)(totalMsgs*Account.TRAINING_RATIO));
			File groupListFile = new File(accountFolder, groups_list);
			groupTracker.load(groupListFile);	
		}
		
		makeGroupPredictions(currDate);
		makeIndividualPredictions(currDate);
		
	}
	
	protected void buildGroupIRVals(Date currDate) throws IOException{
		Iterator<Group> groups = groupTracker.getAllGroups().iterator();
		while(groups.hasNext()){
			Group group = groups.next();			
			ComparableSet<String> members = new ComparableSet<String>(group.getMembers());
			if(members.size() < 2){
				continue;
			}
			
			ArrayList<EmailInteraction> interactions = group.getInteractions();
			
			double ir = getIR(interactions, currDate);
			
			groupIRValues.put(members, ir);
			
		}
	}

}
