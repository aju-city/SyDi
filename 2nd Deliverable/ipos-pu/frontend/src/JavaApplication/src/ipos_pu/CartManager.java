package ipos_pu;

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
    public static void addItem(String name, String category, double unitPrice, int qty, int stockLimit) {
        for (CartItem item : items) {
            if (item.name.equals(name)) {
                item.qty += qty;
                item.stockLimit = stockLimit; // refresh in case it changed
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
    }
}
