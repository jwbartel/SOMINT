package reader;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImapThreadFinder {


	static String NON_WSP = "([^\\s])"; //any CHAR other than WSP
	static String WSP = "([\\s])";
	static String BLOBCHAR = "([^\\[\\]])"; //any CHAR except '[' and ']
	
	static String SUBJ_BLOB = "(" + "\\["+BLOBCHAR+"*" + "\\]" + WSP + "*" + ")";
	static String SUBJ_REFWD = "(" + "((re)|(fw[d]?))"+WSP+"*"+SUBJ_BLOB+"?"+":" + ")";
	
	static String SUBJ_FWD_HDR = "[fwd:";
	static String SUBJ_FWD_TRL = "]";
	
	static String SUBJ_LEADER = "(" + "("+SUBJ_BLOB+"*"+SUBJ_REFWD+")" + "|" + WSP + ")";
	static String SUBJ_TRAILER = "("+"([(]fwd[)])"+"|"+WSP+")";
	
	


	static Pattern SUBJ_BLOB_PATTERN = Pattern.compile(SUBJ_BLOB);
	static Pattern SUBJ_LEADER_PATTERN = Pattern.compile(SUBJ_LEADER);
	
	
	
	public static boolean isReplyOrFwd(String subject) throws UnsupportedEncodingException{
		String baseSubject = new String(subject.getBytes("UTF-8"), "UTF-8").toLowerCase();
		baseSubject = baseSubject.replaceAll("\t", " ");
		baseSubject = baseSubject.replaceAll("[ ]+", " ");

		while(true){
			while(baseSubject.matches(".*"+SUBJ_TRAILER)){
				if(baseSubject.endsWith("(fwd)")){
					return true;
				}else{
					baseSubject = baseSubject.substring(0, baseSubject.length()-1);
				}
			}

			boolean shouldCheckAgain = true;
			while(shouldCheckAgain){
				Matcher matcher = SUBJ_LEADER_PATTERN.matcher(baseSubject);
				if(matcher.find() && matcher.start() == 0 ){
					String foundVal = matcher.group();
					if(foundVal.contains("re:") || foundVal.contains("fwd:") || foundVal.contains("fw:")){
						return true;
					}
					baseSubject = baseSubject.substring(foundVal.length());
					shouldCheckAgain = true;
				}else{
					shouldCheckAgain = false;
				}

				matcher = SUBJ_BLOB_PATTERN.matcher(baseSubject);
				if(matcher.find() && matcher.start() == 0  && matcher.end() != baseSubject.length()){
					baseSubject = baseSubject.substring(matcher.group().length());
					shouldCheckAgain = true;
				}

			}

			if(baseSubject.startsWith(SUBJ_FWD_HDR) && baseSubject.endsWith(SUBJ_FWD_TRL)){
				return true;
			}else{
				break;
			}
		}
		return false;
	}
	
	public static String getBaseSubject(String subject) throws UnsupportedEncodingException{

		String baseSubject = new String(subject.getBytes("UTF-8"), "UTF-8").toLowerCase();
		baseSubject = baseSubject.replaceAll("\t", " ");
		baseSubject = baseSubject.replaceAll("[ ]+", " ");

		while(true){
			while(baseSubject.matches(".*"+SUBJ_TRAILER)){
				if(baseSubject.endsWith("(fwd)")){
					baseSubject = baseSubject.substring(0, baseSubject.length()-5);
				}else{
					baseSubject = baseSubject.substring(0, baseSubject.length()-1);
				}
			}

			boolean shouldCheckAgain = true;
			while(shouldCheckAgain){
				Matcher matcher = SUBJ_LEADER_PATTERN.matcher(baseSubject);
				if(matcher.find() && matcher.start() == 0 ){
					baseSubject = baseSubject.substring(matcher.group().length());
					shouldCheckAgain = true;
				}else{
					shouldCheckAgain = false;
				}

				matcher = SUBJ_BLOB_PATTERN.matcher(baseSubject);
				if(matcher.find() && matcher.start() == 0  && matcher.end() != baseSubject.length()){
					baseSubject = baseSubject.substring(matcher.group().length());
					shouldCheckAgain = true;
				}

			}

			if(baseSubject.startsWith(SUBJ_FWD_HDR) && baseSubject.endsWith(SUBJ_FWD_TRL)){
				baseSubject = baseSubject.substring(SUBJ_FWD_HDR.length(), baseSubject.length() - SUBJ_FWD_TRL.length());
			}else{
				break;
			}
		}
		return baseSubject;
	}
	
	/*
	public static void main(String[] args) throws UnsupportedEncodingException{
		
		System.out.println("[grads]".matches("(" + "\\["+BLOBCHAR+"*" + "\\]" + WSP + "*" + ")"));
		
		String subject = "[cssa-president] Re: [grads] [FWD: FWD: [faculty] this is  a\ttest\t of\t  subjects(fwd)\n]";
		System.out.print("\""+getBaseSubject(subject)+"\"");
	}
	*/
}
