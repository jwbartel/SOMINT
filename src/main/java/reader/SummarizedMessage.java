package reader;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import reader.threadfinder.CausalObject;
import reader.threadfinder.newsgroups.tools.PostLoader;

public class SummarizedMessage implements Comparable<SummarizedMessage>, CausalObject<SummarizedMessage>{

	//Regular expressions for processing dates
	private static String dayOfWeek = "[a-zA-Z]+";
	private static String monthText = "[a-zA-Z]+";
	private static String monthNum = "\\d{1,2}";
	private static String dayOfMonth = "\\d{1,2}";
	private static String year = "\\d{2}(\\d{2})?";
	private static String slashedDateRegex = "("+dayOfMonth+"/"+monthNum+"/"+year+")";
	private static String wordedDateRegex = "("+"("+dayOfWeek+"(,|\\s)(\\s*))?"+
		"(("+dayOfMonth+"\\s+"+monthText+")|("+monthText+"\\s+"+dayOfMonth+"))(,|\\s)\\s*"+year+")";
	private static String clockTime = "\\d{1,2}:\\d{2}(:\\d{2})?(\\s+(am|AM|pm|PM))?";
	private static String timeZone = "((-)?\\d{4})?(\\s*\\(?[a-zA-Z]{3}\\))?";
	private static String timeRegex = "("+clockTime+"(\\s+"+timeZone+")?)";
	private static String dateRegex = "(("+slashedDateRegex+")|("+wordedDateRegex+"))";
	private static String timedDateRegex = dateRegex+"(\\s+"+timeRegex+")?";
	
	//Message to base all computations off of
	MimeMessage message;
	
	//Used for tracking message threads
	SummarizedMessage parent = null;
	Set<SummarizedMessage> children = new HashSet<SummarizedMessage>();
	
	//Values retrieved and computed from message
	Date sentDate;
	String subject;
	Boolean isReplyOrForward;
	String baseSubject;
	String messageId;
	String contents;
	String currentContent;
	ArrayList<String> references;
	ArrayList<BodyPart> attachments;
	ArrayList<SummarizedMessage> pastMessages;
	
	public SummarizedMessage(String messageId){
		this.messageId = messageId;
	}

	
	public SummarizedMessage(MimeMessage message) throws MessagingException{
		this.message = message;
		/*this.sentDate = message.getSentDate();*/
		this.subject = message.getSubject();
		if(subject == null) subject = "";
		loadInReplyTo();

		
		/*try {
			getAttachments();
			Object content = message.getContent();
			if(content instanceof Multipart){
				Multipart multipart = (Multipart) content;
				System.out.println(subject);
				for (int i = 0; i < multipart.getCount(); i++) {
					BodyPart bodyPart = multipart.getBodyPart(i);
					if(bodyPart.getFileName()!= null){
						attachments.add(bodyPart);
					}
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}*/
	}
	
	public MimeMessage getMessage(){
		return message;
	}
	
	public void setMessage(MimeMessage message){
		this.message = message;
	}
	
	public SummarizedMessage getParent(){
		return this.parent;
	}
	
	public void makeRoot(){
		this.parent = null;
	}
	
	public void removeParent(){
		makeRoot();
	}
	
	public boolean removeChild(SummarizedMessage child){
		return children.remove(child);
	}
	
	public boolean addChild(SummarizedMessage child){
		Set<SummarizedMessage> seenMessages = new HashSet<SummarizedMessage>();
		seenMessages.add(this);
		
		//Don't readd an already known child
		if(children.contains(child)) return true;
		
		// Don't set a parent for a child that has determined its parent from a it's own 
		// references list
		if(child.getMessage() != null && child.parent != null && child.parent != this){
			return false;
		}
		
		//Don't add a child that would introduce a cycle in the thread
		if(child.ensureNoCycle(seenMessages)){
			this.children.add(child);
			child.parent = this;
			return true;
		}
		return false;
	}
	
	public Set<SummarizedMessage> getChildren(){
		return children;
	}
	
	private boolean ensureNoCycle(Set<SummarizedMessage> seenMessages){
		for(SummarizedMessage child: children){
			if(seenMessages.contains(child)) return false;
			Set<SummarizedMessage> childsSeenMessages = new HashSet<SummarizedMessage>(seenMessages);
			if(!child.ensureNoCycle(childsSeenMessages)) return false;
		}
		return true;
	}
	
