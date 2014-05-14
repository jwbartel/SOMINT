package reader.threadfinder.stackoverflow.tools;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.TreeSet;

import bus.tools.io.CollectionIOAssist;
import bus.tools.io.IntegerValueParser;
import bus.tools.io.MapIOAssist;

public abstract class ExperimentFeatureReader<V> {

	public static class OwnerFeatureReader extends ExperimentFeatureReader<Integer> {

		@Override
		public Collection<Integer> readValues(String questionPrefix) throws IOException {
			return CollectionIOAssist.readCollection(new File(questionPrefix + "_OWNER.TXT"),
					new IntegerValueParser());
		}

	}

	public static class TagFeatureReader extends ExperimentFeatureReader<String> {

		@Override
		public Collection<String> readValues(String questionPrefix) throws IOException {
			return CollectionIOAssist.readCollection(new File(questionPrefix + "_TAGS.TXT"));
		}

	}

	public static class WordFeatureReader extends ExperimentFeatureReader<String> {

		@Override
		public Collection<String> readValues(String questionPrefix) throws IOException {
			return MapIOAssist.readMap(new File(questionPrefix + "_SUBJECT_WORDCOUNTS.TXT"))
					.keySet();
		}

	}

	public static class TagWordFeatureReader extends ExperimentFeatureReader<Pair<String, String>> {

		@Override
		public Collection<Pair<String, String>> readValues(String questionPrefix)
				throws IOException {
			Collection<String> tags = CollectionIOAssist.readCollection(new File(questionPrefix
					+ "_TAGS.TXT"));
			Collection<String> words = MapIOAssist.readMap(
					new File(questionPrefix + "_SUBJECT_WORDCOUNTS.TXT")).keySet();

			Collection<Pair<String, String>> retVal = new TreeSet<Pair<String, String>>();
			for (String tag : tags) {
				for (String word : words) {
					retVal.add(new TagWordPair(tag, word));
				}
			}
			return retVal;
		}

	}

	public abstract Collection<V> readValues(String questionPrefix) throws IOException;
}
