package dao;

import model.AdminUsers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for AdminUsers table.
 */
public class AdminUsersDAO {

    private Connection connection;

    public AdminUsersDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Creates a new admin user.
     */
    public void createAdmin(AdminUsers admin) throws SQLException {
        String sql = "INSERT INTO AdminUsers (Username, Password, Role) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, admin.getUsername());
            stmt.setString(2, admin.getPassword());
            stmt.setString(3, admin.getRole());
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves an admin by username.
     */
    public AdminUsers getAdminByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM AdminUsers WHERE Username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAdmin(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves all admin users.
     */
    public List<AdminUsers> getAllAdmins() throws SQLException {
        List<AdminUsers> admins = new ArrayList<>();
        String sql = "SELECT * FROM AdminUsers";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                admins.add(mapResultSetToAdmin(rs));
            }
        }

        return admins;
    }

    /**
     * Updates an admin password.
     */
    public void updatePassword(String username, String newPassword) throws SQLException {
        String sql = "UPDATE AdminUsers SET Password = ? WHERE Username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes an admin user.
     */
    public void deleteAdmin(String username) throws SQLException {
        String sql = "DELETE FROM AdminUsers WHERE Username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }

    /**
     * Authenticates admin login.
     */
    public boolean authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM AdminUsers WHERE Username = ? AND Password = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Maps ResultSet to AdminUser object.
     */
    private AdminUsers mapResultSetToAdmin(ResultSet rs) throws SQLException {
        AdminUsers admin = new AdminUsers();
        admin.setUsername(rs.getString("Username"));
        admin.setPassword(rs.getString("Password"));
        admin.setRole(rs.getString("Role"));
        return admin;
    }
}