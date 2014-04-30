package priority.ml;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public interface DataPointRetriever {

	Collection<InteractionDataPoint> getInteractionPoints(Collection<ThreadDataPoint> threads);
	
	Collection<ThreadDataPoint> getThreads(int maxNum);
	Collection<ThreadDataPoint> getAndWriteThreads(int maxNum, File dest) throws IOException;
	
}
