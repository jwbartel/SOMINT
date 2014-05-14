package recommendation.recipients.old.predictionchecking.hierarchical;

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
import bus.data.structures.EmailInteraction;
import bus.data.structures.groups.GoogleGroupTracker;
import bus.data.structures.groups.Group;

public class HierarchicalSocialConnectionPredictionMaker extends SocialConnectionPredictionMaker{

	public HierarchicalSocialConnectionPredictionMaker(){
		
	}
	
	public HierarchicalSocialConnectionPredictionMaker(File accountFolder, String sender, Set<String> seed, Date currDate) throws IOException, MessagingException {
		super(accountFolder, sender, seed, currDate);
	}
	
	public HierarchicalSocialConnectionPredictionMaker(String accountFolder, String sender, Set<String> seed, Date currDate) throws IOException, MessagingException {
		super(accountFolder, sender, seed, currDate);
	}
	
	protected void makePredictions(String sender, Set<String> seed, Date currDate) throws IOException{
		if(groupTracker == null){
			groupTracker = new GoogleGroupTracker((int)(totalMsgs*Account.TRAINING_RATIO));
			File groupListFile = new File(accountFolder, groups_list);
			groupTracker.load(groupListFile);	
		}
		
		if(oldAccountFolder == null || !oldAccountFolder.equals(accountFolder) || !oldDate.equals(currDate)){
			buildGroupIRVals(currDate);
			oldAccountFolder = accountFolder;
			oldDate = currDate;
		}
		
		makeIndividualPredictions(sender, seed);
		makeGroupPredictions(sender, seed);
	}
	
	protected void buildGroupIRVals(Date currDate) throws IOException{
		groupIRValues.clear();
		
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
