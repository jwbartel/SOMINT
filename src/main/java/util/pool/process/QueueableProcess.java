package util.pool.process;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import tracer.Tracer;

public class QueueableProcess {
	
	Socket socket;
	
	public QueueableProcess() throws UnknownHostException, IOException{
		
		init(ProcessPool.POOL_ADDRESS, ProcessPool.POOL_PORT);
		
		
	}
	
	public QueueableProcess(String address, int port) throws UnknownHostException, IOException{
		init(address, port);
	}
	
	protected void init(String address, int port) throws UnknownHostException, IOException{
		
		socket = new Socket(address, port);
		Tracer.info(this, "Socket started for "+address+":"+port);
		
	}
	
	public void runActions(){

		Tracer.info(this, "Starting SocketActionReceiver");
		Runnable receiver = new SocketActionReceiver(socket);
		receiver.run();
	}
}
