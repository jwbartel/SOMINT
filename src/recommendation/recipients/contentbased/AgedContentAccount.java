package recommendation.recipients.contentbased;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.mail.MessagingException;

import recommendation.recipients.groupbased.google.GoogleAccount;
import bus.accounts.Account;
import bus.data.parsers.MessageFrequencyParser;

public class AgedContentAccount extends GoogleAccount{
	
	public static final String tf_suffix = Account.FREQ_TF_SUFFIX;
	public static final String idf_list = Account.FREQ_IDF_LIST;
	
	Map<String, double[]> addressBook = new TreeMap<String, double[]>();
	
	int totalDocs = 0;
	Map<String, Integer> docsWithWord = new TreeMap<String, Integer>();
	
	
	public AgedContentAccount(String accountFolder) throws IOException, MessagingException{
		super(accountFolder);
	}
	
	public AgedContentAccount(File accountFolder) throws IOException, MessagingException{
		super(accountFolder);
	}
	
	public void experimentalTraining() throws IOException, SQLException{
		long start = System.currentTimeMillis();
		
		int trainedMsgs = (int)(totalMsgs*Account.TRAINING_RATIO);
		for(int i=0; i<trainedMsgs; i++){
			try {
				getNextMessage();
				processCurrMessage();
				super.trainCurrMessage();
			} catch (MessagingException e) {
				throw new RuntimeException(currMessage);
			}
		}
		writeIDFs();
		System.out.println("normalized TF-IDF vectors in "+(System.currentTimeMillis()-start)+" ms");
	}
	
	protected void processCurrMessage() throws IOException{
		MessageFrequencyParser parser = new MessageFrequencyParser(currMessage);
		File tfidf = new File(currMessage+tf_suffix);
		
		Map<String, Integer> freqs = parser.getFreqWordsWithCounts();//parser.getAllWordsWithCounts();
		
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
			
			double tf = getTF(freqs.get(word), parser.getWordCount());
			
			if(docsWithWord.containsKey(word)){
				docsWithWord.put(word, docsWithWord.get(word)+1);
			}else{
				docsWithWord.put(word, 1);
			}
			
			out.write(word+"\t"+tf);
		}
		
		totalDocs++;
		
		out.flush();
		out.close();
	}
	
	
	
	protected double getTF(int freq, int totalWordsInDoc){
		return ((double) freq)/(double) totalWordsInDoc;	
	}
	
	
	protected void writeIDFs() throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(this.accountFolder, idf_list)));
		Iterator<String> words = docsWithWord.keySet().iterator();
		while(words.hasNext()){
			String word = words.next();
			double idf = getIDF(word);
			
			out.write(word+"\t"+idf);
			out.newLine();
		}
		out.flush();
		out.close();
	}
	
	private double getIDF(String word){
		double toLog = ((double) totalDocs)/((double) docsWithWord.get(word));
		return Math.log10(toLog);
	}
	
	protected static Set<String> ignoredAccounts = new TreeSet<String>();
	
	protected static void buildIgnoredAccounts(){
		ignoredAccounts.clear();
		ignoredAccounts.add("kaminski-v");
	}
	
	public static void main(String[] args) throws IOException, MessagingException, SQLException{
		buildIgnoredAccounts();
		File folder = new File("/home/bartizzi/Research/Enron Accounts");
		File[] accounts = folder.listFiles();
		Arrays.sort(accounts);
		
		boolean start = false;
		
		for(int i=0; i<accounts.length; i++){
			
			if(accounts[i].getName().equals("allen-p")) start = true;
			
			if(ignoredAccounts.contains(accounts[i].getName())){
				continue;
			}
			
			if(!start) continue;
			
			System.out.print(accounts[i].getName()+"...");
			AgedContentAccount account = new AgedContentAccount(accounts[i]);
			account.experimentalTraining();
		}
	}
}
