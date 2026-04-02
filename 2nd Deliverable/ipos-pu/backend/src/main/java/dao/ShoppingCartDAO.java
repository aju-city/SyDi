package dao;

import model.ShoppingCart;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for ShoppingCart table.
 */
public class ShoppingCartDAO {

    private Connection connection;

    public ShoppingCartDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Creates a cart for a member.
     */
    public void createMemberCart(String memberEmail) throws SQLException {
        String sql = "INSERT INTO ShoppingCart (customer_type, member_email, guest_token) VALUES ('MEMBER', ?, NULL)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, memberEmail);
            stmt.executeUpdate();
        }
    }

    /**
     * Creates a cart for a guest.
     */
    public void createGuestCart(String guestToken) throws SQLException {
        String sql = "INSERT INTO ShoppingCart (customer_type, member_email, guest_token) VALUES ('GUEST', NULL, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, guestToken);
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves a member cart by member email.
     */
    public ShoppingCart getCartByMemberEmail(String memberEmail) throws SQLException {
        String sql = "SELECT * FROM ShoppingCart WHERE member_email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, memberEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCart(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves a guest cart by guest token.
     */
    public ShoppingCart getCartByGuestToken(String guestToken) throws SQLException {
        String sql = "SELECT * FROM ShoppingCart WHERE guest_token = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, guestToken);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCart(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves a cart by ID.
     */
    public ShoppingCart getCartById(int cartId) throws SQLException {
        String sql = "SELECT * FROM ShoppingCart WHERE cart_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cartId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCart(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves all carts.
     */
    public List<ShoppingCart> getAllCarts() throws SQLException {
        List<ShoppingCart> carts = new ArrayList<>();
        String sql = "SELECT * FROM ShoppingCart";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                carts.add(mapResultSetToCart(rs));
            }
        }

        return carts;
    }

    /**
     * Deletes a cart by ID.
     */
    public void deleteCart(int cartId) throws SQLException {
        String sql = "DELETE FROM ShoppingCart WHERE cart_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cartId);
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a member cart by member email.
     */
    public void deleteCartByMemberEmail(String memberEmail) throws SQLException {
        String sql = "DELETE FROM ShoppingCart WHERE member_email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, memberEmail);
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a guest cart by guest token.
     */
    public void deleteCartByGuestToken(String guestToken) throws SQLException {
        String sql = "DELETE FROM ShoppingCart WHERE guest_token = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, guestToken);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps a ResultSet row to a ShoppingCart object.
     */
    private ShoppingCart mapResultSetToCart(ResultSet rs) throws SQLException {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartId(rs.getInt("cart_id"));
        cart.setCustomerType(rs.getString("customer_type"));
        cart.setMemberEmail(rs.getString("member_email"));
        cart.setGuestToken(rs.getString("guest_token"));
        cart.setCreatedAt(rs.getTimestamp("created_at"));
        cart.setUpdatedAt(rs.getTimestamp("updated_at"));
        return cart;
    }
}