package prediction.features.messages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import snml.dataconvert.BasicFeatureExtractor;
import snml.dataconvert.IntermediateData;
import snml.dataconvert.IntermediateDataInitializer;
import snml.dataconvert.IntermediateDataSet;
import snml.dataconvert.IntermediateRecommendationDataInitializer;
import snml.dataconvert.RecommendationFeatureExtractor;
import snml.dataconvert.weka.WekaDataInitializer;
import snml.dataimport.MessageData;
import snml.dataimport.ThreadData;
import snml.dataimport.ThreadDataSet;
import snml.dataimport.email.EmailDataConfig;
import snml.rule.basicfeature.IBasicFeatureRule;
import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

/**
 * Extracts intermediate data sets for use with SNML
 */
public class MessageIntermediateDataSetExtractor<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator,Message>> {

	final ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties;
	
	public MessageIntermediateDataSetExtractor(ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties) {
		
		this.threadsProperties = threadsProperties;
		
	}
	
	private int[] getCreators(Message message) {
		Set<Integer> creators = new TreeSet<>();
		for (Collaborator creator : message.getCreators()) {
			Integer id = threadsProperties.getCreatorId(creator);
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
		for (Collaborator collaborator : message.getCollaborators()) {
			Integer id = threadsProperties.getCollaboratorId(collaborator);
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
		

		SimpleDateFormat dateFormat = new SimpleDateFormat(
				EmailDataConfig.DATEFORMAT_DEFAULT);
		messageData.addAttribute(MessageDataConfig.DATE_DEFAULT,
				dateFormat.format(message.getLastActiveDate()));
		return messageData;
	}
	
	public WordIndexFinder getWordIndexFinder() {
		return threadsProperties.getWordIndexFinder();
	}
	
	public ThreadData extractThreadDataItem(ThreadType thread) {
		ThreadData threadData = new ThreadData();
		for (Message message : thread.getThreadedActions()) {
			MessageData messageData = extractMessageData(message);
			threadData.addMsgData(messageData);
		}
		return threadData;
	}

	public ThreadDataSet extractThreadData(Collection<ThreadType> threads) {

		ThreadDataSet threadDataSet = new ThreadDataSet();
		for (ThreadType thread : threads) {
			ThreadData threadData = extractThreadDataItem(thread);
			threadDataSet.addThreadData(threadData);
		}
		return threadDataSet;
	}

	public IntermediateDataSet extractFeatureData(Collection<ThreadType> threads,
			String dataSetName,
			IBasicFeatureRule[] featureRules,
			IntermediateDataInitializer initializer) throws Exception {
		ThreadDataSet threadDataSet = extractThreadData(threads);
		BasicFeatureExtractor basicExtractor = new BasicFeatureExtractor(initializer);
		return basicExtractor.extract(threadDataSet, dataSetName, featureRules);
	}
	


	public IntermediateDataSet extractAllIntermediateData(Collection<ThreadType> threads,
			String dataSetName,
			IBasicFeatureRule[] featureRules,
			IBasicFeatureRule[] predictedFeatureRules,
			IntermediateDataInitializer initializer) throws Exception {

		IntermediateDataSet featureSet = extractFeatureData(threads, dataSetName + "-predictors",
				featureRules, initializer);
		IntermediateDataSet predictedFeatureSet = extractFeatureData(threads, dataSetName
				+ "-predictables", predictedFeatureRules, initializer);
		return featureSet.mergeByAttributes(predictedFeatureSet);
	}

	public IntermediateData extractSingleItem(ThreadType thread,
			String dataSetName,
			IBasicFeatureRule[] featureRules,
			IBasicFeatureRule[] predictedFeatureRules,
			IntermediateDataInitializer initializer) throws Exception {
		
		Collection<ThreadType> threadSet = new ArrayList<>();
		threadSet.add(thread);

		IntermediateDataSet intermediateSet = extractAllIntermediateData(threadSet, dataSetName,
				featureRules, predictedFeatureRules, initializer);
		intermediateSet.setTargetIndex();
		if (intermediateSet.size() > 0) {
			return intermediateSet.getDataInstance(0);
		}
		return null;
		
	}
}
