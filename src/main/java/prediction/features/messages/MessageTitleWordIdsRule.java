package prediction.features.messages;

import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;
import snml.dataimport.MessageData;
import snml.dataimport.ThreadData;
import snml.rule.NumericVectorFeatureRule;
import snml.rule.basicfeature.IBasicFeatureRule;

/**
 * Extract the numeric vector of whether an word appeared in the title of a
 * thread
 */
public class MessageTitleWordIdsRule extends NumericVectorFeatureRule implements IBasicFeatureRule {
	
	/**
	 * Create a factory to generate instances of the feature
	 * @param featureName
	 * 			The name of the feature
	 * @return The factory for the feature
	 */
	public static <Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>>
		FeatureRuleFactory<Collaborator, Message, ThreadType> factory(
				Class<Collaborator> collaboratorClass,
				Class<Message> messageClass,
				Class<ThreadType> threadClass,
				final String featureName) {
		
		return new FeatureRuleFactory<Collaborator, Message, ThreadType>() {

			@Override
			public IBasicFeatureRule create(
					ThreadSetProperties<Collaborator, Message, ThreadType> threadsProperties) {
				return new MessageTitleWordIdsRule(featureName, threadsProperties.getWordIndexFinder());
			}
		};
	}

	private WordIndexFinder wordIndexFinder;
	
	/**
	 * Create an binary feature rule for extracting title words
	 * 
	 * @param destFeatureName
	 *            name for extracted feature
	 * @param totalRecipientNum
	 *            largest address id
	 */
	public MessageTitleWordIdsRule(String featureName, WordIndexFinder wordIndexFinder) {
		super(featureName, wordIndexFinder.numWords());
		this.wordIndexFinder = wordIndexFinder;
	}

	/**
	 * Extract the numeric vector of whether a word in the title of a
	 * given thread
	 * 
	 * @param aThread
	 *            the source thread data
	 * @return double array of whether words are in the title
	 * @throws Exception
	 *             when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {
		double[] val = new double[length];
		for (int i = 0; i < val.length; i++) {
			val[i] = 0;
		}
		MessageData msg = aThread.getKthEarlest(0);
		String[] words = (String[]) msg.getAttribute(MessageDataConfig.TITLE_WORDS);

		for (String word : words) {
			Integer index = wordIndexFinder.indexOf(word);
			if (index != null) {
				val[index - 1] = 1;
			}
		}

		return val;
	}

}
