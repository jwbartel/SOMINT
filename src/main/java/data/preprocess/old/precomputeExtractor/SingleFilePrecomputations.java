package data.preprocess.old.precomputeExtractor;

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

import bus.accounts.Account;

public class SingleFilePrecomputations extends BuildExtractedFolderSystem{

	
	public static void buildContextFreeList(String oldFile, String outFile) throws IOException{
		buildContextFreeList(new File(oldFile), new File(outFile));
	}
	
	public static void buildContextFreeList(File oldFile, File outFile) throws IOException{
		String context = oldFile.getParent()+"/";
		
		BufferedReader in = new BufferedReader(new FileReader(oldFile));
		BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
		
		String line = in.readLine();
		out.write(line);
		out.newLine();
		
		line = in.readLine();
		while(line != null){
			boolean lineIsAttachmentLoc = false;
			if(line.startsWith("\t")){
				line = line.substring(1);
				lineIsAttachmentLoc = true;
			}
			
			if(line.startsWith(context)){
				line = line.substring(context.length());
			}
			
			if(lineIsAttachmentLoc){
				line = "\t" + line;
			}
			
			out.write(line);
			out.newLine();
			
			line = in.readLine();
		}
	}
	
	public static void buildPrecomputeFile(String oldRoot, String outFile) throws IOException{
		buildPrecomputeFile(new File(oldRoot), new File(outFile));
	}
	
	public static void buildPrecomputeFile(File oldRoot, File outFile) throws IOException{
		File outFolder = outFile.getParentFile();
		if(!outFolder.exists()){
			outFolder.mkdirs();
		}
		
		buildExtractedList(new File(oldRoot, PrecomputeDataIdentifier.getMsgListName()), outFile);
		
	}
	public static boolean isWindows() {
 
		String os = System.getProperty("os.name").toLowerCase();
		// windows
		return (os.indexOf("win") >= 0);
 
	}
	
	public static String fixFilenameForOS(String filename){
		char from, to;
		if(isWindows()){
			from = '/';
			to = '\\';
		}else{
			from = '\\';
			to = '/';
		}
		for(int i=0; i<filename.length(); i++){
			if(filename.charAt(i) == from){
				String start = filename.substring(0, i);
				String end = "";
				if(i<filename.length() - 1){
					end = filename.substring(i+1);
				}
				filename = start + to + end;
			}
		}
		
		return filename;
		
	}
	
	public static void extractPrecomputations(String src, String destFolder) throws IOException{
		extractPrecomputations(new File(src), new File(destFolder));
	}
	
	public static void extractPrecomputations(File src, File destFolder) throws IOException{
		if(!destFolder.exists()){
			destFolder.mkdirs();
		}
		String prefix = destFolder.getAbsolutePath()+"\\";
		prefix = fixFilenameForOS(prefix);
		
		File listFile = new File(destFolder, PrecomputeDataIdentifier.getMsgListName());
		BufferedWriter out = new BufferedWriter(new FileWriter(listFile));
		
		BufferedReader in = new BufferedReader(new FileReader(src));
		String line = in.readLine();
		out.write(line);
		out.newLine();
		
		line = in.readLine();
		while(line != null && !line.equals("GROUPS:")){
			
			line = fixFilenameForOS(line);
			
			String msgFile = prefix+line;
			out.write(msgFile);
			out.newLine();
			
			File msgFolder = new File((new File(msgFile)).getParent());
			if(!msgFolder.exists()){
				msgFolder.mkdirs();
			}
			
			line = in.readLine();
			line = extractWordCounts(in, msgFile, line);
			line = extractAddressList(in, msgFile, line);
			line = extractDate(in, msgFile, line);
			
			while(line != null && line.startsWith("\t")){
				line = fixFilenameForOS(line);
				out.write("\t"+prefix+line.substring(1));
				out.newLine();
				line = in.readLine();
			}
			//break;
		}
		out.flush();
		out.close();

		line = in.readLine();
		
		File groupFile = new File(destFolder, Account.ADAPTED_GOOGLE_GROUPS_LIST);
		out = new BufferedWriter(new FileWriter(groupFile));
		while(line != null && !line.equals("Individuals:")){
			line = line.substring(1);
			if(line.charAt(0)!='\t'){
				
				out.write(line);
				out.newLine();
				
			}else{
				
				String start = "\t";
				line = line.substring(1);
				
				int splitPt = line.indexOf('\t')+1;
				start += line.substring(0, splitPt);
				line = line.substring(splitPt);
				
				splitPt = line.indexOf('\t')+1;
				start += line.substring(0, splitPt);
				line = line.substring(splitPt);
				
				line = prefix+fixFilenameForOS(line);
						
				out.write(start+line);
				out.newLine();
			}
			
			line = in.readLine();
		}
		
		out.flush();
		out.close();
		
		line = in.readLine();
		
		File individualsFile = new File(destFolder, Account.INDIVIDUALS_GOOGLE_LIST);
		out = new BufferedWriter(new FileWriter(individualsFile));
		
		while(line != null){
			line = line.substring(1);
			if(line.charAt(0)=='\t'){
				
				out.write(line);
				out.newLine();
				
			}else{
				
				String start = "";
				
				int splitPt = line.indexOf('\t')+1;
				start += line.substring(0, splitPt);
				line = line.substring(splitPt);
				
				splitPt = line.indexOf('\t')+1;
				start += line.substring(0, splitPt);
				line = line.substring(splitPt);
				
				line = prefix+fixFilenameForOS(line);
						
				out.write(start+line);
				out.newLine();
			}
			
			line = in.readLine();
		}
		out.flush();
		out.close();
		
		in.close();
		
		
	}
	
