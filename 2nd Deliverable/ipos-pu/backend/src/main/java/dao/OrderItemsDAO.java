package dao;

import model.OrderItems;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for OrderItems table.
 */
public class OrderItemsDAO {

    private Connection connection;

    public OrderItemsDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Creates a new order item.
     */
    public void createOrderItem(OrderItems item) throws SQLException {
        String sql = "INSERT INTO OrderItems (order_id, campaign_id, product_id, product_description, quantity, unit_price, discount_percent, line_total) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getOrderId());

            if (item.getCampaignId() != null) {
                stmt.setInt(2, item.getCampaignId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }

            stmt.setString(3, item.getProductId());
            stmt.setString(4, item.getProductDescription());
            stmt.setInt(5, item.getQuantity());
            stmt.setDouble(6, item.getUnitPrice());
            stmt.setDouble(7, item.getDiscountPercent());
            stmt.setDouble(8, item.getLineTotal());
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves an order item by id.
     */
    public OrderItems getOrderItemById(int orderItemId) throws SQLException {
        String sql = "SELECT * FROM OrderItems WHERE order_item_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderItemId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrderItem(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves all items for an order.
     */
    public List<OrderItems> getItemsByOrderId(int orderId) throws SQLException {
        List<OrderItems> items = new ArrayList<>();
        String sql = "SELECT * FROM OrderItems WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapResultSetToOrderItem(rs));
                }
            }
        }

        return items;
    }

    /**
     * Deletes an order item.
     */
    public void deleteOrderItem(int orderItemId) throws SQLException {
        String sql = "DELETE FROM OrderItems WHERE order_item_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderItemId);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps ResultSet to OrderItems object.
     */
    private OrderItems mapResultSetToOrderItem(ResultSet rs) throws SQLException {
        OrderItems item = new OrderItems();
        item.setOrderItemId(rs.getInt("order_item_id"));
        item.setOrderId(rs.getInt("order_id"));

        int campaignIdValue = rs.getInt("campaign_id");
        if (rs.wasNull()) {
            item.setCampaignId(null);
        } else {
            item.setCampaignId(campaignIdValue);
        }

        item.setProductId(rs.getString("product_id"));
        item.setProductDescription(rs.getString("product_description"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getDouble("unit_price"));
        item.setDiscountPercent(rs.getDouble("discount_percent"));
        item.setLineTotal(rs.getDouble("line_total"));
        return item;
    }
}