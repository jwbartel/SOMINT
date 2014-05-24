package util.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import recommendation.recipients.old.predictionchecking.PredictionAcceptanceModeler;
import bus.accounts.Account;

public class EnronStatsFinder {

	private final File rootFolder;
	private DescriptiveStatistics numMessageStats;
	private DescriptiveStatistics numMessageWithAttachmentsStats;
	private DescriptiveStatistics numAttachmentsStats;
	private DescriptiveStatistics numUniqueAttachmentsStats;
	private DescriptiveStatistics attachmentsPerMessageStats;

	public EnronStatsFinder(File folder) {
		this.rootFolder = folder;
	}

	public DescriptiveStatistics getNumMessagesSummary() throws NumberFormatException, IOException {
		if (numMessageStats == null) {
			numMessageStats = new DescriptiveStatistics();
			File[] folders = rootFolder.listFiles();
			for (File accountFolder : folders) {
				if (!accountFolder.isDirectory()) {
					continue;
				}
				File list = new File(accountFolder, Account.ALL_MSGS_ADAPTED);
				BufferedReader in = new BufferedReader(new FileReader(list));
				int numMessages = Integer.parseInt(in.readLine());
				numMessageStats.addValue(numMessages);
				in.close();
			}
		}
		return numMessageStats;
	}

	public DescriptiveStatistics getRecipientsPerMessageSummary() throws NumberFormatException,
			IOException {
		if (attachmentsPerMessageStats == null) {
			DescriptiveStatistics localStats = new DescriptiveStatistics();
			File[] folders = rootFolder.listFiles();
			for (File accountFolder : folders) {
				if (!accountFolder.isDirectory()) {
					continue;
				}
				File list = new File(accountFolder, Account.ALL_MSGS_ADAPTED);
				countRecipientsPerMessage(list, localStats);
			}
			attachmentsPerMessageStats = localStats;
		}
		return attachmentsPerMessageStats;
	}

	public DescriptiveStatistics getMessagesWithAttachmentsSummary() throws NumberFormatException,
			IOException {
		if (numMessageWithAttachmentsStats == null) {
			DescriptiveStatistics localStats = new DescriptiveStatistics();
			File[] folders = rootFolder.listFiles();
			for (File accountFolder : folders) {
				if (!accountFolder.isDirectory()) {
					continue;
				}
				File list = new File(accountFolder, Account.ALL_MSGS_ADAPTED);
				localStats.addValue(countMessagesWithAttachments(list));
			}
			numMessageWithAttachmentsStats = localStats;
		}
		return numMessageWithAttachmentsStats;
	}

	public DescriptiveStatistics getNumAttachmentsSummary() throws NumberFormatException,
			IOException {
		if (numAttachmentsStats == null) {
			DescriptiveStatistics localStats = new DescriptiveStatistics();
			File[] folders = rootFolder.listFiles();
			for (File accountFolder : folders) {
				if (!accountFolder.isDirectory()) {
					continue;
				}
				File list = new File(accountFolder, Account.ALL_MSGS_ADAPTED);
				localStats.addValue(countAttachments(list));
			}
			numAttachmentsStats = localStats;
		}
		return numAttachmentsStats;
	}

	public DescriptiveStatistics getNumUniqueAttachmentsSummary() throws NumberFormatException,
			IOException {
		if (numUniqueAttachmentsStats == null) {
			DescriptiveStatistics localStats = new DescriptiveStatistics();
			File[] folders = rootFolder.listFiles();
			for (File accountFolder : folders) {
				if (!accountFolder.isDirectory()) {
					continue;
				}
				File list = new File(accountFolder, Account.ALL_MSGS_ADAPTED);
				localStats.addValue(countUniqueAttachments(list));
			}
			numUniqueAttachmentsStats = localStats;
		}
		return numUniqueAttachmentsStats;
	}

	public DescriptiveStatistics getAttachmentsPerMessageSummary() throws NumberFormatException,
			IOException {
		if (attachmentsPerMessageStats == null) {
			DescriptiveStatistics localStats = new DescriptiveStatistics();
			File[] folders = rootFolder.listFiles();
			for (File accountFolder : folders) {
				if (!accountFolder.isDirectory()) {
					continue;
				}
				File list = new File(accountFolder, Account.ALL_MSGS_ADAPTED);
				countAttachmentsPerMessage(list, localStats);
			}
			attachmentsPerMessageStats = localStats;
		}
		return attachmentsPerMessageStats;
	}

