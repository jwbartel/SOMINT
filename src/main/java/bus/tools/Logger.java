package bus.tools;

public class Logger {

	private static LogWriter writer = null;
	
	public static void setWriter(LogWriter w){
		writer = w;
	}
	
	public static void logln(String message){
		if(writer == null){
			System.out.println(message);
		}else{
			writer.println(message);
		}
	}
	
	public static void log(String message){
		if(writer == null){
			System.out.print(message);
		}else{
			writer.print(message);
		}
	}
}
