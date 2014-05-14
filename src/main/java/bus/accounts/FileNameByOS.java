package bus.accounts;

import java.io.File;

public class FileNameByOS {
	
	public static final int WINDOWS = 0;
	public static final int UNIXX = 1;
	
	private static String UNIX_PATH = "/home/bartizzi/Research/Enron Accounts/";
	private static String WINDOWS_PATH = "C:\\Users\\Jacob\\Documents\\My Dropbox\\Sample Enron Accounts\\";
	
	private static String WINDOWS_NEWSGROUPS_PATH = "C:\\Users\\Jacob\\Workspaces\\machine-learning-class\\data\\newsgroups";
	private static String UNIX_NEWSGROUPS_PATH = "/home/bartizzi/Workspaces/machine-learning-class/data/newsgroups";
	
	
	public static int getOS(){
		String os = System.getProperty("os.name").toLowerCase();
		if(os.contains("windows")){
			return WINDOWS;
		}
		return UNIXX;
	}
	
	
	public static File getMappedFile(File file){
		
		String newName = getMappedFileName(file.getPath());
		return new File(newName);
	}
	
	public static String getMappedFileName(String oldName){
		if(oldName == null){
			return null;
		}
		
		if(getOS() == WINDOWS){
			if(oldName.length()>=UNIX_PATH.length() && oldName.substring(0, UNIX_PATH.length()).equals(UNIX_PATH)){

				String end = oldName.substring(UNIX_PATH.length());
				end = end.replace('/', '\\');
				return WINDOWS_PATH+end;
			}
		}
		
		return oldName;
	}
	
	public static String getMappedNewsgroupFileName(String oldName){
		if(oldName == null){
			return null;
		}
		
		if(getOS() == WINDOWS){
			if(oldName.length()>=UNIX_NEWSGROUPS_PATH.length() && oldName.substring(0, UNIX_NEWSGROUPS_PATH.length()).equals(UNIX_NEWSGROUPS_PATH)){

				String end = oldName.substring(UNIX_NEWSGROUPS_PATH.length());
				end = end.replace('/', '\\');
				return WINDOWS_NEWSGROUPS_PATH+end;
				
			}
		}
		
		return oldName;
	}
	
	public static File getMappedNewsgroupFile(File file){

		String newName = getMappedNewsgroupFileName(file.getPath());
		return new File(newName);
		
	}
	
	public static void main(String[] args){
		System.out.println(getMappedFileName("/home/bartizzi/Research/Enron Accounts/meyers-a/ExMerge - Meyers, Albert/Sent Items/8-1-25act.xls"));
	}
}