	public Date getSentDate() throws MessagingException{
		if(sentDate == null && message != null){
			sentDate = message.getSentDate();
		}		
		return sentDate;
	}
	
	public String getSubject() throws MessagingException{
		if(subject == null && message != null){
			subject = message.getSubject();
		}
		return subject;
	}
	
	public String getBaseSubject() throws UnsupportedEncodingException{
		
		if(message != null && baseSubject == null && subject != null){
			baseSubject = ImapThreadFinder.getBaseSubject(subject);
		}
		return baseSubject;
	}
	
	public Boolean isReplyOrForward() throws UnsupportedEncodingException{
		if(message != null && isReplyOrForward == null && subject != null){
			isReplyOrForward = ImapThreadFinder.isReplyOrFwd(subject);
		}
		return isReplyOrForward;
	}
	
	public String getMessageId() throws MessagingException{
		if(messageId == null && message != null){
			messageId = message.getMessageID();
		}
		return messageId;
	}
	
	public ArrayList<BodyPart> getAttachments() throws MessagingException, IOException{
		try{
			if(attachments == null && message != null){
				attachments = new ArrayList<BodyPart>();
				Object content = message.getContent();
				if(content instanceof Multipart){
					Multipart multipart = (Multipart) content;
					//System.out.println(getSubject());
					for (int i = 0; i < multipart.getCount(); i++) {
						BodyPart bodyPart = multipart.getBodyPart(i);
						if(bodyPart.getFileName()!= null){
							attachments.add(bodyPart);
						}
					}
				}
			}
		}catch(MessagingException e){
			attachments = null;
			throw e;
		} catch (IOException e) {
			attachments = null;
			throw e;
		}
		return attachments;
	}
	
	public ArrayList<String> getReferences() throws MessagingException{
		
		if(references == null && message != null){

			String delimeter = "(\n|\r|,|;)\\s*";

			String[] inReplyToVals = getSplitMultilineHeader("In-Reply-To", delimeter); //message.getHeader("In-Reply-To");
			String[] referenceVals = getSplitMultilineHeader("References", delimeter);

			references = new ArrayList<String>();
			for(String reference: referenceVals){
				references.add(reference);
			}
			/*for(String inReplyTo: inReplyToVals){
				if(!inReplyTos.contains(inReplyTo)){
					inReplyTos.add(inReplyTo);
				}
			}*/
		}
		
		return references;
	}
	
	private static String[] getContents(Part mailPart) throws MessagingException, IOException{
		String type = mailPart.getContentType();
		String[] contentsAndType = null;
		if(type.startsWith("multipart/")){
			MimeMultipart mailMultipart = (MimeMultipart) mailPart.getContent();
			for(int i=0; i<mailMultipart.getCount(); i++){
				String[] newContentsAndType = getContents(mailMultipart.getBodyPart(i));
				if(newContentsAndType != null && (contentsAndType == null || (!contentsAndType[1].startsWith("text/plain") && newContentsAndType[1].startsWith("text/plain")))){
					contentsAndType = newContentsAndType;
				}
				
			}
		}else if(type.startsWith("text/")){
			contentsAndType = new String[2];
			contentsAndType[0] = (String) mailPart.getContent();
			contentsAndType[1] = type;
		}
		return contentsAndType;
	}
	
	public String getCurrentContents() throws MessagingException, IOException{
		if(message != null && currentContent == null){
			getPastMessages();
		}
		return currentContent;
	}
	
	public String getContents() throws MessagingException, IOException{
		if(message != null && contents == null){
			String[] contentsAndType = getContents(message);
			if(contentsAndType != null){
				contents = contentsAndType[0].trim();
			}
		}
		return contents;
	}
	
	public ArrayList<SummarizedMessage> getPastMessages() throws MessagingException, IOException {
		if(message != null && pastMessages == null){
			pastMessages = parsePastMessages(getContents(), this);
		}
		return pastMessages;
	}
	
	protected void setCurrentContent(String currentContent){
		this.currentContent = currentContent;
	}
	
