package recipients.predictionchecking.hierarchical;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.MessagingException;

import recipients.predictionchecking.MultispacePredictionMaker;
import recipients.predictionchecking.PredictionAcceptanceModeler;
import recipients.predictionchecking.PredictionMaker;

import bus.accounts.Account;
import bus.data.parsers.MessageFrequencyParser;
import bus.data.structures.AddressLists;

public class HierarchicalPredictionAcceptanceModeler extends 	PredictionAcceptanceModeler {


	protected int listsWithCorrectPrediction = 0;
	protected int nonEmptyListsGenerated = 0;
	protected int emptyListsGenerated = 0;
	protected int listsWithMoreThanOneSelection = 0;
	
	
	public HierarchicalPredictionAcceptanceModeler(File accountFolder) throws IOException, MessagingException {
		super(accountFolder);
	}
	
	public HierarchicalPredictionAcceptanceModeler(String accountFolder) throws IOException, MessagingException {
		super(accountFolder);
	}
	
	public int[] modelPredictionAcceptances() throws IOException, MessagingException{
		
		int trainedMsgs = (int) (totalMsgs * TRAINING_RATIO);
		int i=0;
		for(; i<trainedMsgs; i++){
			getNextMessage();
		}
		
		
		for(; i<totalMsgs; i++){
			getNextMessage();
			
			ArrayList<String> correctOrderedEmails = getOrderedEmails(currMessage);//getOrderedEmails(getCurrMessage());

			Set<String> seed = new TreeSet<String>();
			while(correctOrderedEmails.size() >0 && seed.size() < INITIAL_SEED_SIZE){
				String seedVal = correctOrderedEmails.get(0);
				seed.add(seedVal);
				while(correctOrderedEmails.remove(seedVal)){}
			}
			
			addressesUnknown += correctOrderedEmails.size();

			File addressFile = new File(currMessage+Account.ADDR_FILE_SUFFIX);
			if(!addressFile.exists()){
				Account.saveAddresses(new File(currMessage), addressFile);
			}
			
			AddressLists addressLists = new AddressLists(addressFile);
			String from;
			if(addressLists.getFrom().size() > 0){
				from = addressLists.getFrom().get(0);
			}else{
				from = null;
			}
			
			modelPredictionsForCurrMessage(from, seed, correctOrderedEmails);
			
		}

		int[] toReturn = new int[6];
		toReturn[0] = addressesRight;
		toReturn[1] = addressesUnknown;
		toReturn[2] = listsWithCorrectPrediction;
		toReturn[3] = nonEmptyListsGenerated;
		toReturn[4] = emptyListsGenerated;
		toReturn[5] = listsWithMoreThanOneSelection;
		return toReturn;
		
	}
	
	protected void modelPredictionsForCurrMessage(String sender, Set<String> seed, ArrayList<String> correctOrderedEmails) throws IOException, MessagingException{

		
		Map<String,Integer> wordCounts = null;
		if(PredictionMaker.useTextContent){
			MessageFrequencyParser parser = new MessageFrequencyParser(currMessage);
			wordCounts = parser.getAllWordsWithCounts();
		}
		
		while(correctOrderedEmails.size() > 0){

			MultispaceIndividualsWithTopGroup predictionMaker = new MultispaceIndividualsWithTopGroup(accountFolder, sender, seed, currDate, wordCounts);
			checkCurrPredictions(predictionMaker, correctOrderedEmails, seed);
		}
	}
	
	int largestMatchSize = 0;
	Set<String> largestMatchGroup = null;
	
	private void checkCurrPredictions(MultispaceIndividualsWithTopGroup predictionMaker, ArrayList<String> correctOrderedEmails, Set<String> seed){
		largestMatchSize = 0;
		largestMatchGroup = null;
		
		ArrayList<Prediction> predictionsList = predictionMaker.getPredictionsList().getValues();
		if(predictionsList.size() > 0){
			nonEmptyListsGenerated++;
		}else{
			emptyListsGenerated++;
		}
		for(int i=0; i<predictionsList.size(); i++){
			findLargestMatch(predictionsList.get(i), correctOrderedEmails);
		}
		
		if(largestMatchGroup != null){
			
			if(largestMatchGroup.size()>1){
				listsWithMoreThanOneSelection++;
			}
			
			listsWithCorrectPrediction++;
			addressesRight += largestMatchGroup.size();
			
			seed.addAll(largestMatchGroup);
			correctOrderedEmails.removeAll(largestMatchGroup);
		}else{
			seed.add(correctOrderedEmails.get(0));
			correctOrderedEmails.remove(0);
		}
		
		int x = 0;
		x++;
		
	}
	
