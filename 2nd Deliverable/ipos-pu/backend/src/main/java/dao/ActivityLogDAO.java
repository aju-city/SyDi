package dao;

import model.ActivityLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for ActivityLog table.
 */
public class ActivityLogDAO {

    private Connection connection;

    public ActivityLogDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Creates a new activity log entry.
     */
    public void createActivity(ActivityLog activity) throws SQLException {
        String sql = "INSERT INTO ActivityLog (customer_type, member_email, guest_token, campaign_id, product_id, order_id, event_type, event_datetime) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, activity.getCustomerType());
            stmt.setString(2, activity.getMemberEmail());
            stmt.setString(3, activity.getGuestToken());

            if (activity.getCampaignId() != null) {
                stmt.setInt(4, activity.getCampaignId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            stmt.setString(5, activity.getProductId());

            if (activity.getOrderId() != null) {
                stmt.setInt(6, activity.getOrderId());
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.setString(7, activity.getEventType());

            if (activity.getEventDatetime() != null) {
                stmt.setTimestamp(8, activity.getEventDatetime());
            } else {
                stmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
            }

            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves an activity log entry by id.
     */
    public ActivityLog getActivityById(int activityId) throws SQLException {
        String sql = "SELECT * FROM ActivityLog WHERE activity_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, activityId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToActivity(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves all activity log entries.
     */
    public List<ActivityLog> getAllActivities() throws SQLException {
        List<ActivityLog> activities = new ArrayList<>();
        String sql = "SELECT * FROM ActivityLog";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                activities.add(mapResultSetToActivity(rs));
            }
        }

        return activities;
    }

    /**
     * Retrieves all activity log entries for a member email.
     */
    public List<ActivityLog> getActivitiesByMemberEmail(String memberEmail) throws SQLException {
        List<ActivityLog> activities = new ArrayList<>();
        String sql = "SELECT * FROM ActivityLog WHERE member_email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, memberEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    activities.add(mapResultSetToActivity(rs));
                }
            }
        }

        return activities;
    }

    /**
     * Deletes an activity log entry.
     */
    public void deleteActivity(int activityId) throws SQLException {
        String sql = "DELETE FROM ActivityLog WHERE activity_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, activityId);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps ResultSet to ActivityLog object.
     */
    private ActivityLog mapResultSetToActivity(ResultSet rs) throws SQLException {
        ActivityLog activity = new ActivityLog();
        activity.setActivityId(rs.getInt("activity_id"));
        activity.setCustomerType(rs.getString("customer_type"));
        activity.setMemberEmail(rs.getString("member_email"));
        activity.setGuestToken(rs.getString("guest_token"));

        int campaignIdValue = rs.getInt("campaign_id");
        if (rs.wasNull()) {
            activity.setCampaignId(null);
        } else {
            activity.setCampaignId(campaignIdValue);
        }

        activity.setProductId(rs.getString("product_id"));

        int orderIdValue = rs.getInt("order_id");
        if (rs.wasNull()) {
            activity.setOrderId(null);
        } else {
            activity.setOrderId(orderIdValue);
        }

        activity.setEventType(rs.getString("event_type"));
        activity.setEventDatetime(rs.getTimestamp("event_datetime"));
        return activity;
    }
}