package ipos_pu;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Shared cart state for IPOS-PU.
 * Keeps an in-memory list for display (CartPage) and syncs additions
 * to the backend API when available.
 *
 * @author nuhur
 */
public class CartManager {

    // guest cart token returned by the backend (null if member session)
    public static String guestToken = null;

    // logged-in member email (null if guest session)
    public static String memberEmail = null;

    public static class CartItem {
        public String name;
        public String category;
        public double unitPrice;
        public int qty;
        public int stockLimit;

        public CartItem(String name, String category, double unitPrice, int qty, int stockLimit) {
            this.name       = name;
            this.category   = category;
            this.unitPrice  = unitPrice;
            this.qty        = qty;
            this.stockLimit = stockLimit;
        }
    }

    private static final List<CartItem> items = new ArrayList<>();

    /**
     * Add a product to the cart.
     * Updates the local in-memory list and best-effort syncs to the backend.
     * If the same product is already in the cart its quantity is incremented.
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

    /** Returns a snapshot of all current cart items. */
    public static List<CartItem> getItems() {
        return new ArrayList<>(items);
    }

    /** Total number of individual units across all cart lines. */
    public static int getTotalCount() {
        int count = 0;
        for (CartItem item : items) count += item.qty;
        return count;
    }

    /** Update the quantity of an existing item. If newQty <= 0 the item is removed. */
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

    /** Remove an item from the cart by name. */
    public static void removeItem(String name) {
        items.removeIf(item -> item.name.equals(name));
    }

    /** Empties the cart (call after a successful order). */
    public static void clear() {
        items.clear();
        guestToken = null;
        memberEmail = null;
    }

    /**
     * Creates a guest cart session on the backend and stores the returned token
     * in CartManager.guestToken.
     */
    public static String createGuestCartOnBackend() throws IOException {
        URL url = new URL("http://localhost:8080/api/cart/guest");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(3000);
        conn.setReadTimeout(3000);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        // empty body — server generates the token
        conn.getOutputStream().close();

        int status = conn.getResponseCode();
        if (status != 200 && status != 201) {
            throw new IOException("Guest cart creation failed: HTTP " + status);
        }

        String response = new String(conn.getInputStream().readAllBytes());
        System.out.println("Guest cart response: " + response);

        // parse "guestToken" from JSON: { "guestToken": "GT-xxxxx" }
        String token = null;
        int idx = response.indexOf("\"guestToken\"");
        if (idx >= 0) {
            int start = response.indexOf('"', idx + 12) + 1;
            int end   = response.indexOf('"', start);
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
