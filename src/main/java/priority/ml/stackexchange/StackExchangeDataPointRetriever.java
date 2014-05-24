package priority.ml.stackexchange;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import priority.ml.DataPointRetriever;
import priority.ml.InteractionDataPoint;
import priority.ml.ThreadDataPoint;
import priority.ml.VectorGenerator;
import util.tools.ResultsZipManager;

public class StackExchangeDataPointRetriever implements DataPointRetriever {

	private static final File TEMP_FOLDER = new File("data/temp/StackOverflow");
	private static final String ZIP_FILE_PARENT_FOLDER = "data/Jacob/StackOverflow/";
	private static final String INTERACTIONS_FOLDER = "interactions";
	private static final String TAGS_FILE = "tags.txt";
	private static final String THREADS_FILE = "threads.csv";
	private static final String ENTRIES_FILE = "entryVectors.csv";
	private static final String GOALS_FILE = "goalVectors.csv";
	
	
	private Connection connection;
	
	private Connection getConnection() throws SQLException, IOException {
		if (connection == null || connection.isClosed()) {

			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			System.out.print("mysql host:");
			String host = in.readLine();
			System.out.print("database:");
			String db = in.readLine();
			System.out.print("user:");
			String user = in.readLine();
			System.out.print("password:");
			String pw = in.readLine();
			in.close();
			
			String connectionAddr = "jdbc:mysql://"+host+"/"+db+"?"+"user="+user+"&password="+pw;

			connection = DriverManager.getConnection(connectionAddr);
		}
		return connection;
	}
	
	public StackExchangeDataPointRetriever() throws IOException, SQLException {
		
		getConnection();
	}
	
