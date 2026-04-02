package model;

import java.sql.Timestamp;

/**
 * Model class representing an email log entry.
 */
public class EmailLog {

    private int emailId;
    private Integer orderId; // can be null
    private String recipientEmail;
    private String emailType;
    private String subject;
    private String body;
    private Timestamp sentDatetime;
    private String sendStatus;
    private String failureReason;

    public EmailLog() {
    }

    public int getEmailId() {
        return emailId;
    }

    public void setEmailId(int emailId) {
        this.emailId = emailId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getEmailType() {
        return emailType;
    }

    public void setEmailType(String emailType) {
        this.emailType = emailType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Timestamp getSentDatetime() {
        return sentDatetime;
    }

    public void setSentDatetime(Timestamp sentDatetime) {
        this.sentDatetime = sentDatetime;
    }

    public String getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}