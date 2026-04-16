package dao;

import db.DatabaseConnection;
import model.NonCommercialMember;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for NonCommercialMember table.
 */
public class NonCommercialMemberDAO {

    private Connection connection;

    public NonCommercialMemberDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserts a new member.
     */
    public void createMember(NonCommercialMember member) throws SQLException {
        String sql = "INSERT INTO NonCommercialMember (Email, Password, MustChangePassword, TotalOrders) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, member.getEmail());
            stmt.setString(2, member.getPassword());
            stmt.setBoolean(3, member.isMustChangePassword());
            stmt.setInt(4, member.getTotalOrders());
            stmt.executeUpdate();
        }
    }

    public void updateAccountNumber(int memberId, String accountNo) throws SQLException {
        String sql = "UPDATE NonCommercialMember SET MemberAccountNo = ? WHERE MemberID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, accountNo);
            stmt.setInt(2, memberId);
            stmt.executeUpdate();
        }
    }

    public boolean updatePasswordAndClearFlag(String email, String newPassword) throws Exception {
        String sql = "UPDATE NonCommercialMember SET Password = ?, MustChangePassword = 0 WHERE Email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, email);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Registers a new member and returns the generated member id.
     */
    public int register(NonCommercialMember member) throws SQLException {
        String sql = "INSERT INTO NonCommercialMember (Email, Password, MustChangePassword, TotalOrders) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, member.getEmail());
            stmt.setString(2, member.getPassword());
            stmt.setBoolean(3, member.isMustChangePassword());
            stmt.setInt(4, member.getTotalOrders());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int memberId = rs.getInt(1);

                    // Generate account number
                    String accountNo = "PU" + String.format("%04d", memberId);

                    // Update it
                    String updateSql = "UPDATE NonCommercialMember SET MemberAccountNo = ? WHERE MemberID = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                        updateStmt.setString(1, accountNo);
                        updateStmt.setInt(2, memberId);
                        updateStmt.executeUpdate();
                    }

                    return memberId;
                }
            }
        }

        throw new SQLException("Failed to register member.");
    }
    /**
     * Authenticates member login.
     */
    public NonCommercialMember login(String email, String password) throws SQLException {
        String sql = "SELECT * FROM NonCommercialMember WHERE Email = ? AND Password = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMember(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves a member by email.
     */
    public NonCommercialMember getMemberByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM NonCommercialMember WHERE Email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMember(rs);
                }
            }
        }
        return null;
    }

    /**
     * Retrieves a member by ID.
     */
    public NonCommercialMember getMemberById(int memberId) throws SQLException {
        String sql = "SELECT * FROM NonCommercialMember WHERE MemberID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, memberId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMember(rs);
                }
            }
        }
        return null;
    }

    /**
     * Updates member password.
     */
    public void updatePassword(int memberId, String newPassword) throws SQLException {
        String sql = "UPDATE NonCommercialMember SET Password = ?, MustChangePassword = 0 WHERE MemberID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, memberId);
            stmt.executeUpdate();
        }
    }

    /**
     * Increments total orders count.
     */
    public void incrementTotalOrders(int memberId) throws SQLException {
        String sql = "UPDATE NonCommercialMember SET TotalOrders = TotalOrders + 1 WHERE MemberID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, memberId);
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves all members.
     */
    public List<NonCommercialMember> getAllMembers() throws SQLException {
        List<NonCommercialMember> list = new ArrayList<>();
        String sql = "SELECT * FROM NonCommercialMember";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapResultSetToMember(rs));
            }
        }
        return list;
    }

    public static int getTotalOrders(String email) throws Exception {
        String sql = "SELECT TotalOrders FROM ipos_pu.NonCommercialMember WHERE Email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("TotalOrders");
            }

            return 0; // default if somehow not found
        }
    }

    /**
     * Maps ResultSet row to NonCommercialMember object.
     */
    private NonCommercialMember mapResultSetToMember(ResultSet rs) throws SQLException {
        NonCommercialMember member = new NonCommercialMember();
        member.setMemberID(rs.getInt("MemberID"));
        member.setEmail(rs.getString("Email"));
        member.setPassword(rs.getString("Password"));
        member.setMustChangePassword(rs.getBoolean("MustChangePassword"));
        member.setTotalOrders(rs.getInt("TotalOrders"));
        member.setCreatedAt(rs.getTimestamp("CreatedAt"));
        member.setMemberAccountNo(rs.getString("MemberAccountNo"));
        return member;
    }
}