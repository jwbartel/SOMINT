package recipients.groupbased.hierarchical;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import bus.data.structures.ComparableSet;

public class HierarchicalGroupRecommendation<V extends Comparable<V>> implements HierarchicalRecommendation<V> {
	int size = 0;
	HierarchicalGroupRecommendation<V> parent = null;
	ComparableSet<V> associatedGroup = new ComparableSet<V>();
	ArrayList<HierarchicalRecommendation<V>> values = new ArrayList<>();
	
	
	public HierarchicalGroupRecommendation(ComparableSet<V> associatedGroup){
		this.associatedGroup = associatedGroup;
	}
	
	@Override
	public ComparableSet<V> getAssociatedGroup(){
		return associatedGroup;
	}
	
	@Override
	public int getSize() {
		return size;
	}
	
	public HierarchicalGroupRecommendation<V> add(HierarchicalRecommendation<V> recommendation){
		
		for(int i=0; i<values.size(); i++){
			HierarchicalRecommendation<V> knownRecommendation = values.get(i);
			
			if(knownRecommendation.getAssociatedGroup().equals(recommendation.getAssociatedGroup())){
				if(knownRecommendation instanceof HierarchicalGroupRecommendation){
					((HierarchicalGroupRecommendation<V>) knownRecommendation).add(recommendation);
				}else{
					
					HierarchicalGroupRecommendation<V> grouping = new HierarchicalGroupRecommendation<V>(recommendation.getAssociatedGroup());
					grouping = grouping.add(knownRecommendation);
					
					grouping.setParent(this);
					grouping.values.add(recommendation);
					grouping.incrementSize(recommendation.getSize());
					values.remove(i);
					values.add(i, grouping);
				}
				return this;
			}else if(knownRecommendation.getAssociatedGroup().containsAll(recommendation.getAssociatedGroup())){
				
				if(knownRecommendation instanceof HierarchicalGroupRecommendation){
					((HierarchicalGroupRecommendation<V>) knownRecommendation).add(recommendation);
				}else{

					
					HierarchicalGroupRecommendation<V> grouping = new HierarchicalGroupRecommendation<V>(knownRecommendation.getAssociatedGroup());
					grouping = grouping.add(knownRecommendation);
					
					grouping.setParent(this);
					grouping.values.add(recommendation);
					grouping.incrementSize(recommendation.getSize());
					values.remove(i);
					values.add(i, grouping);
					
				}
				return this;
			}else if(recommendation.getAssociatedGroup().containsAll(knownRecommendation.getAssociatedGroup())){
				
				HierarchicalGroupRecommendation<V> grouping = new HierarchicalGroupRecommendation<V>(recommendation.getAssociatedGroup());
				
				int addPos = values.size();
				for(int j=0; j< values.size(); j++){
					HierarchicalRecommendation<V> sibling = values.get(j);
					if(grouping.getAssociatedGroup().containsAll(sibling.getAssociatedGroup())){
						if(j < addPos) addPos = j;
						grouping.add(sibling);
						
						values.remove(j);
						j--;
					}
				}
				
				grouping.setParent(this);
				grouping.values.add(recommendation);
				grouping.incrementSize(recommendation.getSize());
				values.add(addPos, grouping);
				return this;
			}
		}
		
		
		size += recommendation.getSize();
		if(parent != null){
			parent.incrementSize(recommendation.getSize());
		}
		
		recommendation.setParent(this);
		values.add(recommendation);
		
		return this;
	}
	
	public ArrayList<HierarchicalRecommendation<V>> getValues(){
		return values;
	}
	
	protected void incrementSize(int amount){
		size += amount;
		if(parent != null){
			parent.incrementSize(amount);
		}
	}
	
	@Override
	public void setParent(HierarchicalGroupRecommendation<V> parent){
		this.parent = parent;
	}
	
	@Override
	public String toString(){
		if(values.size() == 0){
			return "";
		}else if(size == 1 || values.size() == 1){
			return values.get(0).toString();
		}else{
			String toReturn = "(";
			for(int i=0; i<values.size(); i++){
				if(i != 0){
					toReturn += ", ";
				}
				toReturn += values.get(i).toString();
			}
			toReturn += ")";
			return toReturn;
		}
	}
	
	@Override
	public Set<V> getMembers(){
		Set<V> toReturn = new TreeSet<>();
		
		for(int i=0; i<values.size(); i++){
			toReturn.addAll(values.get(i).getMembers());
		}
		
		return toReturn;
	}

}
