package bus.tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;


public class Histogram {

	Map<Double,Integer> counts = new TreeMap<Double, Integer>();
	double bucketSize = 1.0;
	
	public Histogram(){
		
	}
	
	public Histogram(double bucketSize){
		this.bucketSize = bucketSize;
	}
	
	
	public void addValue(double value){
		Double bucket = value - value%bucketSize;
		
		Integer oldCount = counts.get(bucket);
		if(oldCount == null){
			counts.put(bucket, 1);
		}else{
			counts.put(bucket, oldCount+1);
		}
	}
	
	public void write(File dest) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		for(Entry<Double,Integer> entry: counts.entrySet()){
			out.write(""+entry.getKey()+","+entry.getValue());
			out.newLine();
		}
		out.flush();
		out.close();
	}
	
	public static void main(String[] args){
		Histogram hist = new Histogram(0.5);
		hist.addValue(2.2);
		hist.addValue(2.3);
		hist.addValue(2.6);
		hist.addValue(1);
		hist.addValue(3.4);
		
		System.out.println(hist.counts);
	}
}
