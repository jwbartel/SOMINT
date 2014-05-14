package bus.precomputeExtractor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.MessagingException;

public class BuildExtractedFolderSystem {
	
	public static void createFolder(File folder){
		if(folder.exists()){
			return;
		}
		
		folder.mkdirs();
	}
	
	public static void copy(File src, File dest) throws IOException{
		FileReader in = new FileReader(src);
		FileWriter out = new FileWriter(dest);
		int c;

	    while ((c = in.read()) != -1)
	      out.write(c);
	    
	    in.close();
	    out.flush();
	    out.close();
	}
	
	public static void buildExtractedList(String oldListLoc, String newListLoc) throws IOException{
		buildExtractedList(new File(oldListLoc), new File(newListLoc));
	}
	
	public static void buildExtractedList(File oldListLoc, File newListLoc) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(oldListLoc));
		BufferedWriter out = new BufferedWriter(new FileWriter(newListLoc));
		
		String line = in.readLine();
		out.write(line);
		out.newLine();
		
		String oldFolder = oldListLoc.getParent() + "/";
		String newFolder = newListLoc.getParent() + "/";
		
		Set<String> suffixes = PrecomputeDataIdentifier.getPrecomputeFileSuffixes();;
		
		line = in.readLine();
		while(line != null){
			
			boolean lineIsAttachmentLoc = false;
			if(line.startsWith("\t")){
				line = line.substring(1);
				lineIsAttachmentLoc = true;
			}
			
			if(line.startsWith(oldFolder)){
				line = line.substring(oldFolder.length());
			}
			
			if(lineIsAttachmentLoc){
				line = "\t" + line;
			}else{
				try {
					PrecomputesFileBuilder builder = new PrecomputesFileBuilder(new File(oldFolder+line));
					builder.writePrecomputes();
				} catch (MessagingException e) {
					e.printStackTrace();
					System.exit(0);
				}
				
				Iterator<String> suffixesIter = suffixes.iterator();
				while(suffixesIter.hasNext()){
					String suffix = suffixesIter.next();
					
					File oldFile = new File(oldFolder+line+suffix);
					if(oldFile.exists()){
						File newFile = new File(newFolder+line+suffix);
						if(newFile.exists()) continue;
						createFolder(newFile.getParentFile());
						
						copy(oldFile, newFile);
						//System.out.println("copied "+line+suffix);
						//TODO: copy
					}
				}
				//break;
			}
			
			
			out.write(line);
			out.newLine();
			
			
			line = in.readLine();
		}
		
		in.close();
		out.flush();
		out.close();
	}
	
	public static void buildExtractedGoogleGroupsList(String oldGroupsList, String newGroupsList) throws IOException{
		buildExtractedGoogleGroupsList(new File(oldGroupsList), new File(newGroupsList));
	}
	
	public static void buildExtractedGoogleGroupsList(File oldGroupsList, File newGroupsList) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(oldGroupsList));
		BufferedWriter out = new BufferedWriter(new FileWriter(newGroupsList));
		

		String oldFolder = oldGroupsList.getParent() + "/";
		
		String line = in.readLine();
		while(line != null){
			if(line.startsWith("\t")){
				
				line = line.substring(1);
				
				String dateAndDirection = "\t"+line.substring(0, line.indexOf('\t')) + "\t";
				line = line.substring(line.indexOf('\t')+1);
				dateAndDirection = line.substring(0, line.indexOf('\t')) + "\t";
				line = line.substring(line.indexOf('\t')+1);
				
				if(line.startsWith(oldFolder)){
					line = line.substring(oldFolder.length());
				}
				
				out.write(dateAndDirection + line);
				out.newLine();
			}else{
				out.write(line);
				out.newLine();
			}
			
			line = in.readLine();
		}
		
		in.close();
		out.flush();
		out.close();
	}
	
	public static void buildExtractedFolderSystem(String oldRoot, String newRoot) throws IOException{
		buildExtractedFolderSystem(new File(oldRoot), new File(newRoot));
	}
	
	public static void buildExtractedFolderSystem(File oldRoot, File newRoot) throws IOException{
		createFolder(newRoot);
		
		String msgListName = PrecomputeDataIdentifier.getMsgListName();
		buildExtractedList(new File(oldRoot, msgListName), new File(newRoot, msgListName));
		
//		String googleGroupListName = PrecomputeDataIdentifier.getGoogleGroupsList();
//		buildExtractedGoogleGroupsList(new File(oldRoot, googleGroupListName), new File(newRoot, googleGroupListName));
	}
	

	
	private static Set<String> buildIgnoredAccounts(){
		Set<String> ignoredAccounts = new TreeSet<String>();
		ignoredAccounts.add("beck-s");
		ignoredAccounts.add("dasovich-j");
		ignoredAccounts.add("farmer-d");
		ignoredAccounts.add("kaminski-v");
		ignoredAccounts.add("kitchen-l");
		ignoredAccounts.add("nemec-g");
		ignoredAccounts.add("shackleton-s");
		ignoredAccounts.add("taylor-m");
		return ignoredAccounts;
	}
	
	public static void main(String[] args) throws IOException{
		
		File accountsFolder = new File("/home/bartizzi/Research/Enron Accounts");
		File[] accounts = accountsFolder.listFiles();
		Arrays.sort(accounts);
		
		Set<String> ignoredAccounts = buildIgnoredAccounts();
		
		for(int i=0; i<accounts.length; i++){
			if(!accounts[i].isDirectory()) continue;
			if(ignoredAccounts.contains(accounts[i].getName())) continue;
			
			System.out.println(accounts[i].getName());
			
			File destFolder = new File("/home/bartizzi/Research/Precompute only", accounts[i].getName());
			buildExtractedFolderSystem(accounts[i], destFolder);
		}
		
		
		
		/*String currMessage = "/home/bartizzi/Research/Enron Accounts/hain-m/Mary_Hain_Aug2000_Jul2001/Notes Folders/Notes inbox/3";		
		File addressFile = new File(currMessage+Account.ADDR_FILE_SUFFIX);
		//if(!addressFile.exists()){
			Account.saveAddresses(new File(currMessage), addressFile);
		//}*/
	}

}
