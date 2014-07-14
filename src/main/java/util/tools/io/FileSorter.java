package util.tools.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeSet;

public class FileSorter {

	private static class SortedItem<V extends Comparable<V>> implements Comparable<SortedItem<V>> {

		protected final V item;
		protected final int source;

		public SortedItem(V item, int source) {
			this.item = item;
			this.source = source;
		}

		
		public int compareTo(SortedItem<V> o) {
			return item.compareTo(o.item);
		}

		
		public String toString() {
			return "" + source + ":" + item.toString();
		}

	}

	public static <V extends Comparable<V>> void sortInMemory(File toSort, ValueParser<V> parser,
			ValueWriter<V> writer) throws IOException {

		TreeSet<V> sortedSet = CollectionIOAssist.readTreeSet(toSort, parser);
		CollectionIOAssist.writeCollection(toSort, sortedSet);
	}

	private static <V extends Comparable<V>> void combineSortedExternalFiles(File toSort,
			ValueParser<V> valueParser, ValueWriter<V> writer, ArrayList<File> externalFiles)
			throws IOException {

		BufferedWriter out = new BufferedWriter(new FileWriter(toSort));

		ArrayList<BufferedReader> ins = new ArrayList<BufferedReader>();
		for (File external : externalFiles) {
			ins.add(new BufferedReader(new FileReader(external)));
		}

		ArrayList<String> lines = new ArrayList<String>();
		for (BufferedReader in : ins) {
			lines.add(in.readLine());
		}

		TreeSet<SortedItem<V>> sortedItems = new TreeSet<SortedItem<V>>();
		for (int i = 0; i < externalFiles.size(); i++) {
			String line = lines.get(i);
			if (line != null) {
				V item = valueParser.parse(line);
				sortedItems.add(new SortedItem<V>(item, i));
				line = ins.get(i).readLine();
				lines.set(i, line);
			}
		}

		while (sortedItems.size() > 0) {
			SortedItem<V> sortedItem = sortedItems.iterator().next();
			sortedItems.remove(sortedItem);
			out.write(writer.writeVal(sortedItem.item));
			out.newLine();
			out.flush();

			String line = lines.get(sortedItem.source);
			boolean added = false;
			while (line != null && !added) {

				V item = valueParser.parse(line);

				SortedItem<V> newSortedItem = new SortedItem<V>(item, sortedItem.source);

				int oldSize = sortedItems.size();
				sortedItems.add(newSortedItem);
				if (oldSize == sortedItems.size() - 1) {
					added = true;
				}
				line = ins.get(sortedItem.source).readLine();
				lines.set(sortedItem.source, line);
			}
		}

		for (BufferedReader in : ins) {
			in.close();
		}

		out.flush();
		out.close();
	}

	private static <V extends Comparable<V>> void divideIntoSortedExternalFiles(File toSort,
			ValueParser<V> valueParser, ValueWriter<V> writer, ArrayList<File> externalFiles)
			throws IOException {

		ArrayList<BufferedWriter> outs = new ArrayList<BufferedWriter>();
		for (File external : externalFiles) {
			outs.add(new BufferedWriter(new FileWriter(external)));
		}

		int outPos = 0;
		BufferedReader in = new BufferedReader(new FileReader(toSort));

		String line = in.readLine();
		while (line != null) {

			V val = valueParser.parse(line);
			outs.get(outPos).write(writer.writeVal(val));
			outs.get(outPos).newLine();
			outs.get(outPos).flush();

			outPos++;
			outPos = outPos % externalFiles.size();
			line = in.readLine();
		}

		for (BufferedWriter out : outs) {
			out.close();
		}

		for (File external : externalFiles) {
			sortInMemory(external, valueParser, writer);
		}

	}

	public static <V extends Comparable<V>> void externalSort(File toSort,
			ValueParser<V> valueParser, ValueWriter<V> writer, int numExternalFiles)
			throws IOException {

		ArrayList<File> externalFiles = new ArrayList<File>();
		File parentFolder = toSort.getParentFile();
		int tempCount = 0;
		for (int i = 0; i < numExternalFiles; i++) {

			File tempFile = new File(parentFolder, ".temp" + tempCount);
			while (tempFile.exists()) {
				tempCount++;
				tempFile = new File(parentFolder, ".temp" + tempCount);
			}
			externalFiles.add(tempFile);
			tempFile.createNewFile();
			tempCount++;
		}

		divideIntoSortedExternalFiles(toSort, valueParser, writer, externalFiles);
		combineSortedExternalFiles(toSort, valueParser, writer, externalFiles);

		for (File external : externalFiles) {
			external.deleteOnExit();
		}
	}
}
