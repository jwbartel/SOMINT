package pool.process.test;

import java.io.IOException;
import java.net.UnknownHostException;

import pool.process.ProcessPool;
import pool.process.QueueableProcess;
import tracer.ImplicitKeywordKind;
import tracer.Tracer;

public class ProcessStarter {
	
	public static void main(String[] args) throws UnknownHostException, IOException {
		setTracing();
		
		QueueableProcess process = new QueueableProcess();
		process.runActions();
	}
	
	public static void setTracing(){
		Tracer.showInfo(false);
		Tracer.setKeyWordStatus(Tracer.ALL_KEYWORDS, false);
		Tracer.setImplicitKeywordKind(ImplicitKeywordKind.OBJECT_PACKAGE_NAME);
		Tracer.setKeyWordStatus(ProcessPool.class, true);
	}

}
