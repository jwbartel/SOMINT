package recipients.contentbased;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.mail.MessagingException;

import recipients.contentbased.SimpleTFIDFAccount;
import recipients.groupbased.GroupAccount;
import recipients.predictionchecking.TopGroupPrediction;

import bus.accounts.Account;
import bus.accounts.FileNameByOS;
import bus.data.parsers.MessageFrequencyParser;
import bus.data.structures.AddressLists;
import bus.data.structures.ComparableSet;
import bus.data.structures.groups.GoogleGroupTracker;
import bus.data.structures.groups.Group;
import bus.tools.EmailAccountAdjuster;

public class SimpleTFIDFAccount extends GroupAccount {
	static String msg_list =  Account.ALL_MSGS_ADAPTED;
	static String addr_book = Account.INDIVIDUAL_ADDRESS_BOOK;
	static String groups_list = Account.ADAPTED_GROUPS_LIST;
	static String pred_suffix = Account.EMAIL_TFIDF_PREDICTION_FILE_SUFFIX;
	

	public SimpleTFIDFAccount(){
		
	}
	
	public SimpleTFIDFAccount(String accountFolder) throws IOException {
		super(accountFolder);
	}
	
	public SimpleTFIDFAccount(File accountFolder) throws IOException {
		super(accountFolder);
	}


	protected void init(File accountFolder) throws IOException{
				
		this.accountFolder = accountFolder;
		
		in = new BufferedReader(new FileReader(new File(accountFolder,msg_list)));
		totalMsgs = Integer.parseInt(in.readLine());
		
		groupTracker = new GoogleGroupTracker( (int) (totalMsgs*Account.TRAINING_RATIO));
	}
	
	public void experimentalTraining() throws IOException, SQLException, MessagingException{
				
		long start = System.currentTimeMillis();
		
		//int trainedMsgs = (int)(totalMsgs*Account.TRAINING_RATIO);
		for(int i=0; i<totalMsgs; i++){
			getNextMessage();
			processCurrMessage();
		}
		
		EmailAccountAdjuster.buildWordList(accountFolder);
		
		System.out.println("normalized TF-IDF vectors in "+(System.currentTimeMillis()-start)+" ms");
	}
	
	protected void processCurrMessage() throws IOException{
		MessageFrequencyParser parser = new MessageFrequencyParser(currMessage);
		File tfidf = new File(currMessage+Account.TFIDF_SUFFIX);
		
		//Map<String, Integer> freqs = parser.getFreqWordsWithCounts(); 
		Map<String, Integer> freqs = parser.getAllWordsWithCounts();
		
		/*Iterator<String> words = freqs.keySet().iterator();
		long total = 0;
		while(words.hasNext()){
			String word = words.next();
			int freq = freqs.get(word);
			total += freq*freq;
		}
		double size = Math.sqrt(total);*/
		
		Iterator<String> words = freqs.keySet().iterator();
		BufferedWriter out = new BufferedWriter(new FileWriter(tfidf));
		boolean isFirst = true;
		while(words.hasNext()){
			String word = words.next();
			
			if(isFirst){
				isFirst = false;
			}else{
				out.newLine();
			}
			
			int val = freqs.get(word);
			out.write(word+"\t"+val);
		}
		
		out.flush();
		out.close();
		
		//out.close();
		
	}
	
	private Map<String, Integer> wordIndices;
	public void experimentalPredictions() throws IOException, MessagingException{
		//Find the indices of each word in the TF-IDF vectors
		buildWordIndices();
		

		long start = System.currentTimeMillis();
		
		//Build the address book if it doesn't exist already
		File addressBookFile = new File(accountFolder, addr_book);
		if(!addressBookFile.exists()){
			buildAddressBook();
		}
		System.out.println("created address book in "+(System.currentTimeMillis()-start)+" ms");
			
		
		/*int trainedMessages = (int) (totalMsgs*Account.TRAINING_RATIO);
		int predictedMessages = totalMsgs - trainedMessages;
		
		long start = System.currentTimeMillis();
		
		runPredictions();

		System.out.println("made predictions on "+predictedMessages+" in "+(System.currentTimeMillis()-start)+" ms");*/
	}

	
	private void buildWordIndices() throws IOException{
		int wordCount = 0;
		wordIndices = new TreeMap<String,Integer>();
		BufferedReader in = new BufferedReader(new FileReader(new File(accountFolder, Account.WORDS_LIST)));
		String line = in.readLine();
		while(line != null){
			wordIndices.put(line, wordCount);
			wordCount++;
			
			line = in.readLine();
		}
		
		//System.out.println(wordIndices.size());
	}
	
