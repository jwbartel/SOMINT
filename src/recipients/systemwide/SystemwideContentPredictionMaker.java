package recipients.systemwide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.mail.MessagingException;

import recipients.predictionchecking.ContentPredictionMaker;

import bus.accounts.Account;
import bus.data.structures.EmailInteraction;
import bus.data.structures.groups.GoogleGroupTracker;
import bus.data.structures.groups.Group;

public class SystemwideContentPredictionMaker extends ContentPredictionMaker {
	
	protected static String oldSender;

	public SystemwideContentPredictionMaker(String accountsFolder, String sender, Map<String, Integer> wordCounts, Date currDate, Set<String> seed) throws IOException, MessagingException{
		super(accountsFolder, wordCounts, currDate, seed);
		makePredictions(sender, wordCounts, currDate, seed);
	}
	
	public SystemwideContentPredictionMaker(File accountsFolder, String sender, Map<String, Integer> wordCounts, Date currDate, Set<String> seed) throws IOException, MessagingException{
		super(accountsFolder, wordCounts, currDate, seed);
		makePredictions(sender, wordCounts, currDate, seed);
	}
	
	protected void makePredictions(Map<String, Integer> wordCounts, Date currDate, Set<String> seed){
		//To reduce unnecessary computation from super class
	}
	
	protected void makePredictions(String sender, Map<String, Integer> wordCounts, Date currDate, Set<String> seed) throws IOException{
		if(oldAccountFolder != null && oldAccountFolder.equals(accountFolder) && 
				oldWordCounts != null && oldWordCounts.equals(wordCounts) && 
				(!isAged || (oldDate != null && oldDate.equals(currDate))) &&
				oldPredictIndividuals != null && oldPredictIndividuals == predictIndividuals &&
				( (oldSender == null && sender == null) || (oldSender != null && oldSender.equals(sender)) )){
			
			return;
			
		}
		
		oldWordCounts = wordCounts;
		oldDate = currDate;
		oldAccountFolder = accountFolder;
		oldPredictIndividuals = predictIndividuals;
		oldSender = sender;
			    
		
		
		
		if(predictIndividuals){
			//makeIndividualPredictions(wordCounts, currDate,  seed);
		}else{
			makeGroupPredictions(sender, wordCounts, currDate, seed);
		}
	}
	
	protected void makeGroupPredictions(String sender, Map<String, Integer> wordCounts, Date currDate, Set<String> seed) throws IOException{
		if(groupTracker == null){
			groupTracker = new GoogleGroupTracker((int)(totalMsgs*Account.TRAINING_RATIO));
			File groupListFile = new File(accountFolder, groups_list);
			groupTracker.load(groupListFile);	
		}
		
		if(isAged  || w_out != 1){
			buildGroupAddressBook(sender, currDate);
		}else{
			if(groupAddressBook == null){
				groupAddressBook = new TreeMap<Group, double[]>();
				File addressbookFile = new File(this.accountFolder, unaged_grouped_addressbook);
				if(addressbookFile.exists()){
					loadGroupAddressBook(addressbookFile);
				}else{
					buildGroupAddressBook(sender, currDate);
					saveGroupAddressBook(addressbookFile);
				}
			}
		}
		
		double[] freqArray = getFreqArray(wordCounts);
		buildGroupContentSimilarities(freqArray, seed);
	}
	
	protected void buildGroupAddressBook(String sender, Date currDate) throws IOException{
		if(groupAddressBook == null) groupAddressBook = new TreeMap<Group, double[]>();
		groupAddressBook.clear();
		loadIDFs();
		getWordList();
		
		
		Iterator<Group> groups = groupTracker.getAllGroups().iterator();
		while(groups.hasNext()){
			Group group = groups.next();
			
			ArrayList<EmailInteraction> interactions = group.getInteractions();
			double[] tfidfVector = getTFIDF(sender, interactions, currDate);
			
			groupAddressBook.put(group, tfidfVector);
			
		}
	}
	
	protected void makeIndividualPredictions(String sender, Map<String, Integer> wordCounts, Date currDate, Set<String> seed) throws IOException{
		
		if(individualMsgs == null){
			File individualsListFile = new File(accountFolder, individuals_list);
			loadIndividualMsgList(individualsListFile);
		}
		
		if(isAged  || w_out != 1){
			buildIndividualAddressBook(sender, currDate);
		}else{
			if(individualAddressBook == null){
				File addressbookFile = new File(this.accountFolder, unaged_individual_addressbook);
				if(addressbookFile.exists()){
					loadIndividualAddressBook(addressbookFile);
				}else{
					buildIndividualAddressBook(sender, currDate);
					saveIndividualAddressBook(addressbookFile);
				}
			}
		}
		
		double[] freqArray = getFreqArray(wordCounts);
		buildIndividualContentSimilarities(freqArray, seed);
		
	}
	
	protected void buildIndividualAddressBook(String sender, Date currDate) throws IOException{
		if(individualAddressBook == null) individualAddressBook = new TreeMap<String, double[]>();
		individualAddressBook.clear();
		loadIDFs();
		getWordList();
		
		Iterator<String> individuals = individualMsgs.keySet().iterator();
		while(individuals.hasNext()){
			String individual = individuals.next();
			
			ArrayList<EmailInteraction> interactions = individualMsgs.get(individual);
			double[] tfidfVector = getTFIDF(sender, interactions, currDate);
			
			individualAddressBook.put(individual, tfidfVector);
		}
	}
	 
	protected double[] getTFIDF(String sender, ArrayList<EmailInteraction> interactions, Date currDate) throws IOException{
		double[] toReturn = new double[wordList.size()];
		
		for(int i=0; i<interactions.size(); i++){
			
			File tfFile = new File(interactions.get(i).getEmailLocation()+tf_suffix);
			
			BufferedReader in = new BufferedReader(new FileReader(tfFile));
			String line = in.readLine();
			while(line != null){
				
				int splitPos = line.indexOf('\t');
				String word = line.substring(0, splitPos);
				double tf = Double.parseDouble(line.substring(splitPos+1));
				double idf = idfVals.get(word);
				
				double tfidf = tf*idf;
				
				if(isAged){
					double aging = 1.0;
					if(!useHalfLives){
						
						long currDateLong = currDate.getTime();
						long oldestDate;
						if(predictIndividuals){
							oldestDate = getOldestIndividualDate().getTime();
						}else{
							oldestDate = groupTracker.getEarliestDate().getTime();					
						}
						long interactionDate = interactions.get(i).getDate().getTime();
						
						aging = ((double) (interactionDate - oldestDate))/((double) currDateLong - oldestDate);
						
					}else{
						
						long currDateLong = currDate.getTime();	
						long interactionDate = interactions.get(i).getDate().getTime();
						
						double exponent = ((double) currDateLong - interactionDate)/half_life;
						aging = Math.pow(0.5, exponent);
					}
					
					tfidf = aging * tfidf;
				}

				String interactionSender = Account.getSender(interactions.get(i).getEmailLocation());
				boolean wasReceived = (sender == null && interactionSender == null) || (sender != null && sender.equals(interactionSender));
				if(wasReceived){
					tfidf *= w_out;
				}
				
				toReturn[wordList.indexOf(word)] += tfidf;
				
				line = in.readLine();
			}
			in.close();
		}
		
		return toReturn;
	}
}