	private int countMessagesWithAttachments(File accountFileList) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(accountFileList));
		in.readLine();

		int count = 0;
		String line = in.readLine();
		boolean hasAttachments = false;
		while (line != null) {
			if (line.startsWith("\t")) {
				hasAttachments = true;
			} else {
				if (hasAttachments)
					count++;
				hasAttachments = false;
			}
			line = in.readLine();
		}
		in.close();

		return count;
	}

	private void countRecipientsPerMessage(File accountFileList, DescriptiveStatistics stats)
			throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(accountFileList));
		in.readLine();

		String line = in.readLine();
		while (line != null) {
			if (!line.startsWith("\t")) {
				System.out.println(line);
				ArrayList<String> recipients = PredictionAcceptanceModeler.getOrderedEmails(line);
				HashSet<String> recipientSet = new HashSet<String>();
				for (String recipient : recipients) {
					recipientSet.add(recipient.toLowerCase());
				}
				stats.addValue(recipientSet.size());
			}
			line = in.readLine();
		}
		in.close();
	}

	private int countAttachments(File accountFileList) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(accountFileList));
		in.readLine();

		int numAttachments = 0;
		String line = in.readLine();
		while (line != null) {
			if (line.startsWith("\t")) {
				numAttachments++;
			}
			line = in.readLine();
		}
		in.close();

		return numAttachments;
	}

	private int countUniqueAttachments(File accountFileList) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(accountFileList));
		in.readLine();

		Set<String> attachments = new HashSet<String>();
		String line = in.readLine();
		while (line != null) {
			if (line.startsWith("\t")) {
				attachments.add(new File(line.substring(1)).getName());
			}
			line = in.readLine();
		}
		in.close();

		return attachments.size();
	}

	private void countAttachmentsPerMessage(File accountFileList, DescriptiveStatistics stats)
			throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(accountFileList));
		in.readLine();

		String line = in.readLine();
		int currCount = 0;
		while (line != null) {
			if (line.startsWith("\t")) {
				currCount++;
			} else {
				if (currCount > 0) {
					stats.addValue(currCount);
				}
				currCount = 0;
			}
			line = in.readLine();
		}
		in.close();
	}

	private static void printSummary(DescriptiveStatistics stats, String title) {
		System.out.println("============" + title + "============");
		System.out.println("Mean: " + stats.getMean());
		System.out.println("STDEV: " + stats.getStandardDeviation());
		System.out.println("Min: " + stats.getMin());
		System.out.println("Median: " + stats.getPercentile(50.0));
		System.out.println("Max: " + stats.getMax());
	}

	public static void main(String[] args) throws NumberFormatException, IOException {
		File rootFolder = new File("D:\\Enron data\\extracted precomputes");
		EnronStatsFinder statsFinder = new EnronStatsFinder(rootFolder);

		DescriptiveStatistics numMessageStats = statsFinder.getNumMessagesSummary();
		printSummary(numMessageStats, "Messages");

		DescriptiveStatistics numRecipientsStats = statsFinder.getRecipientsPerMessageSummary();
		printSummary(numRecipientsStats, "Recipients per message");

		// DescriptiveStatistics messagesWithAttachmentsStats = statsFinder
		// .getMessagesWithAttachmentsSummary();
		// printSummary(messagesWithAttachmentsStats,
		// "Messages with attachments");
		//
		// DescriptiveStatistics numAttachmentsStats =
		// statsFinder.getNumAttachmentsSummary();
		// printSummary(numAttachmentsStats, "Attachments");
		//
		// DescriptiveStatistics numUniqueAttachmentsStats = statsFinder
		// .getNumUniqueAttachmentsSummary();
		// printSummary(numUniqueAttachmentsStats, "Unique Attachments");
		//
		// DescriptiveStatistics attachmentsPerMessageStats = statsFinder
		// .getAttachmentsPerMessageSummary();
		// printSummary(attachmentsPerMessageStats, "Attachments Per Message");
	}

}
