package ipos_pu;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared cart state for IPOS-PU.
 * Keeps an in-memory list for display and stores session cart details.
 *
 * @author nuhur
 */
public class CartManager {

    // Guest cart token returned by the backend.
    public static String guestToken = null;

    // Logged-in member email. Null when using a guest session.
    public static String memberEmail = null;

    public static Integer activeCampaignId = null;

    public static class CartItem {
        public String name;
        public String category;
        public double unitPrice;
        public int qty;
        public int stockLimit;

        public CartItem(String name, String category, double unitPrice, int qty, int stockLimit) {
            this.name = name;
            this.category = category;
            this.unitPrice = unitPrice;
            this.qty = qty;
            this.stockLimit = stockLimit;
        }
    }

    private static final List<CartItem> items = new ArrayList<>();

    /**
     * Adds a product to the in-memory cart.
     * If the item already exists, its quantity is increased.
     */
    public static void addItem(String name, String category, double unitPrice, int qty, int stockLimit) {
        for (CartItem item : items) {
            if (item.name.equals(name)) {
                item.qty += qty;
                item.stockLimit = stockLimit;
                return;
            }
        }
        items.add(new CartItem(name, category, unitPrice, qty, stockLimit));
    }

    public static List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    public static void clearItemsOnly() {
        items.clear();
    }

    public static int getTotalCount() {
        int count = 0;
        for (CartItem item : items) count += item.qty;
        return count;
    }

    /**
     * Updates the quantity of an item.
     * Removes the item if the new quantity is zero or less.
     */
    public static void setQty(String name, int newQty) {
        if (newQty <= 0) {
            removeItem(name);
            return;
        }
        for (CartItem item : items) {
            if (item.name.equals(name)) {
                item.qty = newQty;
                return;
            }
        }
    }

    public static void removeItem(String name) {
        items.removeIf(item -> item.name.equals(name));
    }

    /**
     * Clears the cart and resets the session details.
     */
    public static void clear() {
        items.clear();
        guestToken = null;
        memberEmail = null;
    }

    /**
     * Creates a guest cart on the backend and stores the returned token.
     */
    public static String createGuestCartOnBackend() throws IOException {
        URL url = new URL("http://localhost:8080/api/cart/guest");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        conn.getOutputStream().close();

        int status = conn.getResponseCode();
        if (status != 200 && status != 201) {
            throw new IOException("Guest cart creation failed: HTTP " + status);
        }

        String response = new String(conn.getInputStream().readAllBytes());
        System.out.println("Guest cart response: " + response);

        // Parse guestToken from JSON response.
        String token = null;
        int idx = response.indexOf("\"guestToken\"");
        if (idx >= 0) {
            int start = response.indexOf('"', idx + 12) + 1;
            int end = response.indexOf('"', start);
            if (start > 0 && end > start) {
                token = response.substring(start, end);
            }
        }

        if (token == null || token.isEmpty()) {
            throw new IOException("Could not parse guestToken from response: " + response);
        }

        guestToken = token;
        return token;
    }
}