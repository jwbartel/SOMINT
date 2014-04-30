package bus.tools;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PrintValues{
	
	public static void printAccuracies(String csvFile) throws IOException{
		printAccuracies(new File(csvFile));
	}
	
	public static void printAccuracies(File csvFile) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(csvFile));
		String line = in.readLine();
		while(line != null){
			
			int firstComma = line.indexOf(',');
			System.out.print(line.substring(0,firstComma)+",");
			line = line.substring(firstComma+1);
			
			String[] data = line.split(",");
			for(int i=0; i+3<data.length; i = i+6){
				String data1 = data[i+2];
				String data2 = data[i+3];
				if(!data2.equals("0")){
					System.out.print("="+data1+"/"+data2);
				}
				System.out.print(",");
			}
			
			System.out.println();
			
			line = in.readLine();
		}
	}
	
	
	public static void printCoverages(String csvFile) throws IOException{
		printCoverages(new File(csvFile));
	}
	
	public static void printCoverages(File csvFile) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(csvFile));
		String line = in.readLine();
		while(line != null){
			
			int firstComma = line.indexOf(',');
			System.out.print(line.substring(0,firstComma+1));
			line = line.substring(firstComma+1);
			
			String[] data = line.split(",");
			for(int i=0; i+4<data.length; i = i+6){
				String data1 = data[i+3];
				String data2 = data[i+4];
				if(!data2.equals("0")){
					System.out.print("="+data1+"/"+"("+data1 + "+" + data2 + ")");
				}
				System.out.print(",");
			}
			
			System.out.println();
			
			line = in.readLine();
		}
	}
	
	public static void printAcceptanceSize(String csvFile) throws IOException{
		printAcceptanceSize(new File(csvFile));
	}
	
	public static void printAcceptanceSize(File csvFile) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(csvFile));
		String line = in.readLine();
		while(line != null){
			
			int firstComma = line.indexOf(',');
			System.out.print(line.substring(0,firstComma+1));
			line = line.substring(firstComma+1);
			
			String[] data = line.split(",");
			for(int i=0; i+2<data.length; i = i+6){
				String data1 = data[i];
				String data2 = data[i+2];
				if(!data2.equals("0")){
					System.out.print("="+data1+"/"+data2);
				}
				System.out.print(",");
			}
			
			System.out.println();
			
			line = in.readLine();
		}
	}
	
	
	public static void main(String[] args) throws IOException{
		//printAccuracies("D:\\Enron data\\results\\combined intersection-subset\\temp2.csv");
		//printCoverages("D:\\Enron data\\results\\combined intersection-subset\\temp2.csv");
		//printAcceptanceSize("D:\\Enron data\\results\\combined intersection-subset\\temp2.csv");
		
		Set<Integer> s1 = new HashSet<Integer>();
		s1.add(34230540);
		s1.add(54451);
		s1.add(889);
		
		Set<Integer> s2 = new HashSet<Integer>();
		s2.add(48);
		s2.add(9784654);
		s2.add(783);
		
		Collection<Set<Integer>> list = new ArrayList<Set<Integer>>();
		list.add(s1);
		list.add(s2);
		
		s1 = new HashSet<Integer>();
		s1.add(54451);
		s1.add(34230540);
		s1.add(889);
		
		System.out.println(list.contains(s1));
	}
}