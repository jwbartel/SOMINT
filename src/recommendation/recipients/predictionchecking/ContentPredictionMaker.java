package recommendation.recipients.predictionchecking;

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
import java.util.Set;
import java.util.TreeMap;

import javax.mail.MessagingException;

import bus.accounts.Account;
import bus.data.structures.ComparableSet;
import bus.data.structures.DirectedEmailInteraction;
import bus.data.structures.EmailInteraction;
import bus.data.structures.groups.GoogleGroupTracker;
import bus.data.structures.groups.Group;

public class ContentPredictionMaker extends PredictionMaker{
	
	protected static Map<String, Integer> oldWordCounts;
	protected static Date oldDate;
	protected static File oldAccountFolder;
	protected static Boolean oldPredictIndividuals;
	
	public static final String unaged_individual_addressbook = Account.INDIVIDUAL_ADDRESS_BOOK;
	public static final String unaged_grouped_addressbook = Account.GROUP_ADDRESS_BOOK;
	
	
	protected static Map<String, Double> idfVals = new TreeMap<String,Double>();
	protected static ArrayList<String> wordList = new ArrayList<String>();
	
	protected static Map<String, double[]> individualAddressBook = null;
	protected static Map<Group, double[]> groupAddressBook = null;

	static Map<ComparableSet<String>, Double> groupContentSimilarities = new TreeMap<ComparableSet<String>, Double>(); 
	static Map<String, Double> individualContentSimilarities = new TreeMap<String, Double>(); 
	
	
	public ContentPredictionMaker(File accountFolder, Map<String, Integer> wordCounts, Date currDate, Set<String> seed) throws IOException, MessagingException {
		super(accountFolder);
		makePredictions(wordCounts, currDate, seed);
	}

	public ContentPredictionMaker(String accountFolder, Map<String, Integer> wordCounts, Date currDate, Set<String> seed) throws IOException, MessagingException {
		super(accountFolder);
		makePredictions(wordCounts, currDate, seed);
	}
	
	protected void makePredictions(Map<String, Integer> wordCounts, Date currDate, Set<String> seed) throws IOException{
		if(oldAccountFolder != null && oldAccountFolder.equals(accountFolder) && 
				oldWordCounts != null && oldWordCounts.equals(wordCounts) && 
				(!isAged || (oldDate != null && oldDate.equals(currDate))) &&
				oldPredictIndividuals != null && oldPredictIndividuals == predictIndividuals){
			
			return;
			
		}
		
		oldWordCounts = wordCounts;
		oldDate = currDate;
		oldAccountFolder = accountFolder;
		oldPredictIndividuals = predictIndividuals;
			    
		
		
		
		if(predictIndividuals){
			makeIndividualPredictions(wordCounts, currDate,  seed);
		}else{
			makeGroupPredictions(wordCounts, currDate, seed);
		}
	}
	
	public Map<String, Double> getIndividualPredictions(){
		return individualContentSimilarities;
	}
	
	public Map<ComparableSet<String>, Double> getGroupPredictions(){
		return groupContentSimilarities;
	}
	
	protected void makeGroupPredictions(Map<String, Integer> wordCounts, Date currDate, Set<String> seed) throws IOException{
		if(groupTracker == null){
			groupTracker = new GoogleGroupTracker((int)(totalMsgs*Account.TRAINING_RATIO));
			File groupListFile = new File(accountFolder, groups_list);
			groupTracker.load(groupListFile);	
		}
		
		if(isAged  || w_out != 1){
			buildGroupAddressBook(currDate);
		}else{
			if(groupAddressBook == null){
				groupAddressBook = new TreeMap<Group, double[]>();
				File addressbookFile = new File(this.accountFolder, unaged_grouped_addressbook);
				if(addressbookFile.exists()){
					loadGroupAddressBook(addressbookFile);
				}else{
					buildGroupAddressBook(currDate);
					saveGroupAddressBook(addressbookFile);
				}
			}
		}
		
		double[] freqArray = getFreqArray(wordCounts);
		buildGroupContentSimilarities(freqArray, seed);
	}
	
