package data.representation.actionbased.messages.stackoverflow;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;

import data.representation.actionbased.CollaborativeAction;
import data.representation.actionbased.messages.SingleMessage;

public class StackOverflowMessage<Recipient> implements SingleMessage<Recipient> {

	public static enum MessageType {
		Question,
		Answer,
		Comment,
	}
	
	private Long id;
	private Recipient ownerId;
	private Date date;
	private Long threadId;
	private MessageType type;
	private Collection<Recipient> tags;
	private String title;
	private boolean wasSent;
	
	public StackOverflowMessage(long id, Recipient owner, Date date,
			long threadId, MessageType type, Collection<Recipient> tags,
			String title, boolean wasSent) {
		this.id = id;
		this.ownerId = owner;
		this.date = date;
		this.threadId = threadId;
		this.type = type;
		this.tags = tags;
		this.title = title;
		this.wasSent = wasSent;
	}
	
	public Long getId() {
		return id;
	}
	
	public Recipient getOwner() {
		return ownerId;
	}
	
	public Date getDate() {
		return date;
	}
	
	public Long getThreadId() {
		return threadId;
	}
	
	public MessageType getType() {
		return type;
	}
	
	public Collection<Recipient> getTags() {
		return new TreeSet<>(tags);
	}
	
	public String getTitle() {
		return title;
	}

	@Override
	public Collection<Recipient> getCreators() {
		return Arrays.asList(ownerId);
	}

	@Override
	public Date getStartDate() {
		return getDate();
	}

	@Override
	public Date getLastActiveDate() {
		return getDate();
	}

	@Override
	public Collection<Recipient> getCollaborators() {
		return getTags();
	}

	@Override
	public boolean wasSent() {
		return wasSent;
	}

	@Override
	public int compareTo(CollaborativeAction<Recipient> action) {
		if (!getStartDate().equals(action.getStartDate())) {
			return getStartDate().compareTo(action.getStartDate());
		}
		if (action instanceof StackOverflowMessage) {
			StackOverflowMessage message = (StackOverflowMessage) action;
			return getId().compareTo(message.getId());
		}
		return toString().compareTo(action.toString());
	}
}
