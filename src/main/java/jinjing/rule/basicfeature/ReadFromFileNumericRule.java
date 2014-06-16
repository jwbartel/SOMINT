package jinjing.rule.basicfeature;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import jinjing.dataimport.ThreadData;
import jinjing.rule.NumericFeatureRule;

/**
 * Read numeric feature values of a thread set from file
 * The format of file should be like:
 * threadid1	featurevalue
 * threadid2	featurevalue
 *
 * @author Jinjing Ma (jinjingm@cs.unc.edu)
 * @version $1$
 */
public class ReadFromFileNumericRule extends NumericFeatureRule implements IBasicFeatureRule{
	
	/** save <threadid, nominal value> */
	HashMap<Integer, Double> attrVals;
	
	/**
	 * Create a ReadFromFileNumericRule
	 * Read and keep numeric feature values of a thread set from file
	 * 
	 * @param destFeatureName name for extracted feature
	 * @param fileName path of file to read
	 * @throws IOException if opening or reading operations have exception
	 */
	public ReadFromFileNumericRule(String destFeatureName, String fileName) throws IOException {
		super(destFeatureName);
		attrVals = new HashMap<Integer, Double>();
		read(fileName);
	}
	
	/**
	 * Read and keep numeric feature values of a thread set from file
	 * 
	 * @param fileName path of file to read
	 * @throws IOException if opening or reading operations have exception
	 */
	private void read(String fileName) throws IOException{
		
		File file = new File(fileName);
		if(file.exists()){
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while((line=reader.readLine())!=null){
				Scanner scanner = new Scanner(line);
				int id = scanner.nextInt();
				double val  = scanner.nextDouble();
				scanner.close();
				attrVals.put(id, val);
			}
			reader.close();
		}

	}

	/**
	 * Get the numeric feature value of given thread
	 * 
	 * @param aThread the source thread data
	 * @return the numeric feature value of given thread
	 * @throws Exception when extracted value is invalid
	 */
	@Override
	public Object extract(ThreadData aThread) throws Exception {
		return attrVals.get(aThread.getThreadId());
	}

}
