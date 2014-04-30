package reader.threadfinder.stackoverflow.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public abstract class PostReader {

	public static final String ID = "ID:";
	public static final String CREATION_DATE = "CreationDate:";
	public static final String TITLE = "Title:";
	public static final String TAGS = "Tags:";
	public static final String ACCEPTED_ANSWER = "AcceptedAnswer:";
	public static final String OWNER = "Owner:";
	public static final String PARENT = "Parent:";

	protected Integer readID(String post) {
		if (post.startsWith(ID)) {
			String id = post.substring(ID.length(), post.indexOf('\t'));
			return Integer.parseInt(id);
		}
		return null;
	}

	protected Long readCreationDate(String post) {
		String creationDate = getValue(post, CREATION_DATE);
		if (creationDate != null) {
			return Long.parseLong(creationDate);
		}
		return null;
	}

	protected Integer readOwner(String post) {
		String owner = getValue(post, OWNER);
		if (owner != null && !owner.equals("null")) {
			return Integer.parseInt(owner);
		}
		return null;
	}

	protected String readTitle(String post) {
		String title = getValue(post, TITLE);
		if (title != null && !title.equals("null")) {
			return title;
		}
		return null;
	}

	protected String[] readTags(String post) {
		String tags = getValue(post, TAGS);
		if (tags == null || tags.equals("null")) {
			return null;
		}
		tags = tags.trim().toLowerCase();
		if (tags.startsWith("<")) {
			tags = tags.substring(1);
		}
		if (tags.endsWith(">")) {
			tags = tags.substring(0, tags.length() - 1);
		}
		return tags.split("><");
	}

	protected Integer readAcceptedAnswer(String post) {
		String acceptedAnswer = getValue(post, ACCEPTED_ANSWER);
		if (acceptedAnswer != null && !acceptedAnswer.equals("null")) {
			return Integer.parseInt(acceptedAnswer);
		}
		return null;
	}

	protected Integer readParent(String post) {
		String parent = getValue(post, PARENT);
		if (parent != null && !parent.equals("null")) {
			return Integer.parseInt(parent);
		}
		return null;
	}

	private String getValue(String post, String label) {
		int pos = post.indexOf(label);
		if (pos == -1) {
			return null;
		}

		post = post.substring(pos + label.length());
		int endPos = post.indexOf('\t');
		if (endPos == -1 || label.equals(TITLE)) {
			return post;
		}
		return post.substring(0, endPos);
	}

	public abstract void readPost(String post);

	public static void readPosts(File postsFile, PostReader reader) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(postsFile));

		String line = in.readLine();
		int count = 0;
		while (line != null) {
			if (count % 1 == 0) {
//			if (count % 100000 == 0) {
				System.out.println(count);
			}
			reader.readPost(line);
			line = in.readLine();
			count++;
		}

		in.close();
	}
}
