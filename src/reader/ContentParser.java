package reader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentParser {

	public final static String stopWordsFile = "specs/stopwords.txt";

	//String nonWords = "([.?!,]?(\\s+|\\s*?\\z|\"))|(\\s*[/()\\[\\]\";:|<>@#]\\s*)";
	public static final String nonWordChar = "([^a-zA-Z0-9.'$-])";
	public static final String nonWords = nonWordChar+"+";
	
	public static final String legalWordREGEX = ".*[a-zA-Z].*";
	public static final Pattern legalWord = Pattern.compile(legalWordREGEX);
	
	private static Set<String> stopWords;
	
	public static void loadStopWords(){
		try {
			BufferedReader in = new BufferedReader(new FileReader(stopWordsFile));
			stopWords = new TreeSet<String>();
			String line = in.readLine();
			while(line != null){
				stopWords.add(line);
				
				line = in.readLine();
			}
			
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Map<String, Integer> parse(String text){
		Map<String, Integer> retVal = new HashMap<String, Integer>();
		parseFrequencies(text, retVal);
		return retVal;
	}
	
	public static void parseFrequencies(String text, Map<String, Integer> wordFreqs){
		if(text==null){
			return;
		}
		
		if(stopWords == null){
			loadStopWords();
		}
		
		String[] words = text.split(nonWords);
		
		for(int i=0; i<words.length; i++){
			String word = words[i];
			
			while(word.length()>0 && (word.charAt(0)=='-' || word.charAt(0)=='\'')){
				words[i] = words[i].substring(1);
				word = words[i];
			}
			

			while(words[i].length()>0 &&  words[i].charAt(words[i].length()-1)=='\''){
				words[i] = words[i].substring(0, words[i].length()-1);
				word = words[i];
			}
						
			if(words[i].length()==0){
				continue;
			}
			
			if(! (words[i].equals("-")||words[i].equals("'"))){
				
				if(words[i].charAt(words[i].length()-1)=='-'){
					if(words[i].length()>1 && words[i].charAt(words[i].length()-2)=='-'){
						while(words[i].length()>0 && (words[i].charAt(words[i].length()-1)=='-' || words[i].charAt(words[i].length()-1)=='@' || words[i].charAt(words[i].length()-1)=='\'')){
							words[i] = words[i].substring(0, words[i].length()-1);
							word = words[i];
						}
						if(words[i].length()==0){
							continue;
						}
					}else{
						i++;
						if(i<words.length){
							words[i] = words[i-1].substring(0,words[i-1].length()-1)+words[i];
							//System.out.println(words[i]);
						}else{
							break;
						}
					}
				}
				
				Matcher m = Pattern.compile("(-|[']){2}").matcher(words[i]);
				
				if(m.find()){
					int index = m.start();
					parseFrequencies(words[i].substring(0,index)+" "+words[i].substring(index), wordFreqs);
					continue;
				}
				
				String key = words[i].toLowerCase();
				
				while(key.endsWith(".")){
					key = key.substring(0, key.length() - 1);
				}
				
				while(key.startsWith(".")){
					key = key.substring(1);
				}
				
				if(!legalWord.matcher(key).matches() ){
					continue;
				}
				

				if(stopWords.contains(key)){
					continue;
				}
				
				
				if(wordFreqs.containsKey(key)){
					int freq = wordFreqs.get(key) + 1;
					wordFreqs.put(key, freq);
				}else{
					wordFreqs.put(key, 1);
				}
			}
		}
		
	}
}
