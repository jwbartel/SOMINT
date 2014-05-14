package bus.thunderbird.structures;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bus.data.parsers.AddressParser;

public class ThunderbirdAddressParser extends AddressParser {
	
	public ThunderbirdAddressParser(String addresses){
		init();
		parseAndAdd(addresses.toLowerCase());
	}
	
	public ThunderbirdAddressParser(){
		init();
	}
	
	
	protected static String quotedEmailRegex = "((\")?\""+localName+"(\\\\)?\"@"+domainName+")";
	protected static String fieldStartRegex = "Content-Type:";
	

	protected static Pattern fieldStartPattern = Pattern.compile(fieldStartRegex);
	
	protected void init(){
		emailRegex = "("+normEmailRegex+"|"+quotedEmailRegex+")";
		String externalQuoteEmailRegex = "('"+emailRegex+"')"+"|"+"(\""+emailRegex+"\")";
		String eitherNormalOrExternalQuoteEmailRegex = "("+emailRegex +"|" + externalQuoteEmailRegex + ")";
		
		emailEntryRegex = "("+"("+emailRegex+")"+"|"+"("+"'"+emailRegex+"'"+")"+"|"+"("+"\""+emailRegex+"\""+")"+")";
		complexEntryRegex = "("+"(("+complexName+")\\s*)?"+"<"+eitherNormalOrExternalQuoteEmailRegex+"[;]?"+">"+")"+"|"+"("+"(([^,;/].*?)\\s*)?"+"<"+eitherNormalOrExternalQuoteEmailRegex+"[;]?"+"/"+")";
		complexEntry2Regex = "([\"][']"+emailRegex+"\\s*['][\"])"+"<"+contactName+entrySeparator;
		
		nameEntryPattern = Pattern.compile(nameEntryRegex);
		emailEntryPattern = Pattern.compile(emailEntryRegex);
		complexEntryPattern = Pattern.compile(complexEntryRegex);
		complexEntry2Pattern = Pattern.compile(complexEntry2Regex);
	}
	
	public void parseAndAdd(String addresses){
		
		if(addresses.equals("\"(null)\" <mailer-daemon>") || addresses.equals("\"(null)\" <mailer-daemon")){
			return;
		}
		
		Matcher nameEntryMatcher = nameEntryPattern.matcher(addresses);
		Matcher emailEntryMatcher = emailEntryPattern.matcher(addresses);
		Matcher complexEntryMatcher = complexEntryPattern.matcher(addresses);
		Matcher complexEntry2Matcher = complexEntry2Pattern.matcher(addresses);
		
		boolean nameFound = nameEntryMatcher.find();
		boolean emailFound = emailEntryMatcher.find();
		boolean complexFound= complexEntryMatcher.find();
		boolean complex2Found= complexEntry2Matcher.find();
		
		if(!(emailFound || complexFound || complex2Found) && addresses.length() > 0){
			//System.out.println("all names:"+addresses);
		}
		
		while(nameFound || emailFound || complexFound || complex2Found){
			
			int nameStart = -1, emailStart = -1, complexStart = -1, complex2Start = -1;
			String entry = null;
			if(nameFound) nameStart = nameEntryMatcher.start();
			if(emailFound) emailStart = emailEntryMatcher.start();
			if(complexFound) complexStart = complexEntryMatcher.start();
			if(complex2Found) complex2Start = complexEntry2Matcher.start();
			
			boolean shouldResizeAddresses = true;
			
			boolean isComplex = false;
			boolean isComplex2 = false;
			boolean isName = false;
			if(complexStart == 0){
				entry = complexEntryMatcher.group();
				isComplex = true;
			}else if(emailStart == 0){
					entry = emailEntryMatcher.group();
					addresses = addresses.substring(addresses.indexOf(entry)+entry.length());
					
					int divCharPt = Math.max(addresses.indexOf(','), addresses.indexOf(';') );
					int fieldPt = -1;
					Matcher temp = fieldStartPattern.matcher(addresses);
					if(temp.find()){
						fieldPt = temp.start();
					}
					
					int startPt = findNonNegMin(divCharPt, fieldPt, addresses.length());
					if(startPt == fieldPt) startPt = addresses.length();
					addresses = addresses.substring(startPt);
					shouldResizeAddresses = false;
			}else if(complex2Start == 0){
				entry = complexEntry2Matcher.group();
				isComplex2 = true;
			}else if(complexStart == 0){
				entry = complexEntryMatcher.group();
				isComplex = true;
			}else if(nameStart == 0){
				isName = true;
				entry = nameEntryMatcher.group();
			}else{
				int min = findNonNegMin(nameStart, emailStart, complexStart);
				if(complex2Start == min){
					entry = complexEntry2Matcher.group();
					isComplex2 = true;
				}else if(complexStart == min){
					entry = complexEntryMatcher.group();
					isComplex = true;
				}else if(emailStart == min){
					entry = emailEntryMatcher.group();
				}else if(nameStart == min){
					entry = nameEntryMatcher.group();
					isName = true;
				}
			}
			
			
			char lastChar = entry.charAt(entry.length()-1);
			if(lastChar == ',' || lastChar == ';'){
				addresses = addresses.substring(addresses.indexOf(entry)+entry.length());
				entry = entry.substring(0,entry.length()-1);
			}else if(shouldResizeAddresses){
				addresses = addresses.substring(addresses.indexOf(entry)+entry.length());
				int startPt = findNonNegMin(addresses.indexOf(','), addresses.indexOf(';'), addresses.length());
				if(startPt < addresses.length()) startPt++;
				addresses = addresses.substring(startPt);
				shouldResizeAddresses = false;
			}
			
			if(isComplex){
				entry = entry.substring(entry.lastIndexOf('<')+1);
				int last = entry.lastIndexOf('>');
				if(last == -1) last = entry.lastIndexOf('/');
				entry = entry.substring(0, last);
			}
			
			if(isComplex2){
				entry = entry.substring(entry.indexOf("\"'")+2);
				entry = entry.substring(0, entry.indexOf("'\""));
			}

			if(addresses.length()>0){
				String firstChar = addresses.substring(0,1);
				while(Pattern.matches("\\s", firstChar) && addresses.length()>0){
					addresses = addresses.substring(1);
					if(addresses.length()>0){
						firstChar = addresses.substring(0,1);
					}
				}
			}
			
			if(!isName){
				addSingleAddress(entry);
			}
			
			if(nameFound) nameEntryMatcher = nameEntryPattern.matcher(addresses);
			if(emailFound) emailEntryMatcher = emailEntryPattern.matcher(addresses);
			if(complexFound) complexEntryMatcher = complexEntryPattern.matcher(addresses);
			if(complex2Found) complexEntry2Matcher = complexEntry2Pattern.matcher(addresses);
			
			nameFound = nameEntryMatcher.find();
			emailFound = emailEntryMatcher.find();
			complexFound= complexEntryMatcher.find();
			complex2Found= complexEntry2Matcher.find();

		}
	}
	
