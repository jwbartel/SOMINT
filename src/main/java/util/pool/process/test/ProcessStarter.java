package util.pool.process.test;

import java.io.IOException;
import java.net.UnknownHostException;

import tracer.ImplicitKeywordKind;
import tracer.Tracer;
import util.pool.process.ProcessPool;
import util.pool.process.QueueableProcess;

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