	private static ArrayList<SummarizedMessage> parsePastMessages(String content, SummarizedMessage currSummarizedMsg) throws MessagingException, IOException {
		
		ArrayList<SummarizedMessage> pastMessages = new ArrayList<SummarizedMessage>();
		String[] lines = content.split("\n");
		

		for(int i=0; i<lines.length; i++){
			String line =  lines[i];
			
			if(line.matches("From:.*")||line.matches("Date:.*")){
				while(line.matches(".*\\[\\s*mailto:.*\\]\\s*")){
					Matcher matcher = Pattern.compile("\\[\\s*mailto:").matcher(line);
					matcher.find();
					line = line.substring(0,matcher.start()) + "<"+ line.substring(matcher.end());
					int close = line.lastIndexOf(']');
					line = line.substring(0,close)+">"+line.substring(close+1);
					lines[i] = line;
				}
				String currentContent = getCurrentContent(lines, i);
				currSummarizedMsg.setCurrentContent(currentContent);

				String pastContent = getSimplePastContent(lines, i);
				SummarizedMessage summarizedPastMessage = new SummarizedMessage(PostLoader.createPost(pastContent));

				pastMessages.add(summarizedPastMessage);
				pastMessages.addAll(summarizedPastMessage.getPastMessages());
				break;
			}else if(line.matches("[Tt]o:.*")){
				int prev = i;
				String dateStr = "", fromAddress = "";
				while(prev >= 0){
					if(prev == i || lines[prev].trim().length() == 0){
						prev--;
						continue;
					}else{
						if(lines[prev].matches(".*[Oo]n\\s+"+timedDateRegex+"\\s*(wrote:)?")){
							String lastHeaders = lines[prev].trim();

							Matcher matcher = Pattern.compile("wrote:\\s*").matcher(lastHeaders);
							int end = lastHeaders.length();
							while(matcher.find()){
								end = matcher.start();
							}
							lastHeaders = lastHeaders.substring(0, end).trim();

							String[] dateAndFrom = parseFromThenDate(lines[prev].toLowerCase(), "on");
							dateStr = dateAndFrom[0];
							fromAddress = dateAndFrom[1];
						}else{
							throw new RuntimeException("Incorrect prev");
						}
						break;
					}
				}
				
				String currentContent;
				if(prev >= 0){
					currentContent = getCurrentContent(lines, prev);
				}else{
					currentContent = getCurrentContent(lines, i);
				}
				currSummarizedMsg.setCurrentContent(currentContent);
				
				String pastContent = getPastHeaderedContent(lines, i, fromAddress, dateStr);
				if(pastContent.length() > 0){
					SummarizedMessage summarizedPastMessage = new SummarizedMessage(PostLoader.createPost(pastContent));
					pastMessages.add(summarizedPastMessage);
					pastMessages.addAll(summarizedPastMessage.getPastMessages());
				}
				break;
			}else if(line.matches("\\s*-+\\s*Original Message\\s*-+\\s*") ||
					line.matches("\\s*-+\\s*Forwarded Message\\s*-+\\s*")){

				String currentContent = getCurrentContent(lines, i);
				currSummarizedMsg.setCurrentContent(currentContent);

				String pastContent = getSimplePastContent(lines, i+1);
				if(pastContent.length() > 0){
					SummarizedMessage summarizedPastMessage = new SummarizedMessage(PostLoader.createPost(pastContent));
					pastMessages.add(summarizedPastMessage);
					pastMessages.addAll(summarizedPastMessage.getPastMessages());
				}
				break;
			}else if(line.startsWith(">")){

				String currentContent = null;
				String dateStr = null;
				String fromAddress = null;
				int prev = i;
				while(prev >= 0){
					if(prev == i || lines[prev].trim().length() == 0){
						prev--;
						continue;
					}else{
						if(lines[prev].matches("\\s*On.*wrote:\\s*")){
							Matcher matcher = Pattern.compile("\\s*On").matcher(lines[prev]);
							matcher.find();
							String lastHeaders = lines[prev].substring(matcher.end());
							
							matcher = Pattern.compile("wrote:\\s*").matcher(lastHeaders);
							int end = 0;
							while(matcher.find()){
								end = matcher.start();
							}
							lastHeaders = lastHeaders.substring(0, end).trim();
							
							String[] dateAndFrom = parseDateAndFrom(lastHeaders);
							dateStr = dateAndFrom[0];
							fromAddress = dateAndFrom[1];
						}else{
							prev = -1;
						}
						break;
					}
				}
				if(prev >= 0){
					currentContent = getCurrentContent(lines, prev);
				}else{
					currentContent = getCurrentContent(lines, i);
				}
				currSummarizedMsg.setCurrentContent(currentContent);
				
				String pastContent = getPastLessThanMarkedContent(lines, i, fromAddress, dateStr);
				if(pastContent.length() > 0){
					SummarizedMessage summarizedPastMessage = new SummarizedMessage(PostLoader.createPost(pastContent));
					pastMessages.add(summarizedPastMessage);
					pastMessages.addAll(summarizedPastMessage.getPastMessages());
				}
				
				break;
			}
		}
		
		if(pastMessages.size() == 0){
			currSummarizedMsg.setCurrentContent(content.replaceAll("\\s+", " ").trim());
		}
		
		return pastMessages;
	}

