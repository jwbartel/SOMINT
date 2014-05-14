package reader.threadfinder.stackoverflow.tools.eventflow;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.Element;

public class CommentItem {

	private final int id;
	private final int parentId;
	private final String ownerUserId;
	private final Date creationDate;

	static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	public CommentItem(Element row) throws ParseException {

		id = Integer.parseInt(row.attributeValue("Id"));
		parentId = Integer.parseInt(row.attributeValue("PostId"));
		ownerUserId = row.attributeValue("UserId");

		String creationDateStr = row.attributeValue("CreationDate");
		creationDate = dateFormat.parse(creationDateStr);
	}

	@Override
	public String toString() {
		String str = "ID:" + id + "\tCreationDate:" + creationDate.getTime() + "\tOwner:"
				+ ownerUserId + "\tParent:" + parentId;
		return str;
	}
}
