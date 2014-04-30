package recipients.systemwide;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;

import recipients.predictionchecking.ContentPredictionMaker;
import recipients.predictionchecking.MultispacePredictionMaker;
import recipients.predictionchecking.PredictionAcceptanceModeler;
import recipients.predictionchecking.PredictionMaker;

import bus.accounts.Account;
import bus.data.parsers.MessageFrequencyParser;

public class SystemwidePredictionAcceptanceModeler extends PredictionAcceptanceModeler{
	
	public final static int GROUP_ALG_START = PredictionMaker.SUBSET_GROUP_COUNT, 
							GROUP_ALG_END = PredictionMaker.TOP_CONTACT_SCORE, 
							GROUP_ALG_STEP = 1;
	
	public final static double 	CONTENT_IMP_START = 0.0, 
								CONTENT_IMP_END = 0.0, 
								CONTENT_IMP_STEP = 0.5;
	
	public final static int HALF_LIFE_START = PredictionMaker.ONE_HOUR, 
							HALF_LIFE_END = PredictionMaker.ONE_YEAR, 
							HALF_LIFE_STEP = 1;
	
	public final static double 	W_OUT_START = 0.25, 
								W_OUT_END = 4.0, 
								W_OUT_STEP = 2;
	
	public SystemwidePredictionAcceptanceModeler(File accountsFolder) throws IOException, MessagingException {
		super(accountsFolder);
	}
	
	public SystemwidePredictionAcceptanceModeler(String accountsFolder) throws IOException, MessagingException {
		super(accountsFolder);
	}
	
	protected void modelPredictionsForCurrMessage(Set<String> seed, ArrayList<String> correctOrderedEmails) throws IOException, MessagingException{
		if(correctOrderedEmails.size() == 0) return;
		
		String sender = Account.getSender(currMessage);
		
		Map<String,Integer> wordCounts = null;
		if(PredictionMaker.useTextContent){
			MessageFrequencyParser parser = new MessageFrequencyParser(currMessage);
			wordCounts = parser.getAllWordsWithCounts();
		}
		
		
		while(correctOrderedEmails.size() > 0){
			PredictionMaker predictionMaker = new SystemwideMultispacePredictionMaker(accountFolder, sender, seed, currDate, wordCounts);
			checkCurrPredictions(predictionMaker, correctOrderedEmails, seed);
			predictionMaker.close();
		}
	}
	
	public static void main(String[] args) throws IOException, MessagingException{
		File accountsFolder = new File("/home/bartizzi/Research/Enron Accounts");
		//File accountsFolder = new File("/home/bartizzi/Shared/Dropbox/Sample Enron Accounts");

		for(int group_algorithm=GROUP_ALG_START; group_algorithm <= GROUP_ALG_END; group_algorithm += GROUP_ALG_STEP){
			PredictionMaker.group_algorithm = group_algorithm;
			
			for(double content_importance = CONTENT_IMP_START; content_importance <= CONTENT_IMP_END; content_importance += CONTENT_IMP_STEP){
				PredictionMaker.content_importance = content_importance;
				PredictionMaker.connection_importance = 1 - content_importance;
				
				for(double w_out = W_OUT_START; w_out <= W_OUT_END; w_out *= W_OUT_STEP){
					PredictionMaker.w_out = w_out;

					if(PredictionMaker.useHalfLives){
						
						for(int half_life = HALF_LIFE_START; half_life <= HALF_LIFE_END; half_life += HALF_LIFE_STEP){
							PredictionMaker.setHalfLife(half_life);
							
							String groupAlgorithmName = PredictionMaker.getGroupAlgorithmName(group_algorithm);
							String halfLifeName = PredictionMaker.getHalfLifeTitle(half_life);
							
							System.out.print(groupAlgorithmName + "," + content_importance + "," + w_out + "," + halfLifeName);
							
							PredictionAcceptanceModeler modeler = new SystemwidePredictionAcceptanceModeler(accountsFolder);
							int[] results = modeler.modelPredictionAcceptances();
							for(int j=0; j<results.length; j++){
								System.out.print(","+results[j]);
							}
							modeler.close();
							
							System.out.println();
						}
					}else{
						
						String groupAlgorithmName = PredictionMaker.getGroupAlgorithmName(group_algorithm);
						
						System.out.print(groupAlgorithmName + "," + content_importance + "," + w_out);
						
						PredictionAcceptanceModeler modeler = new SystemwidePredictionAcceptanceModeler(accountsFolder);
						int[] results = modeler.modelPredictionAcceptances();
						for(int j=0; j<results.length; j++){
							System.out.print(","+results[j]);
						}
						modeler.close();
						
						System.out.println();
						
					}
					
					ContentPredictionMaker.clearAddressBooks();
				}
			}
		}
		System.out.println();
		
		MultispacePredictionMaker.clear();
		
	}
	
}
