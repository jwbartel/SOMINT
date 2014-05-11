package general.actionbased.messages.email;

import general.actionbased.messages.SingleMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class EmailMessage<V> implements SingleMessage<V> {

	private final String messageId;
	private final String threadId;
	
	private final Date date;
	
	private final Collection<V> creators;
	private final ArrayList<V> to;
	private final ArrayList<V> cc;
	private final ArrayList<V> bcc;
	private final String subject;
	private final String body;

	public EmailMessage(String messageId, String threadId, Date date, ArrayList<V> from,
			ArrayList<V> to, ArrayList<V> cc, ArrayList<V> bcc, String subject,
			String body) {
		this.messageId = messageId;
		this.threadId = threadId;
		this.date = date;
		this.creators = from;
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.subject = subject;
		this.body = body;
	}

	public String getMessageId() {
		return messageId;
	}

	public String getThreadId() {
		return threadId;
	}

	public Collection<V> getFrom() {
		return getCreators();
	}

	public ArrayList<V> getTo() {
		return to;
	}

	public ArrayList<V> getCc() {
		return cc;
	}

	public ArrayList<V> getBcc() {
		return bcc;
	}

	public String getSubject() {
		return subject;
	}

	public String getBody() {
		return body;
	}

	@Override
	public Collection<V> getCreators() {
		return creators;
	}

	@Override
	public Date getStartDate() {
		return date;
	}

	@Override
	public Date getLastActiveDate() {
		return date;
	}

	@Override
	public Collection<V> getCollaborators() {
		Collection<V> collaborators = new ArrayList<>();
		for (V collaborator : creators) {
			if (!collaborators.contains(collaborator)) {
				collaborators.add(collaborator);
			}
		}
		for (V collaborator : to) {
			if (!collaborators.contains(collaborator)) {
				collaborators.add(collaborator);
			}
		}
		for (V collaborator : cc) {
			if (!collaborators.contains(collaborator)) {
				collaborators.add(collaborator);
			}
		}
		for (V collaborator : bcc) {
			if (!collaborators.contains(collaborator)) {
				collaborators.add(collaborator);
			}
		}
		return collaborators;
	}

}
