package recommendation.groups.evolution.composed.listmaker;

import java.util.Set;
import java.util.TreeSet;

public class GroupAndPredictionPair<V> implements Comparable<GroupAndPredictionPair<V>>{
	Set<V> oldGroup;
	Set<V> prediction;
	
	public GroupAndPredictionPair(Set<V> oldGroup, Set<V> prediction){
		this.oldGroup = oldGroup;
		this.prediction = prediction;
	}
	
	@SuppressWarnings("rawtypes")
	public boolean equals(Object o){
		if(o instanceof GroupAndPredictionPair){
			GroupAndPredictionPair other = (GroupAndPredictionPair) o;
			
			return oldGroup.equals(other.oldGroup) && prediction.equals(other.prediction);
		}
		
		return false;
	}

	@Override
	public int compareTo(GroupAndPredictionPair<V> arg0) {
		
		int oldStrCompare = oldGroup.toString().compareTo(arg0.oldGroup.toString());
		if(oldStrCompare != 0) return oldStrCompare;
		
		int oldStrLengthCompare = new Integer(oldGroup.size()).compareTo(arg0.oldGroup.size());
		if(oldStrLengthCompare != 0) return oldStrLengthCompare;
		
		int predictionStrCompare = prediction.toString().compareTo(arg0.prediction.toString());
		if(predictionStrCompare != 0) return predictionStrCompare;
		
		int predictionStrLengthCompare = new Integer(prediction.size()).compareTo(arg0.prediction.size());
		if(predictionStrLengthCompare != 0) return predictionStrLengthCompare;
		
		if( !(oldGroup.equals(arg0.oldGroup) && prediction.equals(arg0.prediction)) ){
			return 1;
		}
		
		return 0;
	}
	
	public String toString(){
		
		return "(old="+oldGroup+", prediction="+prediction+")";
	}

	public static void main(String[] args){
		
		Set<Integer> oldGroup1 = new TreeSet<Integer>();
		Set<Integer> prediction1 = new TreeSet<Integer>();
		oldGroup1.add(1);
		oldGroup1.add(2);
		prediction1.add(3);
		GroupAndPredictionPair<Integer> pair1 = new GroupAndPredictionPair<Integer>(oldGroup1, prediction1);
		
		Set<Integer> oldGroup2 = new TreeSet<Integer>();
		Set<Integer> prediction2 = new TreeSet<Integer>();
		oldGroup2.add(1);
		oldGroup2.add(2);
		prediction2.add(3);
		prediction2.add(4);
		GroupAndPredictionPair<Integer> pair2 = new GroupAndPredictionPair<Integer>(oldGroup2, prediction2);
		
		Set<GroupAndPredictionPair<Integer>> pairSet = new TreeSet<GroupAndPredictionPair<Integer>>();
		pairSet.add(pair1);
		pairSet.add(pair2);
		
		System.out.println(pairSet);
	}
}


//TODO: Use for alternate measurement of whether a prediction has been used