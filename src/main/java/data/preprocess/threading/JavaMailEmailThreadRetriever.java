package data.preprocess.threading;

import data.representation.actionbased.messages.email.JavaMailEmailMessage;
import data.representation.actionbased.messages.email.JavaMailEmailThread;

public class JavaMailEmailThreadRetriever<Message extends JavaMailEmailMessage> extends
		JavaMailThreadRetriever<Message, JavaMailEmailThread<Message>> {

	public JavaMailEmailThreadRetriever() {

	}

	public JavaMailEmailThreadRetriever(long timeout) {
		this.timeout = timeout;
	}

	@Override
	public JavaMailEmailThread<Message> createThread() {
		return new JavaMailEmailThread<>();
	}
	
}
