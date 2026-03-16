package dao;

import db.DatabaseConnection;
import model.NonCommercialMember;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NonCommercialMemberDAO {

    public void addMember(NonCommercialMember member) {
        String sql = "INSERT INTO NonCommercialMember (Email, Password, MustChangePassword, TotalOrders) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, member.getEmail());
            stmt.setString(2, member.getPassword());
            stmt.setBoolean(3, member.isMustChangePassword());
            stmt.setInt(4, member.getTotalOrders());

            stmt.executeUpdate();
            System.out.println("Non-commercial member inserted successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}