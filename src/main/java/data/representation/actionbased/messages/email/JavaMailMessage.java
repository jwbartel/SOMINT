package data.representation.actionbased.messages.email;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;

public class JavaMailMessage extends EmailMessage<Address> {

	final static DateFormat[] dateFormats = {
			new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z"),
			new SimpleDateFormat("dd MMM yyyy HH:mm:ss z"),
			new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss"),
			new SimpleDateFormat("dd MMM yyyy HH:mm:ss"), };

	Message parent;
	private final Map<String, String[]> seenHeaders = new TreeMap<String, String[]>();
	private final ArrayList<String> attachedFiles = new ArrayList<String>();

	public JavaMailMessage(Message parent, boolean wasSent)
			throws MessagingException, IOException {
		this.parent = parent;
		this.wasSent = wasSent;
		String[] prefetchedHeaders = { "Message-ID", "References",
				"In-Reply-To"};
		preloadData(new PrefetchOptions(prefetchedHeaders, false));
	}

	public JavaMailMessage(Message parent, boolean wasSent,
			PrefetchOptions prefetchOptions) throws MessagingException,
			IOException {
		super();
		this.parent = parent;
		this.wasSent = wasSent;
		preloadData(prefetchOptions);

	}

	private void preloadData(PrefetchOptions prefetchOptions)
			throws MessagingException, IOException {
		date = extractDate();
		try {
			getFrom();
		} catch (MessagingException e) {
			System.out.println("Error retrieving from addresses");
		}
		try {
			getTo();
		} catch (MessagingException e) {
			System.out.println("Error retrieving to addresses");
		}
		try {
			getCc();
		} catch (MessagingException e) {
			System.out.println("Error retrieving cc addresses");
		}
		try {
			getBcc();
		} catch (MessagingException e) {
			System.out.println("Error retrieving bcc addresses");
		}
		getSubject();
		try {
			getNewsgroups();
		} catch (MessagingException e) {
			System.out.println("Error retrieving newsgroups addresses");
		}
		if (prefetchOptions != null) {
			for (String header : prefetchOptions.prefetchedHeaders) {
				seenHeaders.put(header, parent.getHeader(header));
			}
			if (prefetchOptions.prefetchAttachments) {
				loadAttachments();
			}
		}
	}

	private ArrayList<Address> createList(RecipientType recipientType)
			throws MessagingException {
		Address[] addresses = parent.getRecipients(recipientType);
		return createList(addresses);
	}

	private ArrayList<Address> createList(Address[] addresses) {
		ArrayList<Address> addressesList = new ArrayList<>();
		if (addresses != null) {
			for (Address address : addresses) {
				addressesList.add(address);
			}
		}
		return addressesList;
	}

	private Date extractDate() throws MessagingException {
		String[] header = parent.getHeader("Date");

		for (DateFormat dateFormat : dateFormats) {
			try {
				if (header != null && header.length > 0) {
					String dateStr = header[0];
					Date date = dateFormat.parse(dateStr);
					return date;
				}
			} catch (ParseException e) {
			}
		}
		try {
			throw new RuntimeException("Unparsed date:" + header[0]);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return parent.getReceivedDate();
	}

	public String[] getHeader(String header) throws MessagingException {
		if (!seenHeaders.containsKey(header)) {
			throw new MessagingException("Header value " + header
					+ " was not preloaded");
		} else {
			return seenHeaders.get(header);
		}
	}

	public String getSubject() throws MessagingException {
		if (subject == null) {
			subject = parent.getSubject();
		}
		return subject;
	}

	public Collection<Address> getFrom() throws MessagingException {
		if (from == null) {
			from = createList(parent.getFrom());
		}
		return from;
	}

	public ArrayList<Address> getTo() throws MessagingException {
		if (to == null) {
			to = createList(RecipientType.TO);
		}
		return to;
	}

	public ArrayList<Address> getCc() throws MessagingException {
		if (cc == null) {
			cc = createList(RecipientType.CC);
		}
		return cc;
	}

	public ArrayList<Address> getBcc() throws MessagingException {
		if (bcc == null) {
			bcc = createList(RecipientType.BCC);
		}
		return bcc;
	}

	public ArrayList<Address> getNewsgroups() throws MessagingException {
		if (newsgroups == null) {
			newsgroups = createList(javax.mail.internet.MimeMessage.RecipientType.NEWSGROUPS);
		}
		return newsgroups;
	}
	
	@Override
	public String getMessageId() throws MessagingException {
		String[] messageIds = getHeader("Message-ID");
		if (messageIds == null || messageIds.length == 0) {
			return null;
		}
		return messageIds[0];
	}

	public ArrayList<String> getReferences() throws MessagingException {
		ArrayList<String> references = new ArrayList<String>();
		if (getHeader("References") != null) {
			String[] refHeader = getHeader("References");
			for (int i = 0; i < refHeader.length; i++) {
				String[] entries = refHeader[i]
						.split("\\s*((,\\s*\n)|(\n\\s*,)|(,)|(\n))\\s*");
				for (int j = 0; j < entries.length; j++) {
					references.add(entries[j]);
				}
			}
		}
		return references;
	}

	public String getInReplyTo() throws MessagingException {
		String inReplyTo = null;
		if (getHeader("In-Reply-To") != null
				&& getHeader("In-Reply-To").length > 0) {
			inReplyTo = getHeader("In-Reply-To")[0];
		}
		return inReplyTo;
	}

	public ArrayList<String> getAttachedFiles() {
		return attachedFiles;
	}

	private void loadAttachments() throws IOException, MessagingException {
		if (parent.getContent() instanceof Multipart) {
			Multipart multipart = (Multipart) parent.getContent();
			for (int i = 0; i < multipart.getCount(); i++) {
				BodyPart bodypart = multipart.getBodyPart(i);
				if (Part.ATTACHMENT.equalsIgnoreCase(bodypart.getDisposition())) {
					attachedFiles.add(bodypart.getFileName());
				}
			}
		}
	}
}
