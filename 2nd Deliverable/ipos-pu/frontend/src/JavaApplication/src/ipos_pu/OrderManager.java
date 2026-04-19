/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package ipos_pu;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author nuhur
 */
public class OrderManager {

    public static class Order {
        public final String orderId;
        public final String date;
        public final String itemsSummary;
        public final String total;
        public final List<CartManager.CartItem> items;
        public int stage; // 0 = Received, 1 = Dispatched, 2 = Delivered

        public Order(String orderId, String date, String itemsSummary,
                     String total, List<CartManager.CartItem> items) {
            this.orderId      = orderId;
            this.date         = date;
            this.itemsSummary = itemsSummary;
            this.total        = total;
            this.items        = new ArrayList<>(items);
            this.stage        = 0;
        }
    }

    private static final List<Order> orders  = new ArrayList<>();
    private static int               counter = 1;

    public static void placeOrder(List<CartManager.CartItem> items, double total) {
        String id   = String.format("#ORD-%04d", counter++);
        String date = new java.text.SimpleDateFormat("dd/MM/yyyy").format(new java.util.Date());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            CartManager.CartItem it = items.get(i);
            sb.append(it.name).append(" \u00d7").append(it.qty);
            if (i < items.size() - 1) sb.append(",  ");
        }

        orders.add(0, new Order(id, date, sb.toString(),
                String.format("\u00a3%.2f", total), items));
    }

    public static List<Order> getOrders() {
        return new ArrayList<>(orders);
    }

    // true if the next order placed will be a 10th milestone so the loyalty discount applies
    public static boolean isLoyaltyOrder(String memberEmail) {
        try (java.sql.Connection conn = db.DatabaseConnection.getConnection()) {

            String sql = "SELECT COUNT(*) FROM Orders WHERE member_email = ?";
            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, memberEmail);

            java.sql.ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count % 10 == 9;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // total number of orders placed so far, useful for the reports
    public static int getOrderCount() {
        return orders.size();
    }

    public static void clear() {
        orders.clear();
        counter = 1;
    }
}
