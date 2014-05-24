package data.preprocess.oldprecomputeExtractor.newsgroups;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.mail.MessagingException;

import data.preprocess.old.precomputeExtractor.PrecomputeReader;
import data.preprocess.old.precomputeExtractor.PrecomputeWriter;
import data.preprocess.old.precomputeExtractor.PrecomputesFileReader;
import data.preprocess.old.precomputeExtractor.TopRecipientsPrecomputesReader;
import bus.accounts.FileNameByOS;

public class NewsgroupTraverser {

	File rootFolder;
	File listFile;
	File precomputesRoot;

	public NewsgroupTraverser(File rootFolder, File listFile) {
		this.rootFolder = rootFolder;
		this.listFile = listFile;
		this.precomputesRoot = rootFolder;
	}

	public NewsgroupTraverser(File rootFolder, File listFile, File precomputesRoot) {
		this.rootFolder = rootFolder;
		this.listFile = listFile;
		this.precomputesRoot = precomputesRoot;
	}

	private String fixForOS(String fileSuffix) {
		if (FileNameByOS.getOS() == FileNameByOS.WINDOWS) {
			return fileSuffix.replace('/', '\\');
		} else {
			return fileSuffix.replace('\\', '/');
		}
	}

	public void traverse(PrecomputeWriter precomputer) throws IOException, MessagingException {
		BufferedReader in = new BufferedReader(new FileReader(listFile));

		String line = in.readLine();
		while (line != null) {

			line = fixForOS(line);

			File messageFile = new File(rootFolder, line);
			File precomputesPrefix = new File(precomputesRoot, line);

			if (precomputer != null) {
				File precomputesParent = precomputesPrefix.getParentFile();
				if (!precomputesParent.exists()) {
					precomputesParent.mkdirs();
				}
				precomputer.writePrecomputes(messageFile, precomputesPrefix);
			}

			line = in.readLine();
		}
		in.close();
	}

	public void traverse(PrecomputeReader precomputeReader) throws IOException, MessagingException {
		BufferedReader in = new BufferedReader(new FileReader(listFile));

		String line = in.readLine();
		int count = 0;
		while (line != null) {
			count++;
			System.out.println(count);
			line = fixForOS(line);

			File precomputesPrefix = new File(precomputesRoot, line);

			if (precomputeReader != null) {
				File precomputesParent = precomputesPrefix.getParentFile();
				if (!precomputesParent.exists()) {
					precomputesParent.mkdirs();
				}
				precomputeReader.readPrecomputes(precomputesPrefix);
			}

			line = in.readLine();
		}
		in.close();
	}

	public static void main(String[] args) throws IOException, MessagingException {
		// File rootFolder = new
		// File("/home/bartizzi/Research/Newsgroups files/posts");
		// File precomputesFolder = new
		// File("/home/bartizzi/Research/Newsgroups files/precomputes");
		// File listFile = new
		// File("/home/bartizzi/Research/Newsgroups files/precomputes/Cleaned and Ordered File List.txt");

		File rootFolder = new File("D:\\Newsgroup data\\posts");
		File precomputesFolder = new File("D:\\Newsgroup data\\precomputes");
		File listFile = new File(
				"D:\\Newsgroup data\\precomputes\\Cleaned and Ordered File List.txt");

		NewsgroupTraverser traverser = new NewsgroupTraverser(rootFolder, listFile,
				precomputesFolder);
		// PrecomputesFileReader reader = new PrecomputesFileReader(new
		// File("specs/stopwords.txt"));
		PrecomputesFileReader reader = new TopRecipientsPrecomputesReader(new File(
				"D:\\Newsgroup data\\precomputes\\top_recipients.txt"), new File(
				"specs/stopwords.txt"));
		// PrecomputesFileReader reader = new TopWordsPrecomputesReader(new
		// File(
		// "D:\\Newsgroup data\\precomputes\\top_words.txt"), new
		// File("specs/stopwords.txt"));
		// traverser.traverse(reader);
		// PrecomputesFileReader reader = new TopWordsPrecomputesReader(new
		// File(
		// "D:\\Newsgroup data\\precomputes\\top_subject_words.txt"), new File(
		// "specs/stopwords.txt"));
		traverser.traverse(reader);
		reader.writeSummaries(precomputesFolder);

	}
}
