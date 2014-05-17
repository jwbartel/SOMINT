package data.preprocess.threading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.Address;
import javax.mail.MessagingException;

import data.representation.actionbased.messages.email.JavaMailMessage;
import data.representation.actionbased.messages.email.JavaMailThread;

public class EmailThreadRetriever implements
		ThreadRetriever<Address, JavaMailMessage, JavaMailThread> {

	protected Long timeout = null;

	public EmailThreadRetriever() {

	}

	public EmailThreadRetriever(long timeout) {
		this.timeout = timeout;
	}

	@Override
	public Collection<JavaMailThread> retrieveThreads(
			Collection<JavaMailMessage> actions) {

		try {
			ArrayList<Set<JavaMailMessage>> threadSets = new ArrayList<Set<JavaMailMessage>>();
			Set<String> seenMessages = new TreeSet<String>();
			Set<String> unseenMessages = new TreeSet<String>();

			for (JavaMailMessage message : actions) {
				if (timeout != null) {
					try {
						Thread.sleep(timeout);
					} catch (InterruptedException e) {
					}
				}

				if (message.getMessageId() == null) {
					continue;
				}

				String messageID = message.getMessageId();
				if (seenMessages.contains(messageID)) {
					continue;
				} else {
					seenMessages.add(messageID);
				}

				ArrayList<String> references = message.getReferences();
				String inReplyTo = message.getInReplyTo();
				sortIntoThreads(message, messageID, references, inReplyTo,
						new ArrayList<ArrayList<String>>(), threadSets);
			}

			ArrayList<JavaMailThread> threads = new ArrayList<>();
			for (Set<JavaMailMessage> threadSet : threadSets) {
				JavaMailThread thread = new JavaMailThread();
				for (JavaMailMessage message : threadSet) {
					thread.addThreadedAction(message);
				}
				threads.add(thread);
			}
			return threads;
		} catch (MessagingException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	private void sortIntoThreads(JavaMailMessage message, String messageID,
			ArrayList<String> references, String inReplyTo,
			ArrayList<ArrayList<String>> idsForThreads,
			ArrayList<Set<JavaMailMessage>> threads) {

		if (references.size() == 0) {
			references = new ArrayList<String>();
			if (inReplyTo != null) {
				references.add(inReplyTo);
			}
		} else {
			references = (ArrayList<String>) references.clone();
		}
		references.add(messageID);

		Integer prevThread = null;
		for (int i = 0; i < idsForThreads.size(); i++) {
			ArrayList<String> threadIDs = idsForThreads.get(i);
			Set<JavaMailMessage> thread = threads.get(i);

			if (getIntersectionSize(references, threadIDs) > 0) {
				if (prevThread == null) {
					prevThread = i;
					for (int j = 0; j < references.size(); j++) {
						if (!threadIDs.contains(references.get(j))) {
							threadIDs.add(references.get(j));
						}
					}
					thread.add(message);
				} else {
					ArrayList<String> threadToMergeTo = idsForThreads
							.get(prevThread);
					for (int j = 0; j < threadIDs.size(); j++) {
						if (!threadToMergeTo.contains(threadIDs.get(j))) {
							threadToMergeTo.add(threadIDs.get(j));
						}
					}
					threads.get(prevThread).addAll(threads.get(i));

					idsForThreads.remove(i);
					threads.remove(i);
					i--;

				}
			}
		}

		boolean added = false;
		if (prevThread == null) {
			idsForThreads.add(new ArrayList<String>(references));
			Set<JavaMailMessage> thread = new HashSet<JavaMailMessage>();
			thread.add(message);
			threads.add(thread);
			added = true;
		}
	}

	private int getIntersectionSize(ArrayList<String> group1,
			ArrayList<String> group2) {
		ArrayList<String> intersection = new ArrayList<String>(group1);
		intersection.retainAll(group2);
		return intersection.size();
	}

}