	protected static String extractDate(BufferedReader in, String msgFile, String line) throws IOException{
		if(line.equals("\tDATE:")){
			line  = in.readLine();
			if(line != null  && line.startsWith("\t\t")){
				File dateFile = new File(msgFile+Account.DATE_FILE_SUFFIX);
				BufferedWriter out = new BufferedWriter(new FileWriter(dateFile));
				
				line = line.substring(2);
				out.write(line);
				out.flush();
				out.close();
				line = in.readLine();
			}
		}
		return line;
	}
	
	protected static String extractWordCounts(BufferedReader in, String msgFile, String line) throws IOException{
		File wordCountFile = new File(msgFile+Account.WORD_COUNTS_SUFFIX);
		BufferedWriter out = new BufferedWriter(new FileWriter(wordCountFile));
		
		if(line != null && line.equals("\tWORD COUNTS:")){
			line = in.readLine();
			while(line != null && line.indexOf("\t\t")== 0){
				out.write(line.substring(2));
				out.newLine();
				
				line = in.readLine();
			}
		}
		out.flush();
		out.close();
		
		return line;
	}
	
	protected static String extractAddressList(BufferedReader in, String msgFile, String line) throws IOException{
		File addressFile = new File(msgFile + Account.ADDR_FILE_SUFFIX);
		BufferedWriter out = new BufferedWriter(new FileWriter(addressFile));
		
		out.write("FROM:");
		out.newLine();
		if(line.equals("\tFROM:")){
			
			line = in.readLine();
			
			while(line != null && line.startsWith("\t\t")){
				line = line.substring(2);
				out.write(line);
				out.newLine();
				line = in.readLine();
			}
			out.newLine();
		}
		
		out.write("TO:");
		out.newLine();
		if(line.equals("\tTO:")){
			
			line = in.readLine();
			
			while(line != null && line.startsWith("\t\t")){
				line = line.substring(2);
				out.write(line);
				out.newLine();
				line = in.readLine();
			}
			out.newLine();
		}

		out.write("CC:");
		out.newLine();
		if(line.equals("\tCC:")){
			
			line = in.readLine();
			
			while(line != null && line.startsWith("\t\t")){
				line = line.substring(2);
				out.write(line);
				out.newLine();
				line = in.readLine();
			}
			out.newLine();
		}
		
		out.write("BCC:");
		out.newLine();
		if(line.equals("\tBCC:")){
			
			line = in.readLine();
			
			while(line != null && line.startsWith("\t\t")){
				line = line.substring(2);
				out.write(line);
				out.newLine();
				line = in.readLine();
			}
			out.newLine();
		}
		
		out.flush();
		out.close();
		
		return line;
	}
	
