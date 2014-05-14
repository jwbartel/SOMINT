package reader.threadfinder.ML.decisiontrees;

public class DecisionRule {
	int index;
	double separation;
	int direction;
	
	public DecisionRule(int index, double separation, int direction){
		this.index = index;
		this.separation = separation;
		this.direction = direction;
	}
	
	public boolean fits(double[] vector){
		double val = vector[index];
		if(direction < 0){
			return val < separation;
		}else{
			return val >= separation;
		}
	}

	public String toString(){
		return "<"+index+","+separation+","+direction+">";
	}
}
