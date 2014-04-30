package reader.threadfinder.ML.decisiontrees;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DecisionTree {
	int index;
	double separation;
	int leftLabel = 0;
	int rightLabel = 0;
	
	DecisionTree parent;
	
	DecisionTree left;
	DecisionTree right;
	
	Double leftEntropy;
	Double rightEntropy;
	
	Double leftGini;
	Double rightGini;
	
	public DecisionTree(){
		
	}
	
	public DecisionTree(DecisionTree parent, int index, double separation, int leftLabel, int rightLabel){
		this.parent = parent;
		this.index = index;
		this.separation = separation;
	}
	
	public int getLabel(double[] vector){
		if(vector[index] < separation){
			if(left == null){
				return leftLabel;
			}else{
				return left.getLabel(vector);
			}
		}else{
			if(right == null){
				return rightLabel;
			}else{
				return right.getLabel(vector);
			}
		}
	}

	public void setLeftLabel(int leftLabel) {
		this.leftLabel = leftLabel;
	}

	public void setRightLabel(int rightLabel) {
		this.rightLabel = rightLabel;
	}

	public void setLeft(DecisionTree left) {
		this.left = left;
		left.parent= this;
	}

	public void setRight(DecisionTree right) {
		this.right = right;
		right.parent = this;
	}

	public String toString(){
		String str = "<"+index+","+separation+","+leftLabel+","+rightLabel+">";
		str += " ("+((left != null)?left.toString():"")+")";
		str += " ("+((right != null)?right.toString():"")+")";
		return str;
	}
	
	public String importChild(String spec, DecisionTree parent, boolean isLeft){
		if(spec.charAt(0) == '<'){
			int endVals = spec.indexOf('>');
			String[] vals = spec.substring(1,endVals).split(",");
			
			int index = Integer.parseInt(vals[0]);
			double separation = Double.parseDouble(vals[1]);
			int leftLabel = Integer.parseInt(vals[2]);
			int rightLabel = Integer.parseInt(vals[3]);

			DecisionTree child = new DecisionTree(parent, index, separation, leftLabel, rightLabel);
			if(isLeft){
				parent.setLeft(child);
			}else{
				parent.setRight(child);
			}
			
			spec = spec.substring(endVals+1).trim().substring(1);
			
			spec = child.importChild(spec, child, true);
			spec = spec.substring(1).trim().substring(1);
			spec = child.importChild(spec, child, false);
			spec = spec.substring(1).trim();
		}
		
		return spec;
	}
	
	public ArrayList<DecisionRule> getRulesTo(boolean endLeft){
		ArrayList<DecisionRule> rules = new ArrayList<DecisionRule>();
		getRulesTo(rules);
		if(endLeft){
			rules.add(new DecisionRule(index, separation, -1));
		}else{
			rules.add(new DecisionRule(index, separation, +1));
		}
		return rules;
	}
	
	private void getRulesTo(ArrayList<DecisionRule> rules){
		if(parent != null){
			parent.getRulesTo(rules);
			
			if(parent.left == this){
				rules.add(new DecisionRule(parent.index, parent.separation, -1));
			}else{
				rules.add(new DecisionRule(parent.index, parent.separation, +1));
			}
		}
	}
	
	public static DecisionTree importTree(String spec){
		DecisionTree dummy = new DecisionTree();
		String artifact = dummy.importChild(spec, dummy, true);
		return dummy.left;
	}
	
	public double computeEntropyChange(DecisionTree toAdd, boolean isLeft){
		double currEntropy = (isLeft)? leftEntropy : rightEntropy;
		return currEntropy - 0.5*(toAdd.leftEntropy + toAdd.rightEntropy);
	}
	
	public void computeEntropyFromFiles(ArrayList<File> vectorFiles, ArrayList<File> labelFiles) throws IOException{
		if(leftEntropy == null){
			ArrayList<DecisionRule> leftRules = getRulesTo(true);
			leftEntropy = getEntropy(getCountsFromFiles(vectorFiles, labelFiles, leftRules));
		}
		if(rightEntropy == null){
			ArrayList<DecisionRule> rightRules = getRulesTo(false);
			rightEntropy = getEntropy(getCountsFromFiles(vectorFiles, labelFiles, rightRules));
		}
		
	}
	
	public void computeEntropy(ArrayList<double[]> vectors, ArrayList<Integer> labels){
		if(leftEntropy == null){
			leftEntropy = getEntropy(getLeftCounts(vectors, labels));
		}
		if(rightEntropy == null){
			rightEntropy = getEntropy(getRightCounts(vectors, labels));
		}
	}
	
	public void computeGiniFromFiles(ArrayList<File> vectorFiles, ArrayList<File> labelFiles) throws IOException{
		if(leftGini == null){
			ArrayList<DecisionRule> leftRules = getRulesTo(true);
			leftGini = getGini(getCountsFromFiles(vectorFiles, labelFiles, leftRules));
		}
		if(rightGini == null){
			ArrayList<DecisionRule> rightRules = getRulesTo(false);
			rightGini = getGini(getCountsFromFiles(vectorFiles, labelFiles, rightRules));
		}
		
	}

	
	public void computeGini(ArrayList<double[]> vectors, ArrayList<Integer> labels){
		if(leftGini == null){
			leftGini = getGini(getLeftCounts(vectors, labels));
		}
		if(rightGini == null){
			rightGini = getGini(getRightCounts(vectors, labels));
		}
	}
	
	public int[] getCountsFromFiles(ArrayList<File> vectorFiles, ArrayList<File> labelFiles, ArrayList<DecisionRule> rules) throws IOException{
		
		int total = 0;
		int totalPos = 0;
		int totalNeg = 0;
		
		for(int i=0; i<vectorFiles.size(); i++){
			BufferedReader vectorIn = new BufferedReader(new FileReader(vectorFiles.get(i)));
			BufferedReader labelIn = new BufferedReader(new FileReader(labelFiles.get(i)));
			
			String vectorLine = vectorIn.readLine();
			String labelLine = labelIn.readLine();
			while(vectorLine != null && vectorLine != null){
				String[] vectorStr = vectorLine.split(",");
				double[] vector = new double[vectorStr.length];
				for(int j=0; j<vectorStr.length; j++){
					vector[j] = Double.parseDouble(vectorStr[j]);
				}
				int label = Integer.parseInt(labelLine);
				
				boolean fits = true;
				for(DecisionRule rule: rules){
					if(!rule.fits(vector)){
						fits = false;
						break;
					}
				}
				
				if(fits){
					total++;
					if(label < 0){
						totalNeg++;
					}else{
						totalPos++;
					}
				}
				
				
				vectorLine = vectorIn.readLine();
				labelLine = labelIn.readLine();
			}
		}
		
		int[] counts = {total, totalPos, totalNeg};
		return counts;
	}
	
	public int[] getLeftCounts(ArrayList<double[]>vectors, ArrayList<Integer> labels){
		
		int total = 0;
		int totalPos = 0;
		int totalNeg = 0;
		
		for(int i=0; i<vectors.size(); i++){
			double[] vector = vectors.get(i);
			if(vector[index] < separation){
				total++;
				if(labels.get(i) == 1){
					totalPos++;
				}else{
					totalNeg--;
				}
			}
		}
		
		int[] counts = {total, totalPos, totalNeg};
		return counts;
	}
	
	public int[] getRightCounts(ArrayList<double[]>vectors, ArrayList<Integer> labels){
		
		int total = 0;
		int totalPos = 0;
		int totalNeg = 0;
		
		for(int i=0; i<vectors.size(); i++){
			double[] vector = vectors.get(i);
			if(vector[index] >= separation){
				total++;
				if(labels.get(i) == 1){
					totalPos++;
				}else{
					totalNeg--;
				}
			}
		}
		
		int[] counts = {total, totalPos, totalNeg};
		return counts;
	}
	
	public int getTotalRegions(){
		int total = 0;
		if(left == null && right == null){
			return 2;
		}
		if(left == null){
			total +=1;
		}else{
			total += left.getTotalRegions();
		}
		
		if(right == null){
			total+=1;
		}else{
			total += right.getTotalRegions();
		}
		
		return total;
	}
	
	public static double getEntropy(int[] counts){
		return entropyOfLabel(counts[0], counts[1]) + entropyOfLabel(counts[0], counts[2]);
	}
	
	public static double entropyOfLabel(int total, int totalWithLabel){
		double percent = ((double) total)/((double) totalWithLabel);
		return percent*Math.log(percent);
	}
	
	public static double getGini(int[] counts){
		return giniOfLabel(counts[0], counts[1]) + entropyOfLabel(counts[0], counts[2]);
	}
	
	public static double giniOfLabel(int total, int totalWithLabel){
		double percent = ((double) total)/((double) totalWithLabel);
		return 1-(percent*percent);
	}
	
	public static void main(String[] args){
		DecisionTree tree = new DecisionTree(null, 0, 0.5, 1, -1);
		DecisionTree left = new DecisionTree(null, 1, 0.25, 1, -1);
		DecisionTree right = new DecisionTree(null, 0, 0.75, -1, 1);
		tree.setLeft(left);
		tree.setRight(right);
		
		System.out.println(right.getRulesTo(false));
		System.out.println(right.getRulesTo(true));
		
		System.out.println(left.getRulesTo(false));
		System.out.println(left.getRulesTo(true));
		

		System.out.println(tree.getRulesTo(false));
		System.out.println(tree.getRulesTo(true));
		
		/*
		double[] vector = {0.9, 0.5, 0.6};
		
		System.out.println(tree);
		System.out.println(tree.getTotalRegions());
		String export = tree.toString();
		//System.out.println(importTree(export));*/
	}
}