	public static void buildExtractedList(File oldListLoc, File precomputeFile) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(oldListLoc));
		BufferedWriter out = new BufferedWriter(new FileWriter(precomputeFile));
		
		String line = in.readLine();
		out.write(line);
		out.newLine();
		
		String oldFolder = oldListLoc.getParent() + "/";
		
		Set<String> suffixes = PrecomputeDataIdentifier.getPrecomputeFileSuffixes();;
		
		line = in.readLine();
		while(line != null){
			String origLine = line;
			
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
			}
			
			out.write(line);
			out.newLine();
			
			if(!lineIsAttachmentLoc){
				Iterator<String> suffixesIter = suffixes.iterator();
				while(suffixesIter.hasNext()){
					String suffix = suffixesIter.next();
					
					try {
						PrecomputeDataIdentifier.printPrecomputeData(origLine, suffix, out);
					} catch (MessagingException e) {
						e.printStackTrace();
						throw new IOException("Messaging exception interrupted IO");
					}
				}
				//break;
			}
			
			
			
			
			line = in.readLine();
		}
		
		in.close();
		
		out.write("GROUPS:");
		out.newLine();
		File groupsListFile = new File(oldFolder, Account.ADAPTED_GOOGLE_GROUPS_LIST);
		in = new BufferedReader(new FileReader(groupsListFile));
		
		line = in.readLine();
		while(line != null){
			if(line.charAt(0)!='\t'){
				out.write("\t"+line);
				out.newLine();
			}else{
				String start = "\t";
				line = line.substring(1);
				
				int splitPt = line.indexOf('\t')+1;
				start += line.substring(0, splitPt);
				line = line.substring(splitPt);
				
				splitPt = line.indexOf('\t')+1;
				start += line.substring(0, splitPt);
				line = line.substring(splitPt);
				
				if(line.startsWith(oldFolder)){
					line = line.substring(oldFolder.length());
				}
				
				out.write("\t"+start+line);
				out.newLine();
				
			}
			line = in.readLine();
		}
		in.close();
		
		out.write("Individuals:");
		out.newLine();
		File individualsListFile = new File(oldFolder, Account.INDIVIDUALS_GOOGLE_LIST);
		in = new BufferedReader(new FileReader(individualsListFile));
		
		line = in.readLine();
		while(line != null){
			if(line.charAt(0)=='\t'){
				out.write("\t"+line);
				out.newLine();
			}else{
				String start = "";
				
				int splitPt = line.indexOf('\t')+1;
				start += line.substring(0, splitPt);
				line = line.substring(splitPt);
				
				splitPt = line.indexOf('\t')+1;
				start += line.substring(0, splitPt);
				line = line.substring(splitPt);
				
				if(line.startsWith(oldFolder)){
					line = line.substring(oldFolder.length());
				}
				
				out.write("\t"+start+line);
				out.newLine();
				
			}
			line = in.readLine();
		}
		in.close();
		
		
		out.flush();
		out.close();
	}
	
	protected static Set<String> ignoredAccounts = new TreeSet<String>();
	
	protected static void buildIgnoredAccounts(){
		ignoredAccounts.clear();
		ignoredAccounts.add("beck-s");
		ignoredAccounts.add("dasovich-j");
		ignoredAccounts.add("farmer-d");
		ignoredAccounts.add("kaminski-v");
		ignoredAccounts.add("kitchen-l");
		ignoredAccounts.add("nemec-g");
		ignoredAccounts.add("shackleton-s");
		ignoredAccounts.add("taylor-m");
	}
	
	public static void main(String[] args) throws IOException{
		
		
		//AddressLists addresses = new AddressLists("/home/bartizzi/Research/Enron Accounts/allen-p/Phillip_Allen_June2001/Notes Folders/Discussion threads/6"+Account.ADDR_FILE_SUFFIX);
		buildIgnoredAccounts();
		
		//File folder = new File("C:\\Users\\bartel\\Dropbox\\Data\\single file precomputes");//("/home/bartizzi/Research/Enron Accounts/");
		//File folder = new File("/home/bartizzi/Research/single file precomputes/");
		File folder = new File("/home/bartizzi/Research/Enron Accounts");
		File[] accounts = folder.listFiles();
		Arrays.sort(accounts);
		
		boolean start = true;
		//File destFolder = new File("D:\\Enron data\\extracted precomputes");
		File destFolder = new File("/home/bartizzi/Research/extracted precomputes");
		
		if (!destFolder.exists()) {
			destFolder.mkdirs();
		}
		
		for(int i=0; i<accounts.length; i++){
			
			//if(accounts[i].getName().equals("dasovich-j")) start = true;
			
			if(ignoredAccounts.contains(accounts[i].getName())) continue;
			
			if(!start) continue;
			//if((!accounts[i].getName().equals("allen-p"))) continue;;
			
			
			
			
			/*if(!accounts[i].isDirectory()){
				System.out.print(accounts[i].getName());
				File outFile = new File("/home/bartizzi/Research/single file precomputes/"+accounts[i].getName());
				buildContextFreeList(accounts[i], outFile);
				System.out.println();
			}else{
				System.out.print(accounts[i].getName());
				File outFile = new File("/home/bartizzi/Research/single file precomputes/"+accounts[i].getName()+".precompute.txt");
				buildPrecomputeFile(accounts[i], outFile);
				System.out.println();
			}*/
			
//			if(!accounts[i].getName().endsWith(".precompute.txt")){
//				File copyLoc = new File(destFolder, accounts[i].getName());
//				copy(accounts[i], copyLoc);
//				continue;
//			}
//			
//			//if((!accounts[i].getName().equals("bailey-s.precompute.txt"))) continue;;
			
			String name = accounts[i].getName();
//			name = name.substring(0, name.length() - ".precompute.txt".length());
			
			System.out.println(name);
			extractPrecomputations(accounts[i], new File(destFolder, name));
	
			
		}
	}
}
