package groups.comparison;

import groups.seeded.SeededHybridGroupFinder;
import groups.seeded.SeededLCMAGroupFinder;
import groups.seedless.hybrid.IOFunctions;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;

import bus.tools.FileAndMemoryBasedBronKerboschCliqueFinder;


public class SeedModeler {
	
	static final double SEED_RATIO_LIMIT = 0.30;
	
	IOFunctions<Integer> ioHelp;
	
	public static void findAllLCMASeededGroupsbyMerging(){
		System.out.println("seeded lcma by merging");
		double[] thresholds = {0.05, .1, .15, .20, .25, .3, .35, .4, .45, .5, .55, .6, .65, .7, .75, .8, .85, .9, .95, .1};

		/*double[] thresholds = new double[20];
		for(int i=0; i<thresholds.length; i++){
			thresholds[i] = 0.05*i+0.05;
		}*/

		
		System.out.print("seed size,threshold,participant,intended group size,");
		System.out.print("LCMA for merge with least changes,pass for least changes,least changes,");
		System.out.print("least LCMA,pass for least LCMA,changes for least LCMA,");
		System.out.print("lcma for final merge,final pass,lcma for final merge,");
		System.out.println();
		
		int[] participants = /*{10,12,13,16, 17, 19, 21, 22,*/{ 23, 24, 25, 8};
		for(int participantPos = 0; participantPos < participants.length; participantPos++){
			
			int participant = participants[participantPos];
			SeedModeler modeler = new SeedModeler();
			modeler.findLCMASeededGroupsbyMerging(participant, thresholds);
			
		}
	}
	
	public void findLCMASeededGroupsbyMerging(int participant, double[] thresholds){
		
		ioHelp = new IOFunctions<Integer>(Integer.class);
		String idNameMap = "data/Kelli/FriendshipData/2010Study/"+participant+"_People.txt";
		ioHelp.fillNamesAndIDs(idNameMap);
		
		String idealFile = "data/Jacob/Ideal/"+participant+"_ideal.txt";		
		Collection<Set<Integer>> ideals = ioHelp.loadIdealGroups(idealFile);
		
		String inRelationships = "data/Kelli/FriendshipData/2010Study/"+participant+"_MutualFriends.txt";
		UndirectedGraph<Integer, DefaultEdge> graph = ioHelp.createUIDGraph(inRelationships);		
		
		File maximalCliqueFile = new File("data/Kelli/Cliques/MaximalCliques/"+participant+"_MaximalCliques.txt");
		Collection<Set<Integer>> maximalCliques = getMaximalCliques(graph, maximalCliqueFile);
		
		for(int thresholdPos = 0; thresholdPos < thresholds.length; thresholdPos++){
			double threshold = thresholds[thresholdPos];
			
			for(Set<Integer> ideal: ideals){
				
				SeedFinder<Integer> seedFinder = new SeedFinder<Integer>();
				
				for(int seedSize =2; seedSize < ideal.size(); seedSize++){
					Set<Integer> seed = seedFinder.findPsuedoRandomSeed(ideal, seedSize);
					
					double seedRatio = ((double) seedSize)/ideal.size();
					if(seedRatio > SEED_RATIO_LIMIT){
						break;
					}
					
					SeededLCMAGroupFinder<Integer> finder = new SeededLCMAGroupFinder<Integer>();
					String[] results = finder.findGroupByMerging(threshold, seed, maximalCliques, ideal);
					System.out.print(""+seed.size()+","+threshold+","+participant);
					for(int i=0; i<results.length; i++){
						System.out.print(","+results[i]);
					}
					System.out.println();
				}
				
			}
			
		}
		
	}
	
	public static void findAllLCMASeededGroupsbyMatching(){
		System.out.println("seeded lcma by matching");
		double[] thresholds = {0.05, .1, .15, .20, .25, .3, .35, .4, .45, .5, .55, .6, .65, .7, .75, .8, .85, .9, .95, .1};/*new double[20];
		for(int i=0; i<thresholds.length; i++){
			thresholds[i] = 0.05*i+0.05;
		}*/

		
		System.out.print("threshold,threshold,participant,seed size,intended group size,");
		System.out.print("exact match depth,");
		System.out.print("least LCMA,depth for least LCMA,changes for least LCMA,");
		System.out.print("lcma for least changes,depth for least changes,least changes,");
		System.out.print("matches made,");
		System.out.println();
		
		int[] participants = {10,12,13,16, 17, 19, 21, 22, 23, 24, 25, 8};
		for(int participantPos = 0; participantPos < participants.length; participantPos++){
			
			int participant = participants[participantPos];
			SeedModeler modeler = new SeedModeler();
			modeler.findLCMASeededGroupsbyMatching(participant, thresholds);
			
		}
	}
	
