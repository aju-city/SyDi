package dao;

import db.DatabaseConnection;
import model.NonCommercialMember;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NonCommercialMemberDAO {

    // REGISTER
    public int register(NonCommercialMember member) throws SQLException {

        String sql = "INSERT INTO NonCommercialMember (Email, Password, MustChangePassword, TotalOrders) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, member.getEmail());
            stmt.setString(2, member.getPassword());
            stmt.setBoolean(3, member.isMustChangePassword());
            stmt.setInt(4, member.getTotalOrders());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // MemberID
            } else {
                throw new SQLException("Failed to register member.");
            }
        }
    }

    //LOGIN
    public NonCommercialMember login(String email, String password) throws SQLException {

        String sql = "SELECT * FROM NonCommercialMember WHERE Email = ? AND Password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                NonCommercialMember member = new NonCommercialMember();
                member.setMemberID(rs.getInt("MemberID"));
                member.setEmail(rs.getString("Email"));
                member.setPassword(rs.getString("Password"));
                member.setMustChangePassword(rs.getBoolean("MustChangePassword"));
                member.setTotalOrders(rs.getInt("TotalOrders"));
                return member;
            }
        }

        return null;
    }

    // CHECK IF EMAIL EXISTS IN COMMERCIAL APPLICANTS
    public boolean emailExistsInCommercial(String email) throws SQLException {

        String sql = "SELECT 1 FROM CommercialApplicant WHERE Email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // true if exists
        }
    }
}