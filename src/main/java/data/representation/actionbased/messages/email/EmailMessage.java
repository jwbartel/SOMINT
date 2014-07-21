package data.representation.actionbased.messages.email;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;

import data.representation.actionbased.CollaborativeAction;
import data.representation.actionbased.messages.SingleMessage;

public class EmailMessage<RecipientType> implements SingleMessage<RecipientType> {

	static String NON_WSP = "([^\\s])"; // any CHAR other than WSP
	static String WSP = "([\\s])";
	static String BLOBCHAR = "([^\\[\\]])"; // any CHAR except '[' and ']

	static String SUBJ_BLOB = "(" + "\\[" + BLOBCHAR + "*" + "\\]" + WSP + "*"
			+ ")";
	static String SUBJ_REFWD = "(" + "((re)|(fw[d]?))" + WSP + "*" + SUBJ_BLOB
			+ "?" + ":" + ")";

	static String SUBJ_FWD_HDR = "[fwd:";
	static String SUBJ_FWD_TRL = "]";

	static String SUBJ_LEADER = "(" + "(" + SUBJ_BLOB + "*" + SUBJ_REFWD + ")"
			+ "|" + WSP + ")";
	static String SUBJ_TRAILER = "(" + "([(]fwd[)])" + "|" + WSP + ")";

	static Pattern SUBJ_BLOB_PATTERN = Pattern.compile(SUBJ_BLOB);
	static Pattern SUBJ_LEADER_PATTERN = Pattern.compile(SUBJ_LEADER);

	private EmailMessage<RecipientType> parent;
	private String messageId;
	private String threadId;

	protected Date date;
	protected boolean wasSent;

	protected Collection<RecipientType> from;
	protected ArrayList<RecipientType> to;
	protected ArrayList<RecipientType> cc;
	protected ArrayList<RecipientType> bcc;
	protected ArrayList<RecipientType> newsgroups;
	protected String subject;
	private String baseSubject = null;
	protected String body;

	protected EmailMessage() {
	}
	
	public EmailMessage(EmailMessage<RecipientType> parent) {
		this.parent = parent;
	}

	public EmailMessage(String messageId, String threadId, Date date,
			boolean wasSent, ArrayList<RecipientType> from, ArrayList<RecipientType> to,
			ArrayList<RecipientType> cc, ArrayList<RecipientType> bcc, ArrayList<RecipientType> newsgroups,
			String subject, String body) {
		init(messageId, threadId, date, wasSent, from, to, cc, bcc, newsgroups,
				subject, body);
	}

	protected void init(String messageId, String threadId, Date date,
			boolean wasSent, ArrayList<RecipientType> from, ArrayList<RecipientType> to,
			ArrayList<RecipientType> cc, ArrayList<RecipientType> bcc, ArrayList<RecipientType> newsgroups,
			String subject, String body) {
		this.messageId = messageId;
		this.threadId = threadId;
		this.date = date;
		this.wasSent = wasSent;
		this.from = from;
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.newsgroups = newsgroups;
		this.subject = subject;
		this.body = body;
	}

	public String getMessageId() throws MessagingException {
		if (parent == null) return messageId;
		return parent.getMessageId();
	}

	public String getThreadId() {
		if (parent == null) return threadId;
		return parent.getThreadId();
	}

	public Collection<RecipientType> getFrom() throws MessagingException {
		if (parent == null) return getCreators();
		return parent.getFrom();
	}

	public ArrayList<RecipientType> getTo() throws MessagingException {
		if (parent == null) return to;
		return parent.getTo();
	}

	public ArrayList<RecipientType> getCc() throws MessagingException {
		if (parent == null) return cc;
		return parent.getCc();
	}

	public ArrayList<RecipientType> getBcc() throws MessagingException {
		if (parent == null) return bcc;
		return parent.getBcc();
	}

	public ArrayList<RecipientType> getNewsgroups() throws MessagingException {
		if (parent == null) return newsgroups;
		return parent.getBcc();
	}
	
