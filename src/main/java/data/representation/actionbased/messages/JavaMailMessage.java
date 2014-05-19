package data.representation.actionbased.messages;

import java.util.ArrayList;
import java.util.Collection;

import javax.mail.Address;
import javax.mail.MessagingException;

public interface JavaMailMessage extends SingleMessage<Address>{

	public abstract String[] getHeader(String header) throws MessagingException;

	public abstract String getSubject() throws MessagingException;

	public abstract Collection<Address> getFrom() throws MessagingException;

	public abstract ArrayList<Address> getTo() throws MessagingException;

	public abstract ArrayList<Address> getCc() throws MessagingException;

	public abstract ArrayList<Address> getBcc() throws MessagingException;

	public abstract ArrayList<Address> getNewsgroups()
			throws MessagingException;

	public abstract String getMessageId() throws MessagingException;

	public abstract ArrayList<String> getReferences() throws MessagingException;

	public abstract String getInReplyTo() throws MessagingException;

	public abstract ArrayList<String> getAttachedFiles();

}