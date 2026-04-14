package dao;

import db.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {

    public static void createGuestCart(String guestToken) throws Exception {
        String sql = "INSERT INTO ipos_pu.ShoppingCart (guest_token, customer_type) VALUES (?, 'GUEST')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, guestToken);
            stmt.executeUpdate();
        }
    }

    public static class CartItemView {
        public String itemId;
        public String name;
        public double price;
        public int qty;

        public CartItemView(String itemId, String name, double price, int qty) {
            this.itemId = itemId;
            this.name = name;
            this.price = price;
            this.qty = qty;
        }
    }

    public static void addOrUpdateCartItem(String guestToken, String itemId, int qty) throws Exception {

        String findCartId = "SELECT cart_id FROM ipos_pu.ShoppingCart WHERE guest_token = ?";
        String findExisting = "SELECT quantity FROM ipos_pu.ShoppingCartItems WHERE cart_id = ? AND product_id = ?";
        String stockSql = "SELECT quantity, stock_limit FROM ipos_ca.stock_items WHERE item_id = ?";
        String insert = "INSERT INTO ipos_pu.ShoppingCartItems (cart_id, product_id, quantity) VALUES (?, ?, ?)";
        String update = "UPDATE ipos_pu.ShoppingCartItems SET quantity = ? WHERE cart_id = ? AND product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            // 1. Get cart_id
            int cartId;
            try (PreparedStatement stmt = conn.prepareStatement(findCartId)) {
                stmt.setString(1, guestToken);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) throw new Exception("Cart not found");
                cartId = rs.getInt("cart_id");
            }

            // 2. Check existing quantity in cart
            Integer existingQty = null;
            try (PreparedStatement stmt = conn.prepareStatement(findExisting)) {
                stmt.setInt(1, cartId);
                stmt.setString(2, itemId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) existingQty = rs.getInt("quantity");
            }

            // 3. Fetch stock + stock_limit
            int available = 0;
            int limit = Integer.MAX_VALUE;

            try (PreparedStatement stmt = conn.prepareStatement(stockSql)) {
                stmt.setString(1, itemId);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) throw new Exception("Item not found in stock");
                available = rs.getInt("quantity");
                limit = rs.getInt("stock_limit");
            }

            // 4. Compute new quantity
            int newQty = (existingQty == null ? qty : existingQty + qty);

            // 5. Enforce per-order limit
            if (newQty > limit) {
                throw new Exception("Limit reached: max " + limit + " per order.");
            }

            // 6. Enforce available stock
            if (newQty > available) {
                throw new Exception("Only " + available + " units available.");
            }

            // 7. Insert or update
            if (existingQty == null) {
                try (PreparedStatement stmt = conn.prepareStatement(insert)) {
                    stmt.setInt(1, cartId);
                    stmt.setString(2, itemId);
                    stmt.setInt(3, qty);
                    stmt.executeUpdate();
                }
            } else {
                try (PreparedStatement stmt = conn.prepareStatement(update)) {
                    stmt.setInt(1, newQty);
                    stmt.setInt(2, cartId);
                    stmt.setString(3, itemId);
                    stmt.executeUpdate();
                }
            }
        }
    }

    public static List<CartItemView> getCartItems(String guestToken) throws Exception {
        List<CartItemView> list = new ArrayList<>();

        String sql =
                "SELECT si.item_id, si.name, si.price, sci.quantity " +
                        "FROM ipos_pu.ShoppingCartItems sci " +
                        "JOIN ipos_ca.stock_items si ON sci.product_id = si.item_id " +
                        "JOIN ipos_pu.ShoppingCart sc ON sc.cart_id = sci.cart_id " +
                        "WHERE sc.guest_token = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, guestToken);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new CartItemView(
                        rs.getString("item_id"),   // FIXED
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                ));
            }
        }

        return list;
    }
}