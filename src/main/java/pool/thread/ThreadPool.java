package pool.thread;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import pool.process.ActionHandlerCombo;
import pool.process.QueueableAction;
import pool.process.ReturnValueHandler;
import tracer.Tracer;

public class ThreadPool extends Thread{
	static final int DEFAULT_INITIAL_SIZE = 5;
	
	ArrayList<QueueableThread> availableThreads;
	BlockingQueue<ActionHandlerCombo> actionQueue = new LinkedBlockingQueue<ActionHandlerCombo>();
	
	public ThreadPool(int size){
		init(size);
	}
	
	public ThreadPool(){
		init(DEFAULT_INITIAL_SIZE);
	}
	
	protected synchronized void init(int size){
		Tracer.info(this, "ThreadPool initializing with size "+size);
		availableThreads = new ArrayList<QueueableThread>(size);
		for(int i=0; i<size; i++){
			availableThreads.add(new QueueableThread(this));
			Tracer.info(this, "Added thread to threadpool");
		}
		start();
		Tracer.info(this, "ThreadPool initialization complete");
	}
	
	@Override
	public void run(){
		
		while(true){

			//Tracer.info(this, "Fetching next action from queue");
			ActionHandlerCombo combo = null;
			while(true){
				try {
					combo = actionQueue.take();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
					continue;
				}
				break;
			}
			QueueableAction action = combo.getAction();
			ReturnValueHandler handler = combo.getHandler();
			//Tracer.info(this, "Action retrieved");
			
			synchronized(this){
				while(availableThreads.size() == 0){
					try {
						Tracer.info(this, "No threads available.  Waiting for a thread to become available");
						wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				//Tracer.info(this, "Allocating next thread in thread queue");
				QueueableThread thread = availableThreads.get(0);
				thread.setAction(action, handler);
				//Tracer.info(this, "Starting action thread");
				thread.startAction();
			}
			
		}
	}
	
	public synchronized void makeAvailable(QueueableThread thread){
		if(!availableThreads.contains(thread)){
			availableThreads.add(thread);
		}
		thread.setIsAvailable(true);
		Tracer.info(this, "Thread made available: "+thread);
		notify();
	}
	
	public synchronized void makeUnavailable(QueueableThread thread) throws ThreadPoolException{
		if(!thread.getIsAvailable()){
			//throw new ThreadPoolException("Thread is already unavailable");
		}
		while(availableThreads.remove(thread)){}
		thread.setIsAvailable(false);
		//Tracer.info(this, "Thread made unavailable: "+thread);
	}
	
	public void queueAction(QueueableAction action, ReturnValueHandler handler){
		while(true){
			try {
				ActionHandlerCombo combo = new ActionHandlerCombo(action, handler);
				actionQueue.put(combo);
			} catch (InterruptedException e) {
				continue;
			}
			return;
		}
	}
}