	public void findLCMASeededGroupsbyMatching(int participant, double[] thresholds){
		ioHelp = new IOFunctions<Integer>(Integer.class);
		String idNameMap = "data/Kelli/FriendshipData/2010Study/"+participant+"_People.txt";
		ioHelp.fillNamesAndIDs(idNameMap);
		
		String idealFile = "data/Jacob/Ideal/"+participant+"_ideal.txt";		
		Collection<Set<Integer>> ideals = ioHelp.loadIdealGroups(idealFile);
		
		SeedFinder<Integer> seedFinder = new SeedFinder<Integer>();
		
		for(int thresholdPos = 0; thresholdPos < thresholds.length; thresholdPos++){
			double threshold = thresholds[thresholdPos];
			
			String groupsFile = "data/Jacob/LCMA/"+participant+"_GroupsWithThreshold_"+threshold+".txt";
			
			Collection<Set<Integer>> predictedGroups = ioHelp.loadGroups(groupsFile);

			Iterator<Set<Integer>> idealsIter = ideals.iterator();
			while(idealsIter.hasNext()){
				Set<Integer> ideal = idealsIter.next();
				
				for(int seedSize = 2; seedSize < ideal.size(); seedSize++){
					Set<Integer> seed = seedFinder.findPsuedoRandomSeed(ideal, seedSize);
					
					double seedRatio = ((double) seedSize)/ideal.size();
					if(seedRatio > SEED_RATIO_LIMIT){
						break;
					}
					
					SeededLCMAGroupFinder<Integer> finder = new SeededLCMAGroupFinder<Integer>();
					String[] results = finder.findGroupByMatching(seed, predictedGroups, ideal);
					
					System.out.print(""+threshold+","+participant+","+seedSize+","+ideal.size());
					for(int i=0; i<results.length; i++){
						System.out.print(","+results[i]);
					}
					System.out.println();
				}
			}
			
		}
	}
	
	public static void findAllHybridSeededGroupsByMatching(){
		System.out.println("seeded hybrid by matching");
		
		System.out.print("type,participant,seed size,ideal size,");
		System.out.print("S value for max S,D value for max S,depth of max S,changes for max S,");
		System.out.print("S value for min D,D value for min D,depth of min D,changes for min D,");
		System.out.print("S value for min Euclidean,D value for min Euclidean,depth of min Euclidean,changes for min Euclidean,");
		System.out.print("S value for min changes,D value for min changes,depth of min changes,changes for min changes,");
		System.out.print("matches made");
		System.out.println();
		
		int[] participants = {10,12,13,16, 17, 19, 21, 22, 23, 24, 25, 8};
		for(int participantPos = 0; participantPos < participants.length; participantPos++){
			int participant = participants[participantPos];
			
			SeedModeler modeler = new SeedModeler();
			modeler.findHybridSeededGroupsByMatching(participant);
		}
	}
	
	private void findHybridSeededGroupsByMatching(int participant){
		
		ioHelp = new IOFunctions<Integer>(Integer.class);
		String idNameMap = "data/Kelli/FriendshipData/2010Study/"+participant+"_People.txt";
		ioHelp.fillNamesAndIDs(idNameMap);
		
		String idealFile = "data/Jacob/Ideal/"+participant+"_ideal.txt";		
		Collection<Set<Integer>> ideals = ioHelp.loadIdealGroups(idealFile);
	
		String subcliquesFile = "data/Jacob/Hybrid/"+participant+"_Subcliques.txt";
		Collection<Set<Integer>> subcliques = ioHelp.loadGroups(subcliquesFile);
		
		String networksFile = "data/Jacob/Hybrid/"+participant+"_LargeGroups.txt";
		Collection<Set<Integer>> networks = ioHelp.loadGroups(networksFile);
		
		Collection<Set<Integer>> groups = new HashSet<Set<Integer>>(subcliques);
		groups.addAll(networks);
		
		SeedFinder<Integer> seedFinder = new SeedFinder<Integer>();
		
		Iterator<Set<Integer>> idealsIter = ideals.iterator();
		while(idealsIter.hasNext()){
			Set<Integer> ideal = idealsIter.next();
			
			for(int seedSize = 2; seedSize < ideal.size(); seedSize++){
				
				double seedRatio = ((double) seedSize)/ideal.size();
				if(seedRatio > SEED_RATIO_LIMIT){
					break;
				}
				
				Set<Integer> seed = seedFinder.findPsuedoRandomSeed(ideal, seedSize);	
				
				for(int type=0; type < 3; type++){
					SeededHybridGroupFinder<Integer> finder = new SeededHybridGroupFinder<Integer>();
					String[] results = finder.findGroupByMatching(seed, groups, ideal, type);
					
					String typeStr = (type == 0)? "S most important": (type==1)? "D most important": (type==2)? "Euclidean distance": "";
					
	
					
					System.out.print(""+typeStr+","+participant+","+seedSize+","+ideal.size());
					for(int i=0; i < results.length; i++){
						System.out.print(","+results[i]);
					}
					System.out.println();
					
				}
			}
		}
	}
	
