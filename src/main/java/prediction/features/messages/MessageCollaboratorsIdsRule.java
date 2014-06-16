package prediction.features.messages;

import snml.dataimport.MessageData;
import snml.dataimport.ThreadData;
import snml.rule.NumericVectorFeatureRule;
import snml.rule.basicfeature.IBasicFeatureRule;

/**
 * Extract the numeric vector of whether an address appeared as a collaborator
 * of a thread
 */
public class MessageCollaboratorsIdsRule extends NumericVectorFeatureRule implements
		IBasicFeatureRule {

	/**
	 * Create an binary feature rule for extracting recipient id
	 * 
	 * @param destFeatureName
	 *            name for extracted feature
	 * @param totalRecipientNum
	 *            largest address id
	 */
	public MessageCollaboratorsIdsRule(String featureName, int totalRecipientNum) {
		super(featureName, totalRecipientNum);
	}

	/**
	 * Extract the numeric vector of whether an address is a collaborator of a
	 * given thread
	 * 
	 * @param aThread
	 *            the source thread data
	 * @return double array of whether addresses are collaborators
	 * @throws Exception
	 *             when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {
		double[] val = new double[length];
		for (int i = 0; i < val.length; i++) {
			val[i] = 0;
		}

		for (int k = 0; k < aThread.size(); k++) {
			MessageData msg = aThread.getKthEarlest(k);
			int[] collaborators = (int[]) msg.getAttribute(MessageDataConfig.COLLABORATORS);

			for (int i = 0; i < collaborators.length; i++) {
				val[collaborators[i] - 1] = 1;
			}
		}

		return val;
	}

}
