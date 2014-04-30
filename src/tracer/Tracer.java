package tracer;


import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;
public class Tracer {
	public static String ALL_KEYWORDS = "All Key Words";
	static Map <String, Boolean> keyWordToStatus = new HashMap();
    static TracingLevel tracingLevel = TracingLevel.ERROR; 
    
	static boolean showWarnings = false;
	static boolean showInfo = false;
	
	static MessagePrefixKind messagePrefixKind = MessagePrefixKind.SHORT_CLASS_NAME;
	static ImplicitKeywordKind implicitKeywordKind = ImplicitKeywordKind.OBJECT_PACKAGE_NAME;
	public static void showWarnings(boolean newValue) {
		showWarnings = newValue;
	}
	public static void showInfo(boolean newValue) {
		showInfo = newValue;
	}
	public static void setMessagePrefixKind(MessagePrefixKind newValue) {
		messagePrefixKind = newValue;
	}
	public static void setImplicitKeywordKind(ImplicitKeywordKind newValue) {
		implicitKeywordKind = newValue;
	}
	
	public static void fatalError(String error) {
		String msg = "Fatal Error***" + error + ". Terminating program";
		System.out.println(msg);
		JOptionPane.showMessageDialog(null, msg);
		System.exit(1);
	}
	public static void error(String error) {
		if ( tracingLevel.ordinal() >= TracingLevel.ERROR.ordinal() ) {
			System.out.println("E***" + error);
		}
	}
	public static void warning(String warning) {
		if (showWarnings) {
			System.out.println("W***" + warning);
		}
		else if ( tracingLevel.ordinal() >= TracingLevel.WARNING.ordinal() ) {
			System.out.println("W***" + warning);
		}
	}
	public static void info(String info) {
		if (showInfo) {
			System.out.println("I***" + info);
		}
		else if ( tracingLevel.ordinal() >= TracingLevel.INFO.ordinal() ) {
			System.out.println("I***" + info);
		}
	}
	public static void info(String keyWord, String info) {
		if (getKeyWordStatus(keyWord)) {
			infoWithPrefix(keyWord, info);
		}
	}
	public static void infoWithPrefix(String prefix, String info) {
			info ("(" + prefix + ")" + info);
	}
	public static void info(Object object, String keyWord, String info) {
		if (!getKeyWordStatus(keyWord)) 
			return;
		switch (messagePrefixKind) {
		case SHORT_CLASS_NAME: 
				infoWithPrefix(object.getClass().getSimpleName(), info);
				break;
			
		case FULL_CLASS_NAME: 
				infoWithPrefix(object.getClass().getName(), info);
				break;
				
		case PACKAGE_NAME: 
				infoWithPrefix(keyWord, info);
				break;
		case OBJECT_TO_STRING: 
				infoWithPrefix(object.toString(), info);
				break;
		
		case NONE: 
				info(info);			
		
		}
		
		
		
	}
	public static void info(Object caller, String info) {
		
		info(caller, getImplicitKeyword(caller), info);
	}
	public static String getImplicitKeyword(Object caller) {
		switch (implicitKeywordKind) {
		case OBJECT_CLASS_NAME:
			return caller.getClass().getName();
		case OBJECT_PACKAGE_NAME:
			return caller.getClass().getPackage().getName();
		case OBJECT_TO_STRING:
			return caller.toString();			
		}
		return "";
	}
	public static void setKeyWordStatus(Object object, Boolean status) {
		keyWordToStatus.put(getImplicitKeyword(object), status);
	}
	public static String getImplicitKeyword(Class c) {
		switch (implicitKeywordKind) {
		case OBJECT_CLASS_NAME:
			return c.getName();
		case OBJECT_PACKAGE_NAME:
			return c.getPackage().getName();
		case OBJECT_TO_STRING:
			Tracer.error("Cannot get implicit keyword for class as implicit keyword is OBJECT_TO_STRING");
			return "";			
		}
		return "";
	}
	public static void setKeyWordStatus(Class c, Boolean status) {
		keyWordToStatus.put(getImplicitKeyword(c), status);
	}
	public static void debug(String debugMessage) {
		if ( tracingLevel.ordinal() >= TracingLevel.DEBUG.ordinal() ) {
			System.out.println("D***" + debugMessage);
		}
	}
	public static void userMessage(String userMessage) {
		if ( tracingLevel.ordinal() >= TracingLevel.USER_MESSAGE.ordinal() ) {
			System.out.println("U***" + userMessage);
		}
	}
	public static TracingLevel getTracingLevel() {
		return tracingLevel;
	}
	public static void setTracingLevel(TracingLevel tracingLevel) {
		Tracer.tracingLevel = tracingLevel;
	}
	public static void setKeyWordStatus(String keyWord, Boolean status) {
		keyWordToStatus.put(keyWord, status);
	}
	public  static boolean getKeyWordStatus(String keyWord) {
		return keyWordToStatus.get(ALL_KEYWORDS) || 
		(keyWordToStatus.get(keyWord) != null && 
				keyWordToStatus.get(keyWord));
	}
	
	static {
		setKeyWordStatus(ALL_KEYWORDS, true);
	}
	
}

