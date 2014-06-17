package prediction.features.messages;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import snml.dataconvert.BasicFeatureExtractor;
import snml.dataconvert.IntermediateDataInitializer;
import snml.dataconvert.IntermediateDataSet;
import snml.dataimport.MessageData;
import snml.dataimport.ThreadData;
import snml.dataimport.ThreadDataSet;
import snml.rule.basicfeature.IBasicFeatureRule;
import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

/**
 * Extracts intermediate data sets for use with SNML
 */
public class MessageIntermediateDataSetExtractor<Collaborator, Message extends SingleMessage<Collaborator>> {

	final Map<Collaborator, Integer> creatorIds = new HashMap<>();
	final Map<Collaborator, Integer> collaboratorIds = new HashMap<>();
	final WordIndexFinder wordIndexFinder;
	
	public MessageIntermediateDataSetExtractor(Collection<Message> allPossibleMessages, Set<String> stopWords) {
		
		Set<Collaborator> creators = new HashSet<>();
		Set<Collaborator> collaborators = new HashSet<>();
		Set<String> titleWords = new TreeSet<>();

		for (Message message : allPossibleMessages) {
			creators.addAll(message.getCreators());
			collaborators.addAll(message.getCollaborators());
			titleWords.addAll(SimpleWordIndexFinder.parseWords(message.getTitle()));
		}
		titleWords.removeAll(stopWords);
		wordIndexFinder = new SimpleWordIndexFinder(titleWords, stopWords);
		
		int creatorId = 1;
		for (Collaborator creator : creators) {
			creatorIds.put(creator, creatorId);
			creatorId++;
		}
		
		int collaboratorId = 1;
		for (Collaborator collaborator : collaborators) {
			collaboratorIds.put(collaborator, collaboratorId);
			collaboratorId++;
		}
		
	}
	
	private int[] getCreators(Message message) {
		Set<Integer> creators = new TreeSet<>();
		for (Collaborator creator : message.getCreators()) {
			Integer id = creatorIds.get(creator);
			if (id != null) {
				creators.add(id);
			}
		}
		int[] creatorArray = new int[creators.size()];
		int pos = 0;
		for (int creatorId : creators) {
			creatorArray[pos] = creatorId;
			pos++;
		}
		return creatorArray;
	}
	
	private int[] getCollaborators(Message message) {
		Set<Integer> collaborators = new TreeSet<>();
		for (Collaborator collaborator : message.getCreators()) {
			Integer id = collaboratorIds.get(collaborator);
			if (id != null) {
				collaborators.add(id);
			}
		}
		int[] collaboratorArray = new int[collaborators.size()];
		int pos = 0;
		for (int collaboratorId : collaborators) {
			collaboratorArray[pos] = collaboratorId;
			pos++;
		}
		return collaboratorArray;
	}
	
	private String[] getTitleWords(Message message) {
		Set<String> titleWords = SimpleWordIndexFinder.parseWords(message.getTitle());
		return titleWords.toArray(new String[0]);
	}
	
	private MessageData extractMessageData(Message message) {
		MessageData messageData = new MessageData();

		messageData.addAttribute(MessageDataConfig.TITLE, message.getTitle());
		messageData.addAttribute(MessageDataConfig.TITLE_WORDS, getTitleWords(message));
		
		messageData.addAttribute(MessageDataConfig.CREATORS, getCreators(message));
		messageData.addAttribute(MessageDataConfig.COLLABORATORS, getCollaborators(message));
		messageData.addAttribute(MessageDataConfig.DATE_DEFAULT, message.getLastActiveDate());
		return messageData;
	}
	
	public WordIndexFinder getWordIndexFinder() {
		return wordIndexFinder;
	}

	public ThreadDataSet extractThreadData(
			Collection<MessageThread<Collaborator, Message>> threads) {

		ThreadDataSet threadDataSet = new ThreadDataSet();
		for (MessageThread<Collaborator, Message> thread : threads) {
			ThreadData threadData = new ThreadData();
			for (Message message : thread.getThreadedActions()) {
				MessageData messageData = extractMessageData(message);
				threadData.addMsgData(messageData);
			}
			threadDataSet.addThreadData(threadData);
		}
		return threadDataSet;
	}

	public IntermediateDataSet extractFeatureData(Collection<MessageThread<Collaborator, Message>> threads,
			String dataSetName,
			IBasicFeatureRule[] featureRules,
			IntermediateDataInitializer initializer) throws Exception {
		ThreadDataSet threadDataSet = extractThreadData(threads);
		BasicFeatureExtractor basicExtractor = new BasicFeatureExtractor(initializer);
		return basicExtractor.extract(threadDataSet, dataSetName, featureRules);
	}
	


	public IntermediateDataSet extractAllIntermediateData(Collection<MessageThread<Collaborator, Message>> threads,
			String dataSetName,
			IBasicFeatureRule[] featureRules,
			IBasicFeatureRule[] predictedFeatureRules,
			IntermediateDataInitializer initializer) throws Exception {

		IntermediateDataSet featureSet = extractFeatureData(threads, dataSetName + "-predictors",
				predictedFeatureRules, initializer);
		IntermediateDataSet predictedFeatureSet = extractFeatureData(threads, dataSetName
				+ "-predictables", predictedFeatureRules, initializer);
		return featureSet.mergeByAttributes(predictedFeatureSet);
	}

}
