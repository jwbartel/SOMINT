package recommendation.recipients.old.predictionchecking;

import java.io.File;
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

public class SocialConnectionPredictionMaker extends PredictionMaker {	

	protected static File oldAccountFolder;
	protected static Date oldDate;
	
	protected static Map<ComparableSet<String>, Double> groupIRValues = new TreeMap<ComparableSet<String>, Double>();
	
	protected Map<ComparableSet<String>, Double> groupSocialConnectionSimilarities = new TreeMap<ComparableSet<String>, Double>(); 
	protected Map<String, Double> individualSocialConnectionSimilarities = new TreeMap<String, Double>(); 
	
	public SocialConnectionPredictionMaker(){
		
	}
	
	public SocialConnectionPredictionMaker(File accountFolder, String sender, Set<String> seed, Date currDate) throws IOException, MessagingException {
		super(accountFolder);
		makePredictions(sender, seed, currDate);		
	}
	
	public SocialConnectionPredictionMaker(String accountFolder, String sender, Set<String> seed, Date currDate) throws IOException, MessagingException {
		super(accountFolder);
		makePredictions(sender, seed, currDate);
	}
	
	protected void makePredictions(String sender, Set<String> seed, Date currDate) throws IOException{
		if(groupTracker == null){
			groupTracker = new GoogleGroupTracker((int)(totalMsgs*Account.TRAINING_RATIO));
			File groupListFile = new File(accountFolder, groups_list);
			groupTracker.load(groupListFile);	
		}
		
		if(oldAccountFolder == null || !oldAccountFolder.equals(accountFolder) || !oldDate.equals(currDate)){
			buildGroupIRVals(currDate);
			oldAccountFolder = accountFolder;
			oldDate = currDate;
		}
		
		if(predictIndividuals){
			makeIndividualPredictions(sender, seed);
		}else{
			makeGroupPredictions(sender, seed);
		}
	}

	public Map<String, Double> getIndividualPredictions() {
		return individualSocialConnectionSimilarities;
	}

	public Map<ComparableSet<String>, Double> getGroupPredictions() {
		return groupSocialConnectionSimilarities;
	}
	
	protected void makeIndividualPredictions(String sender, Set<String> seed) throws IOException{
		if(individualMsgs == null){
			File individualsListFile = new File(accountFolder, individuals_list);
			loadIndividualMsgList(individualsListFile);
		}
		
		individualSocialConnectionSimilarities.clear();
		
		Iterator<ComparableSet<String>> groups = groupIRValues.keySet().iterator();
		while(groups.hasNext()){
			ComparableSet<String> group = groups.next();
			
			if(seed.containsAll(group)){
				continue;
			}
			
			double weight = getWeight(sender, group, seed);
			
			if(weight == 0){
				continue;
			}
			
			Iterator<String> individuals = group.iterator();
			while(individuals.hasNext()){
				String individual = individuals.next();
				Double oldWeight = individualSocialConnectionSimilarities.get(individual);
				if(oldWeight == null){
					individualSocialConnectionSimilarities.put(individual, weight);
				}else{
					individualSocialConnectionSimilarities.put(individual, weight+oldWeight);
				}
			}
			
		}
	}
	
	protected void makeGroupPredictions(String sender, Set<String> seed){
		
		groupSocialConnectionSimilarities.clear();
		
		Iterator<ComparableSet<String>> groups = groupIRValues.keySet().iterator();
		while(groups.hasNext()){
			ComparableSet<String> group = groups.next();
			if(seed.containsAll(group)){
				continue;
			}
			
			double weight = getWeight(sender, group, seed);
			if(weight > threshold){
				groupSocialConnectionSimilarities.put(group, weight);
			}
		}
	}
	
