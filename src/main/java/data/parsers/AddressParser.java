package data.parsers;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddressParser {
	
	protected final static String localChars = "[a-zA-Z0-9!#$%&*+/=?^_`'{|}~-]+";
	protected final static String localName = localChars+"([.]"+localChars+")*";
	
	protected final static String domainChars = "[a-zA-Z0-9-]+";
	protected final static String domainName = domainChars+"([.]"+domainChars+")*";
	
	protected static String normEmailRegex = "("+localName+"@"+domainName+")"+"|"+"(\""+localName+"\"@"+domainName+")";
	protected static String enronStyleEmailRegex = "("+
												"(/o=enron/ou=(na|eu)/cn=recipients/cn=[a-zA-Z0-9-_]+)"+
												"|"+"((\")?([a-zA-Z]([a-zA-Z/_]|\\s)*(\")?\\sat\\senron[_]development[@]ccmail))" +
												"|"+"(([?][?]s)?\"?([?][?]s)?[a-zA-Z&%][a-zA-Z& ]*?(/[a-zA-Z_&]+)*\"(((@("+domainChars+"([.]"+domainChars+")*))(@enron)?)|((@("+domainChars+"([.]"+domainChars+")*))?(@enron))))"+
												"|"+"(([?][?]s)?\"?([?][?]s)?[a-zA-Z&%][a-zA-Z0-9_&%. ()-]*\"?@((enron)|"+domainName+"))"+
												")";
	protected static String undisclosedEmailRegex = "("+
												"(([?][?]s)?undisclosed-recipient(s)?(:+)?(;+)?(@((enron)|("+domainName+")))?)"+
												"|"+"(([?][?]s)?unspecified-recipients:+;+(@((enron)|("+domainName+")))?)"+
												"|"+"(recipient list suppressed:;(@((enron)|("+domainName+")))?)"+
												"|"+"([?][?]srecipient list not shown: ;(@((enron)|("+domainName+")))?)"+
												")";
	protected static String enronSpecificEmailRegex = "("+
													"(office of the chairman-@enron)" +
													"|"+"(exchange system administrator <.>)"+
													"|"+"(clickathome and community relations-@enron)"+
													"|"+"(enron property (&|and) services corp.@enron)"+
													"|"+"(houston outage report -- 1@enron)"+
													"|"+"(([?][?]s)?er:;@enron)"+
													"|"+"(\"the desk subscriber\":;@enron)"+
													"|"+"([?][?]s\"<\"@enron)"+
													"|"+"(\"linda\" <@msn.com>@enron)"+
													"|"+"(forecast-operations, calgary.weather@ec.gc.ca)"+
													"|"+"(stan horton, chairman & ceo( ets)?@enron)"+
													"|"+"(george wasaff, global strategic sourcing@enron)"+
													"|"+"(robert knight,( )?director voice operations & trading technology@enron)"+
													"|"+"(bob butts, ibuyit payables executive( )?sponsor@enron)"+
													"|"+"(steve roy- director, human resources, teesside power station@enron)"+
													"|"+"(kevin hannon, jim hughes and john lavorato@enron)"+
													"|"+"(enw office of the chairman- greg piper, sally beck & mark pickering@enron)"+
													"|"+"(john brindle, business controls@enron)"+
													"|"+"(deanrogers@energyclasses[.]com [(]kase and company, inc.[)]@enron)"+
													"|"+"(lholcomb@swko.net [(]larry, julie, cody and zack[)]@enron)"+
													")";
	protected static String emailRegex = "("+enronSpecificEmailRegex+"|"+normEmailRegex+"|"+enronStyleEmailRegex+"|"+undisclosedEmailRegex+")";
	
	protected static String contactName = "((\"[^\"]*?\")"  +  "|" + "('[^']*?')" + "|" + "([^\\s,;].*?))";
	protected static String complexName = "(([^,;>].*?)|(\"[^,;>].*?\")|('[^,;>].*?'))";
	
	protected static String entrySeparator = "(;|,|$)";
	
	//final static String contactRegex = emailRegex+"|"+"("+"(\"|')"+emailRegex+"(\"|')"+")"+"|("+contactName+"\\s*(<|\"|')"+emailRegex+"(>|\"|')"+")";
	
	protected static String nameEntryRegex  = contactName+entrySeparator;
	protected static String emailEntryRegex = "("+"("+emailRegex+")"+"|"+"("+"'"+emailRegex+"'"+")"+"|"+"("+"\""+emailRegex+"\""+")"+")";
	protected static String complexEntryRegex = "("+"(("+complexName+")\\s*)?"+"<"+emailRegex+"[;]?"+">"+")"+"|"+"("+"(([^,;/].*?)\\s*)?"+"<"+emailRegex+"[;]?"+"/"+")";
	protected static String complexEntry2Regex = "([\"][']"+emailRegex+"\\s*['][\"])"+"<"+contactName+entrySeparator;
	
	protected static Pattern undisclosedEmailPattern = Pattern.compile(undisclosedEmailRegex);
	
	protected static Pattern nameEntryPattern = Pattern.compile(nameEntryRegex);
	protected static Pattern emailEntryPattern = Pattern.compile(emailEntryRegex);
	protected static Pattern complexEntryPattern = Pattern.compile(complexEntryRegex);
	protected static Pattern complexEntry2Pattern = Pattern.compile(complexEntry2Regex);
	
	
	protected  Pattern emailPattern = Pattern.compile(emailRegex);
	//final static Pattern contactPattern = Pattern.compile(contactRegex);
	
	

	protected ArrayList<String> addresses = new ArrayList<String>();
	
	public AddressParser(String addresses){
		parseAndAdd(addresses.toLowerCase());
	}
	
	public AddressParser(){
		
	}
	
	public void add(String addresses){
		parseAndAdd(addresses);
	}
	
	protected int findNonNegMin(int val1, int val2, int val3){
		int max = Math.max(val1, val2);
		max = Math.max(max, val3);
		if(val1 == -1) val1=max+1;
		if(val2 == -1) val2=max+1;
		if(val3 == -1) val3=max+1;
		
		int min = Math.min(val1, val2);
		min = Math.min(min, val3);
		return min;
	}
	
	protected void parseAndAdd(String addresses){
		
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
		
		if(addresses.equals("ayala, susie") 
				|| addresses.equals("wilson, shona") 
				|| addresses.equals("vakharia, adarsh") 
				|| addresses.equals("mark e. haedicke.@enron") 
				|| addresses.equals("derryl cleaveland.@enron") 
				|| addresses.equals("ken lay-@enron") 
				|| addresses.equals("delainey, david")
				|| addresses.equals("stan horton.@enron")
				|| addresses.equals("mccarty, danny")){
			addSingleAddress(addresses);
			return;
		}
		
		if(addresses.equals("teruo tanaka <t>")){
			addSingleAddress("teruo.tanaka@ibjbank.co.jp");
			return;
		}
		
		if(!(emailFound || complexFound || complex2Found) && addresses.length() > 0){
			//System.out.println("all names");
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
			if(complexStart == 0){
				entry = complexEntryMatcher.group();
				isComplex = true;
			}else if(emailStart == 0){
					entry = emailEntryMatcher.group();
					addresses = addresses.substring(addresses.indexOf(entry)+entry.length());
					char lastChar = entry.charAt(entry.length()-1);
					int max = addresses.length();
					if(lastChar == ';' || lastChar == ','){
						max = 0;
					}
					int startPt = findNonNegMin(addresses.indexOf(','), addresses.indexOf(';'), max);
					addresses = addresses.substring(startPt);
					shouldResizeAddresses = false;
			}else if(complex2Start == 0){
				entry = complexEntry2Matcher.group();
				isComplex2 = true;
			}else if(complexStart == 0){
				entry = complexEntryMatcher.group();
				isComplex = true;
			}else if(nameStart == 0){
				entry = nameEntryMatcher.group();
			}else{
				int min = findNonNegMin(nameStart, emailStart, complexStart);
				if(complexStart == min){
					entry = complexEntryMatcher.group();
				}else if(emailStart == min){
					entry = emailEntryMatcher.group();
				}else if(nameStart == min){
					entry = nameEntryMatcher.group();
				}
			}
			
			
			char lastChar = entry.charAt(entry.length()-1);
			if(lastChar == ',' || lastChar == ';'){
				if(shouldResizeAddresses){
					addresses = addresses.substring(addresses.indexOf(entry)+entry.length());
				}
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
			if(!undisclosedEmailPattern.matcher(entry).matches()){
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
		
		/*Matcher contactMatcher = contactPattern.matcher(addresses);
		//Matcher contactMatcher = Pattern.compile(contactName+"<"+enronEmailRegex+">").matcher(addresses);
				
		boolean addedAddress = false;
		
		int lastStart = -1;
		
		while(contactMatcher.find()){
			
			
			String after = addresses.substring(contactMatcher.end());
			if(after.matches("[^ ,;].*")){
				if(lastStart == -1){
					lastStart = contactMatcher.start();
				}
			}else{
				String address;
				
				if(lastStart != -1){
					address = addresses.substring(lastStart, contactMatcher.end());
					lastStart = -1;
				}else{
					address = contactMatcher.group();
				}
				
				addSingleAddress(address);
				addedAddress = true;
			}
		}
		
		if(!addedAddress){
			
			if(addresses.contains(";")){
				String[] split = addresses.split(";");
				
				for(int i=0; i<split.length; i++){
					addSingleAddress(split[i]);
				}
				
			}else if(addresses.contains(",")){
				
				String[] split = addresses.split(",");
				
				for(int i=0; i<split.length; i++){
					addSingleAddress(split[i]);
				}
				
			}else{
				
				addSingleAddress(addresses);
			}
		}*/
	}
	
	protected void addSingleAddress(String address){
		if(address.matches("lholcomb@swko.net [(]larry, julie, cody and zack[)]@enron")){
			address = "lholcomb@swko.net";
		}
		
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
				addresses.add(formattedAddr.toLowerCase());
			}
		}else if(address.length()>0){
			addresses.add(address.trim().toLowerCase());
		}
	}
	
	public String[] getAddresses(){
		if(addresses.size()==0){
			return null;
		}else{
			return addresses.toArray(new String[addresses.size()]);
		}
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<String> getAddressesInArrayList(){
		return (ArrayList<String>) addresses.clone();
	}
	
	public int size(){
		return addresses.size();
	}
	
	public String get(int index){
		return addresses.get(index);
	}
	
	public static void main(String[] args){
		AddressParser parser = new AddressParser();
		parser.add("undisclosed-recipients:; bartel.jacob@gmail.com");
	}
}