	public Collection<ThreadDataPoint> getThreads(int maxNum) {
		
		try {
			Statement statement = getConnection().createStatement();
			ResultSet result = statement.executeQuery(
					"select rootID, tags, time_first_interaction, messages from threads order by rand() limit "
					+maxNum);

			Collection<ThreadDataPoint> dataPoints = new ArrayList<ThreadDataPoint>();
			while (result.next()) {
				int rootID = result.getInt("rootID");
				String tagsStr = result.getString("tags");
				Timestamp firstInteraction = result.getTimestamp("time_first_interaction");
				int interactionCount = result.getInt("messages");
				
				Collection<String> tags = new TreeSet<String>();
				String[] tagArray = tagsStr.split("(<>)|(>)|(<)");
				for (String tag: tagArray) {
					if (tag.length() > 0) {
						tags.add(tag);
					}
				}
				
				dataPoints.add(new ThreadDataPoint(rootID, tags, firstInteraction, interactionCount));
			}
			return dataPoints;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Collection<ThreadDataPoint> getAndWriteThreads(int maxNum, File dest) throws IOException {
		
		if (!dest.getParentFile().exists()) {
			dest.getParentFile().mkdirs();
		}
		
		Collection<ThreadDataPoint> threads = getThreads(maxNum);
		
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		boolean isFirst = true;
		for (ThreadDataPoint thread: threads) {
			if (isFirst) {
				isFirst = false;
			} else {
				out.newLine();
			}
			out.write(thread.toString());
		}
		out.flush();
		out.close();
		
		return threads;
	}	

	public Collection<InteractionDataPoint> getInteractionPoints(Collection<ThreadDataPoint> threads) {
		try {
			Collection<InteractionDataPoint> dataPoints = new ArrayList<InteractionDataPoint>();
			
			
			for (ThreadDataPoint thread: threads) {
				Statement statement = getConnection().createStatement();
				Integer seenItems = 1;
				Timestamp timeOfLast = null;

				InteractionDataPoint pastPoint = null;
				ResultSet result = statement.executeQuery(
						"select type, creationDate, body from interactions where rootID = "
						+ thread.getId() + " order by creationDate");
				while (result.next()) {
					String type = result.getString("type");
					Timestamp creationDate = result.getTimestamp("creationDate");
					String body = result.getString("body");
					body = body.replaceAll("<[^>]*>", "").replaceAll("&gt;", ">").replaceAll("&lt;", "<");
					
					int threadID = thread.getId();
					Long totalTime = creationDate.getTime() - thread.getTimeThreadStart().getTime();
					Double averageTime = ((double) totalTime)/seenItems;
					Long timeSinceLast = (timeOfLast != null)? creationDate.getTime() - timeOfLast.getTime() : 0;
					
					if (pastPoint != null) {
						pastPoint.setTimeToNext((double) (creationDate.getTime() - timeOfLast.getTime()));
					}
					
					pastPoint = new InteractionDataPoint(threadID, type, seenItems, totalTime, averageTime,
							timeSinceLast, thread.getTags(), body);
					dataPoints.add(pastPoint);
					
					seenItems++;
					timeOfLast = creationDate;
				}
				
			}
			return dataPoints;

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Collection<InteractionDataPoint> getAndWriteInteractionPoints(
			Collection<ThreadDataPoint> threads,  File destFolder) throws IOException {
		
		destFolder.mkdirs();
		
		Collection<InteractionDataPoint> dataPoints = getInteractionPoints(threads);
		int num = 1;
		for (InteractionDataPoint dataPoint: dataPoints) {
			BufferedWriter out = new BufferedWriter(new FileWriter(new File(destFolder, ""+num)));
			out.write(dataPoint.toString());
			out.flush();
			out.close();
			
			num++;
		}
		return dataPoints;
	}
	
	public static Collection<InteractionDataPoint> loadInteractionDataPoints(File file)
		throws ZipException, IOException {
		
		ZipFile zipFile = new ZipFile(file);
		String prefix = INTERACTIONS_FOLDER;
		
		Collection<InteractionDataPoint> interactions = new ArrayList<InteractionDataPoint>();
		
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if (entry.getName().startsWith(prefix)) {
				InputStream is = zipFile.getInputStream(entry);
				interactions.add(getInteractionDataPoint(is));
				
			}
		}
		zipFile.close();
		return interactions;
	}
	
	public static InteractionDataPoint getInteractionDataPoint(InputStream is) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(is));
		
		int threadID = 0;
		String type = null;
		Integer totalInteractions = null;
		Long totalTime = null;
		Double averageTimeBetweenInteractions = null;
		Long timeSinceLastInteraction = null;
		Collection<String> tags = null;
		String body = null;
		Double timeToNext = null;
		
		String line = in.readLine();
		boolean addToBody = false;
		while (line != null) {
			
			if (addToBody) {
				body += "\n"+ line;
			} else if (line.startsWith("Thread ID:")) {
				threadID = Integer.parseInt(line.substring("Thread ID:".length()));
			} else if (line.startsWith("Type:")) {
				type = line.substring("Type:".length());
			} else if (line.startsWith("Seen Interactions:")) {
				totalInteractions = Integer.parseInt(line.substring("Seen Interactions:".length()));
			} else if (line.startsWith("Time so far:")) {
				totalTime = Long.parseLong(line.substring("Time so far:".length()));
			} else if (line.startsWith("Average time between interactions:")) {
				averageTimeBetweenInteractions = 
					Double.parseDouble(line.substring("Average time between interactions:".length()));
			} else if (line.startsWith("Time since last interaction:")) {
				timeSinceLastInteraction = Long.parseLong(line.substring("Time since last interaction:".length()));
			} else if (line.startsWith("Time to next:")) {
				timeToNext = Double.parseDouble(line.substring("Time to next:".length()));
			} else if (line.startsWith("Tags:")) {
				String tagStr = line.substring("Tags:".length()+1, line.length()-1);
				String[] tagSplit = tagStr.split(",");
				
				tags = new TreeSet<String>();
				for (String tag: tagSplit) {
					tags.add(tag.trim());
				}
			} else if (line.startsWith("Body:")) {
				body = line.substring("Body:".length());
				addToBody = true;
			}
			
			line = in.readLine();
		}
		
		InteractionDataPoint interaction = new InteractionDataPoint(threadID, type, totalInteractions, totalTime,
				averageTimeBetweenInteractions, timeSinceLastInteraction, tags, body);
		interaction.setTimeToNext(timeToNext);
		return interaction;
	}
	
	public static Map<String, Integer> getAndWriteTagPositions(
		Collection<InteractionDataPoint> interactions, File tempDirectory, File zipFile) throws IOException {
		
		Set<String> tags = getAndWriteTags(interactions, tempDirectory, zipFile);
		
		Map<String, Integer> tagPositions = new TreeMap<String, Integer>();
		int pos = 0;
		for (String tag: tags) {
			tagPositions.put(tag, pos);
			pos++;
		}
		return tagPositions;
	}
	
	public static Set<String> getAndWriteTags(Collection<InteractionDataPoint> interactions, 
		File tempDirectory, File zipFile) throws IOException {
		
		if (tempDirectory.exists()) {
			tempDirectory.delete();
		}
		tempDirectory.mkdirs();
		
		Set<String> tags = new HashSet<String>();
		
		for (InteractionDataPoint interaction: interactions) {
			tags.addAll(interaction.getTags());
		}
		
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(tempDirectory, TAGS_FILE)));
		boolean first = true;
		for (String tag: tags) {
			if (first) {
				first = false;
			} else {
				out.newLine();
			}
			out.write(tag);
		}
		out.flush();
		out.close();
		
		ArrayList<File> srcs = new ArrayList<File>();
		srcs.add(tempDirectory);
		
		ResultsZipManager.writeToZipFile(zipFile, srcs);
		
