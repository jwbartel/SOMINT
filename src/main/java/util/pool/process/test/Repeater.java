package util.pool.process.test;

import java.util.Random;

import util.pool.process.ObjectReturnValue;
import util.pool.process.QueueableAction;
import util.pool.process.ReturnValue;

public class Repeater implements QueueableAction {
	static final int printCount = 200000;
	
	String name;
	Random random = new Random();
	
	public Repeater(String name){
		this.name = name;
	}

	@Override
	public ReturnValue run() {
		
		int total = 0;
		
		for(int i=0; i<10000000; i++){
			total = randomIncrement(total);
		}
		
		
		return new ObjectReturnValue(name+ " finished at:" +System.currentTimeMillis());
	}
	
	private int randomIncrement(int val){
		return val + random.nextInt(100);
	}

}
