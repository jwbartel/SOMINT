package groups.evolution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;


public class GroupPredictionList<V> implements Comparable<GroupPredictionList<V>>{
	Set<V> f;
	String fName;
	Collection<Set<V>> predictions = new ArrayList<Set<V>>();
	Map<Set<V>, String> predictionNames;
	
	@Override
	public int compareTo(GroupPredictionList<V> arg0) {
		int val =  new Integer(predictions.size()).compareTo(arg0.predictions.size());
		if(val == 0){
			if(this.equals(arg0)){
				return 0;
			}else{
				return -1;
			}
		}
		return val;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object o){
		if(o instanceof GroupPredictionList){
			boolean fEqual = (f== null && ((GroupPredictionList) o).f == null ); 
			fEqual = fEqual || (f!=null && f.equals(((GroupPredictionList) o).f));
			
			boolean predictionsEqual = predictions == null && ((GroupPredictionList) o).predictions != null;
			predictionsEqual = predictionsEqual || (predictions != null && predictions.equals(((GroupPredictionList) o).predictions));
			
			return fEqual && predictionsEqual;
		}else{
			return false;
		}
	}
	
	public GroupPredictionList(Set<V> f){
		this.f = f;
		fName = null;
	}
	
	public GroupPredictionList(Set<V> f, String fName, Map<Set<V>, String> predictionNames){
		this.f = f;
		this.fName = fName;
		this.predictionNames = predictionNames;
	}
	
	public Set<V> getF(){
		return f;
	}
	
	public void removePrediction(Set<V> prediction){
		while(predictions.remove(prediction)){}
	}
	
	public void addPrediction(Set<V> prediction){
		predictions.add(prediction);
	}
	
	public int size(){
		return predictions.size();
	}
	
	public Collection<Set<V>> getPredictions(){
		return predictions;
	}
	
	public String toString(){
		if(fName == null){
			return "GroupPredictionList@Hash:"+this.hashCode();
		}
		
		String retVal = fName+"->(";
		boolean start = true;
		for(Set<V> prediction: predictions){
			if(!start) retVal+=",";
			else start = false;
			retVal += predictionNames.get(prediction);
		}
		retVal += ")";
		return retVal;
	}
}
