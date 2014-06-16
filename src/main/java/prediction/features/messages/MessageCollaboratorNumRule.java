package prediction.features.messages;

import java.util.HashSet;
import java.util.Set;

import snml.dataimport.MessageData;
import snml.dataimport.ThreadData;
import snml.rule.NumericFeatureRule;
import snml.rule.basicfeature.IBasicFeatureRule;

/**
 * Extract the number of collaborators of a thread
 */
public class MessageCollaboratorNumRule extends NumericFeatureRule implements IBasicFeatureRule {

	/**
	 * Create an EmailRecipientNumRule
	 * 
	 * @param destFeatureName
	 *            name for extracted feature
	 */
	public MessageCollaboratorNumRule(String destFeatureName) {
		super(destFeatureName);
	}

	/**
	 * Extract the number of collaborators from given thread
	 * 
	 * @param aThread
	 *            the source thread data
	 * @return number of collaborators of the thread
	 * @throws Exception
	 *             when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {

		Set<Integer> collaborators = new HashSet<>();
		for (int k=0; k < aThread.size(); k++) {
			MessageData msg = aThread.getKthEarlest(k);
			int[] messageCollaborators = (int[]) msg.getAttribute(MessageDataConfig.COLLABORATORS);
			for (int collaborator : messageCollaborators) {
				collaborators.add(collaborator);
			}
		}
		return collaborators.size();
	}

}
