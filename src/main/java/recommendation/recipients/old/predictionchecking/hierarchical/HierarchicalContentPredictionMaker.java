package recommendation.recipients.old.predictionchecking.hierarchical;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.mail.MessagingException;

import data.structures.EmailInteraction;
import data.structures.groups.Group;
import recommendation.recipients.old.predictionchecking.ContentPredictionMaker;

public class HierarchicalContentPredictionMaker extends ContentPredictionMaker{

	public HierarchicalContentPredictionMaker(File accountFolder, Map<String, Integer> wordCounts, Date currDate, Set<String> seed) throws IOException, MessagingException {
		super(accountFolder, wordCounts, currDate, seed);
	}

	public HierarchicalContentPredictionMaker(String accountFolder, Map<String, Integer> wordCounts, Date currDate, Set<String> seed) throws IOException, MessagingException {
		super(accountFolder, wordCounts, currDate, seed);
	}
	
	protected void makePredictions(Map<String, Integer> wordCounts, Date currDate, Set<String> seed) throws IOException{
		if(oldAccountFolder != null && oldAccountFolder.equals(accountFolder) && 
				oldWordCounts != null && oldWordCounts.equals(wordCounts) && 
				(!isAged || (oldDate != null && oldDate.equals(currDate))) ){
			
			return;
			
		}
		
		oldWordCounts = wordCounts;
		oldDate = currDate;
		oldAccountFolder = accountFolder;
		oldPredictIndividuals = predictIndividuals;
			    
		
		
		
		makeIndividualPredictions(wordCounts, currDate, seed);
		makeGroupPredictions(wordCounts, currDate, seed);
	}
	
	protected void buildGroupAddressBook(Date currDate) throws IOException{
		if(groupAddressBook == null) groupAddressBook = new TreeMap<Group, double[]>();
		groupAddressBook.clear();
		loadIDFs();
		getWordList();
		
		
		Iterator<Group> groups = groupTracker.getAllGroups().iterator();
		while(groups.hasNext()){
			Group group = groups.next();
			if(group.getMembers().size() < 2){
				return;
			}
			
			ArrayList<EmailInteraction> interactions = group.getInteractions();
			double[] tfidfVector = getTFIDF(interactions, currDate);
			
			groupAddressBook.put(group, tfidfVector);
			
		}
	}

}
