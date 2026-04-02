package model;

import java.sql.Timestamp;

/**
 * Model class representing a promotion campaign.
 */
public class PromotionCampaign {

    private int campaignId;
    private String campaignName;
    private Timestamp startDatetime;
    private Timestamp endDatetime;
    private String status;
    private String createdBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public PromotionCampaign() {
    }

    public PromotionCampaign(int campaignId, String campaignName, Timestamp startDatetime,
                             Timestamp endDatetime, String status, String createdBy,
                             Timestamp createdAt, Timestamp updatedAt) {
        this.campaignId = campaignId;
        this.campaignName = campaignName;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public Timestamp getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(Timestamp startDatetime) {
        this.startDatetime = startDatetime;
    }

    public Timestamp getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(Timestamp endDatetime) {
        this.endDatetime = endDatetime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}