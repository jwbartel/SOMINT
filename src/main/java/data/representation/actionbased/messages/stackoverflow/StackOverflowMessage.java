package data.representation.actionbased.messages.stackoverflow;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;

import data.representation.actionbased.messages.SingleMessage;

public class StackOverflowMessage implements SingleMessage<String>, Comparable<StackOverflowMessage> {

	public static enum MessageType {
		Question,
		Answer,
		Comment,
	}
	
	private Long id;
	private String ownerId;
	private Date date;
	private Long threadId;
	private MessageType type;
	private Collection<String> tags;
	private String title;
	private boolean wasSent;
	
	public StackOverflowMessage(long id, String owner, Date date,
			long threadId, MessageType type, Collection<String> tags,
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
	
	public String getOwner() {
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
	
	public Collection<String> getTags() {
		return new TreeSet<>(tags);
	}
	
	public String getTitle() {
		return title;
	}

	@Override
	public Collection<String> getCreators() {
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
	public Collection<String> getCollaborators() {
		return getTags();
	}

	@Override
	public boolean wasSent() {
		return wasSent;
	}
	
	@Override
	public int compareTo(StackOverflowMessage message) {
		if (!getStartDate().equals(message.getStartDate())) {
			return getStartDate().compareTo(message.getStartDate());
		}
		return getId().compareTo(message.getId());
	}
}
