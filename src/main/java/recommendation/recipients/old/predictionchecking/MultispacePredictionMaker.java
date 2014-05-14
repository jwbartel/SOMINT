package recommendation.recipients.old.predictionchecking;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.mail.MessagingException;

import bus.accounts.Account;
import bus.data.structures.ComparableSet;
import bus.data.structures.groups.GoogleGroupTracker;
import bus.data.structures.groups.Group;

public class MultispacePredictionMaker extends PredictionMaker {
	
	protected static TimeAndDirectionPredictionMaker timeAndDir;
	protected SocialConnectionPredictionMaker socialConnection;
	protected ContentPredictionMaker content;
	

	protected Map<ComparableSet<String>, Double> groupSimilarities = new TreeMap<ComparableSet<String>, Double>(); 
	protected Map<String, Double> individualSimilarities = new TreeMap<String, Double>(); 
	
	public MultispacePredictionMaker(){
		
	}
	
	public MultispacePredictionMaker(File accountFolder, String sender, Set<String> seed, Date currDate, Map<String, Integer> wordCounts) throws IOException, MessagingException {
		super(accountFolder);
		makePredictions(accountFolder, sender, seed, currDate, wordCounts);
	}

	public MultispacePredictionMaker(String accountFolder, String sender, Set<String> seed, Date currDate, Map<String, Integer> wordCounts) throws IOException, MessagingException {
		super(accountFolder);
		makePredictions(new File(accountFolder), sender, seed, currDate, wordCounts);
	}
	
	protected void makePredictions(File accountFolder,String sender, Set<String> seed, Date currDate, Map<String, Integer> wordCounts) throws IOException, MessagingException{
		if((!useSocialConnections || PredictionMaker.group_algorithm == PredictionMaker.TOP_CONTACT_SCORE) 
				&& (!useTextContent || PredictionMaker.content_importance == 0.0)){
			
			if(timeAndDir == null || !timeAndDir.getDate().equals(currDate)){
				timeAndDir = new TimeAndDirectionPredictionMaker(accountFolder, currDate);
				timeAndDir.close();
			}
		}else{
			if(useSocialConnections && PredictionMaker.group_algorithm != PredictionMaker.TOP_CONTACT_SCORE){
				socialConnection = new SocialConnectionPredictionMaker(accountFolder, sender, seed, currDate);
				socialConnection.close();
			}
			if(useTextContent && PredictionMaker.content_importance != 0.0 ){
				content = new ContentPredictionMaker(accountFolder, wordCounts, currDate, seed);
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
	
	protected void makeIndividualPredictions() throws IOException{
		combineIndividualContentAndSocial();
	}
	
	private void combineIndividualContentAndSocial() throws IOException{
		individualSimilarities.clear();
		
		if(individualMsgs == null){
			File individualsListFile = new File(accountFolder, individuals_list);
			loadIndividualMsgList(individualsListFile);
		}
		
		Map<String, Double> contentScores = content.getIndividualPredictions();
		Map<String, Double> socialScores = socialConnection.getIndividualPredictions();
		
		Iterator<String> individuals = individualMsgs.keySet().iterator();
		while(individuals.hasNext()){
			String individual = individuals.next();
			
			Double contentScoreObj = contentScores.get(individual);
			double contentScore = 0;
			if(contentScoreObj != null) contentScore = contentScoreObj;
			
			Double socialScoreObj = socialScores.get(individual);
			double socialScore = 0;
			if(socialScoreObj != null) socialScore = socialScoreObj;
			
			individualSimilarities.put(individual, contentScore * content_importance + socialScore * connection_importance);
		}
	}
	
	protected void makeGroupPredictions() throws IOException{
		combineGroupContentAndSocial();
	}
	
	private void combineGroupContentAndSocial() throws IOException{
		groupSimilarities.clear();
		
		if(groupTracker == null){
			groupTracker = new GoogleGroupTracker((int)(totalMsgs*Account.TRAINING_RATIO));
			File groupListFile = new File(accountFolder, groups_list);
			groupTracker.load(groupListFile);	
		}
		
		Map<ComparableSet<String>, Double> contentScores = content.getGroupPredictions();
		Map<ComparableSet<String>, Double> socialScores = socialConnection.getGroupPredictions();
		
		Iterator<Group> groups = groupTracker.getAllGroups().iterator();
		while(groups.hasNext()){
			Group group = groups.next();
			
			Double contentScoreObj = contentScores.get(group.getMembers());
			double contentScore = (contentScoreObj == null)? 0: contentScoreObj;
			
			Double socialScoreObj = socialScores.get(group.getMembers());
			double socialScore = (contentScoreObj == null)? 0: socialScoreObj;
			
			groupSimilarities.put( (ComparableSet<String>) group.getMembers(), contentScore * content_importance + socialScore * connection_importance);
		}
	}

	public Map<String, Double> getIndividualPredictions() {
		if((!useSocialConnections || PredictionMaker.group_algorithm == PredictionMaker.TOP_CONTACT_SCORE) 
				&& (!useTextContent || PredictionMaker.content_importance == 0.0)){
			return timeAndDir.getIndividualPredictions();
		}else{
			if(useSocialConnections && PredictionMaker.group_algorithm != PredictionMaker.TOP_CONTACT_SCORE && (!useTextContent || PredictionMaker.content_importance == 0.0)){
				return socialConnection.getIndividualPredictions();
			}else if( (!useSocialConnections || PredictionMaker.group_algorithm == PredictionMaker.TOP_CONTACT_SCORE ) && useTextContent  && PredictionMaker.content_importance != 0.0){
				return content.getIndividualPredictions();
			}else{
				return individualSimilarities;
			}
		}
	}

	public Map<ComparableSet<String>, Double> getGroupPredictions() {
		if((!useSocialConnections || PredictionMaker.group_algorithm == PredictionMaker.TOP_CONTACT_SCORE) 
				&& (!useTextContent || PredictionMaker.content_importance == 0.0)){
			return timeAndDir.getGroupPredictions();
		}else if(useSocialConnections && !useTextContent){
			return socialConnection.getGroupPredictions();
		}else if(!useSocialConnections && useTextContent ){
			return content.getGroupPredictions();
		}else{
			//TODO:combine content and social connections
			return null;
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
