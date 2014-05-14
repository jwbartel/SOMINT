package recommendation.recipients.old.predictionchecking.hierarchical;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;

import recommendation.recipients.old.predictionchecking.ContentPredictionMaker;
import recommendation.recipients.old.predictionchecking.MultispacePredictionMaker;
import recommendation.recipients.old.predictionchecking.SocialConnectionPredictionMaker;
import recommendation.recipients.old.predictionchecking.TimeAndDirectionPredictionMaker;


public class HierarchicalMultispacePredictionMaker extends MultispacePredictionMaker {

	public HierarchicalMultispacePredictionMaker(){
		
	}
	
	public HierarchicalMultispacePredictionMaker(File accountFolder, String sender, Set<String> seed, Date currDate, Map<String, Integer> wordCounts) throws IOException, MessagingException {
		super(accountFolder, sender, seed, currDate, wordCounts);
	}

	public HierarchicalMultispacePredictionMaker(String accountFolder, String sender, Set<String> seed, Date currDate, Map<String, Integer> wordCounts) throws IOException, MessagingException {
		super(accountFolder, sender, seed, currDate, wordCounts);
	}
	
	protected void makePredictions(File accountFolder, String sender, Set<String> seed, Date currDate, Map<String, Integer> wordCounts) throws IOException, MessagingException{
		if(!(useSocialConnections || useTextContent)){
			if(timeAndDir == null || !timeAndDir.getDate().equals(currDate)){
				timeAndDir = new HierarchicalTimeAndDirectionPredictionMaker(accountFolder, currDate);
				timeAndDir.close();
			}
		}else{
			if(useSocialConnections){
				socialConnection = new HierarchicalSocialConnectionPredictionMaker(accountFolder, sender, seed, currDate);
				socialConnection.close();
			}
			if(useTextContent){
				content = new HierarchicalContentPredictionMaker(accountFolder, wordCounts, currDate, seed);
				content.close();
			}
		}
		
		if(useSocialConnections && useTextContent){
			makeIndividualPredictions();
			makeGroupPredictions();
		}
	}
	
	public static void clear(){
		clearMsgLists();
		if(timeAndDir != null ) timeAndDir = null;
		TimeAndDirectionPredictionMaker.clearMsgLists();
		ContentPredictionMaker.clear();
		SocialConnectionPredictionMaker.clear();		
	}

}
