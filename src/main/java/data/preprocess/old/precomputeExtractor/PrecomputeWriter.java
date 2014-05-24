package data.preprocess.old.precomputeExtractor;

import java.io.File;
import java.io.IOException;

import javax.mail.MessagingException;

public interface PrecomputeWriter {

	public void writePrecomputes(File messageFile, File precomputesPrefix) throws IOException, MessagingException;
}
