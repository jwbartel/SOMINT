package bus.thunderbird.predictions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import recipients.predictionchecking.hierarchical.HierarchicalSocialConnectionPredictionMaker;

import bus.data.structures.ComparableSet;
import bus.data.structures.DirectedEmailInteraction;
import bus.thunderbird.Trainer;

public class ThunderbirdHierarchicalSocialConnectionPredictionMaker extends HierarchicalSocialConnectionPredictionMaker {

	protected static Map<String, ArrayList<DirectedEmailInteraction>> individuals = new TreeMap<String, ArrayList<DirectedEmailInteraction>>();
	
	public ThunderbirdHierarchicalSocialConnectionPredictionMaker(String sender, Set<String> seed, Date currDate) throws IOException{
		makePredictions(sender, seed, currDate);
	}
	
	protected void makePredictions(String sender, Set<String> seed, Date currDate) throws IOException{
		
		boolean shouldBuildIRVals = false;
		if(groupTracker == null){
			groupTracker = Trainer.getGroups();
			shouldBuildIRVals = true;
		}
		
		if( shouldBuildIRVals || !oldDate.equals(currDate)){
			buildGroupIRVals(currDate);
			oldAccountFolder = accountFolder;
			oldDate = currDate;
		}
		
		makeIndividualPredictions(sender, seed);
		makeGroupPredictions(sender, seed);
	}
	

	
	protected void makeIndividualPredictions(String sender, Set<String> seed) throws IOException{
		individuals = Trainer.getIndividuals();
		individualMsgs = null;
		
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
	
	public void close(){
		groupTracker = null;
	}
}
