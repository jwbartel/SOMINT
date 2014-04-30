package reader.threadfinder.enron.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import reader.ImapThreadFinder;
import reader.SummarizedMessage;
import reader.threadfinder.newsgroups.tools.NewsgroupPrecomputeBuilder;
import reader.threadfinder.newsgroups.tools.PostLister;
import reader.threadfinder.newsgroups.tools.PostLoader;

public class EnronPrecomputeBuilder {
	
	static String[] badAccounts = {"kaminski-v"};
	
	public static void buildFilteredFolder(File filteredList, File oldRoot, File newRoot) throws IOException{
		
		ArrayList<File> files = new ArrayList<File>();

		BufferedReader in = new BufferedReader(new FileReader(filteredList));
		in.readLine();
		String line = in.readLine();
		while(line != null){
			
			files.add(new File(line));
			
			line = in.readLine();
			while(line != null && line.startsWith("\t")){
				line = in.readLine();
			}
		}
		in.close();
		
		for(File file: files){
			File newFile = new File(newRoot, file.getPath().substring(oldRoot.getPath().length()));
			PostLister.copyFile(file, newFile);
		}
	}
	
	public static void buildAllFilteredFolders() throws IOException, MessagingException{
		File folder = new File("/home/bartizzi/Research/Enron Accounts");
		File newFolder = new File("/home/bartizzi/Research/Filtered Enron Accounts");
		File[] accounts = folder.listFiles();
		Arrays.sort(accounts);
		
		boolean start = true;
		for(int i=0; i<accounts.length; i++){

			
			if(accounts[i].getName().equals("kaminski-v")){
				continue;
			}
			
			if(!start){
				continue;
			}
			
			
			
			System.out.println(accounts[i].getName()+"...");
			
			File list = new File(accounts[i], "ALL_MESSAGES_ADAPTED.TXT");
			File newRoot = new File(newFolder, accounts[i].getName());
			
			buildFilteredFolder(list, accounts[i], newRoot);
		}
	}

	public static void buildPrecomputesForAccount(File accountFolder, File precomputeFolder) throws IOException, MessagingException{
		NewsgroupPrecomputeBuilder builder = new NewsgroupPrecomputeBuilder(accountFolder, precomputeFolder);
		builder.savePrecomputes();
	}
	
	public static void buildPrecomputesForAllAccounts(File accountsRoot, File precomputesRoot) throws IOException, MessagingException{
		
		File[] accounts = accountsRoot.listFiles();
		Arrays.sort(accounts);
		for(File account: accounts){
			File precomputeFolder = new File(precomputesRoot, account.getName());
			System.out.println("***************"+account.getName()+"***************");
			buildPrecomputesForAccount(account, precomputeFolder);
		}
	}
	
	public static void main(String[] args) throws IOException, MessagingException{
		
		File accountsRoot = new File("C:\\Users\\Jacob\\Dropbox\\Filtered Enron Accounts");
		File precomputesRoot = new File("Enron Precomputes");
		
		/*File msg = new File("C:\\Users\\Jacob\\Dropbox\\Filtered Enron Accounts\\allen-p",
				"PALLEN (Non-Privileged)\\Allen, Phillip K\\Sent Items\\55");
		SummarizedMessage summarizedMsg = new SummarizedMessage(PostLoader.getPost(msg));
		summarizedMsg.getPastMessages();
		System.out.println(summarizedMsg.getCurrentContents());
		for(SummarizedMessage pastMsg: summarizedMsg.getPastMessages()){
			System.out.println(pastMsg.getCurrentContents());
		}*/
		
		
		buildPrecomputesForAllAccounts(accountsRoot, precomputesRoot);
		
		/*File[] accounts = accountsRoot.listFiles();
		Arrays.sort(accounts);
		for(File root: accounts){
			System.out.println("***************"+root.getName()+"***************");
			
			File fileList = new File(new File("Enron Precomputes", root.getName()), "Cleaned and Ordered File List.txt");//\\bailey-s\\Cleaned and Ordered File List.txt");
			//File root = new File("C:\\Users\\Jacob\\Dropbox\\Filtered Enron Accounts\\bailey-s");
			
			BufferedReader in = new BufferedReader(new FileReader(fileList));
			String line = in.readLine();
			int count = 0;
			while(line != null){
				count++;
				MimeMessage msg = PostLoader.getPost(new File(root, line));
				SummarizedMessage summarizedMsg = new SummarizedMessage(msg);
				try{
				if(summarizedMsg.isReplyOrForward() 
						&& summarizedMsg.getPastMessages().size() < 1 
						&& summarizedMsg.getCurrentContents().length() > 0){
					
					System.out.println(""+count+" "+line+" "+summarizedMsg.getBaseSubject());
					
				}
				}catch(Exception e){
					e.printStackTrace();
					System.out.println(line);
					System.exit(0);
				}
				
				line = in.readLine();
			}
			break;
		}
		
		/*File messageLoc = new File("C:\\Users\\bartel\\Dropbox\\Filtered Enron Accounts\\bailey-s\\SBAILE2 (Non-Privileged)\\Bailey, Susan\\Sent Items\\14");
		//File messageLoc = new File("C:\\Users\\bartel\\Desktop\\Test Emails\\sample email");
		MimeMessage msg = PostLoader.getPost(messageLoc);
		
		SummarizedMessage summarizedMessage = new SummarizedMessage(msg);
		for(String contentPart : summarizedMessage.getSeparatedContents()){
			System.out.println(contentPart);
		}
		
		/*buildAllFilteredFolders();*/
		/*File accountFolder = new File("C:\\Users\\Jacob\\Dropbox\\Filtered Enron Accounts\\meyers-a");
		//File accountFolder = new File("/home/bartizzi/Research/Filtered Enron Accounts/bailey-s");
		File precomputesFolder = new File("Enron Precomputes/meyers-a");
		
		buildPrecomputesForAccount(accountFolder, precomputesFolder);*/
	}
}
