package dao;

import model.PromotionCampaign;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for PromotionCampaign table.
 */
public class PromotionCampaignDAO {

    private Connection connection;

    public PromotionCampaignDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Creates a new promotion campaign.
     */
    public void createCampaign(PromotionCampaign campaign) throws SQLException {
        String sql = "INSERT INTO PromotionCampaign (campaign_name, start_datetime, end_datetime, status, created_by) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, campaign.getCampaignName());
            stmt.setTimestamp(2, campaign.getStartDatetime());
            stmt.setTimestamp(3, campaign.getEndDatetime());
            stmt.setString(4, campaign.getStatus());
            stmt.setString(5, campaign.getCreatedBy());
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves a campaign by id.
     */
    public PromotionCampaign getCampaignById(int campaignId) throws SQLException {
        String sql = "SELECT * FROM PromotionCampaign WHERE campaign_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, campaignId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCampaign(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves all promotion campaigns.
     */
    public List<PromotionCampaign> getAllCampaigns() throws SQLException {
        List<PromotionCampaign> campaigns = new ArrayList<>();
        String sql = "SELECT * FROM PromotionCampaign";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                campaigns.add(mapResultSetToCampaign(rs));
            }
        }

        return campaigns;
    }

    /**
     * Updates the status of a campaign.
     */
    public void updateCampaignStatus(int campaignId, String status) throws SQLException {
        String sql = "UPDATE PromotionCampaign SET status = ? WHERE campaign_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, campaignId);
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a promotion campaign.
     */
    public void deleteCampaign(int campaignId) throws SQLException {
        String sql = "DELETE FROM PromotionCampaign WHERE campaign_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, campaignId);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps ResultSet to PromotionCampaign object.
     */
    private PromotionCampaign mapResultSetToCampaign(ResultSet rs) throws SQLException {
        PromotionCampaign campaign = new PromotionCampaign();
        campaign.setCampaignId(rs.getInt("campaign_id"));
        campaign.setCampaignName(rs.getString("campaign_name"));
        campaign.setStartDatetime(rs.getTimestamp("start_datetime"));
        campaign.setEndDatetime(rs.getTimestamp("end_datetime"));
        campaign.setStatus(rs.getString("status"));
        campaign.setCreatedBy(rs.getString("created_by"));
        campaign.setCreatedAt(rs.getTimestamp("created_at"));
        campaign.setUpdatedAt(rs.getTimestamp("updated_at"));
        return campaign;
    }
}