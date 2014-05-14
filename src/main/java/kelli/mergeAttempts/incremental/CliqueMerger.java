package kelli.mergeAttempts.incremental;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;


public interface CliqueMerger {
	   public boolean mergeCliques(Set<Integer> largerClique, Set<Integer> smallerClique, int largerCliqueNumber, int smallerCliqueNumber);
	   boolean checkToRemoveCliques(int passNumber);
	   public void init (float theVaryPercentage, 
				Collection<Set<Integer>> theMergedCliques, 
				HashMap<Integer, Boolean> theToRemoveClique, 
				IncrementalMerger theIncrementalMerger);

}
