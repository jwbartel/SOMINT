package groups.seedless;

import groups.seedless.fellows.TrianglesCommunityScorerFactory;
import groups.seedless.hybrid.IOFunctions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;


public class LocalCommunityExpander<V> {

	protected UndirectedGraph<V, DefaultEdge> graph;
	protected Collection<Set<V>> previouslyFoundExpansions = new HashSet<Set<V>>();
	
	public LocalCommunityExpander(UndirectedGraph<V, DefaultEdge> graph){
		this.graph = graph;
	}
	
	public Collection<Set<V>> getExpansions(){
		Collection<Set<V>> retVal = new HashSet<Set<V>>();
		
		CommunityScorer scorer = CommunityScorerSelector.createCommunityScorer();
		
		Set<V> vertexSet = graph.vertexSet();
		for(V vertex: vertexSet){
			
			Set<V> initialCommunity = new TreeSet<V>();
			initialCommunity.add(vertex);
			Set<V> localCommunity = expandLocalCommunity(initialCommunity, scorer);
			System.out.println(localCommunity.size());
			
		}
		
		
		return retVal;
	}
	
	protected Set<V> expandLocalCommunity(Set<V> initialCommunity, CommunityScorer<V> scorer){
		
		Set<V> community = new TreeSet<V>(initialCommunity);
		
		Set<V> border = new TreeSet<V>(initialCommunity);
		
		Set<V> unknown = new TreeSet<V>();
		for(V initialNode: initialCommunity){
			Set<DefaultEdge> setupEdges = graph.edgesOf(initialNode);
			for(DefaultEdge edge: setupEdges){
				V source = graph.getEdgeSource(edge);
				if(!community.contains(source)){
					unknown.add(source);
				}

				V target = graph.getEdgeTarget(edge);
				if(!community.contains(target)){
					unknown.add(target);
				}
			}
		}
		
		pruneBorder(border, unknown, graph);
		
		
		double oldScore = scorer.scoreCommunityExpansion(community, border, unknown, graph);
		
		while(true){
			if(previouslyFoundExpansions.contains(community)){
				return null;
			}
			
			V bestNode = null;
			double bestScore = 0;
			
			for(V borderNode: border){
				
				Set<DefaultEdge> edges = graph.edgesOf(borderNode); 
				for(DefaultEdge edge: edges){
					V newNode = null;
					
					V source = graph.getEdgeSource(edge);
					if(unknown.contains(source)){
						 newNode = source;
					}
					
					V target = graph.getEdgeTarget(edge);
					if(unknown.contains(target)){
						newNode = target;
					}
					
					if(newNode != null){
						
						
						double score = scorer.scoreCommunityExpansion(newNode, community, border, unknown, graph);
						
						if(bestNode == null || score > bestScore){
							bestScore = score;
							bestNode = newNode;
						}
					}
				}
			}
			
			if(bestNode != null && scorer.goodScore(oldScore, bestScore)){
				
				community.add(bestNode);
				oldScore = bestScore;
				
				border.add(bestNode);
				
				updateUnknown(bestNode, community, unknown, graph);
				pruneBorder(border, unknown, graph);
				
				
			}
			
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void pruneBorder(Set border, Set unknown, UndirectedGraph graph){
		
		Set toRemove = new TreeSet();
		
		for(Object borderNode: border){
			boolean isBorder = false;
			
			Set<DefaultEdge> edges = graph.edgesOf(borderNode); 
			for(DefaultEdge edge: edges){
				Object source = graph.getEdgeSource(edge);
				if(!unknown.contains(source)){
					isBorder = true;
					break;
				}

				Object target = graph.getEdgeTarget(edge);
				if(!unknown.contains(target)){
					isBorder = true;
					break;
				}
			}
			
			if(!isBorder){
				toRemove.add(borderNode);
			}
			
		}
		
		border.removeAll(toRemove);
		
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void updateUnknown(Object newNode, Set community, Set unknown, UndirectedGraph graph){

		Set<DefaultEdge> edges = graph.edgesOf(newNode); 
		for(DefaultEdge edge: edges){
			Object source = graph.getEdgeSource(edge);
			if(!community.contains(source)){
				unknown.add(source);
			}

			Object target = graph.getEdgeTarget(edge);
			if(!community.contains(target)){
				unknown.add(target);
			}
		}
		
		unknown.removeAll(community);
	}
	
	double scorePossibleExpansion(CommunityScorer<V> scorer, V newMember, Set<V> community, Set<V> border, Set<V> unknown){
		Set<V> possibleExpansion = new TreeSet<V>(community);
		possibleExpansion.add(newMember);
		
		
		return scorer.scoreCommunityExpansion(community, border, unknown, graph);
	}
	
	
	public static void main(String[] args){
		
		CommunityScorerSelector.setFactory(new TrianglesCommunityScorerFactory<Integer>());
		
		IOFunctions<Integer> ioHelp = new IOFunctions<Integer>(Integer.class);
		
		String inRelationships = "data/Kelli/FriendshipData/2010Study/" + 10 + "_MutualFriends.txt";
		UndirectedGraph<Integer, DefaultEdge> graph = ioHelp.createUIDGraph(inRelationships);
		
		LocalCommunityExpander<Integer> expander = new LocalCommunityExpander<Integer>(graph);
		expander.getExpansions();
	}
}