	protected void addSingleAddress(String address){
		
		if(address.equals("\"") || address.equals("\'") || address.length() == 0){
			return;
		}
		
		Matcher emailMatcher = emailPattern.matcher(address);
		String formattedAddr = null;
		
		if(address.length()>0){
			if(address.length() > 0 && address.charAt(0)=='<'){ 
				address = address.substring(1);
			}
			
			if(address.length() > 0 && address.charAt(address.length()-1)=='>'){
				address = address.substring(0, address.length()-1);
			}
			
			while(address.length() > 0 ){
				if(address.length() >= 4 && address.substring(0,2).equals("\\\"") && address.substring(address.length()-2).equals("\\\"") ){
					address = address.substring(2, address.length()-2);
					continue;
				}else if(address.length() >= 2 && address.substring(0,1).matches("\"") && address.substring(address.length()-1).matches("\"") ){
					address = address.substring(1, address.length()-1);
					continue;
				}else if(address.length() >= 2 && address.substring(0,1).matches("'") && address.substring(address.length()-1).matches("'")){
					address = address.substring(1, address.length()-1);
					continue;
				}else if ( address.substring(0,1).matches("\\s")){
					address = address.substring(1);
					continue;
				}else if( address.charAt(0)=='\''){
					address = address.substring(1);
					continue;
				}else if ( address.substring(address.length()-1).matches("\\s")){
					address = address.substring(0,address.length()-1);
					continue;
				}else{
					break;
				}
			}
		}
		
		while(!address.matches(emailRegex) && emailMatcher.find()){
			formattedAddr = emailMatcher.group();
		}
		
		
		if(formattedAddr != null && formattedAddr.length()>0){
			if(formattedAddr.charAt(0)=='<' && formattedAddr.charAt(formattedAddr.length()-1)=='>'){
				formattedAddr = formattedAddr.substring(1, formattedAddr.length()-1);
			}
			if(formattedAddr.length()>0){
				formattedAddr = fixQuotedEmailAddress(formattedAddr);
				if(!formattedAddr.contains("@")){
					System.out.println(formattedAddr);
				}
				addresses.add(formattedAddr.toLowerCase());
			}
		}else if(address.length()>0){
			address = fixQuotedEmailAddress(address);
			if(!address.contains("@")){
				System.out.println(address);
			}
			addresses.add(address.trim().toLowerCase());
		}
	}
	
	protected String fixQuotedEmailAddress(String address){
		if(!address.matches(quotedEmailRegex)){
			return address;
		}
		
		while(address.charAt(0)=='"'){
			address = address.substring(1);
		}
		
		int pos = address.indexOf('@')-1;
		while(address.charAt(pos)=='"' ||address.charAt(pos)=='\\'){
			address = address.substring(0, pos) + address.substring(pos+1);
			pos -= 1;
		}
		
		return address;
	}
	

	
	public String[] getAddresses(){
		String[] retVal = super.getAddresses();
		if(retVal == null) return new String[0];
		return retVal;
	}
	
	
}
