package kelli.mergeAttempts.incremental;

import java.util.HashMap;
import java.util.Map;

public class CliqueMergerRegistry {
	static Map<MergeKind, CliqueMerger> mergeKindToCliqueMerger = new HashMap();
	static {
		mergeKindToCliqueMerger.put(MergeKind.INTERSECTION_MERGE, new AnIntersectionMerger());
		mergeKindToCliqueMerger.put(MergeKind.DIFFERENCE_MERGE, new ADifferenceMerger());
		mergeKindToCliqueMerger.put(MergeKind.HYBRID_MERGE, new AHybridMerge());
	}
	public static CliqueMerger getCliqueMerger (MergeKind mergeKind) {
		return mergeKindToCliqueMerger.get(mergeKind);
	}

}
