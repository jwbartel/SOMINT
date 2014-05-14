package priority.ml;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public abstract class VectorGenerator {
	
	static int numTypes = 3;

	public static void writeKnownData(BufferedWriter out,
		Collection<InteractionDataPoint> interactions, Map<String, Integer> tagPositions) throws IOException {
		
		for (InteractionDataPoint interaction: interactions) {
			
			double[] entry = new double[numTypes + tagPositions.size() + 4];
			
			if (interaction.getType().equals("question")) {
				entry[0] = 1;
			} else if (interaction.getType().equals("answer")) {
				entry[1] = 1;
			} else if (interaction.getType().equals("comment")) {
				entry[2] = 1;
			}
					
			Collection<String> tags = interaction.getTags();
			for (String tag: tags) {
				Integer tagPos = tagPositions.get(tag);
				if (tagPos != null) {
					entry[numTypes + tagPos] = 1.0;
				}
			}
			
			entry[numTypes + tagPositions.size()] = interaction.getTotalInteractions();
			entry[numTypes + tagPositions.size() + 1] = ((double) interaction.getTotalTime())/1000.0/3600.0;
			entry[numTypes + tagPositions.size() + 2] = ((double) interaction.getAverageTimeBetweenInteractions())/1000.0/3600.0;
			entry[numTypes + tagPositions.size() + 3] = ((double) interaction.getTimeSinceLastInteraction())/1000.0/3600.0;

			String line = "";
			for (int j=0; j<entry.length; j++) {
				if (j>0) {
					line += ",";
				}
				line += entry[j];
			}
			out.write(line);
			out.newLine();
		}
	}
	
	public static double[][] getKnownData(
		Collection<InteractionDataPoint> interactions, Map<String, Integer> tagPositions) {
		
		double[][] entries = new double[interactions.size()][numTypes + tagPositions.size() + 4];
		
		int pos = 0;
		for (InteractionDataPoint interaction: interactions) {
			
			double[] entry = new double[numTypes + tagPositions.size() + 4];
			
			if (interaction.getType().equals("question")) {
				entry[0] = 1;
			} else if (interaction.getType().equals("answer")) {
				entry[1] = 1;
			} else if (interaction.getType().equals("comment")) {
				entry[2] = 1;
			}
					
			Collection<String> tags = interaction.getTags();
			for (String tag: tags) {
				Integer tagPos = tagPositions.get(tag);
				if (tagPos != null) {
					entry[numTypes + tagPos] = 1.0;
				}
			}
			
			entry[numTypes + tagPositions.size()] = interaction.getTotalInteractions();
			entry[numTypes + tagPositions.size() + 1] = ((double) interaction.getTotalTime())/1000.0/3600.0;
			entry[numTypes + tagPositions.size() + 2] = ((double) interaction.getAverageTimeBetweenInteractions())/1000.0/3600.0;
			entry[numTypes + tagPositions.size() + 3] = ((double) interaction.getTimeSinceLastInteraction())/1000.0/3600.0;
			
			entries[pos] = entry;
			pos++;
		}
		
		return entries;
	}
	
	public static void writeGoalData(
			BufferedWriter out, Collection<InteractionDataPoint> interactions) throws IOException {
			for (InteractionDataPoint interaction: interactions) {
				double goal = ((double) interaction.getTimeToNext())/1000.0/3600.0;
				out.write(""+goal);
				out.newLine();
			}
	}
	
	public static double[] getGoalData(Collection<InteractionDataPoint> interactions) {
		
		double[] retVal = new double[interactions.size()];
		int pos = 0;
		for (InteractionDataPoint interaction: interactions) {
			retVal[pos] = ((double) interaction.getTimeToNext())/1000.0/3600.0;
			pos++;
		}
		return retVal;
	}

}