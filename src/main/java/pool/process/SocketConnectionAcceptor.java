package pool.process;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import tracer.Tracer;

public class SocketConnectionAcceptor implements Runnable{

	ProcessPool parent;
	int port;
	
	public SocketConnectionAcceptor(ProcessPool parent, int port){
		this.parent = parent;
		this.port = port;
	}
	
	
	public void run(){
		try {
			Tracer.info(this, "Creating ServerSocket on port "+port);
			ServerSocket serverSocket = new ServerSocket(port);
			
			while(true){

				Tracer.info(this, "Waiting to accept connection");
				Socket socket = serverSocket.accept();
				
				Thread asyncActionSender = new Thread(new SocketActionSender(parent, socket));
				asyncActionSender.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
