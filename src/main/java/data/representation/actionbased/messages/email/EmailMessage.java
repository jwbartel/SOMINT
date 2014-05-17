package data.representation.actionbased.messages.email;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.MessagingException;

import data.representation.actionbased.messages.SingleMessage;

public class EmailMessage<V> implements SingleMessage<V> {

	static String NON_WSP = "([^\\s])"; // any CHAR other than WSP
	static String WSP = "([\\s])";
	static String BLOBCHAR = "([^\\[\\]])"; // any CHAR except '[' and ']

	static String SUBJ_BLOB = "(" + "\\[" + BLOBCHAR + "*" + "\\]" + WSP + "*" + ")";
	static String SUBJ_REFWD = "(" + "((re)|(fw[d]?))" + WSP + "*" + SUBJ_BLOB + "?" + ":" + ")";

	static String SUBJ_FWD_HDR = "[fwd:";
	static String SUBJ_FWD_TRL = "]";

	static String SUBJ_LEADER = "(" + "(" + SUBJ_BLOB + "*" + SUBJ_REFWD + ")" + "|" + WSP + ")";
	static String SUBJ_TRAILER = "(" + "([(]fwd[)])" + "|" + WSP + ")";

	static Pattern SUBJ_BLOB_PATTERN = Pattern.compile(SUBJ_BLOB);
	static Pattern SUBJ_LEADER_PATTERN = Pattern.compile(SUBJ_LEADER);

	private String messageId;
	private String threadId;
	
	protected Date date;
	protected boolean wasSent;
	
	protected Collection<V> from;
	protected ArrayList<V> to;
	protected ArrayList<V> cc;
	protected ArrayList<V> bcc;
	protected ArrayList<V> newsgroups;
	protected String subject;
	private String baseSubject = null;
	protected String body;

	EmailMessage() {
	}
	
	public EmailMessage(String messageId, String threadId, Date date, boolean wasSent, ArrayList<V> from,
			ArrayList<V> to, ArrayList<V> cc, ArrayList<V> bcc, ArrayList<V> newsgroups, String subject,
			String body) {
		init(messageId, threadId, date, wasSent, from, to, cc, bcc, newsgroups, subject, body);
	}
	
	protected void init(String messageId, String threadId, Date date, boolean wasSent, ArrayList<V> from,
			ArrayList<V> to, ArrayList<V> cc, ArrayList<V> bcc, ArrayList<V> newsgroups, String subject,
			String body) {
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
		return messageId;
	}

	public String getThreadId() {
		return threadId;
	}

	public Collection<V> getFrom() throws MessagingException {
		return getCreators();
	}

	public ArrayList<V> getTo() throws MessagingException {
		return to;
	}

	public ArrayList<V> getCc() throws MessagingException {
		return cc;
	}

	public ArrayList<V> getBcc() throws MessagingException {
		return bcc;
	}

	public ArrayList<V> getNewsgroups() throws MessagingException {
		return newsgroups;
	}

	public String getSubject() throws MessagingException {
		return subject;
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

	private String extractBaseSubject() throws UnsupportedEncodingException, MessagingException {

		String baseSubject = new String(getSubject().getBytes("UTF-8"), "UTF-8").toLowerCase();
		baseSubject = baseSubject.replaceAll("\t", " ");
		baseSubject = baseSubject.replaceAll("[ ]+", " ");

		while (true) {
			while (baseSubject.matches(".*" + SUBJ_TRAILER)) {
				if (baseSubject.endsWith("(fwd)")) {
					baseSubject = baseSubject.substring(0, baseSubject.length() - 5);
				} else {
					baseSubject = baseSubject.substring(0, baseSubject.length() - 1);
				}
			}

			boolean shouldCheckAgain = true;
			while (shouldCheckAgain) {
				Matcher matcher = SUBJ_LEADER_PATTERN.matcher(baseSubject);
				if (matcher.find() && matcher.start() == 0) {
					baseSubject = baseSubject.substring(matcher.group().length());
					shouldCheckAgain = true;
				} else {
					shouldCheckAgain = false;
				}

				matcher = SUBJ_BLOB_PATTERN.matcher(baseSubject);
				if (matcher.find() && matcher.start() == 0 && matcher.end() != baseSubject.length()) {
					baseSubject = baseSubject.substring(matcher.group().length());
					shouldCheckAgain = true;
				}

			}

			if (baseSubject.startsWith(SUBJ_FWD_HDR) && baseSubject.endsWith(SUBJ_FWD_TRL)) {
				baseSubject = baseSubject.substring(SUBJ_FWD_HDR.length(), baseSubject.length()
						- SUBJ_FWD_TRL.length());
			} else {
				break;
			}
		}
		return baseSubject;
	}

	public String getBody() {
		return body;
	}

	@Override
	public Collection<V> getCreators() {
		return from;
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
	public boolean wasSent() {
		return wasSent;
	}

	@Override
	public Collection<V> getCollaborators() {
		Collection<V> collaborators = new ArrayList<>();
		for (V collaborator : from) {
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
		for (V collaborator : newsgroups) {
			if (!collaborators.contains(collaborator)) {
				collaborators.add(collaborator);
			}
		}
		return collaborators;
	}

}
