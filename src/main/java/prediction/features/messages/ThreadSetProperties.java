package prediction.features.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import data.representation.actionbased.messages.MessageThread;
import data.representation.actionbased.messages.SingleMessage;

public class ThreadSetProperties<Collaborator, Message extends SingleMessage<Collaborator>, ThreadType extends MessageThread<Collaborator, Message>> {

	private Set<String> stopWords;
	private Set<ThreadType> threads;
	private Set<String> titleWords;
	private Set<Collaborator> creators;
	private Set<Collaborator> collaborators;
	private WordIndexFinder wordIndexFinder;
	private Map<Collaborator, Integer> creatorIds = new HashMap<>();
	private Map<Collaborator, Integer> collaboratorIds = new HashMap<>();

	public ThreadSetProperties(Collection<ThreadType> trainThreads,
			Collection<ThreadType> testThreads, Set<String> stopWords) {
		this.threads = new HashSet<>(trainThreads);
		this.threads.addAll(testThreads);
		this.stopWords = stopWords;
		extractProperties();
	}

	public ThreadSetProperties(Collection<ThreadType> threads,
			Set<String> stopWords) {
		this.threads = new HashSet<>(threads);
		this.stopWords = stopWords;
		extractProperties();
	}

	private void extractProperties() {
		titleWords = new TreeSet<>();
		creators = new TreeSet<>();
		collaborators = new TreeSet<>();

		for (ThreadType thread : threads) {
			titleWords.addAll(SimpleWordIndexFinder.parseWords(thread
					.getTitle()));
			for (Collaborator creator : thread.getCreators()) {
				if (creator != null) {
					creators.add(creator);
				}
			}
			collaborators.addAll(thread.getCollaborators());
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

		wordIndexFinder = new SimpleWordIndexFinder(getTitleWords(),
				new ArrayList<String>());
	}

	public Set<ThreadType> getThreads() {
		return threads;
	}

	public Set<String> getTitleWords() {
		return titleWords;
	}

	public Set<Collaborator> getCreators() {
		return creators;
	}

	public Set<Collaborator> getCollaborators() {
		return collaborators;
	}

	public WordIndexFinder getWordIndexFinder() {
		return wordIndexFinder;
	}

	public Integer getCreatorId(Collaborator creator) {
		return creatorIds.get(creator);
	}

	public Integer getCollaboratorId(Collaborator collaborator) {
		return collaboratorIds.get(collaborator);
	}
}
