package dao;

import db.DatabaseConnection;
import model.CommercialApplicant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CommercialApplicantDAO {

    // ⚠️ Deprecated: Do NOT use - no transaction support
    public void addApplicant(CommercialApplicant applicant) {
        String sql = "INSERT INTO CommercialApplicant " +
                "(CompanyName, CompanyRegistrationNumber, BusinessType, Email, ApplicationDate, ApplicationStatus, NotificationPreference, CompanyAddress) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, applicant.getCompanyName());
            stmt.setString(2, applicant.getCompanyRegistrationNumber());
            stmt.setString(3, applicant.getBusinessType());
            stmt.setString(4, applicant.getEmail());
            stmt.setDate(5, applicant.getApplicationDate());
            stmt.setString(6, applicant.getApplicationStatus());
            stmt.setString(7, applicant.getNotificationPreference());
            stmt.setString(8, applicant.getCompanyAddress());

            stmt.executeUpdate();
            System.out.println("Commercial applicant inserted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int addApplicant(CommercialApplicant applicant, Connection conn) throws SQLException {

        String sql = "INSERT INTO CommercialApplicant " +
                "(CompanyName, CompanyRegistrationNumber, BusinessType, Email, ApplicationDate, ApplicationStatus, NotificationPreference, CompanyAddress) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        int generatedId;

        try (PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, applicant.getCompanyName());
            stmt.setString(2, applicant.getCompanyRegistrationNumber());
            stmt.setString(3, applicant.getBusinessType());
            stmt.setString(4, applicant.getEmail());
            stmt.setDate(5, applicant.getApplicationDate());
            stmt.setString(6, applicant.getApplicationStatus());
            stmt.setString(7, applicant.getNotificationPreference());
            stmt.setString(8, applicant.getCompanyAddress());

            stmt.executeUpdate();

            var rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                generatedId = rs.getInt(1);
            } else {
                throw new SQLException("Failed to retrieve ApplicationID.");
            }
        }

        return generatedId;
    }
}