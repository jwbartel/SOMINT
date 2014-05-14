package recommendation.andrew.email;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew Ghobrial
 * Date: 9/29/13
 * Time: 7:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class Message2 {
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
    private String from;
    public void setFrom(String id) {
        this.from=id;
    }
    public String getFrom() {
        return from;
    }
    private String receivedDate;
    public String getReceivedDate() {
        return receivedDate;
    }
    public void setReceivedDate(String receivedDate) {
        this.receivedDate = receivedDate;
    }
    private String [] recipients;
    public String[] getRecipients() {
        return recipients;
    }
    public void setRecipients(String[] recipients) {
        this.recipients = recipients;
    }
}