	private static String[] parseFromThenDate(String line, String separator){
		String dateStr = null;
		String emailStr = null;
		
		Matcher matcher = Pattern.compile(separator+"\\s+"+timedDateRegex).matcher(line);
		int splitPt = 0;
		while(matcher.find()){
			splitPt = matcher.start();
		}
		if(splitPt > 0){
			emailStr = line.substring(0, splitPt).trim();
			dateStr = line.substring(splitPt + separator.length()).trim();
		}
		
		String[] retVal = {dateStr, emailStr};
		return retVal;
	}
	
	private static String[] parseDateAndFrom(String line){
		
		String dateStr = null;
		String emailStr = null;
		if(line.matches(wordedDateRegex+".*")){
			Matcher matcher = Pattern.compile(dateRegex+"\\s+at\\s+"+timeRegex).matcher(line);
			matcher.find();
			dateStr = matcher.group();
			int splitPt = dateStr.lastIndexOf("at");
			dateStr = dateStr.substring(0, splitPt).trim() + " " + dateStr.substring(splitPt+2).trim();
			
			emailStr = line.substring(matcher.end()).trim();
			if(emailStr.startsWith(",")){
				emailStr = emailStr.substring(1);
			}
			emailStr = emailStr.trim();
		}else if(line.matches(timedDateRegex+".*")){
			Matcher matcher = Pattern.compile(timedDateRegex).matcher(line);
			matcher.find();
			dateStr = matcher.group();
			line = line.substring(matcher.end());
			if(line.startsWith(",")){
				line = line.substring(1);
			}
			emailStr = line.trim();
		}else{
			System.out.println("FOUND NON-HANDLED DATE");
		}
		
		String[] retVal = {dateStr, emailStr};
		return retVal;
	}
	
	private static String getCurrentContent(String[] lines, int breakPoint){
		
		String content = "";
		
		for(int i=0; i<breakPoint; i++){
			if(i>0) content += "\n";
			content += lines[i];
		}
		
		return content.replaceAll("\\s+", " ").trim();
		
	}
	
	private static String getSimplePastContent(String[] lines, int breakPoint){
		String content = "";
		
		for(int i=breakPoint; i<lines.length; i++){

			if(!lines[i].matches("Content-Type:\\s*multipart/.*")){
				if(i>breakPoint) content += "\n";
				content += lines[i];
			}
			
		}
		
		return content;
	}
	
	private static String getPastHeaderedContent(String[] lines, int breakpoint, String fromAddress, String dateStr){
		String content = "";
		if(fromAddress != null){
			content = "From: "+fromAddress+"\n";
		}
		if(dateStr != null){
			content += "Sent: "+dateStr+"\n";
		}
		content += getSimplePastContent(lines, breakpoint);
		return content;
	}
	
	private static String getPastLessThanMarkedContent(String[] lines, int breakpoint, String fromAddress, String dateStr){
		String content = "";
		if(fromAddress != null){
			content = "From: "+fromAddress+"\n";
		}
		if(dateStr != null){
			content += "Sent: "+dateStr+"\n";
		}
		content += "\n";
		
		ArrayList<String> pastLines = new ArrayList<String>();
		
		for(int i=breakpoint; i<lines.length; i++){
			String line = lines[i];
			if(line.startsWith(">")){
				line = line.substring(1).trim();
			}
			if(!line.matches("Content-Type:\\s*multipart/.*")){
				pastLines.add(line);
			}
		}
			
		for(int i=pastLines.size()-1; i>0; i--){
			String line = pastLines.get(i);
			if(i != 0 && !line.startsWith(">") && !line.matches("\\s*On.*wrote:\\s*")){
				if(line.length() > 0){
					pastLines.set(i-1, pastLines.get(i-1)+" "+line);
				}
				pastLines.remove(i);
			}
		}
		
		for(String line: pastLines){
			if(content.length() > 0){
				content += " \n";
			}
			content += line;
		}
		
		return content.trim();
	}
	
