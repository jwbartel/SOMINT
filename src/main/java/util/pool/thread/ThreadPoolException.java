package util.pool.thread;

public class ThreadPoolException extends RuntimeException {
	
	public ThreadPoolException(){
		super();
	}
	
	public ThreadPoolException(String message){
		super(message);
	}
	
	public ThreadPoolException(Throwable throwable){
		super(throwable);
	}
	
	public ThreadPoolException(String message, Throwable throwable){
		super(message, throwable);
	}
}
