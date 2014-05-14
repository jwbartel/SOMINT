package bus.thunderbird.predictions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.MessagingException;

import recommendation.recipients.predictionchecking.PredictionMaker;
import recommendation.recipients.predictionchecking.hierarchical.GroupPrediction;
import recommendation.recipients.predictionchecking.hierarchical.Prediction;
import bus.thunderbird.Trainer;
import bus.thunderbird.structures.ThunderbirdAddressParser;

public class PredictionFetcher{

	
	public static PredictionFetcher newInstance(){
		return new PredictionFetcher();
	}


	static{
		PredictionMaker.setHalfLife(PredictionMaker.ONE_YEAR);
		PredictionMaker.group_algorithm = PredictionMaker.COMBINED_WEIGHTED_SCORE;
		PredictionMaker.w_out = 0.5;
		PredictionMaker.relative_intersection_importance = 1;
	}
	
	public PredictionsAndNames getPredictions(String sender, String seedStr, String date) throws IOException, MessagingException{
		Set<String> seed = new TreeSet<String>();
		if(seedStr.length() > 0){
			ThunderbirdAddressParser parser = new ThunderbirdAddressParser(seedStr);
			seed.addAll(parser.getAddressesInArrayList());
		}
		
		return getPredictions(sender, seed, date);
	}
	
	public PredictionsAndNames getPredictions(String sender, Set<String> seed, String date) throws IOException, MessagingException{
		//System.out.println("Fetching predictions");
		
		ThunderbirdMultispaceIndividualsWithTopGroup predictor = new ThunderbirdMultispaceIndividualsWithTopGroup(sender, seed, new Date(Long.parseLong(date)), null);
		ArrayList<Prediction> predictions = predictor.getPredictionsList().getValues();
		
		ArrayList<Object> sortedVals = new ArrayList<Object>();
		ArrayList<String> names = new ArrayList<String>();
		for(int i=0; i<predictions.size(); i++){
			Prediction prediction = predictions.get(i);
			
			addToSortedVals(prediction, sortedVals, names);
			
		}

		//System.out.println("Predictions fetched");
		return new PredictionsAndNames(names.toArray(stringArrayType), sortedVals.toArray());
	}
	
	int count = 1;
	
	static String[] stringArrayType = new String[0];
	protected void addToSortedVals(Prediction prediction, ArrayList<Object> sortedVals, ArrayList<String> names){
		String[] arrayVal = prediction.getMembers().toArray(stringArrayType);
	
		Object val = null;
		if(arrayVal.length > 1){
			val = arrayVal;
		}else if(arrayVal.length == 1){
			val = arrayVal[0];
		}
		
		if(val != null){
			sortedVals.add(val);
			if(prediction instanceof GroupPrediction){

				int currCount = count;
				names.add("open"+currCount);
				
				ArrayList<Prediction> predictions = ((GroupPrediction) prediction).getValues();
				for(int i=0; i<predictions.size(); i++){
					addToSortedVals(predictions.get(i), sortedVals, names);
				}

				sortedVals.add(val);
				names.add("close"+currCount);
			}else{
				names.add(null);
			}
		}
	}
	
	public static void main(String[] args) throws IOException, MessagingException{

		Trainer.load("C:\\hierarchical_email_predictions\\groups.txt", "C:\\hierarchical_email_predictions\\individuals.txt");
		
		Set<String> seed = new TreeSet<String>();
		seed.add("bartel.jacob@gmail.com");
		seed.add("kkeys@email.unc.edu");
		
		PredictionFetcher fetcher = new PredictionFetcher();
		PredictionsAndNames results = fetcher.getPredictions("bartel.jacob@gmail.com", seed,""+System.currentTimeMillis());
		
		String[] names = results.getNames();
		Object[] predictions = results.getPredictions();
		
		for(int i=0; i<names.length; i++){
			System.out.println(names[i]+":\t"+predictions[i]);
		}
	}
}
