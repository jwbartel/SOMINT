package util.tools.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ArrayIOAssist {

	public static <V> void writeArray(File dest, V[][] array) throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				if (array[i][j] != null) {
					out.write(array[i][j].toString());
				}
				out.write(",");
				out.flush();
			}
			out.newLine();
		}
		out.flush();
		out.close();
	}

	public static <V> void writeArray(File dest, V[][] array, ValueWriter<V> writer)
			throws IOException {
		BufferedWriter out = new BufferedWriter(new FileWriter(dest));
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[0].length; j++) {
				out.write(writer.writeVal(array[i][j]));
				out.write(",");
				out.flush();
			}
			out.newLine();
		}
		out.flush();
		out.close();
	}
}
