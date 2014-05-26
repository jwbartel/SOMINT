package util.pool.thread;

import util.pool.process.QueueableAction;
import util.pool.process.ReturnValue;
import util.pool.process.ReturnValueHandler;


public class QueueableThread extends Thread {
	ThreadPool parent;
	QueueableAction queuedAction;
	ReturnValueHandler queuedHandler;
	boolean isAvailable = true;
	
	public QueueableThread(ThreadPool parent){
		this.parent = parent;
	}
	
	public QueueableThread(Runnable action){
		super(action);
	}
	
	@Override
	public synchronized void run(){
		while(true){
			
			if(queuedAction != null){
				//Tracer.info(this, "Action is non-null.  Running the action.");
				ReturnValue value = queuedAction.run();
				if(queuedHandler != null){
					queuedHandler.handleReturnValue(value);
				}
			}

			queuedAction = null;
			//Tracer.info(this, "Action completed. Making self available.");
			parent.makeAvailable(this);
			
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setAction(QueueableAction action, ReturnValueHandler handler){
		queuedAction = action;
		queuedHandler = handler;
	}
	
	public boolean getIsAvailable(){
		return isAvailable;
	}
	
	public void setIsAvailable(boolean available){
		this.isAvailable = available;
	}
	
	public synchronized void startAction(){
		try{
			//Tracer.info(this, "Making self unavailable");
			parent.makeUnavailable(this);
		}catch(ThreadPoolException e){
			e.printStackTrace();
			return;
		}
		if(isAlive()){
			notify();
		}else{
			super.start();
		}
		
	}

}
