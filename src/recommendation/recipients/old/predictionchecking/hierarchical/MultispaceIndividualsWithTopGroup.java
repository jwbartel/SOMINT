package recommendation.recipients.old.predictionchecking.hierarchical;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.mail.MessagingException;

import recommendation.recipients.old.predictionchecking.PredictionAcceptanceModeler;
import recommendation.recipients.old.predictionchecking.TopGroupPrediction;
import recommendation.recipients.old.predictionchecking.TopPrediction;
import bus.data.structures.ComparableSet;

public class MultispaceIndividualsWithTopGroup {
	public static final int prediction_list_size = PredictionAcceptanceModeler.PREDICTION_LIST_SIZE;
	
	protected HierarchicalMultispacePredictionMaker predictionMaker;
	protected GroupPrediction predictionsList;
	
	public MultispaceIndividualsWithTopGroup(){
		
	}
	
	public  MultispaceIndividualsWithTopGroup(File accountFolder, String sender, Set<String> seed, Date currDate, Map<String, Integer> wordCounts) throws IOException, MessagingException{
		makePredictions(accountFolder, sender, seed, currDate, wordCounts);
	}
	
	public  MultispaceIndividualsWithTopGroup(String accountFolder, String sender, Set<String> seed, Date currDate, Map<String, Integer> wordCounts) throws IOException, MessagingException{
		makePredictions(new File(accountFolder), sender, seed, currDate, wordCounts);
	}
	
	protected void makePredictions(File accountFolder, String sender, Set<String> seed, Date currDate, Map<String, Integer> wordCounts) throws IOException, MessagingException{
		predictionMaker = new HierarchicalMultispacePredictionMaker(accountFolder, sender, seed, currDate, wordCounts);
		predictionMaker.close();
		
		ArrayList<String> individualPredictionsList = getIndividualPredictionList(seed);
		/*individualPredictionsList.add("Prasun");
		individualPredictionsList.add("Jacob");
		individualPredictionsList.add("Kelli");
		individualPredictionsList.add("Sammi");*/
		Map<String, TopGroupPrediction> individualGroupAssociations = getIndividualGroupAssociations(individualPredictionsList);
		/*ComparableSet<String> jacobGroup = new ComparableSet<String>();
		jacobGroup.add("Jacob");
		jacobGroup.add("Prasun");
		individualGroupAssociations.put("Jacob", new TopGroupPrediction(jacobGroup,1.0));

		ComparableSet<String> prasunGroup = new ComparableSet<String>();
		prasunGroup.add("Prasun");
		prasunGroup.add("Jacob");
		individualGroupAssociations.put("Prasun", new TopGroupPrediction(prasunGroup, 1.0));

		ComparableSet<String> kelliGroup = new ComparableSet<String>();
		kelliGroup.add("Kelli");
		kelliGroup.add("Sammi");
		kelliGroup.add("Prasun");
		kelliGroup.add("Jacob");
		individualGroupAssociations.put("Kelli", new TopGroupPrediction(kelliGroup, 1.0));
		
		ComparableSet<String> sammiGroup = new ComparableSet<String>();
		sammiGroup.add("Kelli");
		sammiGroup.add("Sammi");
		individualGroupAssociations.put("Sammi", new TopGroupPrediction(sammiGroup, 1.0));*/
		
		predictionsList = new GroupPrediction(null);
		for(int i=0; i<individualPredictionsList.size(); i++){
			
			String individualStr = individualPredictionsList.get(i);
			ComparableSet<String> associatedGroup = (ComparableSet<String>) individualGroupAssociations.get(individualPredictionsList.get(i)).getRecipients();
			IndividualPrediction individual = new IndividualPrediction(individualPredictionsList.get(i), associatedGroup);
			
			predictionsList = predictionsList.add(individual);
		}
		
		//System.out.println(predictionsList);
	}
	
	protected ArrayList<String> getIndividualPredictionList(Set<String> seed){
		Iterator<Entry<String, Double>> individualPredictionScores = predictionMaker.getIndividualPredictions().entrySet().iterator();
		
		Set<TopPrediction> individualPredictions = new TreeSet<TopPrediction>();
		while(individualPredictionScores.hasNext()){
			Entry<String, Double> entry = individualPredictionScores.next();
			if(seed.contains(entry.getKey())){
				continue;
			}
			individualPredictions.add(new TopPrediction(entry.getKey(), entry.getValue()));
		}
		
		ArrayList<String> individualPredictionsList = new ArrayList<String>(prediction_list_size);
		
		Iterator<TopPrediction> predictions = individualPredictions.iterator();
		while(predictions.hasNext() && individualPredictionsList.size() < prediction_list_size){
			TopPrediction nextIndividual = predictions.next();
			String individual = nextIndividual.recipient;
			individualPredictionsList.add(individual);
		}
		
		return individualPredictionsList;
		
	}
	
	protected Map<String, TopGroupPrediction> getIndividualGroupAssociations(ArrayList<String> individualPredictionsList){
		
		Map<String, TopGroupPrediction> individualGroupAssociations = new TreeMap<String, TopGroupPrediction>();
		
		Iterator<Entry<ComparableSet<String>, Double>> groupPredictionScores = predictionMaker.getGroupPredictions().entrySet().iterator();
		while(groupPredictionScores.hasNext()){
			Entry<ComparableSet<String>, Double> entry = groupPredictionScores.next();
			TopGroupPrediction groupPrediction = new TopGroupPrediction(entry.getKey(), entry.getValue());
			
			for(int i=0; i<individualPredictionsList.size(); i++){
				String individual = individualPredictionsList.get(i);
				if(individual.equals("bill.williams.iii@enron.com")){
					int x = 0;
					x++;
				}
				
				if(!groupPrediction.recipients.contains(individual)) continue;
				
				TopGroupPrediction oldGroup = individualGroupAssociations.get(individual);
				if(oldGroup == null || oldGroup.cosineSim < groupPrediction.cosineSim){
					individualGroupAssociations.put(individual, groupPrediction);
				}
				
			}
		}
		
		return individualGroupAssociations;
		
	}
	
	/*public static void main(String[] args) throws IOException, MessagingException{
		MultispaceIndividualsWithTopGroup predictor = new MultispaceIndividualsWithTopGroup("", null, null, null);
	}*/
	
	public GroupPrediction getPredictionsList(){
		return predictionsList;
	}
}
