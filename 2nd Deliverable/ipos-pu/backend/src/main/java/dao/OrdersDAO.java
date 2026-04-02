package dao;

import model.Orders;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for Orders table.
 */
public class OrdersDAO {

    private Connection connection;

    public OrdersDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Inserts a new order.
     */
    public void createOrder(Orders order) throws SQLException {
        String sql = "INSERT INTO Orders (customer_type, member_email, customer_email, delivery_address, total_amount, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, order.getCustomerType());

            if (order.getMemberEmail() != null) {
                stmt.setString(2, order.getMemberEmail());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }

            stmt.setString(3, order.getCustomerEmail());
            stmt.setString(4, order.getDeliveryAddress());
            stmt.setBigDecimal(5, order.getTotalAmount());
            stmt.setString(6, order.getStatus());
            stmt.executeUpdate();
        }
    }

    /**
     * Inserts a new order and returns the generated order ID.
     */
    public int createOrderAndReturnId(Orders order) throws SQLException {
        String sql = "INSERT INTO Orders (customer_type, member_email, customer_email, delivery_address, total_amount, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, order.getCustomerType());

            if (order.getMemberEmail() != null) {
                stmt.setString(2, order.getMemberEmail());
            } else {
                stmt.setNull(2, Types.VARCHAR);
            }

            stmt.setString(3, order.getCustomerEmail());
            stmt.setString(4, order.getDeliveryAddress());
            stmt.setBigDecimal(5, order.getTotalAmount());
            stmt.setString(6, order.getStatus());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return -1;
    }

    /**
     * Retrieves an order by ID.
     */
    public Orders getOrderById(int orderId) throws SQLException {
        String sql = "SELECT * FROM Orders WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToOrder(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves all orders for a contact email.
     */
    public List<Orders> getOrdersByCustomerEmail(String customerEmail) throws SQLException {
        List<Orders> orders = new ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE customer_email = ? ORDER BY order_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, customerEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        }

        return orders;
    }

    /**
     * Retrieves all orders for a member.
     */
    public List<Orders> getOrdersByMemberEmail(String memberEmail) throws SQLException {
        List<Orders> orders = new ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE member_email = ? ORDER BY order_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, memberEmail);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapResultSetToOrder(rs));
                }
            }
        }

        return orders;
    }

    /**
     * Retrieves all guest orders.
     */
    public List<Orders> getGuestOrders() throws SQLException {
        List<Orders> orders = new ArrayList<>();
        String sql = "SELECT * FROM Orders WHERE customer_type = 'GUEST' ORDER BY order_date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        }

        return orders;
    }

    /**
     * Retrieves all orders.
     */
    public List<Orders> getAllOrders() throws SQLException {
        List<Orders> orders = new ArrayList<>();
        String sql = "SELECT * FROM Orders ORDER BY order_date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                orders.add(mapResultSetToOrder(rs));
            }
        }

        return orders;
    }

    /**
     * Updates the status of an order.
     */
    public void updateOrderStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE Orders SET status = ? WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        }
    }

    /**
     * Updates the total amount of an order.
     */
    public void updateTotalAmount(int orderId, BigDecimal totalAmount) throws SQLException {
        String sql = "UPDATE Orders SET total_amount = ? WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, totalAmount);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes an order by ID.
     */
    public void deleteOrder(int orderId) throws SQLException {
        String sql = "DELETE FROM Orders WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps a ResultSet row to an Orders object.
     */
    private Orders mapResultSetToOrder(ResultSet rs) throws SQLException {
        Orders order = new Orders();
        order.setOrderId(rs.getInt("order_id"));
        order.setCustomerType(rs.getString("customer_type"));
        order.setMemberEmail(rs.getString("member_email"));
        order.setCustomerEmail(rs.getString("customer_email"));
        order.setOrderDate(rs.getTimestamp("order_date"));
        order.setDeliveryAddress(rs.getString("delivery_address"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        order.setStatus(rs.getString("status"));
        return order;
    }
}