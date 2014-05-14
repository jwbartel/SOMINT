package bus.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import bus.accounts.Account;

public class PrecomputationBuilder {

	public static void buildAddressLists(String accountFolder) throws IOException{
		buildAddressLists(new File(accountFolder));
	}
	
	public static void buildAddressLists(File accountFolder) throws IOException{
		File msgList = new File(accountFolder, Account.ALL_MSGS);
		BufferedReader in = new BufferedReader(new FileReader(msgList));
		
		in.readLine();
		String line = in.readLine();
		while(line != null){
			if(!line.startsWith("\t")){
				File addressFile = new File(line+Account.ADDR_FILE_SUFFIX);
				Account.saveAddresses(new File(line), addressFile);
			}
			
			line = in.readLine();
		}
	}
	
	
	public static void main(String[] args) throws IOException{
		File accountsFolder = new File("/home/bartizzi/Research/Enron Accounts");
		File[] accounts = accountsFolder.listFiles();
		Arrays.sort(accounts);
		
		for(int i=0; i<accounts.length; i++){
			if(!accounts[i].isDirectory()) continue;
			
			System.out.println(accounts[i].getName());
			buildAddressLists(accounts[i]);
		}
	}
}
