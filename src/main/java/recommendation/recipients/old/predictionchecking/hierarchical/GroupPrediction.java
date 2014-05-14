package recommendation.recipients.old.predictionchecking.hierarchical;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import bus.data.structures.ComparableSet;

public class GroupPrediction implements Prediction {
	int size = 0;
	GroupPrediction parent = null;
	ComparableSet<String> associatedGroup = new ComparableSet<String>();
	ArrayList<Prediction> values = new ArrayList<Prediction>();
	
	
	public GroupPrediction(ComparableSet<String> associatedGroup){
		this.associatedGroup = associatedGroup;
	}
	
	public ComparableSet<String> getAssociatedGroup(){
		return associatedGroup;
	}
	
	public int getSize() {
		return size;
	}
	
	public GroupPrediction add(Prediction prediction){
		
		for(int i=0; i<values.size(); i++){
			Prediction knownPrediction = values.get(i);
			
			if(knownPrediction.getAssociatedGroup().equals(prediction.getAssociatedGroup())){
				if(knownPrediction instanceof GroupPrediction){
					((GroupPrediction) knownPrediction).add(prediction);
				}else{
					
					GroupPrediction grouping = new GroupPrediction(prediction.getAssociatedGroup());
					grouping = grouping.add(knownPrediction);
					
					grouping.setParent(this);
					grouping.values.add(prediction);
					grouping.incrementSize(prediction.getSize());
					values.remove(i);
					values.add(i, grouping);
				}
				return this;
			}else if(knownPrediction.getAssociatedGroup().containsAll(prediction.getAssociatedGroup())){
				
				if(knownPrediction instanceof GroupPrediction){
					((GroupPrediction) knownPrediction).add(prediction);
				}else{

					
					GroupPrediction grouping = new GroupPrediction(knownPrediction.getAssociatedGroup());
					grouping = grouping.add(knownPrediction);
					
					grouping.setParent(this);
					grouping.values.add(prediction);
					grouping.incrementSize(prediction.getSize());
					values.remove(i);
					values.add(i, grouping);
					
				}
				return this;
			}else if(prediction.getAssociatedGroup().containsAll(knownPrediction.getAssociatedGroup())){
				
				GroupPrediction grouping = new GroupPrediction(prediction.getAssociatedGroup());
				
				int addPos = values.size();
				for(int j=0; j< values.size(); j++){
					Prediction sibling = values.get(j);
					if(grouping.getAssociatedGroup().containsAll(sibling.getAssociatedGroup())){
						if(j < addPos) addPos = j;
						grouping.add(sibling);
						
						values.remove(j);
						j--;
					}
				}
				
				grouping.setParent(this);
				grouping.values.add(prediction);
				grouping.incrementSize(prediction.getSize());
				values.add(addPos, grouping);
				return this;
			}
		}
		
		
		size += prediction.getSize();
		if(parent != null){
			parent.incrementSize(prediction.getSize());
		}
		
		prediction.setParent(this);
		values.add(prediction);
		
		return this;
	}
	
	public ArrayList<Prediction> getValues(){
		return values;
	}
	
	protected void incrementSize(int amount){
		size += amount;
		if(parent != null){
			parent.incrementSize(amount);
		}
	}
	
	public void setParent(GroupPrediction parent){
		this.parent = parent;
	}
	
	public String toString(){
		if(values.size() == 0){
			return "";
		}else if(size == 1 || values.size() == 1){
			return values.get(0).toString();
		}else{
			String toReturn = "";
			if(parent != null) toReturn += "(";
			for(int i=0; i<values.size(); i++){
				if(i != 0){
					toReturn += ", ";
				}
				toReturn += values.get(i).toString();
			}
			if(parent != null) toReturn += ")";
			return toReturn;
		}
	}
	
	public Set<String> getMembers(){
		Set<String> toReturn = new TreeSet<String>();
		
		for(int i=0; i<values.size(); i++){
			toReturn.addAll(values.get(i).getMembers());
		}
		
		return toReturn;
	}
	
	/*public static void main(String[] args){
		GroupPrediction g1 = new GroupPrediction();
		GroupPrediction g2 = new GroupPrediction();
		GroupPrediction g3 = new GroupPrediction();
		
		IndividualPrediction i1 = new IndividualPrediction("Jacob");
		IndividualPrediction i2 = new IndividualPrediction("Jason");
		IndividualPrediction i3 = new IndividualPrediction("Prasun");
		//IndividualPrediction i4 = new IndividualPrediction("Kelli");
		
		g3.add(i1);
		g2.add(i2);
		g2.add(g3);
		g1.add(i3);
		g1.add(g2);
		
		System.out.println(g1);
	}*/

}
