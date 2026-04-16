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
     * Inserts or updates a campaign item discount for (campaign_id, product_id).
     */
    public void upsertCampaignItem(int campaignId, String productId, double discountRate) throws SQLException {
        String sql =
                "INSERT INTO PromotionCampaignItems (campaign_id, product_id, discount_rate) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE discount_rate = VALUES(discount_rate)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, campaignId);
            stmt.setString(2, productId);
            stmt.setDouble(3, discountRate);
            stmt.executeUpdate();
        }
    }

    /**
     * Returns campaign items joined with product details from ipos_ca.
     * Columns: product_id, discount_rate, name, description, price
     */
    public List<java.util.Map<String, Object>> getCampaignProductsWithDiscount(int campaignId) throws SQLException {
        List<java.util.Map<String, Object>> out = new ArrayList<>();
        String sql =
                "SELECT pci.product_id, pci.discount_rate, si.name, si.description, si.price " +
                "FROM PromotionCampaignItems pci " +
                "JOIN ipos_ca.stock_items si ON si.item_id = pci.product_id " +
                "WHERE pci.campaign_id = ? " +
                "ORDER BY si.name";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, campaignId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    java.util.Map<String, Object> m = new java.util.LinkedHashMap<>();
                    m.put("productId", rs.getString("product_id"));
                    m.put("discountRate", rs.getDouble("discount_rate"));
                    m.put("name", rs.getString("name"));
                    m.put("description", rs.getString("description"));
                    m.put("price", rs.getDouble("price"));
                    out.add(m);
                }
            }
        }
        return out;
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