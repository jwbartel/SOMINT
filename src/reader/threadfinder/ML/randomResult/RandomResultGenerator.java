package reader.threadfinder.ML.randomResult;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class RandomResultGenerator {

	
	public static void generate5050Result(File src, File dest) throws IOException{
		BufferedReader in = new BufferedReader(new FileReader(src));
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		
		Random random = new Random();
		
		String line = in.readLine();
		while(line != null){
			
			int value = random.nextInt(2);
			value = value*2-1;
			out.write(""+value);
			out.newLine();
			line = in.readLine();
		}
		
		out.close();
	}
	
	
	public static void generateAll5050RunResults(File labelsAndScoresFolder) throws IOException{
		File[] files = labelsAndScoresFolder.listFiles();
		for(File file: files){
			String fileName = file.getName();
			if(fileName.startsWith("labels_run_")){
				File newFile =  new File(labelsAndScoresFolder, "random_"+fileName);
				generate5050Result(file, newFile);
			}
		}
	}
	
	public static double compute5050Accuracies(File labelsFile, File randomLabelsFile) throws IOException{
		int totalLabels = 0;
		int totalCorrectRandoms = 0;
		
		BufferedReader labelsIn = new BufferedReader(new FileReader(labelsFile));
		BufferedReader randomsIn = new BufferedReader(new FileReader(randomLabelsFile));
		
		String labelStr = labelsIn.readLine();
		String randomsStr = randomsIn.readLine();
		while(labelStr != null){
			totalLabels++;
			int label = Integer.parseInt(labelStr);
			int random = Integer.parseInt(randomsStr);
			
			if(label == random){
				totalCorrectRandoms++;
			}
			
			labelStr = labelsIn.readLine();
			randomsStr = randomsIn.readLine();
		}
		
		return ((double) totalCorrectRandoms)/((double) totalLabels);
	}
	
	public static void computeAll5050Accuracies(File labelsAndScoresFolder, File outFile) throws IOException{
		File[] files = labelsAndScoresFolder.listFiles();
		BufferedWriter out = new BufferedWriter(new FileWriter(outFile));
		
		for(File file: files){
			String fileName = file.getName();
			if(fileName.startsWith("labels_run_")){
				File  randomsFile = new File(labelsAndScoresFolder, "random_"+fileName);
				if(!randomsFile.exists()){
					System.out.println("creating new file");
					generate5050Result(file, randomsFile);
				}

				double accuracy = compute5050Accuracies(file, randomsFile);
				out.write(""+accuracy);
				out.newLine();
			}
		}
		
		out.flush();
		out.close();
	}
	
	public static void main(String[] args) throws IOException{
		
		File rootFolder = new File("C:\\Users\\bartel\\Workspaces\\Machine Learning\\data\\results\\100 buffered vectors");
		File labelsAndScoresFolder = new File(rootFolder, "labels and scores");
		File randomAccuraciesFile = new File(rootFolder, "random accuracies.csv");
		//generateAll5050RunResults(labelsAndScoresFolder);
		computeAll5050Accuracies(labelsAndScoresFolder, randomAccuraciesFile);
	}
	
	
	
}

