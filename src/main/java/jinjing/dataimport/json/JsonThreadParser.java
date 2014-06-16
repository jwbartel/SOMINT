package jinjing.dataimport.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;

import jinjing.dataimport.MessageData;
import jinjing.dataimport.MsgDataConfig;
import jinjing.dataimport.ThreadData;
import jinjing.dataimport.ThreadDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An data parser for JSON-format files.
 * Each file should contain a thread/conversation.
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class JsonThreadParser {
	
	/** data configure of the start message (like question) */
	static JsonDataConfig STARTMSGCONFIG;
	
	/** data configure of follow messages (like answers) */
	static JsonDataConfig FOLLOWMSGCONFIG;
	
	/** date field used to sort messages*/
	protected static String keyDateAttrName = "Date";
	
	
	/**
	   * Create the JSON-format data parser when >=1 fields are date format string,
	   * or the only date attribute's name is not "Date"
	   * 
	   * @param startMsgConfig data configure of the start message
	   * @param followMsgConfig data configure of follow messages
	   * @param keyDateAttrName the name of date field to sort messages
	   */
	public JsonThreadParser(JsonDataConfig startMsgConfig, 
			JsonDataConfig followMsgConfig, String keyDateAttrName){
		STARTMSGCONFIG = startMsgConfig;
		FOLLOWMSGCONFIG = followMsgConfig;
		JsonThreadParser.keyDateAttrName = keyDateAttrName;
	}
	

	/**
	   * Create the JSON-format data parser the only date attribute has name "Date".
	   * 
	   * @param startMsgConfig data configure of the start message
	   * @param followMsgConfig data configure of follow messages
	   */
	public JsonThreadParser(JsonDataConfig startMsgConfig, JsonDataConfig followMsgConfig){
		STARTMSGCONFIG = startMsgConfig;
		FOLLOWMSGCONFIG = followMsgConfig;
		JsonThreadParser.keyDateAttrName = "Date";
	}
	
	/**
	   * Parse a JSONObject (containing info of a single message) into 
	   * a message data with given config, then put into a thread data. 
	   * 
	   * @param json the input JSONObject
	   * @param thread the thread data saving the parsed message
	   * @param config the corresponding data config for the message,
	   * 		should be eigher a start message config or follow msg config
	   * @throws Exception when error occurs in parsing the JSONObject
	   */
	protected static void parseMsgObject(JSONObject json, 
			ThreadData thread, JsonDataConfig config) throws Exception{
		
		MessageData msg = new MessageData();
		
		for(Entry<String, String> entry : config.attrTypeEntries()){
			String attrName = entry.getKey();
			String attrType = entry.getValue();
			
			if(!json.has(attrName)) continue;
			
			Object obj = json.get(attrName);
			//System.out.println(o);
			if(obj instanceof String){							
				String s = (String)obj;
	
				switch(attrType){
				case(JsonDataConfig.INT): 
					msg.addAttribute(attrName, Integer.parseInt(s));
					//msg.addAttribute(attrName, json.getInt(attrName));
					break;
				case(JsonDataConfig.DOUBLE):
					msg.addAttribute(attrName, Double.parseDouble(s));
					//msg.addAttribute(attrName, json.getDouble(attrName));
					break;
				case(JsonDataConfig.STRING):
					msg.addAttribute(attrName, s);
					//msg.addAttribute(attrName, json.getString(attrName));
					break;
				case(JsonDataConfig.TYPEDATE):
					SimpleDateFormat readformat = new SimpleDateFormat(config.getDateReadFormat());
					Date date = readformat.parse(s);
					SimpleDateFormat saveformat = new SimpleDateFormat(JsonDataConfig.DATEFORMAT_DEFAULT);
					if(attrName.equals(keyDateAttrName)){				
						msg.addAttribute(MsgDataConfig.DATE_DEFAULT, saveformat.format(date));
					}else{
						msg.addAttribute(attrName, saveformat.format(date));
					}
					break;
				case(JsonDataConfig.JSON):
					msg.addAttribute(attrName, s);
					break;
				default:
					break;
				}
			}else if(obj instanceof JSONObject){
				JSONObject jsonobj = (JSONObject)obj;
				if(attrType.equals(JsonDataConfig.MESSAGE)){								
					attrName = attrName.substring(0, attrName.length()-1);
					if(jsonobj.has(attrName)){
						Object tmp = jsonobj.get(attrName);
						if(tmp instanceof JSONArray){
							JSONArray arr = (JSONArray)tmp;
							for(int i=0; i<arr.length(); i++){
								JSONObject follow = arr.getJSONObject(i);
								parseMsgObject(follow, thread, FOLLOWMSGCONFIG);
							}
						}
					}
				}
			}else if(obj instanceof JSONArray){
				JSONArray arr = (JSONArray)obj;
				if(attrType.equals(JsonDataConfig.MESSAGE)){													
					for(int i=0; i<arr.length(); i++){
						JSONObject follow = arr.getJSONObject(i);
						parseMsgObject(follow, thread, FOLLOWMSGCONFIG);
					}					
				}
			}
		}
		
		thread.addMsgData(msg);		
	}
	
	/**
	   * Parse a JSONObject (containing info of a thread) into a thread data. 
	   * 
	   * @param jsonfile the input JSON file containing info of a thread 
	   * @throws Exception when error occurs in parsing the JSON files
	   */
	public ThreadData parseSingleFile(File jsonfile) throws Exception{
		
		BufferedReader reader = new BufferedReader(new FileReader(jsonfile));
		StringBuilder sb = new StringBuilder();
		String line;
		while((line=reader.readLine())!=null){
			sb.append(line);
		}
		
		JSONObject json = new JSONObject(sb.toString());
		//get rid of wraps
		if(json.has("query")){
			json = json.getJSONObject("query");
		}
		if(json.has("results")){
			json = json.getJSONObject("results");
		}
		if(json.has("Question")){
			json = json.getJSONObject("Question");
		}
		
		ThreadData thread = new ThreadData();
		parseMsgObject(json, thread, STARTMSGCONFIG);
		
		//System.out.println(thread.toString());
		return thread;	
	}
	
	/**
	   * Parse a directory of JSON files (containing info of a thread) into 
	   * a thread dataset.  
	   * 
	   * @param dirfile the directory of input JSON files containing thread info
	   * @return the output thread dataset
	   * @throws Exception when error occurs in reading/parsing the JSON files
	   */
	public ThreadDataSet parseDirectory(File dirfile) throws Exception{
		
		if(!dirfile.isDirectory()){
			throw new Exception(dirfile.getAbsolutePath() + " suppose to be a directory");
		}
		
		ThreadDataSet threadSet = new ThreadDataSet();
		threadSet.addThreadData(null);
		parseDirectory(dirfile, threadSet);
		
		return threadSet;
	}
	
	/**
	   * Parse a directory of JSON files (containing info of a thread) into 
	   * a thread dataset. It is called recursively to parse directory in 
	   * directory files 
	   * 
	   * @param dirfile the directory of input JSON files containing thread info
	   * 		or other directories
	   * @param the output thread dataset
	   * @throws Exception when error occurs in reading/parsing the JSON files
	   */
	protected void parseDirectory(File dirfile, ThreadDataSet threadSet) throws Exception{
		
		if(!dirfile.isDirectory()){
			throw new Exception(dirfile.getAbsolutePath() + " suppose to be a directory");
		}
		
		File[] files = dirfile.listFiles();
		for(int i=0; i< files.length; i++){
			if(files[i].isDirectory()){
				parseDirectory(files[i], threadSet);
			}
			if(!files[i].getName().endsWith(".txt")){
				continue;
			}
			//System.out.println("parsing "+files[i].getName());
			ThreadData thread = parseSingleFile(files[i]);
			int id = threadSet.size();
			thread.setThreadId(id);
			threadSet.setThreadData(id, thread);
		}
	}

}
