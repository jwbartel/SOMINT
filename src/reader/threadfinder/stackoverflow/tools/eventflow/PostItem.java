package reader.threadfinder.stackoverflow.tools.eventflow;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.Element;

public class PostItem {

	private final int id;
	private String type;
	private final Date creationDate;
	private final String ownerUserId;
	private Integer acceptedAnswerId;
	private Integer parentId;
	private String tags;
	private final String title;

	static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

	public PostItem(Element row) throws ParseException {
		id = Integer.parseInt(row.attributeValue("Id"));

		String typeVal = row.attributeValue("PostTypeId");
		if (typeVal.equals("1")) {
			type = "Question";
			try {
				acceptedAnswerId = Integer.parseInt(row.attributeValue("AcceptedAnswerId"));
			} catch (NumberFormatException e) {
			}
			tags = row.attributeValue("Tags");
		} else if (typeVal.equals("2")) {
			type = "Answer";
			parentId = Integer.parseInt(row.attributeValue("ParentId"));
		} else {
			throw new RuntimeException("Invalid post type");
		}

		if (type.equals("Question")) {
		} else {
		}

		String creationDateStr = row.attributeValue("CreationDate");
		creationDate = dateFormat.parse(creationDateStr);

		ownerUserId = row.attributeValue("OwnerUserId");

		title = row.attributeValue("Title");

	}

	public int getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public int getAcceptedAnswerId() {
		return acceptedAnswerId;
	}

	public int getParentId() {
		return parentId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public String getOwnerUserId() {
		return ownerUserId;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		String str = "ID:" + id + "\tType:" + type + "\tCreationDate:" + creationDate.getTime()
				+ "\tOwner:" + ownerUserId + "\tAcceptedAnswer:" + acceptedAnswerId + "\tParent:"
				+ parentId + "\tTags:" + tags + "\tTitle:" + title;
		return str;
	}
}