		return tags;
	}
	
	public static void writeVectoredData(Collection<InteractionDataPoint> interactions, Map<String, Integer> tagPositions,
			File tempDirectory, File zipFile) throws IOException {
		
		if (tempDirectory.exists()) {
			tempDirectory.delete();
		}
		tempDirectory.mkdirs();
		
		//double[][] entries = VectorGenerator.getKnownData(interactions, tagPositions);
		//double[] goals = VectorGenerator.getGoalData(interactions);
		
		BufferedWriter out = new BufferedWriter(new FileWriter(new File(tempDirectory, ENTRIES_FILE)));
		VectorGenerator.writeKnownData(out, interactions, tagPositions);
		/*for (int i=0; i<entries.length; i++) {
			String line = "";
			for (int j=0; j<entries[i].length; j++) {
				if (j>0) {
					line += ",";
				}
				line += entries[i][j];
			}
			out.write(line);
			out.newLine();
		}*/
		out.flush();
		out.close();
		
		out = new BufferedWriter(new FileWriter(new File(tempDirectory, GOALS_FILE)));
		VectorGenerator.writeGoalData(out, interactions);
		/*for (int i=0; i<goals.length; i++) {
			out.write(""+goals[i]);
			out.newLine();
		}*/
		out.flush();
		out.close();
		
		ArrayList<File> srcs = new ArrayList<File>();
		srcs.add(tempDirectory);
		
		ResultsZipManager.writeToZipFile(zipFile, srcs);
	}
	
	public void getSamples(int maxNumThreads) throws IOException, SQLException{
		
		System.out.println("=============== Sampling "+maxNumThreads + " Threads ===============");
		File tempParentFolder = TEMP_FOLDER;
		File zipFile = new File("data/Jacob/StackOverflow/samples for "+maxNumThreads+" threads.zip");
		
		if (zipFile.exists()) {
			zipFile.delete();
		}
		
		if (tempParentFolder.exists()) {
			tempParentFolder.delete();
		}
		tempParentFolder.mkdirs();
		
		Collection<ThreadDataPoint> threads = 
				getAndWriteThreads(maxNumThreads, new File(tempParentFolder, THREADS_FILE));
		System.out.println("Got theads data.");
		Collection<InteractionDataPoint> interactions = 
				getAndWriteInteractionPoints(threads, new File(tempParentFolder, INTERACTIONS_FOLDER));
		System.out.println("Got interactions data.");
		ArrayList<File> srcs = new ArrayList<File>();
		srcs.add(tempParentFolder);
		ResultsZipManager.writeToZipFile(zipFile, srcs);
		tempParentFolder.delete();
		System.out.println("Getting tag position data.");
		Map<String, Integer> tagPositions = getAndWriteTagPositions(interactions, tempParentFolder, zipFile);
		System.out.println("Writing vectored data...");
		writeVectoredData(interactions, tagPositions, tempParentFolder, zipFile);
		
		System.out.println("===================================================");
	}
	
	
	private static int NUM_THREADS = 20;
	
	public static void main(String[] args) throws IOException, SQLException {
		
		StackExchangeDataPointRetriever retriever = new StackExchangeDataPointRetriever();
		retriever.getSamples(10);
		retriever.getSamples(20);
		retriever.getSamples(100);
		retriever.getSamples(1000);
		/*File tempParentFolder = new File("data/temp/StackOverflow");
		
		File zipFile = new File("data/Jacob/StackOverflow/samples for "+NUM_THREADS+" threads.zip");
		
		
		Collection<InteractionDataPoint> interactions = loadInteractionDataPoints(zipFile);
		Map<String, Integer> tagPositions = getAndWriteTagPositions(interactions, tempParentFolder, zipFile);
		writeVectoredData(interactions, tagPositions, tempParentFolder, zipFile);
		
		
		/*double[][] entries = VectorGenerator.getKnownData(interactions, tagPositions);
		double[] goals = VectorGenerator.getGoalData(interactions);*/
		
		
		/*if(tempParentFolder.exists()){
			tempParentFolder.delete();
		}
		tempParentFolder.mkdirs();
		
		StackExchangeDataPointRetriever retriever = new StackExchangeDataPointRetriever();
		Collection<ThreadDataPoint> threads = 
				retriever.getAndWriteThreads(NUM_THREADS, new File(tempParentFolder, THREADS_FILE));
		System.out.println("Threads: "+ threads);
		retriever.getAndWriteInteractionPoints(threads, new File(tempParentFolder, INTERACTIONS_FOLDER));
		
		ArrayList<File> srcs = new ArrayList<File>();
		srcs.add(tempParentFolder);
		
		ResultsZipManager.writeToZipFile(zipFile, srcs);
		
		tempParentFolder.deleteOnExit();*/
	}
}
