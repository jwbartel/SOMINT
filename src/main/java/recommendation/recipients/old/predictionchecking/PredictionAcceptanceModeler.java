package recommendation.recipients.old.predictionchecking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import data.parsers.AddressParser;
import data.parsers.MessageFrequencyParser;
import data.structures.AddressLists;
import data.structures.ComparableSet;
import recommendation.recipients.old.contentbased.AdaptedAgedContentAccount;
import bus.accounts.Account;

public class PredictionAcceptanceModeler extends AdaptedAgedContentAccount {

	public static final int PREDICTION_LIST_SIZE = 4;
	public static final int INITIAL_SEED_SIZE = 2;

	protected int addressesUnknown = 0;
	protected int addressesPredictedFor = 0;;
	protected int addressesRight = 0;
	protected int groupsRight = 0;
	protected int groupListsGenerated = 0;

	public PredictionAcceptanceModeler(File accountFolder) throws IOException, MessagingException {
		super(accountFolder);
	}

	public PredictionAcceptanceModeler(String accountFolder) throws IOException, MessagingException {
		super(accountFolder);
	}

	public int[] modelPredictionAcceptances() throws IOException, MessagingException {

		int trainedMsgs = (int) (totalMsgs * TRAINING_RATIO);
		int i = 0;
		for (; i < trainedMsgs; i++) {
			getNextMessage();
		}

		for (; i < totalMsgs; i++) {
			getNextMessage();

			ArrayList<String> correctOrderedEmails = getOrderedEmails(currMessage);// getCurrMessage());

			Set<String> seed = new TreeSet<String>();
			while (correctOrderedEmails.size() > 0 && seed.size() < INITIAL_SEED_SIZE) {
				String seedVal = correctOrderedEmails.get(0);
				seed.add(seedVal);
				while (correctOrderedEmails.remove(seedVal)) {
				}
			}

			addressesUnknown += correctOrderedEmails.size();

			File addressFile = new File(currMessage + Account.ADDR_FILE_SUFFIX);
			if (!addressFile.exists()) {
				Account.saveAddresses(new File(currMessage), addressFile);
			}

			AddressLists addressLists = new AddressLists(addressFile);
			String from = addressLists.getFrom().get(0);

			modelPredictionsForCurrMessage(from, seed, correctOrderedEmails);
		}

		if (PredictionMaker.predictIndividuals) {
			int[] toReturn = new int[3];
			toReturn[0] = addressesRight;
			toReturn[1] = addressesPredictedFor;
			toReturn[2] = addressesUnknown;
			return toReturn;
		} else {
			int[] toReturn = new int[4];
			toReturn[0] = groupsRight;
			toReturn[1] = groupListsGenerated;
			toReturn[2] = addressesRight;
			toReturn[3] = addressesUnknown;
			return toReturn;
		}
	}

	protected void modelPredictionsForCurrMessage(String sender, Set<String> seed,
			ArrayList<String> correctOrderedEmails) throws IOException, MessagingException {

		Map<String, Integer> wordCounts = null;
		if (PredictionMaker.useTextContent) {
			MessageFrequencyParser parser = new MessageFrequencyParser(currMessage);
			wordCounts = parser.getAllWordsWithCounts();
		}

		while (correctOrderedEmails.size() > 0) {
			PredictionMaker predictionMaker = new MultispacePredictionMaker(accountFolder, sender,
					seed, currDate, wordCounts);
			checkCurrPredictions(predictionMaker, correctOrderedEmails, seed);
			predictionMaker.close();
		}
	}

	protected void checkCurrPredictions(PredictionMaker predictionMaker,
			ArrayList<String> correctOrderedEmails, Set<String> seed) {
		if (PredictionMaker.predictIndividuals) {
			checkCurrIndividualPredictions(predictionMaker, correctOrderedEmails, seed);
		} else {
			checkCurrGroupPredictions(predictionMaker, correctOrderedEmails, seed);
		}
	}

