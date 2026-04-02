package dao;

import model.PaymentTransaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for PaymentTransaction table.
 */
public class PaymentTransactionDAO {

    private Connection connection;

    public PaymentTransactionDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Creates a new payment transaction.
     */
    public void createPayment(PaymentTransaction payment) throws SQLException {
        String sql = "INSERT INTO PaymentTransaction " +
                "(order_id, amount, payment_method, masked_card_number, payment_status, processor_reference, failure_reason) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, payment.getOrderId());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setString(3, payment.getPaymentMethod());
            stmt.setString(4, payment.getMaskedCardNumber());
            stmt.setString(5, payment.getPaymentStatus());
            stmt.setString(6, payment.getProcessorReference());
            stmt.setString(7, payment.getFailureReason());
            stmt.executeUpdate();
        }
    }

    /**
     * Creates a new payment transaction and returns the generated ID.
     */
    public int createPaymentAndReturnId(PaymentTransaction payment) throws SQLException {
        String sql = "INSERT INTO PaymentTransaction " +
                "(order_id, amount, payment_method, masked_card_number, payment_status, processor_reference, failure_reason) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, payment.getOrderId());
            stmt.setBigDecimal(2, payment.getAmount());
            stmt.setString(3, payment.getPaymentMethod());
            stmt.setString(4, payment.getMaskedCardNumber());
            stmt.setString(5, payment.getPaymentStatus());
            stmt.setString(6, payment.getProcessorReference());
            stmt.setString(7, payment.getFailureReason());

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
     * Retrieves a payment by ID.
     */
    public PaymentTransaction getPaymentById(int paymentId) throws SQLException {
        String sql = "SELECT * FROM PaymentTransaction WHERE payment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        }

        return null;
    }

    /**
     * Retrieves all payments for a given order.
     */
    public List<PaymentTransaction> getPaymentsByOrderId(int orderId) throws SQLException {
        List<PaymentTransaction> payments = new ArrayList<>();
        String sql = "SELECT * FROM PaymentTransaction WHERE order_id = ? ORDER BY payment_datetime DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
        }

        return payments;
    }

    /**
     * Retrieves all payment transactions.
     */
    public List<PaymentTransaction> getAllPayments() throws SQLException {
        List<PaymentTransaction> payments = new ArrayList<>();
        String sql = "SELECT * FROM PaymentTransaction ORDER BY payment_datetime DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
        }

        return payments;
    }

    /**
     * Updates payment status and failure reason.
     */
    public void updatePaymentStatus(int paymentId, String paymentStatus, String failureReason) throws SQLException {
        String sql = "UPDATE PaymentTransaction SET payment_status = ?, failure_reason = ? WHERE payment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, paymentStatus);
            stmt.setString(2, failureReason);
            stmt.setInt(3, paymentId);
            stmt.executeUpdate();
        }
    }

    /**
     * Updates processor reference for a payment.
     */
    public void updateProcessorReference(int paymentId, String processorReference) throws SQLException {
        String sql = "UPDATE PaymentTransaction SET processor_reference = ? WHERE payment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, processorReference);
            stmt.setInt(2, paymentId);
            stmt.executeUpdate();
        }
    }

    /**
     * Deletes a payment by ID.
     */
    public void deletePayment(int paymentId) throws SQLException {
        String sql = "DELETE FROM PaymentTransaction WHERE payment_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, paymentId);
            stmt.executeUpdate();
        }
    }

    /**
     * Maps a ResultSet row to a PaymentTransaction object.
     */
    private PaymentTransaction mapResultSetToPayment(ResultSet rs) throws SQLException {
        PaymentTransaction payment = new PaymentTransaction();
        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setOrderId(rs.getInt("order_id"));
        payment.setPaymentDatetime(rs.getTimestamp("payment_datetime"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setMaskedCardNumber(rs.getString("masked_card_number"));
        payment.setPaymentStatus(rs.getString("payment_status"));
        payment.setProcessorReference(rs.getString("processor_reference"));
        payment.setFailureReason(rs.getString("failure_reason"));
        return payment;
    }
}