	public static void findAllHybridSeededGroupsByMerging(){
		System.out.println("seeded hybrid by merging");
		double[][] weights = new double[2][2];
		weights[0][0] = 0.9; weights[0][1] = 0.35;
		weights[1][0] = 1.0; weights[1][1] = 0.15;
		
		System.out.print("seed size,S max, D min, participant,intended group size,S more important");
		System.out.print("S for merge with least changes,D for merge with least changes,pass for least changes,least changes,");
		System.out.print("S for merge with least S,D for merge with least S,pass for least S,least changes for least S,");
		System.out.print("S for merge with least D,D for merge with least D,pass for least D,least changes for least D,");
		System.out.print("final S,final D,final pass,final changes,");
		System.out.println();
		
		int[] participants = /*{10,12,13,16, 17, 19, 21, 22, 23, 24,*/{ 25, 8};
		for(int participantPos = 0; participantPos < participants.length; participantPos++){
			
			int participant = participants[participantPos];
			SeedModeler modeler = new SeedModeler();
			modeler.findHybridSeededGroupsByMerging(participant, weights);
			
		}
	}
	
	public void findHybridSeededGroupsByMerging(int participant, double[][] weights){
		
		ioHelp = new IOFunctions<Integer>(Integer.class);
		String idNameMap = "data/Kelli/FriendshipData/2010Study/"+participant+"_People.txt";
		ioHelp.fillNamesAndIDs(idNameMap);
		
		String idealFile = "data/Jacob/Ideal/"+participant+"_ideal.txt";		
		Collection<Set<Integer>> ideals = ioHelp.loadIdealGroups(idealFile);
		
		String inRelationships = "data/Kelli/FriendshipData/2010Study/"+participant+"_MutualFriends.txt";
		UndirectedGraph<Integer, DefaultEdge> graph = ioHelp.createUIDGraph(inRelationships);		
		
		File maximalCliqueFile = new File("data/Kelli/Cliques/MaximalCliques/"+participant+"_MaximalCliques.txt");
		Collection<Set<Integer>> maximalCliques = getMaximalCliques(graph, maximalCliqueFile);
		
				
		for(int weightsPos = 0; weightsPos < weights.length; weightsPos++){
			double[] weightVals = weights[weightsPos];
			
			for(Set<Integer> ideal: ideals){
				
				SeedFinder<Integer> seedFinder = new SeedFinder<Integer>();
				
				
				for(int seedSize =2; seedSize < ideal.size(); seedSize++){
					Set<Integer> seed = seedFinder.findPsuedoRandomSeed(ideal, seedSize);
					
					double seedRatio = ((double) seedSize)/ideal.size();
					if(seedRatio > SEED_RATIO_LIMIT){
						break;
					}
					
					for(int sOrD=0; sOrD<2; sOrD++){
						SeededHybridGroupFinder<Integer> finder = new SeededHybridGroupFinder<Integer>();
						String[] results = finder.findGroupByMerging(weightVals[0], weightVals[1], seed, maximalCliques, ideal, sOrD==0);
						System.out.print(""+seed.size()+","+weightVals[0]+","+weightVals[1]+","+participant+","+(sOrD==0));
						for(int i=0; i<results.length; i++){
							System.out.print(","+results[i]);
						}
						System.out.println();
					}
				}
				
			}
			
		}
	}
	
	public Collection<Set<Integer>> getMaximalCliques(UndirectedGraph<Integer, DefaultEdge> graph, File maximalCliqueFile){
		
		Collection<Set<Integer>> maximalCliques;
		if(maximalCliqueFile == null || !maximalCliqueFile.exists()){		
			BronKerboschCliqueFinder<Integer, DefaultEdge> BKcliqueFind = new FileAndMemoryBasedBronKerboschCliqueFinder<Integer, DefaultEdge>(graph);
			maximalCliques = BKcliqueFind.getAllMaximalCliques();
			if(maximalCliqueFile != null){
				ioHelp.printCliqueIDsToFile(maximalCliqueFile.getPath(), maximalCliques);
				System.out.println("Can be found in "+maximalCliqueFile.getPath());
			}
		}else{
			//System.out.println("Loading cliques from file "+maximalCliqueFile.getPath());
			maximalCliques = ioHelp.loadCliqueIDs(maximalCliqueFile.getPath());
		}
		

		//compareCliques();
		//status message
		//status message: all done :)

		return maximalCliques;
	}
	
	public static void main(String[] args){
		
		findAllLCMASeededGroupsbyMerging();
		//findAllLCMASeededGroupsbyMatching();
		
		//findAllHybridSeededGroupsByMerging();
		//findAllHybridSeededGroupsByMatching();
	}
}
