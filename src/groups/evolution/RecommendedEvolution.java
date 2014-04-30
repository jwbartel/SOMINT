package groups.evolution;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class RecommendedEvolution<V> implements Comparable<RecommendedEvolution<V>>{
	Set<V> oldGroup;
	Set<V> recommenderEngineResult;
	Set<V> merging;
	
	public RecommendedEvolution(Set<V> oldGroup, Set<V> recommenderEngineResult, Collection<V> newMembers){
		this.oldGroup = oldGroup;
		this.recommenderEngineResult = recommenderEngineResult;
		addMerging(oldGroup, recommenderEngineResult, newMembers);
	}
	
	public void addMerging(Set<V> oldGroup, Set<V> recommenderEngineResult, Collection<V> newMembers) {
		merging = new TreeSet<V>(recommenderEngineResult);
		merging.retainAll(newMembers);
		merging.addAll(oldGroup);
	}
	
	public Set<V> getOldGroup(){
		return oldGroup;
	}
	
	public Set<V> getRecommenderEngineResult(){
		return recommenderEngineResult;
	}
	
	public Set<V> getMerging(){
		return merging;
	}

	@Override
	public int compareTo(RecommendedEvolution<V> arg0) {
		
		int oldStrCompare = oldGroup.toString().compareTo(arg0.oldGroup.toString());
		if(oldStrCompare != 0) return oldStrCompare;
		
		int oldStrLengthCompare = new Integer(oldGroup.size()).compareTo(arg0.oldGroup.size());
		if(oldStrLengthCompare != 0) return oldStrLengthCompare;
		
		int recommenderEngineResultStrCompare = recommenderEngineResult.toString().compareTo(arg0.recommenderEngineResult.toString());
		if(recommenderEngineResultStrCompare != 0) return recommenderEngineResultStrCompare;
		
		int recommenderEngineResultStrLengthCompare = new Integer(recommenderEngineResult.size()).compareTo(arg0.recommenderEngineResult.size());
		if(recommenderEngineResultStrLengthCompare != 0) return recommenderEngineResultStrLengthCompare;
		
		if( !(oldGroup.equals(arg0.oldGroup) && recommenderEngineResult.equals(arg0.recommenderEngineResult)) ){
			return 1;
		}
		
		return 0;
	}
	
	public String toString(){
		return "(" + oldGroup + " + " + recommenderEngineResult + ")->"+merging; 
	}
	
}
