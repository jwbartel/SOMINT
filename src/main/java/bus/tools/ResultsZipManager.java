package bus.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ResultsZipManager {
	private static byte[] buf = new byte[1024];
	

	static final String ENRON_STUDIES_FOLDER = "data/Jacob/Enron/";
	
	static final String FACEBOOK_STUDIES_FOLDER = "data/Jacob/Facebook/";
	static final String FACEBOOK_2010_STUDY_FOLDER = FACEBOOK_STUDIES_FOLDER + "2010 Study/";
	
	public static ZipFile getEnronPrecomputesZipFile() throws ZipException, IOException {
		return new ZipFile(getEnronPrecomputesFile());
	}
	
	public static File getEnronPrecomputesFile() {
		return getInternetMessagesPrecomputesFile(ENRON_STUDIES_FOLDER);
	}
	
	public static File getInternetMessagesPrecomputesFile(String folder) {
		return new File(folder, "precomputes.zip");
	}
	
	public static String getTestSetFolderName(String studyFolder, String testName) {
		return (new File(studyFolder, testName)).getPath();
	}

	public static ZipFile getAccountsDataZipFile(String folder) throws ZipException, IOException {
		return new ZipFile(getAccountsDataFile(folder)); 
	}

	public static File getAccountsDataFile(String folder) {
		return new File(folder, "accounts data.zip"); 
	}
	
	public static void writeTestResults(String folder, String testLable, ArrayList<File> results) throws IOException {
		String id = ""+System.currentTimeMillis();
		writeTestResults(folder, testLable, id, results);
	}
	
	public static void writeTestResults(String folder, String testLabel, String testID, ArrayList<File> results) throws IOException {
		File zipFile = new File(folder, testLabel+"_"+testID+".zip");
		writeToZipFile(zipFile, results);
	}
	
	public static BufferedReader getReader(ZipFile zipFile, String compressedFileName) throws IOException{
			ZipEntry entry = zipFile.getEntry(compressedFileName);
			if(entry == null) return null;
			return new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry)));
	}
	
	public static void writeToZipFile(File zipFile, ArrayList<File> srcs) throws IOException {
		
		if (!zipFile.getParentFile().exists()) {
			zipFile.getParentFile().mkdirs();
		}

		if (srcs == null || srcs.size() == 0) {
			return;
		}
		
		System.out.println("writing data to zip file...");
		
		ZipOutputStream zipOut; 
		if (zipFile.exists()) {
			File tempFile = File.createTempFile(zipFile.getName(), null);
			tempFile.delete();
			boolean renameOk=zipFile.renameTo(tempFile);
		    if (!renameOk) {
		        throw new RuntimeException("could not rename the file "
		        				+ zipFile.getAbsolutePath()
		        				+ " to "
		        				+ tempFile.getAbsolutePath()
		        );
		    }
		    
		    zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
		    ZipInputStream zipIn = new ZipInputStream(new FileInputStream(tempFile));
			
			ZipEntry entry = zipIn.getNextEntry();
			while (entry != null) {
				zipOut.putNextEntry(entry);
				int len;
				while ((len = zipIn.read(buf)) > 0) {
					zipOut.write(buf, 0, len);
				}
				zipOut.closeEntry();
				entry = zipIn.getNextEntry();
			}
			zipIn.close();
		} else {
			zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
		}		
		
		for (File src: srcs) {
			if (src.isDirectory()) {
				File[] contents = src.listFiles();
				for (File content: contents) {
					writeFile(null, content, zipOut);
				}
			} else {
				writeFile(null, src, zipOut);
			}
		}
		zipOut.close();
		System.out.println("done.");
	}
	
	protected static void writeFile(String prefix, File src, ZipOutputStream zipOut) throws IOException {
		if (src.isDirectory()) {
			prefix = (prefix==null || prefix.length() == 0)? "": (prefix.endsWith("/"))? prefix: prefix + "/";
			prefix += src.getName();
			
			File[] children = src.listFiles();
			for (File child: children) {
				writeFile(prefix, child, zipOut);
			}
		} else if (prefix == null && src.getName().toLowerCase().endsWith(".zip")) {
			ZipInputStream zipIn = new ZipInputStream(new FileInputStream(src));
			
			ZipEntry entry = zipIn.getNextEntry();
			while (entry != null) {
				zipOut.putNextEntry(entry);
				int len;
				while ((len = zipIn.read(buf)) > 0) {
					zipOut.write(buf, 0, len);
				}
				zipOut.closeEntry();
				entry = zipIn.getNextEntry();
			}
			zipIn.close();
		} else {
			InputStream in = new FileInputStream(src);
	        // Add ZIP entry to output stream.
			String name;
			
			if (prefix == null || prefix.length() == 0) {
				name = src.getName();
			} else {
				name = (prefix.endsWith("/"))? prefix : prefix + "/";
				name += src.getName();
			}
			try {
				zipOut.putNextEntry(new ZipEntry(name));


				// Transfer bytes from the file to the ZIP file
				int len;
				while ((len = in.read(buf)) > 0) {
					zipOut.write(buf, 0, len);
				}
				// Complete the entry
				zipOut.closeEntry();
				in.close();
			} catch (ZipException e) {
				System.err.println(e.getMessage());
				in.close();
			}
		}
		src.delete();
	}
	
	public static void main(String[] args) throws IOException {
		/*File zipFile = getAccountsDataFile(FACEBOOK_2010_STUDY_FOLDER);
		
		ArrayList<File> srcs = new ArrayList<File>();
		srcs.add(new File("data/Kelli/FriendshipData/2010Study"));
		srcs.add(new File("data/Kelli/Cliques/MaximalCliques"));
		srcs.add(new File("data/Jacob/Ideal"));
		
		writeToZipFile(zipFile, srcs);*/
		
		/*ZipFile zipFile = getAccountsDataZipFile(FACEBOOK_2010_STUDY_FOLDER);
		BufferedReader in = getReader(zipFile, "10_People.txt");
		String line = in.readLine();
		while(line != null){
			System.out.println(line);
			line = in.readLine();
		}*/
		
		ArrayList<File> srcs = new ArrayList<File>();
		srcs.add(new File("data/Jacob/Stats/relativeScaledMaintenance/adjustedMatching"));

		String folder = getTestSetFolderName(FACEBOOK_2010_STUDY_FOLDER, "friend list evolution");
		writeTestResults(folder, "expected_growth", srcs);
	}
}