	private void findLargestMatch(Prediction prediction, ArrayList<String> correctOrderedEmails){
		if(prediction.getSize() > largestMatchSize){
			Set<String> members = prediction.getMembers();
			if(correctOrderedEmails.containsAll(members)){
				largestMatchSize = members.size();
				largestMatchGroup = members;
			}else if(prediction instanceof GroupPrediction){
				ArrayList<Prediction> predictionsList = ((GroupPrediction) prediction).getValues();
				for(int i=0; i<predictionsList.size(); i++){
					findLargestMatch(predictionsList.get(i), correctOrderedEmails);
				}
			}
		}
	}
	
	
	public static void main(String[] args) throws IOException, MessagingException{
		System.out.println("hierarchical and intersection, weighted score");
		buildIgnoredAccounts();
		
		double[] w_outVals = {0.25, 0.5, 1.0, 2.0, 4.0};
		int[] half_lifeVals = {PredictionMaker.ONE_HOUR, PredictionMaker.ONE_DAY, PredictionMaker.FOUR_WEEKS, PredictionMaker.SIX_MONTHS, PredictionMaker.TWO_YEARS};//{PredictionMaker.ONE_WEEK, PredictionMaker.ONE_YEAR};
		
		File folder = new File("D:\\Enron data\\extracted precomputes");//("/home/bartizzi/Research/Enron Accounts");
		File[] accounts = folder.listFiles();
		Arrays.sort(accounts);
		
		boolean start = false;
		for(int i=0; i<accounts.length; i++){
			
			
			
			if(accounts[i].getName().equals("arnold-j")) start = true;
			if(!accounts[i].isDirectory()) continue;
			
			if(ignoredAccounts.contains(accounts[i].getName())) continue;
			
			if(!start) continue;
			//if((accounts[i].getName().equals("perlingiere-d"))) return;
			
			
			System.out.print(accounts[i].getName());
			//for(int group_algorithm=5; group_algorithm <=5; group_algorithm++){
				//PredictionMaker.group_algorithm = group_algorithm;
			    PredictionMaker.group_algorithm = PredictionMaker.COMBINED_WEIGHTED_SCORE;
			    
			    for(double intersectionImportance=0.25; intersectionImportance <= 1.0; intersectionImportance += .25){
			    	PredictionMaker.relative_intersection_importance = intersectionImportance;
				    
					//for(double w_out=0.25; w_out <= 2.0; w_out *= 2.0){
					//	PredictionMaker.w_out = w_out;
						//PredictionMaker.w_out = 1.0;
		    		for(int half_lifePos = 0; half_lifePos < half_lifeVals.length; half_lifePos++){
		    			PredictionMaker.setHalfLife(half_lifeVals[half_lifePos]);
			    		
					
						//for(int half_life = 1; half_life <= 5; half_life++){
						//	PredictionMaker.setHalfLife(half_life);
							//PredictionMaker.setHalfLife(PredictionMaker.ONE_WEEK);
				    	for(int w_outPos=0; w_outPos < w_outVals.length; w_outPos++){
				    		PredictionMaker.w_out = w_outVals[w_outPos];
					
							HierarchicalPredictionAcceptanceModeler modeler = new HierarchicalPredictionAcceptanceModeler(accounts[i]);
							int[] results = modeler.modelPredictionAcceptances();
							for(int j=0; j<results.length; j++){
								System.out.print(","+results[j]);
							}
							modeler.close();
							//System.out.print(" "+j);
						}
					}
				}
			//}
			System.out.println();
			
			MultispacePredictionMaker.clear();
		}
	}
}
