package recommendation.andrew.email;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Ghobrial
 * Date: 9/29/13
 * Time: 7:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class Message {
    private int messageId;
    public void setMessageId(int id) {
        this.messageId=id;
    }
    public int getMessageId() {
        return messageId;
    }
    private int threadId;
    public void setThreadId(int id) {
        this.threadId=id;
    }
    public int getThreadId() {
        return threadId;
    }
    private int fromId;
    public void setFromId(int id) {
        this.fromId=id;
    }
    public int getFromId() {
        return fromId;
    }
    private Date receivedDate;
    public Date getReceivedDate() {
        return receivedDate;
    }
    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }
    private int [] recipients;
    public int[] getRecipients() {
        return recipients;
    }
    public void setRecipients(int[] recipients) {
        this.recipients = recipients;
    }
}
