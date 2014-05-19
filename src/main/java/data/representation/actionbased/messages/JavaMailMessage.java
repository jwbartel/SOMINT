package data.representation.actionbased.messages;

import java.util.ArrayList;
import java.util.Collection;

import javax.mail.MessagingException;

public interface JavaMailMessage extends SingleMessage<ComparableAddress>{

	public abstract String[] getHeader(String header) throws MessagingException;

	public abstract String getSubject() throws MessagingException;

	public abstract Collection<ComparableAddress> getFrom() throws MessagingException;

	public abstract ArrayList<ComparableAddress> getTo() throws MessagingException;

	public abstract ArrayList<ComparableAddress> getCc() throws MessagingException;

	public abstract ArrayList<ComparableAddress> getBcc() throws MessagingException;

	public abstract ArrayList<ComparableAddress> getNewsgroups()
			throws MessagingException;

	public abstract String getMessageId() throws MessagingException;

	public abstract ArrayList<String> getReferences() throws MessagingException;

	public abstract String getInReplyTo() throws MessagingException;

	public abstract ArrayList<String> getAttachedFiles();

}