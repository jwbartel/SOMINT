package reader.threadfinder.newsgroups.tools;

import java.io.File;
import java.util.ArrayList;

public class PostListHandler implements NewsgroupPostHandler{

	ArrayList<File> fileList = new ArrayList<File>();
	
	public PostListHandler(ArrayList<File> fileList){
		this.fileList = fileList;
	}
	
	@Override
	public void handle(File postLoc) {
		fileList.add(postLoc);
	}

}
