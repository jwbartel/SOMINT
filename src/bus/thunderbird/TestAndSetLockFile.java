package bus.thunderbird;

import java.io.File;
import java.io.IOException;

public class TestAndSetLockFile {
	
	public synchronized boolean testAndSet(String lockFileLoc){
		File lockFile = new File(lockFileLoc);
		if(lockFile.exists()) return false;
		
		while(true){
			try {
				lockFile.createNewFile();
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public synchronized void release(String lockFileLoc){
		File lockFile = new File(lockFileLoc);
		lockFile.delete();
	}
	
}