	private void buildAddressBook() throws IOException, MessagingException{
		//The combined normalized TF-IDF vector for each recipient
		Map<String, double[]> vectors = new TreeMap<String, double[]>();
		
		GoogleGroupTracker groupTracker = new GoogleGroupTracker(totalMsgs);
		groupTracker.load(new File(accountFolder,groups_list));
		
		Set<Group> groups = groupTracker.getAllGroups();
		//Set<String> allAddresses = new TreeSet<String>();
		
		Iterator<Group> groupIter = groups.iterator();
		while(groupIter.hasNext()){
			Group group = groupIter.next();
			Iterator<String> addrIter = group.getMembers().iterator();
			while(addrIter.hasNext()){
				String address = addrIter.next();
				if(!vectors.containsKey(address)){
					vectors.put(address, new double[wordIndices.size()]);
				}
			}
			//vectors.put(group, new double[wordIndices.size()]);
		}
		
		int trainedMessages = (int) (totalMsgs*Account.TRAINING_RATIO);
		
		for(int i=0; i<trainedMessages; i++){
			getNextMessage();
			
			String msgName = FileNameByOS.getMappedFileName(currMessage);
			
			//Get the addresses for each message
			File addrFile = new File(msgName+Account.ADDR_FILE_SUFFIX);
			if(!addrFile.exists()){
				Account.saveAddresses(new File(msgName), addrFile);
			}
			
			AddressLists addrLists = new AddressLists(addrFile);
			Set<String> addresses = addrLists.getAll();
			//Set<Group> groupsInMsg = groupTracker.findMatchingGroups(addresses);
			
			//Sum the normalized TF-IDF for each recipient included in the message
			BufferedReader tfidf = new BufferedReader(new FileReader(msgName+Account.TFIDF_SUFFIX));
			String val = tfidf.readLine();
			while(val != null){
				int splitPt = val.lastIndexOf('\t');
				String word = val.substring(0,splitPt);
				double size = Double.parseDouble(val.substring(splitPt+1));
				int index =-1;
				try{
					index = wordIndices.get(word);
				}catch(NullPointerException e){

					val = tfidf.readLine();
					continue;
				}
				
				Iterator<String> iter = addresses.iterator(); //groupsInMsg.iterator();
				while(iter.hasNext()){
					String address = iter.next();
					double[] vector = vectors.get(address);
					if(vector==null){
						vector = new double[wordIndices.size()];
						vectors.put(address, vector);
					}
					vector[index] += size;
				}
				
				
				val = tfidf.readLine();
			}
			tfidf.close();
			
			
		}
		
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(accountFolder, addr_book)));
		Iterator<String> addrIter = vectors.keySet().iterator();
		while(addrIter.hasNext()){
			String addr = addrIter.next();
			double[] vector = vectors.get(addr);
			
			out.write(addr);
			out.newLine();
			
			for(int i=0; i<vector.length; i++){
				out.write("\t"+vector[i]);
			}
			out.newLine();
		}
		out.flush();
		out.close();
	}
	
	protected void runPredictions() throws IOException, MessagingException{
		int trainedMessages = (int) (totalMsgs*Account.TRAINING_RATIO);
		
		int i=0;
		for(; i<trainedMessages; i++){
			getNextMessage();
		}
		
		for(; i<totalMsgs; i++){
			getNextMessage();
			predictCurrMessage();
		}
	}
	
	private void predictCurrMessage() throws IOException{
		double[] vector = getCurrTFIDFVector();
		
		double maxVal = 0.0;
		
		BufferedReader in = new BufferedReader(new FileReader(new File(accountFolder, addr_book)));
		String line = in.readLine();
		Set<TopGroupPrediction> predictions = new TreeSet<TopGroupPrediction>();
		while(line != null){
			TreeSet<String> addresses = new TreeSet<String>();
			
			while(line!=null && line.length()>0 && line.charAt(0)!='\t'){
				addresses.add(line);
				line = in.readLine();
			}
			
			if(line == null){
				continue;
			}
			
			line = line.substring(1);
			String[] vals = line.split("\t");
			double[] comparisonVector = new double[vals.length];
			
			for(int i=0; i<vals.length; i++){
				comparisonVector[i] = Double.parseDouble(vals[i]);
			}
			
			double similarity = cosineSimilarity(vector, comparisonVector);
			maxVal += similarity;
			
			if(similarity>0){
				TopGroupPrediction prediction = new TopGroupPrediction(new ComparableSet<String>(addresses), similarity);
				predictions.add(prediction);
			}
			
			line = in.readLine();
		}
		in.close();
		
		BufferedWriter out = new BufferedWriter(new FileWriter(currMessage+pred_suffix));
		out.write(""+maxVal);
		out.newLine();
		Iterator<TopGroupPrediction> iter = predictions.iterator();
		while(iter.hasNext()){
			out.write(iter.next().toString());
			out.newLine();
		}
		out.flush();
		out.close();
		
	}
	
	public static double cosineSimilarity(double[] vector1, double[] vector2){
		if(vector1.length != vector2.length){
			throw new RuntimeException("Non-matching vectors!");
		}
		
		double size1 = 0;
		double size2 = 0;
		double dotProduct = 0;
		
		for(int i=0; i<vector1.length; i++){
			size1 += vector1[i]*vector1[i];
			size2 += vector2[i]*vector2[i];
			dotProduct += vector1[i]*vector2[i];
		}
		
		size1 = Math.sqrt(size1);
		size2 = Math.sqrt(size2);
		
		if(size1==0.0 || size2==0.0){
			return 0;
		}
		
		return dotProduct/(size1*size2);
	}
	
	private double[] getCurrTFIDFVector() throws IOException{
		double[] toReturn = new double[wordIndices.size()];
		BufferedReader in = new BufferedReader(new FileReader(new File(currMessage+Account.NORMALIZED_TFIDF_SUFFIX)));
		
		String line = in.readLine();
		while(line != null){
			
			int splitPt = line.lastIndexOf('\t');
			String word = line.substring(0,splitPt);
			double size = Double.parseDouble(line.substring(splitPt+1));
			try{
				int index = wordIndices.get(word);
				
				toReturn[index] = size;
			}catch(NullPointerException e){}
			
			line = in.readLine();
		}
		
		return toReturn;
	}
	
	static Set<String> ignoredAccounts = new HashSet<String>();
	
	protected static void createIgnoredAccountsList(){
		ignoredAccounts.add("beck-s");
		ignoredAccounts.add("campbell-l");
		ignoredAccounts.add("dasovich-j");
		ignoredAccounts.add("farmer-d");
		ignoredAccounts.add("fossum-d");
		ignoredAccounts.add("guzman-m");
		ignoredAccounts.add("haedicke-m");
		ignoredAccounts.add("hain-m");
		ignoredAccounts.add("hyvl-d");
		ignoredAccounts.add("kaminski-v");
		ignoredAccounts.add("kitchen-l");
		ignoredAccounts.add("lay-k");
		ignoredAccounts.add("lokay-m");
		ignoredAccounts.add("nemec-g");
		ignoredAccounts.add("sager-e");
		ignoredAccounts.add("farmer-d");
		ignoredAccounts.add("sanders-r");
		ignoredAccounts.add("shackleton-s");
		ignoredAccounts.add("farmer-d");
		ignoredAccounts.add("shapiro-r");
		ignoredAccounts.add("steffes-j");
		ignoredAccounts.add("taylor-m");
	}
	
	static Set<String> badAccounts = new HashSet<String>();
	
	protected static void createBadAccountsList() throws IOException{
		BufferedReader in = new BufferedReader(new FileReader("/home/bartizzi/Research/group results/bad_accounts.txt"));
		String line = in.readLine();
		while(line != null){
			badAccounts.add(line);
			line = in.readLine();
		}
		in.close();
	}
	
	public static void main(String[] args) throws IOException, MessagingException, SQLException{
		//System.out.println("GROUP ADDRESS BOOK");
		/*SimpleTFIDFAccount account = new SimpleTFIDFAccount("/home/bartizzi/Research/Enron Accounts/germany-c");
		account.currMessage = "/home/bartizzi/Research/Enron Accounts/germany-c/ExMerge - Germany, Chris/Bankrupt/Cleburne/Lone Star/75";

		account.buildWordIndices();
		account.processCurrMessage();
		account.buildWordIndices();
		account.predictCurrMessage();*/
		
		MessageFrequencyParser.loadData();		
		createIgnoredAccountsList();

		File folder = new File("/home/bartizzi/Research/Enron Accounts");
		File[] accounts = folder.listFiles();
		Arrays.sort(accounts);
		
		
		boolean start = false;
		for(int i=0; i<accounts.length; i++){
			if(accounts[i].getName().equals("allen-p")){
				start = true;
			}
			
			//if(!accounts[i].getName().equals("arora-h")) break;

			if(!start) continue;
			
			if(ignoredAccounts.contains(accounts[i].getName())){
				System.out.println(accounts[i].getName());
				continue;
			}
			
			//if(!badAccounts.contains(accounts[i].getName())){
			//	System.out.println(accounts[i].getName());
			//	continue;
			//}
			
			
			System.out.print(accounts[i].getName()+"...");
			SimpleTFIDFAccount account = new SimpleTFIDFAccount(accounts[i]);
			account.experimentalTraining();
			//account.experimentalPredictions();
			account.close();
			
			
		}
	}
}
