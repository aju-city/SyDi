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

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, campaign.getCampaignName());
            stmt.setTimestamp(2, campaign.getStartDatetime());
            stmt.setTimestamp(3, campaign.getEndDatetime());
            stmt.setString(4, campaign.getStatus());
            stmt.setString(5, campaign.getCreatedBy());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    campaign.setCampaignId(keys.getInt(1));
                }
            }
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
     * Retrieves only campaigns that are active now (by time window) and not terminated.
     * This does not mutate DB status; callers can display computed status if needed.
     */
    public List<PromotionCampaign> getActiveCampaigns(Timestamp now) throws SQLException {
        List<PromotionCampaign> campaigns = new ArrayList<>();
        String sql =
                "SELECT * FROM PromotionCampaign " +
                "WHERE status <> 'TERMINATED' " +
                "AND start_datetime <= ? AND end_datetime >= ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, now);
            stmt.setTimestamp(2, now);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PromotionCampaign c = mapResultSetToCampaign(rs);
                    // Present as ACTIVE in responses when in-window
                    c.setStatus("ACTIVE");
                    campaigns.add(c);
                }
            }
        }
        return campaigns;
    }

    /**
     * Updates campaign name and date range.
     */
    public void updateCampaign(int campaignId, String campaignName, Timestamp startDatetime, Timestamp endDatetime) throws SQLException {
        String sql = "UPDATE PromotionCampaign SET campaign_name = ?, start_datetime = ?, end_datetime = ? WHERE campaign_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, campaignName);
            stmt.setTimestamp(2, startDatetime);
            stmt.setTimestamp(3, endDatetime);
            stmt.setInt(4, campaignId);
            stmt.executeUpdate();
        }
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