	@Override
	public String getTitle() {
		try {
			return getSubject();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getSubject() throws MessagingException {
		return subject;
	}

	public String getBaseSubject() throws MessagingException {
		if (parent != null) return parent.getBaseSubject();
		if (baseSubject == null && getSubject() != null) {
			try {
				baseSubject = extractBaseSubject();
			} catch (UnsupportedEncodingException e) {
				throw new MessagingException("Error generating base subject", e);
			}
		}
		return baseSubject;
	}

	protected String extractBaseSubject() throws UnsupportedEncodingException,
			MessagingException {

		String baseSubject = new String(getSubject().getBytes("UTF-8"), "UTF-8")
				.toLowerCase();
		baseSubject = baseSubject.replaceAll("\t", " ");
		baseSubject = baseSubject.replaceAll("[ ]+", " ");

		while (true) {
			while (baseSubject.matches(".*" + SUBJ_TRAILER)) {
				if (baseSubject.endsWith("(fwd)")) {
					baseSubject = baseSubject.substring(0,
							baseSubject.length() - 5);
				} else {
					baseSubject = baseSubject.substring(0,
							baseSubject.length() - 1);
				}
			}

			boolean shouldCheckAgain = true;
			while (shouldCheckAgain) {
				Matcher matcher = SUBJ_LEADER_PATTERN.matcher(baseSubject);
				if (matcher.find() && matcher.start() == 0) {
					baseSubject = baseSubject.substring(matcher.group()
							.length());
					shouldCheckAgain = true;
				} else {
					shouldCheckAgain = false;
				}

				matcher = SUBJ_BLOB_PATTERN.matcher(baseSubject);
				if (matcher.find() && matcher.start() == 0
						&& matcher.end() != baseSubject.length()) {
					baseSubject = baseSubject.substring(matcher.group()
							.length());
					shouldCheckAgain = true;
				}

			}

			if (baseSubject.startsWith(SUBJ_FWD_HDR)
					&& baseSubject.endsWith(SUBJ_FWD_TRL)) {
				baseSubject = baseSubject.substring(SUBJ_FWD_HDR.length(),
						baseSubject.length() - SUBJ_FWD_TRL.length());
			} else {
				break;
			}
		}
		return baseSubject;
	}

	public String getBody() {
		if (parent != null) return parent.getBody();
		return body;
	}

	@Override
	public Collection<RecipientType> getCreators() {
		if (parent != null) return parent.getCreators();
		return from;
	}

	@Override
	public Date getStartDate() {
		if (parent != null) return parent.getStartDate();
		return date;
	}

	@Override
	public Date getLastActiveDate() {
		if (parent != null) return parent.getLastActiveDate();
		return date;
	}

	@Override
	public boolean wasSent() {
		if (parent != null) return parent.wasSent();
		return wasSent;
	}

	@Override
	public Collection<RecipientType> getCollaborators() {
		if (parent != null) return parent.getCollaborators();
		Collection<RecipientType> collaborators = new ArrayList<>();
		try {
			for (RecipientType collaborator : getFrom()) {
				if (!collaborators.contains(collaborator)) {
					collaborators.add(collaborator);
				}
			}
		} catch (MessagingException e) {
			System.out.println("Error retrieving from");
		}
		try {
			for (RecipientType collaborator : getTo()) {
				if (!collaborators.contains(collaborator)) {
					collaborators.add(collaborator);
				}
			}
		} catch (MessagingException e) {
			System.out.println("Error retrieving to");
		}
		try {
			for (RecipientType collaborator : getCc()) {
				if (!collaborators.contains(collaborator)) {
					collaborators.add(collaborator);
				}
			}
		} catch (MessagingException e) {
			System.out.println("Error retrieving cc");
		}
		try {
			for (RecipientType collaborator : getBcc()) {
				if (!collaborators.contains(collaborator)) {
					collaborators.add(collaborator);
				}
			}
		} catch (MessagingException e) {
			System.out.println("Error retrieving bcc");
		}
		try {
			if (getNewsgroups() != null) {
				for (RecipientType collaborator : getNewsgroups()) {
					if (!collaborators.contains(collaborator)) {
						collaborators.add(collaborator);
					}
				}
			}
		} catch (MessagingException e) {
			System.out.println("Error retrieving newsgroups");
		}
		return collaborators;
	}

	@Override
	public String toString() {
		if (parent != null) return parent.toString();
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
	
	@Override
	public int compareTo(CollaborativeAction<RecipientType> action) {
		if (!getStartDate().equals(action.getStartDate())) {
			return getStartDate().compareTo(action.getStartDate());
		}
		return this.toString().compareTo(action.toString());
	}

}
