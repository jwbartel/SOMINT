package reader.threadfinder.stackoverflow.tools;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import reader.threadfinder.stackoverflow.tools.eventflow.RowParser;

public class StackExchangeDumpReader {

	public static <V> void readRows(File file, RowParser<V> rowParser) throws IOException,
			DocumentException {
		BufferedReader in = new BufferedReader(new FileReader(file));

		String line = in.readLine();
		int count = 0;
		while (line != null) {

			if (line.startsWith("  <row")) {
				try {
					rowParser.parseRow(loadRow(line));
				} catch (RuntimeException e) {
					System.out.println("Failed for post " + count);
					System.out.println(line.trim());
					e.printStackTrace();
				}
				count++;
				if ((count % 10000) == 0) {
					System.out.println("Completed post " + count);
				}
			}
			line = in.readLine();
		}
		in.close();
	}

	private static Element loadRow(String rowStr) throws DocumentException,
			UnsupportedEncodingException {
		InputStream stream = new ByteArrayInputStream(rowStr.getBytes("UTF-8"));
		SAXReader reader = new SAXReader();
		Document doc = reader.read(stream);
		return doc.getRootElement();
	}
}
