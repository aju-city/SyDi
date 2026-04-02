package model;

import java.sql.Timestamp;

/**
 * Model class representing an activity log entry.
 */
public class ActivityLog {

    private int activityId;
    private String customerType;
    private String memberEmail;
    private String guestToken;
    private Integer campaignId;
    private String productId;
    private Integer orderId;
    private String eventType;
    private Timestamp eventDatetime;

    public ActivityLog() {
    }

    public ActivityLog(int activityId, String customerType, String memberEmail, String guestToken,
                       Integer campaignId, String productId, Integer orderId,
                       String eventType, Timestamp eventDatetime) {
        this.activityId = activityId;
        this.customerType = customerType;
        this.memberEmail = memberEmail;
        this.guestToken = guestToken;
        this.campaignId = campaignId;
        this.productId = productId;
        this.orderId = orderId;
        this.eventType = eventType;
        this.eventDatetime = eventDatetime;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public String getGuestToken() {
        return guestToken;
    }

    public void setGuestToken(String guestToken) {
        this.guestToken = guestToken;
    }

    public Integer getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Integer campaignId) {
        this.campaignId = campaignId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Timestamp getEventDatetime() {
        return eventDatetime;
    }

    public void setEventDatetime(Timestamp eventDatetime) {
        this.eventDatetime = eventDatetime;
    }
}