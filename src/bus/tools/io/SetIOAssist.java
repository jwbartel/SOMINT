package bus.tools.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SetIOAssist {

	public static <V> void writeSet(String dest, Set<V> set) throws IOException {
		writeSet(new File(dest), set);
	}

	public static <V> void writeSet(File dest, Set<V> set) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));

		for (V item : set) {
			out.write(item.toString());
			out.newLine();
		}

		out.flush();
	}

	public static Set<String> readSet(File src) throws IOException {
		return readSet(src, new StringValueWriterAndParser());
	}

	public static <V> Set<V> readSet(File src, ValueParser<V> valueParser) throws IOException {

		BufferedReader in = new BufferedReader(new FileReader(src));

		Set<V> retVal = new HashSet<V>();

		String line = in.readLine();
		while (line != null) {

			retVal.add(valueParser.parse(line));
			line = in.readLine();
		}

		in.close();
		return retVal;

	}
}