	private void checkCurrIndividualPredictions(PredictionMaker predictionMaker,
			ArrayList<String> correctOrderedEmails, Set<String> seed) {

		Map<String, Double> predictionMap = predictionMaker.getIndividualPredictions();
		if (predictionMap.size() > 0) {
			addressesPredictedFor++;
		} else {
			seed.add(correctOrderedEmails.get(0));
			correctOrderedEmails.remove(0);
			return;
		}

		Set<TopPrediction> predictions = new TreeSet<TopPrediction>();

		Iterator<String> individuals = predictionMap.keySet().iterator();
		while (individuals.hasNext()) {
			String individual = individuals.next();
			if (seed.contains(individual)) {
				continue;
			}
			double similarity = predictionMap.get(individual);

			predictions.add(new TopPrediction(individual, similarity));
		}

		boolean foundMatch = false;
		int listSize = 0;
		Iterator<TopPrediction> predictionsIter = predictions.iterator();
		if (predictionsIter.hasNext()) {
			int x = 0;
			x++;
		}

		while (predictionsIter.hasNext() && listSize < PREDICTION_LIST_SIZE) {
			TopPrediction prediction = predictionsIter.next();
			if (seed.contains(prediction.recipient)) {
				continue; // Ignore predictions have already been accepted
							// because they won't be shown
			}

			listSize++;
			if (correctOrderedEmails.contains(prediction.recipient)) {
				foundMatch = true;
				addressesRight++;
				seed.add(prediction.recipient);
				while (correctOrderedEmails.remove(prediction.recipient)) {
				}
				break;
			}
		}

		if (!foundMatch) {
			String toAddress = correctOrderedEmails.get(0);
			seed.add(toAddress);
			while (correctOrderedEmails.remove(toAddress)) {
			}
		}

	}

	private void checkCurrGroupPredictions(PredictionMaker predictionMaker,
			ArrayList<String> correctOrderedEmails, Set<String> seed) {

		Map<ComparableSet<String>, Double> predictionMap = predictionMaker.getGroupPredictions();
		if (predictionMap.size() > 0) {
			groupListsGenerated++;
		} else {
			seed.add(correctOrderedEmails.get(0));
			correctOrderedEmails.remove(0);
			return;
		}

		Set<TopGroupPrediction> predictions = new TreeSet<TopGroupPrediction>();

		Iterator<ComparableSet<String>> groups = predictionMap.keySet().iterator();
		while (groups.hasNext()) {
			ComparableSet<String> group = new ComparableSet<String>(groups.next());
			double similarity = predictionMap.get(group);

			predictions.add(new TopGroupPrediction(group, similarity));
		}

		boolean foundMatch = false;
		int listSize = 0;
		Iterator<TopGroupPrediction> predictionsIter = predictions.iterator();
		while (predictionsIter.hasNext() && listSize < PREDICTION_LIST_SIZE) {
			TopGroupPrediction prediction = predictionsIter.next();

			Set<String> recipientsInPrediction = prediction.getRecipients();
			recipientsInPrediction.removeAll(seed);

			if (recipientsInPrediction.size() == 0) {
				continue; // Ignore when all predictions have already been
							// accepted because they won't be shown
			}

			listSize++;
			if (correctOrderedEmails.containsAll(recipientsInPrediction)) {
				foundMatch = true;
				groupsRight++;
				addressesRight += recipientsInPrediction.size();
				Iterator<String> recipientsInPredictionsIter = recipientsInPrediction.iterator();
				while (recipientsInPredictionsIter.hasNext()) {
					String recipient = recipientsInPredictionsIter.next();
					seed.add(recipient);
					while (correctOrderedEmails.remove(recipient)) {
					}
				}
				break;
			}
		}

		if (!foundMatch) {
			String toAddress = correctOrderedEmails.get(0);
			seed.add(toAddress);
			while (correctOrderedEmails.remove(toAddress)) {
			}
		}
	}

	protected static Set<String> ignoredAccounts = new TreeSet<String>();

