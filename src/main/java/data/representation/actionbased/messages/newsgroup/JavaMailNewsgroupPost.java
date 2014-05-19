package data.representation.actionbased.messages.newsgroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;

import data.representation.actionbased.messages.JavaMailMessage;
import data.representation.actionbased.messages.email.JavaMailEmailMessage;
import data.representation.actionbased.messages.email.PrefetchOptions;

public class JavaMailNewsgroupPost extends NewsgroupPost<Address> implements JavaMailMessage {

	public JavaMailNewsgroupPost(Message parent, boolean wasSent)
			throws MessagingException, IOException {
		super(new JavaMailEmailMessage(parent, wasSent));
	}

	public JavaMailNewsgroupPost(Message parent, boolean wasSent,
			PrefetchOptions prefetchOptions) throws MessagingException, IOException {
		super(new JavaMailEmailMessage(parent, wasSent, prefetchOptions));
	}
	
	private JavaMailMessage getParent() {
		return (JavaMailMessage) parent;
	}

	public String[] getHeader(String header) throws MessagingException {
		return getParent().getHeader(header);
	}

	public String getSubject() throws MessagingException {
		return getParent().getSubject();
	}

	public Collection<Address> getFrom() throws MessagingException {
		return getParent().getFrom();
	}

	public ArrayList<Address> getTo() throws MessagingException {
		return getParent().getTo();
	}

	public ArrayList<Address> getCc() throws MessagingException {
		return getParent().getCc();
	}

	public ArrayList<Address> getBcc() throws MessagingException {
		return getParent().getBcc();
	}

	public ArrayList<Address> getNewsgroups() throws MessagingException {
		return getParent().getNewsgroups();
	}
	
	@Override
	public String getMessageId() throws MessagingException {
		return getParent().getMessageId();
	}

	public ArrayList<String> getReferences() throws MessagingException {
		return getParent().getReferences();
	}

	public String getInReplyTo() throws MessagingException {
		return getParent().getInReplyTo();
	}

	public ArrayList<String> getAttachedFiles() {
		return getParent().getAttachedFiles();
	}

}