	protected double getWeight(String sender, ComparableSet<String> group, Set<String> correctGuesses){
		
		double socialConnectionWeight = 0.0;
		
		if(group_algorithm == SUBSET_GROUP_COUNT){
			socialConnectionWeight = subsetGroupCount(group, correctGuesses);
		}else if(group_algorithm == SUBSET_GROUP_SCORE){
			socialConnectionWeight = subsetGroupScore(group, correctGuesses);
		}else if(group_algorithm == SUBSET_WEIGHTED_SCORE){
			socialConnectionWeight = subsetWeightedScore(group, correctGuesses);
		}else if(group_algorithm == INTERSECTION_GROUP_COUNT){
			socialConnectionWeight = intersectionGroupCount(group, correctGuesses);
		}else if(group_algorithm == INTERSECTION_GROUP_SCORE){
			socialConnectionWeight = intersectionGroupScore(group, correctGuesses);
		}else if(group_algorithm == INTERSECTION_WEIGHTED_SCORE){
			socialConnectionWeight = intersectionWeightedScore(group, correctGuesses);
		}else if(group_algorithm == TOP_CONTACT_SCORE){
			socialConnectionWeight = groupIRValues.get(group);
		}else if(group_algorithm == COMBINED_GROUP_COUNT){
			socialConnectionWeight = combinedGroupCount(sender, group, correctGuesses);
		}else if(group_algorithm == COMBINED_GROUP_SCORE){
			socialConnectionWeight = combinedGroupScore(sender, group, correctGuesses);
		}else if(group_algorithm == COMBINED_WEIGHTED_SCORE){
			socialConnectionWeight = combinedWeightedScore(sender, group, correctGuesses);
		}
		
		return socialConnectionWeight;
	}
	
	protected void buildGroupIRVals(Date currDate) throws IOException{
		groupIRValues.clear();
		
		Iterator<Group> groups = groupTracker.getAllGroups().iterator();
		while(groups.hasNext()){
			Group group = groups.next();
			ComparableSet<String> members = new ComparableSet<String>(group.getMembers());
			ArrayList<EmailInteraction> interactions = group.getInteractions();
			
			double ir = getIR(interactions, currDate);
			
			groupIRValues.put(members, ir);
			
		}
	}
	
	protected double getIR(ArrayList<EmailInteraction> interactions, Date currDate) throws IOException{
		double sentTotal = 0.0;
		double receivedTotal = 0.0;
		
		for(int i=0; i<interactions.size(); i++){
			EmailInteraction interaction = interactions.get(i);
			double val = 0.0;
			
			if(isAged){
				if(!useHalfLives){

					long currDateLong = currDate.getTime();
					long oldestDate = groupTracker.getEarliestDate().getTime();					
					long interactionDate = interaction.getDate().getTime();
					
					val = ((double) (interactionDate - oldestDate))/((double) currDateLong - oldestDate);

				}else{

					long currDateLong = currDate.getTime();	
					long interactionDate = interaction.getDate().getTime();
					
					double exponent = (double) currDateLong - interactionDate;
					double halfLife = half_life;
					exponent = exponent/halfLife;
					val = Math.pow(0.5, exponent);
					
				}
			}else{
				val = 1.0;
			}
			
			boolean wasReceived = ((DirectedEmailInteraction) interaction).wasReceived();
			
			if(wasReceived){
				receivedTotal += val;
			}else{
				sentTotal += val;
			}
		}
		if(w_out == 1.0) return sentTotal+receivedTotal;
		return (w_out * sentTotal) + receivedTotal;
	}
	

	protected double subsetGroupCount(ComparableSet<String> group, Set<String> correctGuesses){
		
		if(group.containsAll(correctGuesses)){
			return 1.0;
		}else{
			return 0.0;
		}
	}
	
	protected double subsetGroupScore(ComparableSet<String> group, Set<String> correctGuesses){
		
		if(group.containsAll(correctGuesses)){
			return groupIRValues.get(group);
		}else{
			return 0.0;
		}
	}
	
	protected double subsetWeightedScore(ComparableSet<String> group, Set<String> correctGuesses){
		
		if(group.containsAll(correctGuesses)){
			return groupIRValues.get(group)*correctGuesses.size()/group.size();
		}else{
			return 0.0;
		}
	}
	