	protected static void buildIgnoredAccounts() {
		ignoredAccounts.clear();
		ignoredAccounts.add("beck-s");
		ignoredAccounts.add("dasovich-j");
		ignoredAccounts.add("farmer-d");
		ignoredAccounts.add("kaminski-v");
		ignoredAccounts.add("kitchen-l");
		ignoredAccounts.add("nemec-g");
		ignoredAccounts.add("shackleton-s");
		ignoredAccounts.add("taylor-m");
	}

	public static ArrayList<String> getOrderedEmails(String currMessage) throws IOException {
		ArrayList<String> toReturn = new ArrayList<String>();

		File addressFile = new File(currMessage + Account.ADDR_FILE_SUFFIX);
		if (!addressFile.exists()) {
			Account.saveAddresses(new File(currMessage), addressFile);
		}

		AddressLists addressLists = new AddressLists(addressFile);

		toReturn.addAll(addressLists.getFrom());
		toReturn.addAll(addressLists.getTo());
		toReturn.addAll(addressLists.getCC());
		toReturn.addAll(addressLists.getBCC());
		return toReturn;
	}

	protected static ArrayList<String> getOrderedEmails(MimeMessage msg) {

		AddressParser parser = new AddressParser();

		String[] headers = null;

		try {
			headers = msg.getHeader("from");
			if (headers != null) {
				parser.add(headers[0]);
			}
		} catch (MessagingException e) {
		}

		try {
			headers = msg.getHeader("to");
			if (headers != null) {
				parser.add(headers[0]);
			}
		} catch (MessagingException e) {
		}

		try {
			headers = msg.getHeader("cc");
			if (headers != null) {
				parser.add(headers[0]);
			}
		} catch (MessagingException e) {
		}

		try {
			headers = msg.getHeader("bcc");
			if (headers != null) {
				parser.add(headers[0]);
			}
		} catch (MessagingException e) {
		}

		return removeDuplicates(parser.getAddressesInArrayList());
	}

	protected static ArrayList<String> removeDuplicates(ArrayList<String> list) {
		ArrayList<String> toReturn = new ArrayList<String>();
		Set<String> seenValues = new TreeSet<String>();

		for (int i = 0; i < list.size(); i++) {
			if (!seenValues.contains(list.get(i))) {
				seenValues.add(list.get(i));
				toReturn.add(list.get(i));
			}
		}

		return toReturn;
	}

	public static void main(String[] args) throws IOException, MessagingException {
		System.out.println("Content and Group, half-life time, groupAlg = real 4");
		buildIgnoredAccounts();

		File folder = new File("/home/bartizzi/Research/Enron Accounts");
		File[] accounts = folder.listFiles();
		Arrays.sort(accounts);

		boolean start = false;
		for (int i = 0; i < accounts.length; i++) {

			if (accounts[i].getName().equals("watson-k"))
				start = true;

			if (ignoredAccounts.contains(accounts[i].getName()))
				continue;

			if (!start)
				continue;
			// if(!(accounts[i].getName().equals("dasovich-j")) ) continue;

			System.out.print(accounts[i].getName());

			for (int group_algorithm = 4; group_algorithm <= 4; group_algorithm++) {
				PredictionMaker.group_algorithm = group_algorithm;

				for (double content_importance = 0.2; content_importance <= 0.8; content_importance += 0.2) {
					PredictionMaker.content_importance = content_importance;
					PredictionMaker.connection_importance = 1 - content_importance;

					for (int half_life = 1; half_life <= 3; half_life++) {
						PredictionMaker.setHalfLife(half_life);

						// for(double w_out=0.5; w_out <= 2.0; w_out *= 2.0){
						// PredictionMaker.w_out = w_out;
						PredictionMaker.w_out = 0.25;

						PredictionAcceptanceModeler modeler = new PredictionAcceptanceModeler(
								accounts[i]);
						int[] results = modeler.modelPredictionAcceptances();
						for (int j = 0; j < results.length; j++) {
							System.out.print("," + results[j]);
						}
						modeler.close();
						// System.out.print(" "+j);
						// }
					}
				}
			}
			System.out.println();

			MultispacePredictionMaker.clear();
		}
	}

}
