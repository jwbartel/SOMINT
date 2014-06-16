package jinjing.rule.basicfeature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

import jinjing.dataimport.ThreadData;
import jinjing.rule.NumericVectorFeatureRule;

/**
 * Read nominal feature values of a thread set from file, extract as numeric vector value
 * The format of file should be like:
 * threadid1	featurevalue
 * threadid2	featurevalue
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class ReadFromFileNumericVectorRule extends NumericVectorFeatureRule implements IBasicFeatureRule{
	
	/** save <threadid, nominal value> */
	HashMap<Integer, String> attrVals;
	
	/** domain of nominal values */
	ArrayList<String> domain;
	
	/**
	 * Create a ReadFromFileNumericVectorRule
	 * Read nominal feature values of a thread set from file
	 * 
	 * @param destFeatureName name for extracted feature
	 * @param fileName path of file to read
	 * @throws IOException if opening or reading operations have exception
	 */
	public ReadFromFileNumericVectorRule(String destFeatureName, String fileName) throws IOException {
		super(destFeatureName, 0);
		
		attrVals = new HashMap<Integer, String>();
		domain = read(fileName);
		this.length = domain.size(); 
	}
	
	/**
	 * Read and keep nominal feature values of a thread set from file
	 * 
	 * @param fileName path of file to read
	 * @throws IOException if opening or reading operations have exception
	 */
	private ArrayList<String> read(String fileName) throws IOException{
		HashSet<String> domainSet = new HashSet<String>();
		
		File file = new File(fileName);
		if(file.exists()){
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while((line=reader.readLine())!=null){
				Scanner scanner = new Scanner(line);
				int id = scanner.nextInt();
				String val  = scanner.next();
				scanner.close();
				attrVals.put(id, val);
				domainSet.add(val);
			}
			reader.close();
		}
		
		ArrayList<String> domain = new ArrayList<String>();
		Iterator<String> it = domainSet.iterator();
	    while(it.hasNext()){
	           domain.add(it.next());
	    }
		domain.trimToSize();
	    return domain;
	}

	/**
	 * Get the double array format of nominal feature value of given thread
	 * 
	 * @param aThread the source thread data
	 * @return the double array format of nominal feature value
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {
		double[] val = new double[length];
		for(int i=0; i<val.length; i++){
			val[i]=0;
		}
		
		int index = domain.indexOf(attrVals.get(aThread.getThreadId()));		
		val[index] = 1;
		
		return val;
	}
	

}
