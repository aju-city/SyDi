package dao;

import model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private final Connection connection;

    public ProductDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Returns all products from ipos_ca.stock_items
     */
    public List<Product> getAllProducts() throws SQLException {
        List<Product> list = new ArrayList<>();

        String sql = "SELECT item_id, name, description, price, quantity, stock_limit " +
                "FROM ipos_ca.stock_items ORDER BY name";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Product(
                        rs.getString("item_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getInt("stock_limit")
                ));
            }
        }

        return list;
    }

    /**
     * Searches products by name or description (case-insensitive)
     */
    public List<Product> searchProducts(String query) throws SQLException {
        List<Product> list = new ArrayList<>();

        String sql = "SELECT item_id, name, description, price, quantity, stock_limit " +
                "FROM ipos_ca.stock_items " +
                "WHERE LOWER(name) LIKE LOWER(?) OR LOWER(description) LIKE LOWER(?) " +
                "ORDER BY name";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String like = "%" + query + "%";
            stmt.setString(1, like);
            stmt.setString(2, like);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Product(
                            rs.getString("item_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getInt("stock_limit")
                    ));
                }
            }
        }

        return list;
    }

    /**
     * Returns a single product by ID
     */
    public Product getProductById(String id) throws SQLException {
        String sql = "SELECT item_id, name, description, price, quantity, stock_limit " +
                "FROM ipos_ca.stock_items WHERE item_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getString("item_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getInt("stock_limit")
                    );
                }
            }
        }

        return null;
    }
}
