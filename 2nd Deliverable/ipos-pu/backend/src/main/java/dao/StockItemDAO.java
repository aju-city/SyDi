package dao;

import model.StockItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for ipos_ca.stock_items table.
 */
public class StockItemDAO {

    private Connection connection;

    public StockItemDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Retrieves a stock item by id.
     */
    public StockItem getStockItemById(String itemId) throws SQLException {
        String sql = "SELECT * FROM ipos_ca.stock_items WHERE item_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, itemId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStockItem(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves all stock items.
     */
    public List<StockItem> getAllStockItems() throws SQLException {
        List<StockItem> items = new ArrayList<>();
        String sql = "SELECT * FROM ipos_ca.stock_items";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                items.add(mapResultSetToStockItem(rs));
            }
        }

        return items;
    }

    /**
     * Searches stock items by name or description.
     */
    public List<StockItem> searchStockItems(String keyword) throws SQLException {
        List<StockItem> list = new ArrayList<>();

        String sql = "SELECT * FROM ipos_ca.stock_items " +
                "WHERE name LIKE ? OR description LIKE ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String pattern = "%" + keyword + "%";

            stmt.setString(1, pattern);
            stmt.setString(2, pattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToStockItem(rs));
                }
            }
        }

        return list;
    }

    /**
     * Checks if sufficient stock is available.
     */
    public boolean hasSufficientStock(String itemId, int requestedQty) throws SQLException {
        String sql = "SELECT quantity FROM ipos_ca.stock_items WHERE item_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, itemId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity") >= requestedQty;
                }
            }
        }

        return false;
    }

    /**
     * Deducts stock when an order is completed and payment is successful.
     */
    public boolean deductStock(String itemId, int quantityToDeduct) throws SQLException {
        String sql = "UPDATE ipos_ca.stock_items SET quantity = quantity - ? WHERE item_id = ? AND quantity >= ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, quantityToDeduct);
            stmt.setString(2, itemId);
            stmt.setInt(3, quantityToDeduct);

            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Maps ResultSet to StockItem object.
     */
    private StockItem mapResultSetToStockItem(ResultSet rs) throws SQLException {
        StockItem item = new StockItem();
        item.setItemId(rs.getString("item_id"));
        item.setName(rs.getString("name"));
        item.setDescription(rs.getString("description"));
        item.setPackageType(rs.getString("package_type"));
        item.setUnit(rs.getString("unit"));

        int unitsPerPackValue = rs.getInt("units_per_pack");
        if (rs.wasNull()) {
            item.setUnitsPerPack(null);
        } else {
            item.setUnitsPerPack(unitsPerPackValue);
        }

        item.setPrice(rs.getDouble("price"));
        item.setQuantity(rs.getInt("quantity"));
        item.setStockLimit(rs.getInt("stock_limit"));
        return item;
    }
}