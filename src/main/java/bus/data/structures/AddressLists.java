package bus.data.structures;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class AddressLists {
	ArrayList<String> from = new ArrayList<String>();
	ArrayList<String> to = new ArrayList<String>();
	ArrayList<String> cc = new ArrayList<String>();
	ArrayList<String> bcc = new ArrayList<String>();
	
	
	public AddressLists(String addressFileName) throws IOException{
		init(new File(addressFileName));
	}
	
	public AddressLists(File addressFile) throws IOException{
		init(addressFile);
	}
	
	private void init(File addressFile) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(addressFile));
		
		ArrayList<String> currAddressList = null;
		
		String line = in.readLine();
		while(line != null){
			
			if(line.equals("FROM:")){
				currAddressList = from;
			}else if(line.equals("TO:")){
				currAddressList = to;
			}else if(line.equals("CC:")){
				currAddressList = cc;
			}else if(line.equals("BCC:")){
				currAddressList = bcc;
			}else if(!line.equals("")){
				currAddressList.add(line.toLowerCase());
			}
			
			line = in.readLine();
		}
		
		in.close();
	}
	
	public ArrayList<String> getFrom(){
		return from;
	}
	
	public ArrayList<String> getTo(){
		return to;
	}
	
	public ArrayList<String> getCC(){
		return cc;
	}
	
	public ArrayList<String> getBCC(){
		return bcc;
	}
	
	public Set<String> getReaders(){
		Set<String> toReturn = new TreeSet<String>();
		
		toReturn.addAll(to);
		toReturn.addAll(cc);
		toReturn.addAll(bcc);
		
		return toReturn;
	}
	
	public Set<String> getReadersInOrder(){
		Set<String> toReturn = new InsertionOrderedSet<String>();
		
		toReturn.addAll(to);
		toReturn.addAll(cc);
		toReturn.addAll(bcc);
		
		return toReturn;
	}
	
	public Set<String> getAll(){
		Set<String> toReturn = new TreeSet<String>();
		
		toReturn.addAll(from);
		toReturn.addAll(to);
		toReturn.addAll(cc);
		toReturn.addAll(bcc);
		
		return toReturn;
	}
	
	public Set<String> getAllInOrder(){
		Set<String> toReturn = new InsertionOrderedSet<String>();
		
		toReturn.addAll(from);
		toReturn.addAll(to);
		toReturn.addAll(cc);
		toReturn.addAll(bcc);
		
		return toReturn;
	}
}
