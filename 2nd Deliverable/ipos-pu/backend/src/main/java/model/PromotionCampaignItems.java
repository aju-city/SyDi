package model;

/**
 * Model class representing an item in a promotion campaign.
 */
public class PromotionCampaignItems {

    private int campaignItemId;
    private int campaignId;
    private String productId;
    private double discountRate;

    public PromotionCampaignItems() {
    }

    public PromotionCampaignItems(int campaignItemId, int campaignId, String productId, double discountRate) {
        this.campaignItemId = campaignItemId;
        this.campaignId = campaignId;
        this.productId = productId;
        this.discountRate = discountRate;
    }

    public int getCampaignItemId() {
        return campaignItemId;
    }

    public void setCampaignItemId(int campaignItemId) {
        this.campaignItemId = campaignItemId;
    }

    public int getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(int campaignId) {
        this.campaignId = campaignId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }
}