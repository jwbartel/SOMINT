package data.representation.actionbased.messages.newsgroup;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.mail.MessagingException;

import data.representation.actionbased.messages.email.EmailMessage;

public class NewsgroupPost<RecipientType> extends EmailMessage<RecipientType> {

	protected EmailMessage<RecipientType> parent = null;
	String baseSubject = null;
	
	public NewsgroupPost(EmailMessage<RecipientType> parent) {
		super(parent);
		this.parent = parent;
	}
	
	public NewsgroupPost(String messageId, 
			String threadId,
			Date date,
			boolean wasSent,
			ArrayList<RecipientType> from,
			ArrayList<RecipientType> to,
			ArrayList<RecipientType> cc,
			ArrayList<RecipientType> bcc,
			ArrayList<RecipientType> newsgroups,
			String subject,
			String body) {
		super(messageId, threadId, date, wasSent, from, to, cc, bcc, newsgroups,
				subject, body);
	}
	
	public String getMessageId() throws MessagingException {
		if (parent != null) {
			return parent.getMessageId();
		}
		return super.getMessageId();
	}

	public String getThreadId() {
		if (parent != null) {
			return parent.getThreadId();
		}
		return super.getThreadId();
	}

	public Collection<RecipientType> getFrom() throws MessagingException {
		if (parent != null) {
			return parent.getFrom();
		}
		return super.getFrom();
	}

	public ArrayList<RecipientType> getTo() throws MessagingException {
		if (parent != null) {
			return parent.getTo();
		}
		return super.getTo();
	}

	public ArrayList<RecipientType> getCc() throws MessagingException {
		if (parent != null) {
			return parent.getCc();
		}
		return super.getCc();
	}

	public ArrayList<RecipientType> getBcc() throws MessagingException {
		if (parent != null) {
			return parent.getBcc();
		}
		return super.getBcc();
	}

	public ArrayList<RecipientType> getNewsgroups() throws MessagingException {
		if (parent != null) {
			return parent.getNewsgroups();
		}
		return super.getNewsgroups();
	}

	public String getSubject() throws MessagingException {
		if (parent != null) {
			return parent.getSubject();
		}
		return super.getSubject();
	}

	public String getBaseSubject() throws MessagingException {
		if (baseSubject == null && getSubject() != null) {
			try {
				baseSubject = extractBaseSubject();
			} catch (UnsupportedEncodingException e) {
				throw new MessagingException("Error generating base subject", e);
			}
		}
		return baseSubject;
	}
	
	@Override
	public Collection<RecipientType> getCollaborators() {
		super.getCollaborators();
		try {
			return getNewsgroups();
		} catch (MessagingException e) {
			System.out.println("error retrieving newsgroups");
			return null;
		}
	}

	@Override
	public String toString() {
		try {
			String retVal = getLastActiveDate() + " from:[";
			boolean addComma = false;
			for (RecipientType sender : getFrom()) {
				retVal += sender;
				if (addComma)
					retVal += ",";
				else
					addComma = true;
			}
			retVal += "] newsgroups:[";
			addComma = false;
			for (RecipientType recipient : getNewsgroups()) {
				retVal += recipient;
				if (addComma)
					retVal += ",";
				else
					addComma = true;
			}
			retVal += "] to:[";
			addComma = false;
			for (RecipientType recipient : getTo()) {
				retVal += recipient;
				if (addComma)
					retVal += ",";
				else
					addComma = true;
			}
			retVal += "] cc:[";
			addComma = false;
			for (RecipientType recipient : getCc()) {
				retVal += recipient;
				if (addComma)
					retVal += ",";
				else
					addComma = true;
			}
			retVal += "] bcc:[";
			addComma = false;
			for (RecipientType recipient : getBcc()) {
				retVal += recipient;
				if (addComma)
					retVal += ",";
				else
					addComma = true;
			}
			retVal += "] subject:" + getSubject();
			return retVal;
		} catch (MessagingException e) {
			return super.toString();
		}
	}

}
