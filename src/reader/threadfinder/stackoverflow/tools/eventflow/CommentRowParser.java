package reader.threadfinder.stackoverflow.tools.eventflow;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import reader.threadfinder.stackoverflow.tools.StackExchangeDumpReader;

public class CommentRowParser extends RowParser<Element> {
	File commentsFile;

	BufferedWriter commentsOut;

	public CommentRowParser(File commentsFile) throws IOException {
		this.commentsFile = commentsFile;
		commentsOut = new BufferedWriter(new FileWriter(commentsFile));
	}

	@Override
	public void parseRow(Element row) {
		try {

			CommentItem comment = new CommentItem(row);
			commentsOut.write(comment.toString());
			commentsOut.newLine();
			commentsOut.flush();
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void close() throws IOException {
		commentsOut.flush();
		commentsOut.close();
	}

	public static void main(String[] args) throws IOException, DocumentException {
		File commentsFile = new File(
				"C:\\Users\\bartel\\Workspaces\\StackExchange Data\\Stack Exchange Data Dump - Aug 2012\\Content\\comments.xml");
		File commentsOutFile = new File("D:\\Stack Overflow data\\all questions vals\\comments.txt");

		CommentRowParser parser = new CommentRowParser(commentsOutFile);
		StackExchangeDumpReader.readRows(commentsFile, parser);

//		BufferedReader in = new BufferedReader(new FileReader(commentsFile));
//		String line = in.readLine();
//		int count = 0;
//		while (count < 50) {
//			System.out.println(line);
//			line = in.readLine();
//			count++;
//		}
	}
}
