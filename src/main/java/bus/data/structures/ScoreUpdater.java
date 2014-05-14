package bus.data.structures;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import bus.data.structures.groups.GoogleGroup;

public class ScoreUpdater {
	
	public static final double INTERSECTION_WEIGHT = 1;
	
	public static int intersectingGroupCount(Set<String> searchSet, GoogleGroup group){
		Iterator<String> searchMembers = searchSet.iterator();
		Set<String> groupMembers = group.getMembers();
		boolean intersect = false;
		
		while(searchMembers.hasNext()){
			String searchMember = searchMembers.next();
			if(groupMembers.contains(searchMember)){
				intersect = true;
				break;
			}
		}
		
		if(intersect){
			return 1;
		}else{
			return 0;
		}
	}
	
	public static double intersectionGroupScore(Date currDate, Set<String> searchSet, GoogleGroup group) throws IOException{
		
		int intersectCount = intersectingGroupCount(searchSet, group);
		
		if(intersectCount == 1){
			return group.computeIR(currDate);
		}else{
			return 0;
		}
	}
	
	public static double intersectionWeightedScore(Date currDate, Set<String> searchSet, GoogleGroup group) throws IOException{
		Iterator<String> groupMembers = group.getMembers().iterator();
		int intersectCount = 0;
		
		while(groupMembers.hasNext()){
			if(searchSet.contains(groupMembers.next())){
				intersectCount++;
			}
		}
		
		return group.computeIR(currDate) * INTERSECTION_WEIGHT * intersectCount;
	}
	
	public static double topContactScore(Date currDate, Set<String> searchSet, GoogleGroup group) throws IOException{
		return group.computeIR(currDate);
	}
}