	public String getMultilineHeader(String name) throws MessagingException{
		return message.getHeader(name, "\n");
	}
	
	public String[] getSplitMultilineHeader(String name, String delimeter) throws MessagingException{
		String headerValue = getMultilineHeader(name);
		if(headerValue != null) headerValue = headerValue.trim();
		if(headerValue == null || headerValue.equals("")) return new String[0];
		return headerValue.split(delimeter);
	}

	@Override
	public int compareTo(SummarizedMessage m) {
		int dateCompare;
		try {
			dateCompare = this.getSentDate().compareTo(m.getSentDate());
			if(dateCompare != 0) return dateCompare;
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
		try {
			return this.getSubject().compareTo(m.getSubject());
		} catch (MessagingException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public String toString(){
		try {
			return ""+getSubject()+" "+getSentDate();
		} catch (MessagingException e) {
			e.printStackTrace();
			return super.toString();
		}
	}
	
	private void loadInReplyTo(){
		try {
			//System.out.println("Subject: "+this.subject);
			
			String delimeter = "(\n|\r|,|;)\\s*";
			
			String[] inReplyTos = getSplitMultilineHeader("In-Reply-To", delimeter); //message.getHeader("In-Reply-To");
			String[] references = getSplitMultilineHeader("References", delimeter);
			
			/*String referencesStr = message.getHeader("References", "\n");
			System.out.println(referencesStr);
			String[] references = (referencesStr != null)? referencesStr.split("(\n|\r|,|;)\\s*") : null;
			*/
			
			
			/*System.out.print("\tIn-Reply-To: [");
			for(int i=0; inReplyTos != null && i < inReplyTos.length; i++){
				System.out.print(inReplyTos[i]);
				if(i < inReplyTos.length - 1) System.out.print(", ");
			}
			System.out.println("]");
			
			System.out.print("\tReferences:  [");
			for(int i=0; references != null && i < references.length; i++){
				System.out.print(references[i]);
				if(i < references.length - 1) System.out.print(", ");
			}
			System.out.println("]");
			System.out.println();*/
			
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public int getThreadSize(){
		int length = 1;
		for(SummarizedMessage child: children){
			length += child.getThreadSize();
		}
		return length;
	}
	
	public Integer getThreadID(Map<MimeMessage,File> messageToFile, Map<File,Integer> fileToThreadId){
		if(getMessage() != null){
			File file = messageToFile.get(getMessage());
			Integer threadId = fileToThreadId.get(file);
			if(threadId != null) return threadId;
		}
		for(SummarizedMessage child: getChildren()){
			Integer threadId = child.getThreadID(messageToFile, fileToThreadId);
			if(threadId != null) return threadId;
		}
		return null;
	}

	@Override
	public boolean isCauseOf(SummarizedMessage message) {
		/*if(!getBaseSubject().equals(message.getBaseSubject())){
			return false;
		}
		
		//TODO: return true only if contents imply a causal relationship*/
		
		return true;
	}
	
	public Set<String> getRecipients() throws MessagingException{
		String[] newsgroups = message.getHeader("Newsgroups");
		if(newsgroups != null){
			String[] splitNewsgroups = newsgroups[0].split(",");
			Set<String> recipients = new TreeSet<String>();
			for(String newsgroup : splitNewsgroups){
				newsgroup = newsgroup.trim().toLowerCase();
				newsgroup = newsgroup.replaceAll("-", "");
				recipients.add(newsgroup);
			}
			return recipients;
		}else{
			Address[] addresses = message.getAllRecipients();
			Set<String> recipients = new TreeSet<String>();
			if(recipients != null) for(Address address: addresses){
				recipients.add(address.toString().toLowerCase());
			}
			return recipients;
		}
	}
}
