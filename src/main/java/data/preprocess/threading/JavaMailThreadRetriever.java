package data.preprocess.threading;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.MessagingException;

import data.representation.actionbased.messages.ComparableAddress;
import data.representation.actionbased.messages.JavaMailMessage;
import data.representation.actionbased.messages.MessageThread;

public abstract class JavaMailThreadRetriever<Message extends JavaMailMessage, ThreadType extends MessageThread<ComparableAddress, Message>>
		implements ThreadRetriever<ComparableAddress, Message, ThreadType> {

	protected Long timeout = null;
	
	public abstract ThreadType createThread();

	@Override
	public Collection<ThreadType> retrieveThreads(Collection<Message> actions) {

		try {
			ArrayList<Set<Message>> threadSets = new ArrayList<Set<Message>>();
			ArrayList<ArrayList<String>> idsForThreads = new ArrayList<>();
			Set<String> seenMessages = new TreeSet<String>();

			for (Message message : actions) {
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
						idsForThreads, threadSets);
			}

			ArrayList<ThreadType> threads = new ArrayList<>();
			for (Set<Message> threadSet : threadSets) {
				ThreadType thread = createThread();
				for (Message message : threadSet) {
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
	private void sortIntoThreads(Message message, String messageID,
			ArrayList<String> references, String inReplyTo,
			ArrayList<ArrayList<String>> idsForThreads,
			ArrayList<Set<Message>> threads) {

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
			Set<Message> thread = threads.get(i);

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

		if (prevThread == null) {
			idsForThreads.add(new ArrayList<String>(references));
			Set<Message> thread = new HashSet<Message>();
			thread.add(message);
			threads.add(thread);
		}
	}

	private int getIntersectionSize(ArrayList<String> group1,
			ArrayList<String> group2) {
		ArrayList<String> intersection = new ArrayList<String>(group1);
		intersection.retainAll(group2);
		return intersection.size();
	}
}
