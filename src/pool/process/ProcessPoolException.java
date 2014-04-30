package pool.process;

import pool.thread.ThreadPoolException;

public class ProcessPoolException extends ThreadPoolException {
	
	public ProcessPoolException(){
		super();
	}
	
	public ProcessPoolException(String message){
		super(message);
	}
	
	public ProcessPoolException(Throwable throwable){
		super(throwable);
	}
	
	public ProcessPoolException(String message, Throwable throwable){
		super(message, throwable);
	}

}
