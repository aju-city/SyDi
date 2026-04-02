package dao;

import model.ShoppingCartItems;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for ShoppingCartItems table.
 */
public class ShoppingCartItemsDAO {

    private Connection connection;

    public ShoppingCartItemsDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Creates a new cart item.
     */
    public void createCartItem(ShoppingCartItems item) throws SQLException {
        String sql = "INSERT INTO ShoppingCartItems (cart_id, product_id, quantity) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getCartId());
            stmt.setString(2, item.getProductId());
            stmt.setInt(3, item.getQuantity());
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves a cart item by id.
     */
    public ShoppingCartItems getCartItemById(int cartItemId) throws SQLException {
        String sql = "SELECT * FROM ShoppingCartItems WHERE cart_item_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cartItemId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCartItem(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves all items in a cart.
     */
    public List<ShoppingCartItems> getItemsByCartId(int cartId) throws SQLException {
        List<ShoppingCartItems> items = new ArrayList<>();
        String sql = "SELECT * FROM ShoppingCartItems WHERE cart_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cartId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToCartItem(rs));
                }
            }
        }

        return items;
    }

    /**
     * Updates the quantity of a cart item.
     */
    public void updateQuantity(int cartItemId, int quantity) throws SQLException {
        String sql = "UPDATE ShoppingCartItems SET quantity = ? WHERE cart_item_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, cartItemId);
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a cart item.
     */
    public void deleteCartItem(int cartItemId) throws SQLException {
        String sql = "DELETE FROM ShoppingCartItems WHERE cart_item_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cartItemId);
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes all items in a cart.
     */
    public void clearCart(int cartId) throws SQLException {
        String sql = "DELETE FROM ShoppingCartItems WHERE cart_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cartId);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps ResultSet to ShoppingCartItems object.
     */
    private ShoppingCartItems mapResultSetToCartItem(ResultSet rs) throws SQLException {
        ShoppingCartItems item = new ShoppingCartItems();
        item.setCartItemId(rs.getInt("cart_item_id"));
        item.setCartId(rs.getInt("cart_id"));
        item.setProductId(rs.getString("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        return item;
    }
}