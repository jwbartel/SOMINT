package util.tools.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;

public class CollectionIOAssist {

	public static <V> void writeCollection(String dest, Collection<V> collection)
			throws IOException {
		writeCollection(new File(dest), collection);
	}

	public static <V> void writeCollection(File dest, Collection<V> collection,
			ValueWriter<V> writer) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));

		for (V item : collection) {
			out.write(writer.writeVal(item));
			out.newLine();
		}

		out.flush();
		out.close();
	}

	public static <V> void writeCollection(File dest, Collection<V> collection) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));

		for (V item : collection) {
			out.write(item.toString());
			out.newLine();
		}

		out.flush();
		out.close();
	}

	public static Collection<String> readCollection(File src) throws IOException {
		return readCollection(src, new StringValueWriterAndParser());
	}

	public static <V> Collection<V> readCollection(File src, ValueParser<V> valueParser)
			throws IOException {

		BufferedReader in = new BufferedReader(new FileReader(src));

		Collection<V> retVal = new ArrayList<V>();

		String line = in.readLine();
		while (line != null) {

			retVal.add(valueParser.parse(line));
			line = in.readLine();
		}

		in.close();
		return retVal;

	}

	public static <V extends Comparable<V>> TreeSet<V> readTreeSet(File src,
			ValueParser<V> valueParser) throws IOException {

		BufferedReader in = new BufferedReader(new FileReader(src));

		TreeSet<V> retVal = new TreeSet<V>();

		String line = in.readLine();
		while (line != null) {

			retVal.add(valueParser.parse(line));
			line = in.readLine();
		}

		in.close();
		return retVal;

	}
}
