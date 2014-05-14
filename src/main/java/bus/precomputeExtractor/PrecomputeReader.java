package bus.precomputeExtractor;

import java.io.File;
import java.io.IOException;

import javax.mail.MessagingException;

public interface PrecomputeReader {

	void readPrecomputes(File precomputesPrefix) throws IOException, MessagingException;
}
