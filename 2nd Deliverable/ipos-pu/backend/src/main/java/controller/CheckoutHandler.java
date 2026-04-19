package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.CartDAO;
import dao.OrderItemsDAO;
import dao.OrdersDAO;
import dao.PaymentTransactionDAO;
import db.DatabaseConnection;
import model.OrderItems;
import model.Orders;
import model.PaymentTransaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

/**
 * Handles checkout requests by creating an order, saving order items,
 * recording payment details, logging the confirmation email, and clearing
 * the customer's cart.
 */
public class CheckoutHandler implements HttpHandler {

    /**
     * Processes POST checkout requests.
     *
     * @param exchange the HTTP exchange containing request and response data
     * @throws IOException if an I/O error occurs while handling the request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            String guestToken = extract(body, "guestToken");
            String memberEmail = extract(body, "memberEmail");
            String customerEmail = extract(body, "customerEmail");
            String deliveryAddress = extract(body, "deliveryAddress");
            String paymentMethod = extract(body, "paymentMethod");
            String maskedCardNumber = extract(body, "maskedCardNumber");
            String processorReference = extract(body, "processorReference");
            String amountStr = extract(body, "amount");

            String campaignIdStr = extract(body, "campaignId");
            Integer campaignId = null;

            if (campaignIdStr != null && !campaignIdStr.trim().isEmpty() && !"null".equalsIgnoreCase(campaignIdStr)) {
                try {
                    campaignId = Integer.parseInt(campaignIdStr);
                } catch (NumberFormatException ignored) {
                    campaignId = null;
                }
            }

            boolean isMember = memberEmail != null && !memberEmail.trim().isEmpty();
            String customerType = isMember ? "MEMBER" : "GUEST";

            if (customerEmail == null || customerEmail.trim().isEmpty()) {
                sendJson(exchange, 400, "{ \"error\": \"Missing customerEmail\" }");
                return;
            }

            if (!customerEmail.contains("@")) {
                sendJson(exchange, 400, "{ \"error\": \"Invalid email\" }");
                return;
            }

            if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
                sendJson(exchange, 400, "{ \"error\": \"Missing deliveryAddress\" }");
                return;
            }

            BigDecimal totalAmount = new BigDecimal(amountStr);

            List<CartDAO.CartItemView> cartItems = isMember
                    ? CartDAO.getCartItemsForMember(memberEmail)
                    : CartDAO.getCartItems(guestToken);

            if (cartItems == null || cartItems.isEmpty()) {
                sendJson(exchange, 400, "{ \"error\": \"Cart is empty\" }");
                return;
            }

            try (Connection conn = DatabaseConnection.getConnection()) {
                conn.setAutoCommit(false);

                try {
                    OrdersDAO ordersDAO = new OrdersDAO(conn);
                    OrderItemsDAO orderItemsDAO = new OrderItemsDAO(conn);
                    PaymentTransactionDAO paymentDAO = new PaymentTransactionDAO(conn);

                    Orders order = new Orders();
                    order.setCustomerType(customerType);
                    order.setMemberEmail(isMember ? memberEmail : null);
                    order.setCustomerEmail(customerEmail);
                    order.setDeliveryAddress(deliveryAddress);
                    order.setTotalAmount(totalAmount);
                    order.setStatus("Confirmed");

                    int orderId = ordersDAO.createOrderAndReturnId(order);

                    for (CartDAO.CartItemView item : cartItems) {
                        OrderItems oi = new OrderItems();
                        oi.setOrderId(orderId);
                        oi.setCampaignId(campaignId);
                        oi.setProductId(item.itemId);
                        oi.setProductDescription(item.name);
                        oi.setQuantity(item.qty);
                        oi.setUnitPrice(item.price);

                        double lineTotal = item.price * item.qty;
                        oi.setDiscountPercent(0.0);
                        oi.setLineTotal(lineTotal);

                        orderItemsDAO.createOrderItem(oi);
                    }

                    PaymentTransaction payment = new PaymentTransaction();
                    payment.setOrderId(orderId);
                    payment.setAmount(totalAmount);
                    payment.setPaymentMethod(
                            paymentMethod != null && !paymentMethod.trim().isEmpty() ? paymentMethod : "Other"
                    );
                    payment.setMaskedCardNumber(maskedCardNumber);
                    payment.setPaymentStatus("SUCCESS");
                    payment.setProcessorReference(
                            processorReference != null && !processorReference.trim().isEmpty()
                                    ? processorReference
                                    : ("PU-" + System.currentTimeMillis())
                    );
                    payment.setFailureReason(null);
                    payment.setPaymentDatetime(new Timestamp(System.currentTimeMillis()));
                    paymentDAO.createPayment(payment);

                    logOrderEmail(conn, orderId, customerEmail);

                    if (isMember) {
                        CartDAO.clearCartForMember(memberEmail);
                    } else {
                        CartDAO.clearCartForGuest(guestToken);
                    }

                    conn.commit();

                    ActivityLogger.log(
                            isMember ? memberEmail : null,
                            isMember ? null : guestToken,
                            null,
                            campaignId,
                            orderId,
                            "CHECKOUT"
                    );

                    ActivityLogger.log(
                            isMember ? memberEmail : null,
                            isMember ? null : guestToken,
                            null,
                            campaignId,
                            orderId,
                            "PURCHASE"
                    );

                    sendJson(exchange, 200,
                            "{ \"status\": \"OK\", \"orderId\": " + orderId + " }");

                } catch (Exception ex) {
                    conn.rollback();
                    throw ex;
                } finally {
                    conn.setAutoCommit(true);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{ \"error\": \"Checkout failed\" }");
        }
    }

    /**
     * Records a sent order confirmation email in the EmailLog table.
     *
     * @param conn the active database connection
     * @param orderId the order ID
     * @param email the recipient email address
     * @throws Exception if the insert fails
     */
    private void logOrderEmail(Connection conn, int orderId, String email) throws Exception {
        String sql = "INSERT INTO ipos_pu.EmailLog "
                + "(order_id, recipient_email, email_type, subject, body, sent_datetime, send_status) "
                + "VALUES (?, ?, 'ORDER_CONFIRMATION', ?, ?, NOW(), 'SENT')";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setString(2, email);
            ps.setString(3, "Order Confirmation");
            ps.setString(4, "Your order #" + orderId + " has been successfully placed.");
            ps.executeUpdate();

            System.out.println(
                    "EMAIL SENT for Order #"
                            + orderId
                            + " To: "
                            + email
                            + " At: "
                            + java.time.LocalDateTime.now()
            );
        }
    }

    /**
     * Extracts a value from a simple JSON string using the given key.
     *
     * @param json the JSON request body
     * @param key the key to search for
     * @return the extracted value, or null if not found
     */
    private String extract(String json, String key) {
        String search = "\"" + key + "\"";
        int start = json.indexOf(search);

        if (start == -1) {
            return null;
        }

        int colon = json.indexOf(":", start);
        if (colon == -1) {
            return null;
        }

        int valueStart = colon + 1;
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }

        if (valueStart >= json.length()) {
            return null;
        }

        if (json.charAt(valueStart) == '"') {
            int endQuote = json.indexOf("\"", valueStart + 1);
            if (endQuote == -1) {
                return null;
            }
            return json.substring(valueStart + 1, endQuote);
        }

        int end = valueStart;
        while (end < json.length()
                && json.charAt(end) != ','
                && json.charAt(end) != '}'
                && !Character.isWhitespace(json.charAt(end))) {
            end++;
        }

        return json.substring(valueStart, end);
    }

    /**
     * Sends a JSON response with the given HTTP status code.
     *
     * @param ex the HTTP exchange
     * @param status the HTTP status code
     * @param json the JSON response body
     * @throws IOException if an I/O error occurs while sending the response
     */
    private void sendJson(HttpExchange ex, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json");
        ex.sendResponseHeaders(status, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.getResponseBody().close();
    }
}