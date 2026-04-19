package dao;

import db.DatabaseConnection;
import model.ShoppingCart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access class for shopping cart operations.
 */
public class CartDAO {


    /**
     * Creates a new guest cart.
     *
     * @param guestToken the guest cart token
     * @throws Exception if the insert fails
     */
    public static void createGuestCart(String guestToken) throws Exception {
        String sql = "INSERT INTO ipos_pu.ShoppingCart (guest_token, customer_type) VALUES (?, 'GUEST')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, guestToken);
            stmt.executeUpdate();
        }
    }

    /**
     * Creates a new member cart.
     *
     * @param memberEmail the member email
     * @throws Exception if the insert fails
     */
    public static void createMemberCart(String memberEmail) throws Exception {
        String sql = "INSERT INTO ipos_pu.ShoppingCart (member_email, customer_type) VALUES (?, 'MEMBER')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberEmail);
            stmt.executeUpdate();
        }
    }



    /**
     * Simple view model used when returning cart items.
     */
    public static class CartItemView {
        public String itemId;
        public String name;
        public double price;
        public int qty;

        /**
         * Creates a cart item view.
         *
         * @param itemId the product ID
         * @param name the product name
         * @param price the product price
         * @param qty the quantity in the cart
         */
        public CartItemView(String itemId, String name, double price, int qty) {
            this.itemId = itemId;
            this.name = name;
            this.price = price;
            this.qty = qty;
        }
    }


    /**
     * Finds a cart ID using a guest token.
     *
     * @param conn the active database connection
     * @param guestToken the guest token
     * @return the cart ID, or null if not found
     * @throws Exception if the query fails
     */
    private static Integer findCartIdByGuestToken(Connection conn, String guestToken) throws Exception {
        String sql = "SELECT cart_id FROM ipos_pu.ShoppingCart WHERE guest_token = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, guestToken);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("cart_id");
            return null;
        }
    }

    /**
     * Finds a cart ID using a member email.
     *
     * @param conn the active database connection
     * @param memberEmail the member email
     * @return the cart ID, or null if not found
     * @throws Exception if the query fails
     */
    private static Integer findCartIdByMemberEmail(Connection conn, String memberEmail) throws Exception {
        String sql = "SELECT cart_id FROM ipos_pu.ShoppingCart WHERE member_email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberEmail);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("cart_id");
            return null;
        }
    }


    /**
     * Adds a new item to a guest cart or updates its quantity if it already exists.
     *
     * @param guestToken the guest token
     * @param itemId the product ID
     * @param qty the quantity to add
     * @throws Exception if the update fails or stock rules are violated
     */
    public static void addOrUpdateCartItem(String guestToken, String itemId, int qty) throws Exception {

        String findExisting = "SELECT quantity FROM ipos_pu.ShoppingCartItems WHERE cart_id = ? AND product_id = ?";
        String stockSql = "SELECT quantity, stock_limit FROM ipos_ca.stock_items WHERE item_id = ?";
        String insert = "INSERT INTO ipos_pu.ShoppingCartItems (cart_id, product_id, quantity) VALUES (?, ?, ?)";
        String update = "UPDATE ipos_pu.ShoppingCartItems SET quantity = ? WHERE cart_id = ? AND product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            Integer cartId = findCartIdByGuestToken(conn, guestToken);
            if (cartId == null) {
                createGuestCart(guestToken);
                cartId = findCartIdByGuestToken(conn, guestToken);
            }

            if (cartId == null) {
                throw new Exception("Cart not found");
            }

            Integer existingQty = null;
            try (PreparedStatement stmt = conn.prepareStatement(findExisting)) {
                stmt.setInt(1, cartId);
                stmt.setString(2, itemId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) existingQty = rs.getInt("quantity");
            }

            int available = 0;
            int limit = Integer.MAX_VALUE;

            try (PreparedStatement stmt = conn.prepareStatement(stockSql)) {
                stmt.setString(1, itemId);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) throw new Exception("Item not found in stock");
                available = rs.getInt("quantity");
                limit = rs.getInt("stock_limit");
            }

            int newQty = (existingQty == null ? qty : existingQty + qty);

            if (existingQty != null && newQty <= 0) {
                String delete = "DELETE FROM ipos_pu.ShoppingCartItems WHERE cart_id = ? AND product_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(delete)) {
                    stmt.setInt(1, cartId);
                    stmt.setString(2, itemId);
                    stmt.executeUpdate();
                }
                return;
            }

            if (newQty > limit) throw new Exception("Limit reached: max " + limit + " per order.");
            if (newQty > available) throw new Exception("Only " + available + " units available.");

            if (existingQty == null) {
                if (qty <= 0) return;
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



    /**
     * Adds a new item to a member cart or updates its quantity if it already exists.
     *
     * @param memberEmail the member email
     * @param itemId the product ID
     * @param qty the quantity to add
     * @throws Exception if the update fails or stock rules are violated
     */
    public static void addOrUpdateCartItemForMember(String memberEmail, String itemId, int qty) throws Exception {

        String findExisting = "SELECT quantity FROM ipos_pu.ShoppingCartItems WHERE cart_id = ? AND product_id = ?";
        String stockSql = "SELECT quantity, stock_limit FROM ipos_ca.stock_items WHERE item_id = ?";
        String insert = "INSERT INTO ipos_pu.ShoppingCartItems (cart_id, product_id, quantity) VALUES (?, ?, ?)";
        String update = "UPDATE ipos_pu.ShoppingCartItems SET quantity = ? WHERE cart_id = ? AND product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {

            Integer cartId = findCartIdByMemberEmail(conn, memberEmail);
            if (cartId == null) {
                createMemberCart(memberEmail);
                cartId = findCartIdByMemberEmail(conn, memberEmail);
            }

            Integer existingQty = null;
            try (PreparedStatement stmt = conn.prepareStatement(findExisting)) {
                stmt.setInt(1, cartId);
                stmt.setString(2, itemId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) existingQty = rs.getInt("quantity");
            }

            int available = 0;
            int limit = Integer.MAX_VALUE;

            try (PreparedStatement stmt = conn.prepareStatement(stockSql)) {
                stmt.setString(1, itemId);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) throw new Exception("Item not found in stock");
                available = rs.getInt("quantity");
                limit = rs.getInt("stock_limit");
            }

            int newQty = (existingQty == null ? qty : existingQty + qty);

            if (existingQty != null && newQty <= 0) {
                String delete = "DELETE FROM ipos_pu.ShoppingCartItems WHERE cart_id = ? AND product_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(delete)) {
                    stmt.setInt(1, cartId);
                    stmt.setString(2, itemId);
                    stmt.executeUpdate();
                }
                return;
            }

            if (newQty > limit) throw new Exception("Limit reached: max " + limit + " per order.");
            if (newQty > available) throw new Exception("Only " + available + " units available.");

            if (existingQty == null) {
                if (qty <= 0) return;
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



    /**
     * Returns all cart items for a guest cart.
     *
     * @param guestToken the guest token
     * @return list of cart item views
     * @throws Exception if the query fails
     */
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
                        rs.getString("item_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                ));
            }
        }

        return list;
    }

    /**
     * Returns all cart items for a member cart.
     *
     * @param memberEmail the member email
     * @return list of cart item views
     * @throws Exception if the query fails
     */
    public static List<CartItemView> getCartItemsForMember(String memberEmail) throws Exception {
        List<CartItemView> list = new ArrayList<>();

        String sql =
                "SELECT si.item_id, si.name, si.price, sci.quantity " +
                        "FROM ipos_pu.ShoppingCartItems sci " +
                        "JOIN ipos_ca.stock_items si ON sci.product_id = si.item_id " +
                        "JOIN ipos_pu.ShoppingCart sc ON sc.cart_id = sci.cart_id " +
                        "WHERE sc.member_email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, memberEmail);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new CartItemView(
                        rs.getString("item_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                ));
            }
        }

        return list;
    }


    /**
     * Removes an item from a guest cart.
     *
     * @param guestToken the guest token
     * @param itemId the product ID
     * @throws Exception if the delete fails
     */
    public static void removeItem(String guestToken, String itemId) throws Exception {
        String findCartId = "SELECT cart_id FROM ipos_pu.ShoppingCart WHERE guest_token = ?";
        String delete = "DELETE FROM ipos_pu.ShoppingCartItems WHERE cart_id = ? AND product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            Integer cartId = null;
            try (PreparedStatement stmt = conn.prepareStatement(findCartId)) {
                stmt.setString(1, guestToken);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) cartId = rs.getInt("cart_id");
            }

            if (cartId == null) return;

            try (PreparedStatement stmt = conn.prepareStatement(delete)) {
                stmt.setInt(1, cartId);
                stmt.setString(2, itemId);
                stmt.executeUpdate();
            }
        }
    }

    /**
     * Clears all items from a member cart.
     *
     * @param email the member email
     * @throws Exception if the delete fails
     */
    public static void clearCartForMember(String email) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            Integer cartId = findCartIdByMemberEmail(conn, email);
            if (cartId != null) {
                deleteCartItems(conn, cartId);
            }
        }
    }

    /**
     * Clears all items from a guest cart.
     *
     * @param token the guest token
     * @throws Exception if the delete fails
     */
    public static void clearCartForGuest(String token) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            Integer cartId = findCartIdByGuestToken(conn, token);
            if (cartId != null) {
                deleteCartItems(conn, cartId);
            }
        }
    }

    /**
     * Deletes all items for the given cart ID.
     *
     * @param conn the active database connection
     * @param cartId the cart ID
     * @throws SQLException if the delete fails
     */
    private static void deleteCartItems(Connection conn, int cartId) throws SQLException {
        String sql = "DELETE FROM ShoppingCartItems WHERE cart_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.executeUpdate();
        }
    }

    /**
     * Removes an item from a member cart.
     *
     * @param memberEmail the member email
     * @param itemId the product ID
     * @throws Exception if the delete fails
     */
    public static void removeItemForMember(String memberEmail, String itemId) throws Exception {
        String findCartId = "SELECT cart_id FROM ipos_pu.ShoppingCart WHERE member_email = ?";
        String delete = "DELETE FROM ipos_pu.ShoppingCartItems WHERE cart_id = ? AND product_id = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            Integer cartId = null;
            try (PreparedStatement stmt = conn.prepareStatement(findCartId)) {
                stmt.setString(1, memberEmail);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) cartId = rs.getInt("cart_id");
            }

            if (cartId == null) return;

            try (PreparedStatement stmt = conn.prepareStatement(delete)) {
                stmt.setInt(1, cartId);
                stmt.setString(2, itemId);
                stmt.executeUpdate();
            }
        }
    }
}