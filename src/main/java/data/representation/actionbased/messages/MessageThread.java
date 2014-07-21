package data.representation.actionbased.messages;

import java.util.ArrayList;
import java.util.Date;

import javax.mail.MessagingException;

import data.representation.actionbased.CollaborativeAction;
import data.representation.actionbased.CollaborativeActionThread;

public abstract class MessageThread<V, MessageType extends SingleMessage<V>> extends
		CollaborativeActionThread<V, MessageType> {
	
	public String getTitle() {
		Date earliestStart = null;
		MessageType earliestMessage = null;
		for (MessageType action : getThreadedActions()) {
			if (earliestStart == null || earliestStart.after(action.getStartDate())) {
				earliestStart = action.getStartDate();
				earliestMessage = action;
			}
		}
		
		if (earliestMessage != null) {
			return earliestMessage.getTitle();
		}
		return null;
	}
	
	public Double getTimeToResponse() throws MessagingException {
		Date[] firstAndResponseDates = getFirstAndResponseDates();
		if (firstAndResponseDates == null ||
				firstAndResponseDates.length < 2 ||
				firstAndResponseDates[0] == null ||
				firstAndResponseDates[1] == null) {
			return Double.POSITIVE_INFINITY;
		}
		
		long firstTime = firstAndResponseDates[0].getTime();
		long responseTime = firstAndResponseDates[1].getTime();
		
		return (responseTime-firstTime)/1000.0;
	}
	
	
	public Date[] getFirstAndResponseDates() throws MessagingException {
		if (getThreadedActions().size() < 2) {
			return null;
		}

		Date[] firstAndResponseDates = null;

		MessageType firstMessage = null;
		MessageType response = null;
		for (MessageType message : getThreadedActions()) {
			Date msgDate = message.getLastActiveDate();
			if (msgDate != null) {
				if (firstMessage == null
						|| firstMessage.getLastActiveDate().after(msgDate)) {
					firstMessage = message;
				} else if (response == null
						|| response.getLastActiveDate().after(msgDate)) {
					response = message;

				}
			}
		}

		firstAndResponseDates = new Date[2];
		if (firstMessage != null) {
			firstAndResponseDates[0] = firstMessage.getLastActiveDate();
		}
		if (response != null) {
			firstAndResponseDates[1] = response.getLastActiveDate();
		}

		return firstAndResponseDates;

	}
	
	public Object[] getOriginalAndResponse()
			throws MessagingException {
		if (getThreadedActions().size() < 2) {
			return null;
		}

		MessageType firstMessage = null;
		MessageType response = null;
		for (MessageType message : getThreadedActions()) {
			Date msgDate = message.getLastActiveDate();
			if (msgDate != null) {
				if (firstMessage == null
						|| firstMessage.getLastActiveDate().after(msgDate)) {
					firstMessage = message;
				} else if (response == null
						|| response.getLastActiveDate().after(msgDate)) {
					response = message;

				}
			}
		}

		Object[] originalAndResponseMessages = null;
		if (firstMessage != null && response != null) {
			ArrayList<MessageType> firstAndLast = new ArrayList<>();
			firstAndLast.add(firstMessage);
			firstAndLast.add(response);
			originalAndResponseMessages = firstAndLast.toArray();
		}
		return originalAndResponseMessages;
	}
}
