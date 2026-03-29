package dao;

import db.DatabaseConnection;
import model.CompanyDirector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CompanyDirectorDAO {

    // Deprecated: no transaction support
    public void addDirector(CompanyDirector director) {
        String sql = "INSERT INTO CompanyDirector (ApplicationID, FirstName, LastName, PhoneNumber) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, director.getApplicationID());
            stmt.setString(2, director.getFirstName());
            stmt.setString(3, director.getLastName());
            stmt.setString(4, director.getPhoneNumber());

            stmt.executeUpdate();
            System.out.println("Company director inserted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addDirector(CompanyDirector director, Connection conn) throws SQLException {

        // Safety check
        if (director.getApplicationID() <= 0) {
            throw new SQLException("Invalid ApplicationID for director.");
        }

        String sql = "INSERT INTO CompanyDirector (ApplicationID, FirstName, LastName, PhoneNumber) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, director.getApplicationID());
            stmt.setString(2, director.getFirstName());
            stmt.setString(3, director.getLastName());
            stmt.setString(4, director.getPhoneNumber());

            int rows = stmt.executeUpdate();

            // Extra safety
            if (rows == 0) {
                throw new SQLException("Failed to insert director.");
            }
        }
    }
}