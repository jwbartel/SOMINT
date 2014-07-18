/*
 * An extension of the jgrapht library class to allow creation of maximal cliques in memory and on disk
 */
package util.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.BronKerboschCliqueFinder;

import util.tools.io.CollectionValueParser;
import util.tools.io.ValueParser;


public class FileAndMemoryBasedBronKerboschCliqueFinder<V, E> extends BronKerboschCliqueFinder<V, E>
{
    //~ Instance fields --------------------------------------------------------

	private File tempCliqueFile;
	private FileWriter cliqueWriter;
    private Collection<Set<V>> cliques;
    private final Graph<V, E> graph;
    private final ValueParser<V> parser;
    private final Integer maxVerticesInMemory;

    //private Collection<Set<V>> cliques;

    //~ Constructors -----------------------------------------------------------

    /**
     * Creates a new clique finder.
     *
     * @param graph the graph in which cliques are to be found; graph must be
     * simple
     */
    public FileAndMemoryBasedBronKerboschCliqueFinder(Graph<V, E> graph)
    {
    	super(graph);
        this.graph = graph;
        this.parser = null;
        this.maxVerticesInMemory = null;
    }

    /**
     * Creates a new clique finder.
     *
     * @param graph the graph in which cliques are to be found; graph must be
     * simple
     * @param parser the parser used for file storage and retrieval to avoid 
     * OutOfMemory exceptions
     */
    public FileAndMemoryBasedBronKerboschCliqueFinder(Graph<V, E> graph, ValueParser<V> parser)
    {
    	super(graph);
        this.graph = graph;
        this.parser = parser;
        this.maxVerticesInMemory = null;
    }
    


    /**
     * Creates a new clique finder.
     *
     * @param graph the graph in which cliques are to be found; graph must be
     * simple
     * @param parser the parser used for file storage and retrieval to avoid 
     * OutOfMemory exceptions
     * @param maxVerticesInMemory the maximum number of vertices a graph can
     * have where the clique finder will run all operations in memory rather
     * than on file
     */
    public FileAndMemoryBasedBronKerboschCliqueFinder(Graph<V, E> graph, ValueParser<V> parser,
    		Integer maxVerticesInMemory)
    {
    	super(graph);
        this.graph = graph;
        this.parser = parser;
        this.maxVerticesInMemory = maxVerticesInMemory;
    }

    //~ Methods ----------------------------------------------------------------

    /**
     * Finds all maximal cliques of the graph. A clique is maximal if it is
     * impossible to enlarge it by adding another vertex from the graph. Note
     * that a maximal clique is not necessarily the biggest clique in the graph.
     *
     * @return Collection of cliques (each of which is represented as a Set of
     * vertices) 
     */
    public Collection<Set<V>> getAllMaximalCliques() {

		if (parser == null || (maxVerticesInMemory != null
				&& maxVerticesInMemory >= graph.vertexSet().size())) {
			return super.getAllMaximalCliques();
		}
    	System.out.println("Graph is too large. Finding cliques on file rather than in memory.");
    	try {
			findTempCliqueFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
        List<V> potential_clique = new ArrayList<V>();
        List<V> candidates = new ArrayList<V>();
        List<V> already_found = new ArrayList<V>();
        candidates.addAll(graph.vertexSet());
        findCliquesOnFile(potential_clique, candidates, already_found);
        if (tempCliqueFile != null) {
        	cliques = readCliques();
            tempCliqueFile.delete();
            tempCliqueFile = null;
        }
        return cliques;
    }
    
    private synchronized void findTempCliqueFile() throws IOException {
    	if (parser == null) {
    		cliques = new HashSet<Set<V>>();
    		tempCliqueFile = null;
    		cliqueWriter = null;
    		return;
    	}
    	int tempCount = 0;
    	while (true) {
    		//tempCliqueFile = new File("temp_cliques"+tempCount);
    		tempCliqueFile = new File("/afs/cs.unc.edu/home/bartel/fbfriendslist/data/log/temp_cliques"+tempCount);
    		if (!tempCliqueFile.exists()) {
    			tempCliqueFile.createNewFile();
    			break;
    		}
    		tempCount++;
    	}
    	cliqueWriter = new FileWriter(tempCliqueFile);
    }
    
    private Collection<Set<V>> readCliques() {
    	try{
    		cliqueWriter.flush();
    		cliqueWriter.close();

    		cliques = new ArrayList<Set<V>>();

    		CollectionValueParser<V> collectionParser = new CollectionValueParser<V>(parser);
    		System.out.println(tempCliqueFile.getName());
    		BufferedReader bf = new BufferedReader(new FileReader(tempCliqueFile));
    		while (true) {
				String line = bf.readLine();
				if(line ==null){
					break;
				}
				Set<V> clique = new HashSet<V>(collectionParser.parse(line));
				cliques.add(clique);
    		}
//    		Scanner scanner = new Scanner(tempCliqueFile);
//    		while (scanner.hasNextLine()) {
//    			String line = scanner.nextLine();
//    			Set<V> clique = new HashSet<V>(collectionParser.parse(line));
//    			cliques.add(clique);
//    		}
//    		scanner.close();
    		return cliques;
    	} catch (IOException e) {
    		throw new RuntimeException(e);
    	}
    }

    private void findCliquesOnFile(List<V> potential_clique, List<V> candidates, List<V> already_found)
    {
        List<V> candidates_array = new ArrayList<V>(candidates);
        if (!end(candidates, already_found)) {
            // for each candidate_node in candidates do
            for (V candidate : candidates_array) {
                List<V> new_candidates = new ArrayList<V>();
                List<V> new_already_found = new ArrayList<V>();

                // move candidate node to potential_clique
                potential_clique.add(candidate);
                candidates.remove(candidate);

                // create new_candidates by removing nodes in candidates not
                // connected to candidate node
                for (V new_candidate : candidates) {
                    if (graph.containsEdge(candidate, new_candidate)) {
                        new_candidates.add(new_candidate);
                    } // of if
                } // of for

                // create new_already_found by removing nodes in already_found
                // not connected to candidate node
                for (V new_found : already_found) {
                    if (graph.containsEdge(candidate, new_found)) {
                        new_already_found.add(new_found);
                    } // of if
                } // of for

                // if new_candidates and new_already_found are empty
                if (new_candidates.isEmpty() && new_already_found.isEmpty()) {
                    // potential_clique is maximal_clique
                	if (cliqueWriter != null) {
                		try {
							cliqueWriter.write("" + new HashSet<V>(potential_clique));
	                        cliqueWriter.write("\n");
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
                	} else {
                		cliques.add(new HashSet<V>(potential_clique));
                	}
                } // of if
                else {
                    // recursive call
                    findCliquesOnFile(potential_clique, new_candidates, new_already_found);
                } // of else

                // move candidate_node from potential_clique to already_found;
                already_found.add(candidate);
                potential_clique.remove(candidate);
            } // of for
        } // of if
    }

    private boolean end(List<V> candidates, List<V> already_found)
    {
        // if a node in already_found is connected to all nodes in candidates
        boolean end = false;
        int edgecounter;
        for (V found : already_found) {
            edgecounter = 0;
            for (V candidate : candidates) {
                if (graph.containsEdge(found, candidate)) {
                    edgecounter++;
                } // of if
            } // of for
            if (edgecounter == candidates.size()) {
                end = true;
            }
        } // of for
        return end;
    }
}
