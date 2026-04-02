package dao;

import model.PromotionCampaignItems;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for PromotionCampaignItems table.
 */
public class PromotionCampaignItemsDAO {

    private Connection connection;

    public PromotionCampaignItemsDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Creates a new promotion campaign item.
     */
    public void createCampaignItem(PromotionCampaignItems item) throws SQLException {
        String sql = "INSERT INTO PromotionCampaignItems (campaign_id, product_id, discount_rate) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getCampaignId());
            stmt.setString(2, item.getProductId());
            stmt.setDouble(3, item.getDiscountRate());
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves a campaign item by id.
     */
    public PromotionCampaignItems getCampaignItemById(int campaignItemId) throws SQLException {
        String sql = "SELECT * FROM PromotionCampaignItems WHERE campaign_item_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, campaignItemId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCampaignItem(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves all items for a campaign.
     */
    public List<PromotionCampaignItems> getItemsByCampaignId(int campaignId) throws SQLException {
        List<PromotionCampaignItems> items = new ArrayList<>();
        String sql = "SELECT * FROM PromotionCampaignItems WHERE campaign_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, campaignId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToCampaignItem(rs));
                }
            }
        }

        return items;
    }

    /**
     * Updates the discount rate of a campaign item.
     */
    public void updateDiscountRate(int campaignItemId, double discountRate) throws SQLException {
        String sql = "UPDATE PromotionCampaignItems SET discount_rate = ? WHERE campaign_item_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, discountRate);
            stmt.setInt(2, campaignItemId);
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a campaign item.
     */
    public void deleteCampaignItem(int campaignItemId) throws SQLException {
        String sql = "DELETE FROM PromotionCampaignItems WHERE campaign_item_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, campaignItemId);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps ResultSet to PromotionCampaignItems object.
     */
    private PromotionCampaignItems mapResultSetToCampaignItem(ResultSet rs) throws SQLException {
        PromotionCampaignItems item = new PromotionCampaignItems();
        item.setCampaignItemId(rs.getInt("campaign_item_id"));
        item.setCampaignId(rs.getInt("campaign_id"));
        item.setProductId(rs.getString("product_id"));
        item.setDiscountRate(rs.getDouble("discount_rate"));
        return item;
    }
}