	protected void buildGroupContentSimilarities(double[] currFreqs, Set<String> seed) throws IOException{
		groupContentSimilarities.clear();
		
		Iterator<Group> groups = groupAddressBook.keySet().iterator();
		while(groups.hasNext()){
			Group group = groups.next();
			ComparableSet<String> members = (ComparableSet<String>) group.getMembers();
			if(seed.containsAll(members)){
				continue;
			}
			
			double[] tfidfVector = groupAddressBook.get(group);
			double similarity = similarity(currFreqs, tfidfVector);
			
			if(similarity>0){
				groupContentSimilarities.put(members, similarity);
			}
		}
		
	}
	
	protected void makeIndividualPredictions(Map<String, Integer> wordCounts, Date currDate, Set<String> seed) throws IOException{
		
		if(individualMsgs == null){
			File individualsListFile = new File(accountFolder, individuals_list);
			loadIndividualMsgList(individualsListFile);
		}
		
		if(isAged  || w_out != 1){
			buildIndividualAddressBook(currDate);
		}else{
			if(individualAddressBook == null){
				File addressbookFile = new File(this.accountFolder, unaged_individual_addressbook);
				if(addressbookFile.exists()){
					loadIndividualAddressBook(addressbookFile);
				}else{
					buildIndividualAddressBook(currDate);
					saveIndividualAddressBook(addressbookFile);
				}
			}
		}
		
		double[] freqArray = getFreqArray(wordCounts);
		buildIndividualContentSimilarities(freqArray, seed);
		
	}
	
	protected void buildIndividualContentSimilarities(double[] freqArray, Set<String> seed) throws IOException{
		individualContentSimilarities.clear();
		Iterator<String> individuals = individualMsgs.keySet().iterator();
		while(individuals.hasNext()){
			String individual = individuals.next();
			if(seed.contains(individual)) continue;
			
			double[] tfidfVector = individualAddressBook.get(individual);
			double similarity = similarity(freqArray, tfidfVector);
			
			if(similarity > 0){
				individualContentSimilarities.put(individual, similarity);
			}
		}
		
	}
	
	protected double[] getFreqArray(Map<String, Integer> wordCounts) throws IOException{
		double[] toReturn = new double[wordList.size()];
		
		Iterator<String> words = wordCounts.keySet().iterator();
		while(words.hasNext()){
			String word = words.next();
			
			int pos = wordList.indexOf(word);
			if(pos>=0){
				double val = wordCounts.get(word);
				toReturn[pos] = val;
			}
		}
		
		
		return toReturn;
	}
	
	protected double similarity(double[] vector1, double[] vector2){
		double similarity = 0.0;
		double size1 = 0.0;
		double size2 = 0.0;
		
		for(int i=0; i<vector1.length && i<vector2.length; i++){
			size1 += vector1[i]*vector1[i];
			size2 += vector2[i]*vector2[i];
			similarity += vector1[i]*vector2[i];
		}
		
		size1 = Math.sqrt(size1);
		size2 = Math.sqrt(size2);
		if(similarity == 0.0) return 0.0;
		similarity = similarity/(size1*size2);
		return similarity;
	}
	
	protected void buildGroupAddressBook(Date currDate) throws IOException{
		if(groupAddressBook == null) groupAddressBook = new TreeMap<Group, double[]>();
		groupAddressBook.clear();
		loadIDFs();
		getWordList();
		
		
		Iterator<Group> groups = groupTracker.getAllGroups().iterator();
		while(groups.hasNext()){
			Group group = groups.next();
			
			ArrayList<EmailInteraction> interactions = group.getInteractions();
			double[] tfidfVector = getTFIDF(interactions, currDate);
			
			groupAddressBook.put(group, tfidfVector);
			
		}
	}
	
	protected void buildIndividualAddressBook(Date currDate) throws IOException{
		if(individualAddressBook == null) individualAddressBook = new TreeMap<String, double[]>();
		individualAddressBook.clear();
		loadIDFs();
		getWordList();
		
		Iterator<String> individuals = individualMsgs.keySet().iterator();
		while(individuals.hasNext()){
			String individual = individuals.next();
			
			ArrayList<EmailInteraction> interactions = individualMsgs.get(individual);
			double[] tfidfVector = getTFIDF(interactions, currDate);
			
			individualAddressBook.put(individual, tfidfVector);
		}
	}
	
