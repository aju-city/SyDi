package dao;

import model.EmailLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for EmailLog table.
 */
public class EmailLogDAO {

    private Connection connection;

    public EmailLogDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Creates a new email log entry.
     */
    public void createEmailLog(EmailLog email) throws SQLException {
        String sql = "INSERT INTO EmailLog (order_id, recipient_email, email_type, subject, body, send_status, failure_reason) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (email.getOrderId() != null) {
                stmt.setInt(1, email.getOrderId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }

            stmt.setString(2, email.getRecipientEmail());
            stmt.setString(3, email.getEmailType());
            stmt.setString(4, email.getSubject());
            stmt.setString(5, email.getBody());
            stmt.setString(6, email.getSendStatus());
            stmt.setString(7, email.getFailureReason());

            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves an email log by ID.
     */
    public EmailLog getEmailById(int emailId) throws SQLException {
        String sql = "SELECT * FROM EmailLog WHERE email_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, emailId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmail(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves all emails for a specific order.
     */
    public List<EmailLog> getEmailsByOrderId(int orderId) throws SQLException {
        List<EmailLog> emails = new ArrayList<>();
        String sql = "SELECT * FROM EmailLog WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    emails.add(mapResultSetToEmail(rs));
                }
            }
        }

        return emails;
    }

    /**
     * Retrieves all email logs.
     */
    public List<EmailLog> getAllEmails() throws SQLException {
        List<EmailLog> emails = new ArrayList<>();
        String sql = "SELECT * FROM EmailLog";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                emails.add(mapResultSetToEmail(rs));
            }
        }

        return emails;
    }

    /**
     * Updates email send status.
     */
    public void updateSendStatus(int emailId, String status, String failureReason) throws SQLException {
        String sql = "UPDATE EmailLog SET send_status = ?, failure_reason = ? WHERE email_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, failureReason);
            stmt.setInt(3, emailId);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps ResultSet to EmailLog object.
     */
    private EmailLog mapResultSetToEmail(ResultSet rs) throws SQLException {
        EmailLog email = new EmailLog();
        email.setEmailId(rs.getInt("email_id"));
        email.setOrderId((Integer) rs.getObject("order_id")); // handles null
        email.setRecipientEmail(rs.getString("recipient_email"));
        email.setEmailType(rs.getString("email_type"));
        email.setSubject(rs.getString("subject"));
        email.setBody(rs.getString("body"));
        email.setSentDatetime(rs.getTimestamp("sent_datetime"));
        email.setSendStatus(rs.getString("send_status"));
        email.setFailureReason(rs.getString("failure_reason"));
        return email;
    }
}
