package dao;

import model.CommercialApplication;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for ipos_sa.commercial_applications table.
 */
public class CommercialApplicationDAO {

    private Connection connection;

    public CommercialApplicationDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Creates a new commercial application.
     */
    public void createApplication(CommercialApplication application) throws SQLException {
        String sql = "INSERT INTO ipos_sa.commercial_applications (company_name, reg_number, director_details, business_type, address, email, phone, status, reviewed_by, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, application.getCompanyName());
            stmt.setString(2, application.getRegNumber());
            stmt.setString(3, application.getDirectorDetails());
            stmt.setString(4, application.getBusinessType());
            stmt.setString(5, application.getAddress());
            stmt.setString(6, application.getEmail());
            stmt.setString(7, application.getPhone());
            stmt.setString(8, application.getStatus());

            if (application.getReviewedBy() != null) {
                stmt.setInt(9, application.getReviewedBy());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }

            stmt.setString(10, application.getNotes());
            stmt.executeUpdate();
        }
    }

    /**
     * Creates a new commercial application and returns the generated id.
     */
    public int createApplicationAndReturnId(CommercialApplication application) throws SQLException {
        String sql = "INSERT INTO ipos_sa.commercial_applications (company_name, reg_number, director_details, business_type, address, email, phone, status, reviewed_by, notes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, application.getCompanyName());
            stmt.setString(2, application.getRegNumber());
            stmt.setString(3, application.getDirectorDetails());
            stmt.setString(4, application.getBusinessType());
            stmt.setString(5, application.getAddress());
            stmt.setString(6, application.getEmail());
            stmt.setString(7, application.getPhone());
            stmt.setString(8, application.getStatus());

            if (application.getReviewedBy() != null) {
                stmt.setInt(9, application.getReviewedBy());
            } else {
                stmt.setNull(9, Types.INTEGER);
            }

            stmt.setString(10, application.getNotes());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        throw new SQLException("Failed to create commercial application.");
    }

    /**
     * Checks whether a commercial application exists for the given email.
     */
    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM ipos_sa.commercial_applications WHERE email = ? LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Retrieves an application by id.
     */
    public CommercialApplication getApplicationById(int applicationId) throws SQLException {
        String sql = "SELECT * FROM ipos_sa.commercial_applications WHERE application_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, applicationId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToApplication(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves all commercial applications.
     */
    public List<CommercialApplication> getAllApplications() throws SQLException {
        List<CommercialApplication> applications = new ArrayList<>();
        String sql = "SELECT * FROM ipos_sa.commercial_applications";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                applications.add(mapResultSetToApplication(rs));
            }
        }

        return applications;
    }

    /**
     * Updates the status of an application.
     */
    public void updateApplicationStatus(int applicationId, String status) throws SQLException {
        String sql = "UPDATE ipos_sa.commercial_applications SET status = ? WHERE application_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, applicationId);
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes an application.
     */
    public void deleteApplication(int applicationId) throws SQLException {
        String sql = "DELETE FROM ipos_sa.commercial_applications WHERE application_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, applicationId);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps ResultSet to CommercialApplication object.
     */
    private CommercialApplication mapResultSetToApplication(ResultSet rs) throws SQLException {
        CommercialApplication application = new CommercialApplication();
        application.setApplicationId(rs.getInt("application_id"));
        application.setCompanyName(rs.getString("company_name"));
        application.setRegNumber(rs.getString("reg_number"));
        application.setDirectorDetails(rs.getString("director_details"));
        application.setBusinessType(rs.getString("business_type"));
        application.setAddress(rs.getString("address"));
        application.setEmail(rs.getString("email"));
        application.setPhone(rs.getString("phone"));
        application.setStatus(rs.getString("status"));
        application.setSubmittedAt(rs.getTimestamp("submitted_at"));

        int reviewedByValue = rs.getInt("reviewed_by");
        if (rs.wasNull()) {
            application.setReviewedBy(null);
        } else {
            application.setReviewedBy(reviewedByValue);
        }

        application.setNotes(rs.getString("notes"));
        return application;
    }
}