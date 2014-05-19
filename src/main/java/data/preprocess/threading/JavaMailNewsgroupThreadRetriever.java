package data.preprocess.threading;

import data.representation.actionbased.messages.newsgroup.JavaMailNewsgroupPost;
import data.representation.actionbased.messages.newsgroup.JavaMailNewsgroupThread;

public class JavaMailNewsgroupThreadRetriever<Post extends JavaMailNewsgroupPost>
		extends JavaMailThreadRetriever<Post, JavaMailNewsgroupThread<Post>> {

	@Override
	public JavaMailNewsgroupThread<Post> createThread() {
		return new JavaMailNewsgroupThread<>();
	}
}
