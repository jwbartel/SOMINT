package bus.thunderbird.predictions;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.MessagingException;

import recipients.predictionchecking.PredictionMaker;
import recipients.predictionchecking.hierarchical.HierarchicalMultispacePredictionMaker;

import bus.data.structures.ComparableSet;
import bus.thunderbird.Trainer;

public class ThunderbirdHierarchicalMultispacePredictionMaker extends HierarchicalMultispacePredictionMaker {

	protected static boolean use_groups = true;
	protected static boolean use_text = false;
	
	public ThunderbirdHierarchicalMultispacePredictionMaker(String sender, Set<String> seed, Date currDate, Map<String, Integer> wordCounts) throws IOException, MessagingException {
		
		makePredictions(sender, seed, currDate, wordCounts);
	}

	/*public ThunderbirdHierarchicalMultispacePredictionMaker(String accountFolder,	Set<String> seed, Date currDate, Map<String, Integer> wordCounts) throws IOException, MessagingException {
		super(accountFolder, seed, currDate, wordCounts);
	}*/
	
	protected void makePredictions(String sender, Set<String> seed, Date currDate, Map<String, Integer> wordCounts) throws IOException, MessagingException{
		if(!(use_groups || use_text)){
			if(timeAndDir == null || !timeAndDir.getDate().equals(currDate)){
				timeAndDir = new ThunderbirdHierarchicalTimeAndDirectionPredictionMaker(currDate);
				timeAndDir.close();
			}
		}else{
			if(use_groups){
				socialConnection = new ThunderbirdHierarchicalSocialConnectionPredictionMaker(sender, seed, currDate);
				socialConnection.close();
			}
			if(use_text){
				//content = new HierarchicalContentPredictionMaker(accountFolder, wordCounts, currDate, seed);
				content.close();
			}
		}
		
		if(use_groups && use_text){
			makeIndividualPredictions();
			makeGroupPredictions();
		}
	}
	
	public Map<String, Double> getIndividualPredictions() {
		if((!use_groups || PredictionMaker.group_algorithm == PredictionMaker.TOP_CONTACT_SCORE) 
				&& (!use_text || PredictionMaker.content_importance == 0.0)){
			return timeAndDir.getIndividualPredictions();
		}else{
			if(use_groups && PredictionMaker.group_algorithm != PredictionMaker.TOP_CONTACT_SCORE && (!use_text || PredictionMaker.content_importance == 0.0)){
				return socialConnection.getIndividualPredictions();
			}else if( (!use_groups || PredictionMaker.group_algorithm == PredictionMaker.TOP_CONTACT_SCORE ) && use_text  && PredictionMaker.content_importance != 0.0){
				return content.getIndividualPredictions();
			}else{
				return individualSimilarities;
			}
		}
	}

	public Map<ComparableSet<String>, Double> getGroupPredictions() {
		if((!use_groups || PredictionMaker.group_algorithm == PredictionMaker.TOP_CONTACT_SCORE) 
				&& (!use_text || PredictionMaker.content_importance == 0.0)){
			return timeAndDir.getGroupPredictions();
		}else if(use_groups && !use_text){
			return socialConnection.getGroupPredictions();
		}else if(!use_groups && use_text ){
			return content.getGroupPredictions();
		}else{
			//TODO:combine content and social connections
			return null;
		}
	}

	public static void main(String[] args) throws IOException, MessagingException{
		
		Trainer.load("C:\\hierarchical_email_predictions\\groups.txt", "C:\\hierarchical_email_predictions\\individuals.txt");
		
		Set<String> seed = new TreeSet<String>();
		Date date = new Date(System.currentTimeMillis());
		
		ThunderbirdMultispaceIndividualsWithTopGroup predictor = new ThunderbirdMultispaceIndividualsWithTopGroup("bartel.jacob@gmail.com", seed, date, null);
		System.out.println(predictor.getPredictionsList().getValues());
	}
}
