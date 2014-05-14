package bus.thunderbird.predictions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;

import recommendation.recipients.old.predictionchecking.TopGroupPrediction;
import recommendation.recipients.old.predictionchecking.hierarchical.GroupPrediction;
import recommendation.recipients.old.predictionchecking.hierarchical.IndividualPrediction;
import recommendation.recipients.old.predictionchecking.hierarchical.MultispaceIndividualsWithTopGroup;
import bus.data.structures.ComparableSet;

public class ThunderbirdMultispaceIndividualsWithTopGroup extends
		MultispaceIndividualsWithTopGroup {
	
	public ThunderbirdMultispaceIndividualsWithTopGroup(String sender, Set<String> seed, Date currDate, Map<String, Integer> wordCounts) throws IOException, MessagingException{
		makePredictions(sender, seed, currDate, wordCounts);
	}
	
	protected void makePredictions(String sender, Set<String> seed, Date currDate, Map<String, Integer> wordCounts) throws IOException, MessagingException{
		predictionMaker = new ThunderbirdHierarchicalMultispacePredictionMaker(sender, seed, currDate, wordCounts);
		predictionMaker.close();
		
		ArrayList<String> individualPredictionsList = getIndividualPredictionList(seed);
		Map<String, TopGroupPrediction> individualGroupAssociations = getIndividualGroupAssociations(individualPredictionsList);
		
		predictionsList = new GroupPrediction(null);
		for(int i=0; i<individualPredictionsList.size(); i++){
			
			String individualStr = individualPredictionsList.get(i);
			ComparableSet<String> associatedGroup = (ComparableSet<String>) individualGroupAssociations.get(individualPredictionsList.get(i)).getRecipients();
			IndividualPrediction individual = new IndividualPrediction(individualPredictionsList.get(i), associatedGroup);
			
			predictionsList = predictionsList.add(individual);
		}
		
	}
}
