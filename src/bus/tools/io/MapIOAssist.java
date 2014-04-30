package bus.tools.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class MapIOAssist<K, V> {

	public interface MapLineReader<K, V> {
		void readLine(K key, V value);
	}

	public static <K, V> void writeMap(String dest, Map<K, V> map) throws IOException {
		writeMap(new File(dest), map);
	}

	public static <K, V> void writeMap(File dest, boolean append, Map<K, V> map) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(dest, append));

		for (Entry<K, V> entry : map.entrySet()) {
			out.write(entry.getKey().toString() + "," + entry.getValue().toString());
			out.newLine();
		}

		out.flush();
		out.close();
	}

	public static <K, V> void writeMap(File dest, Map<K, V> map) throws IOException {
		writeMap(dest, false, map);
	}

	public static <K, V> void writeMap(File dest, Map<K, V> map, ValueWriter<K> keyWriter,
			ValueWriter<V> valueWriter) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));

		for (Entry<K, V> entry : map.entrySet()) {
			out.write(keyWriter.writeVal(entry.getKey()) + ","
					+ valueWriter.writeVal(entry.getValue()));
			out.newLine();
		}

		out.flush();
		out.close();
	}

	public static <K, V> void writeMap(File dest, boolean append, Map<K, V> map,
			ValueWriter<V> valueWriter) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(dest, append));

		for (Entry<K, V> entry : map.entrySet()) {
			out.write(entry.getKey().toString() + "," + valueWriter.writeVal(entry.getValue()));
			out.newLine();
		}

		out.flush();
		out.close();
	}

	public static <K, V> void writeMap(File dest, Map<K, V> map, ValueWriter<V> valueWriter)
			throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));

		for (Entry<K, V> entry : map.entrySet()) {
			out.write(entry.getKey().toString() + "," + valueWriter.writeVal(entry.getValue()));
			out.newLine();
		}

		out.flush();
		out.close();
	}

	public static Map<String, String> readMap(File src) throws IOException {
		return readMap(src, new StringValueWriterAndParser(), new StringValueWriterAndParser());
	}

	public static <V> Map<String, V> readMap(File src, ValueParser<V> valueParser)
			throws IOException {
		return readMap(src, new StringValueWriterAndParser(), valueParser);
	}

	public static <K extends Comparable<K>, V> TreeMap<K, V> readTreeMap(File src,
			ValueParser<K> keyParser, ValueParser<V> valueParser) throws IOException {

		BufferedReader in = new BufferedReader(new FileReader(src));

		TreeMap<K, V> retVal = new TreeMap<K, V>();

		String line = in.readLine();
		while (line != null) {

			int splitPt = line.indexOf(',');
			String keyStr = line.substring(0, splitPt);
			String valueStr = line.substring(splitPt + 1);
			retVal.put(keyParser.parse(keyStr), valueParser.parse(valueStr));

			line = in.readLine();
		}

		in.close();
		return retVal;

	}

	public static <K, V> Map<K, V> readMap(File src, ValueParser<K> keyParser,
			ValueParser<V> valueParser) throws IOException {

		BufferedReader in = new BufferedReader(new FileReader(src));

		Map<K, V> retVal = new HashMap<K, V>();

		String line = in.readLine();
		while (line != null) {

			int splitPt = line.indexOf(',');
			String keyStr = line.substring(0, splitPt);
			String valueStr = line.substring(splitPt + 1);
			retVal.put(keyParser.parse(keyStr), valueParser.parse(valueStr));

			line = in.readLine();
		}

		in.close();
		return retVal;

	}

	public static <K, V> void readMapLineByLine(File src, ValueParser<K> keyParser,
			ValueParser<V> valueParser, MapLineReader<K, V> reader) throws IOException {

		BufferedReader in = new BufferedReader(new FileReader(src));

		int count = 0;
		String line = in.readLine();
		while (line != null) {

			count++;
			if (count % 10000 == 0) {
				System.out.print("" + count + "...");
			}

			int splitPt = line.indexOf(',');
			String keyStr = line.substring(0, splitPt);
			String valueStr = line.substring(splitPt + 1);
			reader.readLine(keyParser.parse(keyStr), valueParser.parse(valueStr));

			line = in.readLine();
		}

		in.close();
	}
}
