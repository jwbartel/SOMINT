package bus.thunderbird;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AccountsOwnersManager {
	
	Set<String> owners = new HashSet<String>();
	
	public boolean addOwner(String owner){
		return owners.add(owner.toLowerCase());
	}
	
	@SuppressWarnings("unchecked")
	public Set<String> getOwners(){
		return (Set<String>) ((HashSet<String>) owners).clone();
	}
	
	public void save(String dest) throws IOException{
		save(new File(dest));
	}
	
	public void save(File dest) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		
		Iterator<String> iter = owners.iterator();
		while(iter.hasNext()){
			out.write(iter.next());
			out.newLine();
		}
		
		out.flush();
		out.close();
	}
	
	public void load(String src) throws IOException{
		load(new File(src));
	}
	
	public void load(File src) throws IOException{
		if(!src.exists()) return;
		
		BufferedReader in = new BufferedReader(new FileReader(src));
		
		String line = in.readLine();
		while(line != null){
			owners.add(line);
			line = in.readLine();
		}
		
		in.close();
	}
	
	
}
