package recommendation.recipients.systemwide;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;

import recommendation.recipients.predictionchecking.MultispacePredictionMaker;
import recommendation.recipients.predictionchecking.PredictionMaker;


public class SystemwideMultispacePredictionMaker extends
		MultispacePredictionMaker {
	
	public SystemwideMultispacePredictionMaker(File accountsFolder, String sender, Set<String> seed, Date currDate, Map<String, Integer> wordCounts) throws IOException, MessagingException {
		super(accountsFolder, sender, seed, currDate, wordCounts);
		makePredictions(accountsFolder, sender, seed, currDate, wordCounts);
	}
	
	public SystemwideMultispacePredictionMaker(String accountsFolder, String sender, Set<String> seed, Date currDate, Map<String, Integer> wordCounts) throws IOException, MessagingException {
		super(accountsFolder, sender, seed, currDate, wordCounts);
		makePredictions(new File(accountsFolder), sender, seed, currDate, wordCounts);
	}
	protected void makePredictions(File accountFolder, Set<String> seed, Date currDate, Map<String, Integer> wordCounts) throws IOException, MessagingException{
		//Empty to prevent unnecessary computation in super class
	}
	
	protected void makePredictions(File accountFolder, String sender, Set<String> seed, Date currDate, Map<String, Integer> wordCounts) throws IOException, MessagingException{
		if((!useSocialConnections || PredictionMaker.group_algorithm == PredictionMaker.TOP_CONTACT_SCORE) 
				&& (!useTextContent || PredictionMaker.content_importance == 0.0)){
			
			if(timeAndDir == null || !timeAndDir.getDate().equals(currDate)){
				timeAndDir = new SystemwideTimeAndDirectionPredictionMaker(accountFolder, sender, currDate);
				timeAndDir.close();
			}
		}else{
			if(useSocialConnections && PredictionMaker.group_algorithm != PredictionMaker.TOP_CONTACT_SCORE){
				socialConnection = new SystemwideSocialConnectionPredictionMaker(accountFolder, sender, seed, currDate);
				socialConnection.close();
			}
			if(useTextContent && PredictionMaker.content_importance != 0.0 ){
				content = new SystemwideContentPredictionMaker(accountFolder, sender, wordCounts, currDate, seed);
				content.close();
			}
		}
		
		if(useSocialConnections && PredictionMaker.group_algorithm != PredictionMaker.TOP_CONTACT_SCORE && useTextContent && PredictionMaker.content_importance != 0.0){
			if(predictIndividuals){
				makeIndividualPredictions();
			}else{
				makeGroupPredictions();
			}
		}
	}

}