	protected double intersectionGroupCount(ComparableSet<String> group, Set<String> correctGuesses){
		boolean intersectionExists = correctGuesses.size() == 0;//false;
		Iterator<String> guesses = correctGuesses.iterator();
		while(guesses.hasNext() && !intersectionExists){
			String guess = guesses.next();
			if(group.contains(guess)){
				intersectionExists = true;
				break;
			}
		}
		
		if(intersectionExists){
			return 1;
		}else{
			return 0.0;
		}
		
	}
	
	protected double intersectionGroupScore(ComparableSet<String> group, Set<String> correctGuesses){
		boolean intersectionExists = correctGuesses.size() == 0;
		Iterator<String> guesses = correctGuesses.iterator();
		while(!intersectionExists && guesses.hasNext()){
			String guess = guesses.next();
			if(group.contains(guess)){
				intersectionExists = true;
				break;
			}
		}
		
		if(intersectionExists){
			return groupIRValues.get(group);
		}else{
			return 0.0;
		}
	}
	
	protected double intersectionWeightedScore(ComparableSet<String> group, Set<String> correctGuesses){
		int intersectionCount = 0;
		Iterator<String> guesses = correctGuesses.iterator();
		while(guesses.hasNext()){
			String guess = guesses.next();
			if(group.contains(guess)){
				intersectionCount++;
			}
		}
		double ir = groupIRValues.get(group);
		return ir * intersectionCount;
	}
	
	protected double combinedGroupCount(String sender, ComparableSet<String> group, Set<String> correctGuesses){
		double subsetVal = subsetGroupCount(group, correctGuesses);
		double intersectVal = 0.0;
		
		boolean onlyMatchesSender = true;
		boolean intersectionExists = correctGuesses.size() == 0;//false;
		Iterator<String> guesses = correctGuesses.iterator();
		while(guesses.hasNext() && !intersectionExists){
			String guess = guesses.next();
			if(group.contains(guess)){
				if(!guess.matches(sender)) onlyMatchesSender = false;
				intersectionExists = true;
				break;
			}
		}
		
		if(intersectionExists && ( (group.size() == 1 && group.contains(sender)) || !onlyMatchesSender)){
			intersectVal = 1;
		}
		
		return  subsetVal + (relative_intersection_importance * intersectVal);
	}
	
	protected double combinedGroupScore(String sender, ComparableSet<String> group, Set<String> correctGuesses){
		double subsetVal = subsetGroupScore(group, correctGuesses);
		double intersectVal = 0.0;
		
		boolean onlyMatchesSender = true;
		boolean intersectionExists = correctGuesses.size() == 0;
		Iterator<String> guesses = correctGuesses.iterator();
		while(!intersectionExists && guesses.hasNext()){
			String guess = guesses.next();
			if(group.contains(guess)){
				if(!guess.equals(sender)) onlyMatchesSender = false;
				intersectionExists = true;
				break;
			}
		}
		
		if(intersectionExists && ( (group.size() == 1 && group.contains(sender)) || !onlyMatchesSender)){
			intersectVal = groupIRValues.get(group);
		}
		
		return subsetVal + (relative_intersection_importance * intersectVal);
	}
	
	protected double combinedWeightedScore(String sender, ComparableSet<String> group, Set<String> correctGuesses){
		double subsetVal = subsetWeightedScore(group, correctGuesses);
		double intersectVal = 0.0;
		
		boolean onlyMatchesSender = true;
		int intersectionCount = 0;
		Iterator<String> guesses = correctGuesses.iterator();
		while(guesses.hasNext()){
			String guess = guesses.next();
			if(group.contains(guess)){
				if(!guess.equals(sender)) onlyMatchesSender = false;
				intersectionCount++;
			}
		}
		if(intersectionCount > 0 && ( (group.size() == 1 && group.contains(sender)) || !onlyMatchesSender)){
			intersectVal = groupIRValues.get(group)*intersectionCount/group.size();
		}
		
		return subsetVal + (relative_intersection_importance * intersectVal);
	}
	
	public static void clear(){
		oldDate = null;
		oldAccountFolder = null;
		clearMsgLists();
		
	}

}
