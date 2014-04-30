package bus.thunderbird;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileIO {
	
	static Map<File, FileIOMapping> fileIOObjects = new HashMap<File, FileIOMapping>();
	
	File currFile;
	
	private static class FileIOMapping{
		private boolean appends;
		Object writerOrReader;
		
		public FileIOMapping(Object writerOrReader){
			this.writerOrReader = writerOrReader;
		}
		
		public FileIOMapping(Object writerOrReader, boolean appends){
			this.writerOrReader = writerOrReader;
			this.appends = appends;
		}
		
		public boolean appends(){
			return appends;
		}
		
		public void setAppends(boolean appends){
			this.appends = appends;
		}
		
		public boolean isWriter(){
			return writerOrReader instanceof BufferedWriter;
		}
		
		public boolean isReader(){
			return writerOrReader instanceof BufferedReader;
		}
		
		public BufferedWriter getWriter(){
			if(isWriter()){
				return (BufferedWriter) writerOrReader;
			}else{
				return null;
			}
		}
		
		public BufferedReader getReader(){
			if(isReader()){
				return (BufferedReader) writerOrReader;
			}else{
				return null;
			}
		}
		
		public void setWriter(BufferedWriter writer){
			this.writerOrReader = writer;
		}
		
		public void setReader(BufferedReader reader){
			this.writerOrReader = reader;
		}
		
		public void close() throws IOException{
			if(isWriter()){
				((BufferedWriter) writerOrReader).flush();
				((BufferedWriter) writerOrReader).close();
			}
			if(isReader()){
				((BufferedReader) writerOrReader).close();
			}
		}
	}
	
	
	public FileIO(){
		
	}
	
	public void setFile(String filename){
		init(new File(filename));
	}
	
	public void setFile(File file){
		init(file);
	}
	
	protected void init(File file){
		this.currFile =file;
	}
	
	public void deleteFile(String fileName) throws IOException{
		deleteFile(new File(fileName));
	}
	
	public static void deleteFile(File file) throws IOException{
		FileIOMapping mapping = fileIOObjects.get(file);
		if(mapping != null){
			mapping.close();
			fileIOObjects.remove(file);
		}
		file.delete();
	}
	
	private FileIOMapping getAppender() throws IOException{
		FileIOMapping mapping = fileIOObjects.get(currFile);
		if(mapping == null){
			mapping = new FileIOMapping(new BufferedWriter(new FileWriter(currFile, true)), true);
			fileIOObjects.put(currFile, mapping);
		}else if(!mapping.isWriter() || !mapping.appends()){
			mapping.close();
			mapping.setWriter(new BufferedWriter(new FileWriter(currFile, true)));
			mapping.setAppends(true);
		}
		return mapping;
	}
	
	private FileIOMapping getWriter() throws IOException{
		FileIOMapping mapping = fileIOObjects.get(currFile);
		if(mapping == null){
			mapping = new FileIOMapping(new BufferedWriter(new FileWriter(currFile)), false);
			fileIOObjects.put(currFile, mapping);
		}else if(!mapping.isWriter() || mapping.appends()){
			mapping.close();
			mapping.setWriter(new BufferedWriter(new FileWriter(currFile)));
			mapping.setAppends(false);
		}
		return mapping;
	}
	
	private FileIOMapping getReader() throws IOException{
		FileIOMapping mapping = fileIOObjects.get(currFile);
		if(mapping == null){
			mapping = new FileIOMapping(new BufferedReader(new FileReader(currFile)));
			fileIOObjects.put(currFile, mapping);
		}else if(!mapping.isWriter() || mapping.appends()){
			mapping.close();
			mapping.setReader(new BufferedReader(new FileReader(currFile)));
		}
		return mapping;
	}
	
	
	public void append(String appending) throws IOException{
		FileIOMapping mapping = getAppender();
		mapping.getWriter().write(appending);
	}
	
	public void appendNewLine() throws IOException{
		FileIOMapping mapping = getAppender();
		mapping.getWriter().newLine();
	}
	
	public void write(String writeVal) throws IOException{
		FileIOMapping mapping = getWriter();
		mapping.getWriter().write(writeVal);
	}
	
	public void writeNewLine() throws IOException{
		FileIOMapping mapping = getWriter();
		mapping.getWriter().newLine();
	}
	
	public String readLine() throws IOException{
		FileIOMapping mapping = getReader();
		return mapping.getReader().readLine();
		
	}
	
	public void flush() throws IOException{
		FileIOMapping mapping = getWriter();
		mapping.getWriter().flush();
	}
	
	
	public void close() throws IOException{
		FileIOMapping mapping = fileIOObjects.get(currFile);
		if(mapping != null) mapping.close();
		fileIOObjects.remove(currFile);
	}
	
}