	protected void saveGroupAddressBook(File dest) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		Iterator<Group> groups = groupAddressBook.keySet().iterator();
		while(groups.hasNext()){
			Group group = groups.next();
			Iterator<String> members = group.getMembers().iterator();
			
			while(members.hasNext()){
				String member = members.next();
				out.write(member);
				out.newLine();
			}
			
			double[] tfidf = groupAddressBook.get(group);
			for(int i=0; i<tfidf.length; i++){
				out.write("\t");
				out.write(""+tfidf[i]);
			}
			out.newLine();
		}
		
		out.flush();
		out.close();
	}
	
	@SuppressWarnings("unchecked")
	protected void loadGroupAddressBook(File src) throws IOException{
		groupAddressBook.clear();
		BufferedReader in = new BufferedReader(new FileReader(src));
		String line = in.readLine();
		
		loadIDFs();
		getWordList();
		
		ComparableSet<String> addresses = new ComparableSet<String>();
		while(line!=null){
			
			if(line.length()>0 && line.charAt(0)=='\t'){
				if(addresses.size()>0){
					String[] vectorStr = line.substring(1).split("\t");
					double[] vector = new double[vectorStr.length];
					for(int i=0; i<vectorStr.length; i++){
						vector[i] = Double.parseDouble(vectorStr[i]);
					}
					Group group = new Group((ComparableSet<String>)addresses.clone());
					groupAddressBook.put(group, vector);
					addresses.clear();
				}
			}else{
				addresses.add(line);
			}
			
			line = in.readLine();
		}
		
		in.close();
		
	}
	
	protected void saveIndividualAddressBook(File dest) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		Iterator<String> individuals = individualAddressBook.keySet().iterator();
		while(individuals.hasNext()){
			String individual = individuals.next();
			out.write(individual);
			out.newLine();
			
			double[] tfidf = individualAddressBook.get(individual);
			for(int i=0; i<tfidf.length; i++){
				out.write("\t");
				out.write(""+tfidf[i]);
			}
			out.newLine();
		}
		
		out.flush();
		out.close();
	}
	
	protected void loadIndividualAddressBook(File dest) throws IOException{
		if(individualAddressBook == null){
			individualAddressBook = new TreeMap<String, double[]>();
		}
		loadIDFs();
		getWordList();
		
		BufferedReader in = new BufferedReader(new FileReader(dest));
		String line = in.readLine();
		String word = "";
		while(line != null){
			if(line.length() > 0 && line.charAt(0)=='\t'){
				line = line.substring(1);
				String[] tfidfStr = line.split("\t");
				double[] tfidf = new double[tfidfStr.length];
				
				
				for(int i=0; i<tfidfStr.length; i++){
					tfidf[i] = Double.parseDouble(tfidfStr[i]);
				}
				
				individualAddressBook.put(word, tfidf);
				
			}else{
				word = line;
			}
			
			line = in.readLine();
		}
	}
	 
	protected double[] getTFIDF(ArrayList<EmailInteraction> interactions, Date currDate) throws IOException{
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
				
				if(((DirectedEmailInteraction) interactions.get(i)).wasReceived()){
					tfidf *= w_out;
				}
				
				toReturn[wordList.indexOf(word)] += tfidf;
				
				line = in.readLine();
			}
			in.close();
		}
		
		return toReturn;
	}
	
	protected void loadIDFs() throws IOException{
		idfVals.clear();
		
		File idfListFile = new File(accountFolder, idf_list);
		
		BufferedReader in = new BufferedReader(new FileReader(idfListFile));
		String line = in.readLine();
		while(line != null){
			
			int tabPt = line.indexOf('\t');
			String word = line.substring(0, tabPt);
			double idf = Double.parseDouble(line.substring(tabPt+1));
			idfVals.put(word, idf);
			
			line =in.readLine();
		}
		
		in.close();
	}
	

	
	protected void getWordList(){
		wordList.clear();
		Iterator<String> words = idfVals.keySet().iterator();
		while(words.hasNext()){
			String word = words.next();
			wordList.add(word);
		}
	}
	
	public static void clearAddressBooks(){
		if(individualAddressBook != null){
			individualAddressBook.clear();
			individualAddressBook = null;
		}
		if(groupAddressBook != null){
			groupAddressBook.clear();
			groupAddressBook = null;
		}
	}
	
	public static void clear(){
		oldWordCounts = null;
		oldDate = null;
		oldAccountFolder = null;
		oldPredictIndividuals = null;
		oldestIndividualDate = null;
		clearAddressBooks();
		clearMsgLists();
		
	}

}
