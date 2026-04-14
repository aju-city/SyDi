package ipos_pu;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Shared in-memory cart state for IPOS-PU.
 * Acts as the single source of truth between HomePage and CartPage
 * until a database is connected.
 *
 * @author nuhur
 */
public class CartManager {

    // guestToken for the guest cart
    public static String guestToken = null;

    public static class CartItem {
        public String name;
        public String category;
        public double unitPrice;
        public int qty;
        public int stockLimit; // per-customer order cap from DB


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
     * If the same product (by name) is already in the cart, its quantity is
     * incremented rather than a duplicate row being created.
     */
    public static void addItem(String itemId, int qty) throws IOException {
        URL url = new URL("http://localhost:8080/api/cart/add");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        System.out.println("DEBUG itemId sent = [" + itemId + "]");

        String json = "{"
                + "\"guestToken\":\"" + guestToken + "\","
                + "\"itemId\":\"" + itemId + "\","
                + "\"qty\":" + qty
                + "}";

        OutputStream os = conn.getOutputStream();
        os.write(json.getBytes());
        os.close();

        int status = conn.getResponseCode();
        if (status != 200) {
            InputStream errorStream = conn.getErrorStream();
            if (errorStream != null) {
                String error = new String(errorStream.readAllBytes());
                System.out.println("Backend error: " + error);
            }
            throw new IOException("Server returned status: " + status);
        }
    }

    public static String createGuestCartOnBackend() throws IOException {
        URL url = new URL("http://localhost:8080/api/cart/create-guest");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        int status = conn.getResponseCode();

        InputStream stream = (status == 200)
                ? conn.getInputStream()
                : conn.getErrorStream();

        String response = new String(stream.readAllBytes());
        System.out.println("Guest cart response: " + response);

        // Check for backend error
        if (status != 200 || response.contains("error")) {
            throw new IOException("Failed to create guest cart: " + response);
        }

        // Extract token properly
        String token = response
                .replace("{", "")
                .replace("}", "")
                .replace("\"", "")
                .split(":")[1]
                .trim();

        return token;
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
    }
}
