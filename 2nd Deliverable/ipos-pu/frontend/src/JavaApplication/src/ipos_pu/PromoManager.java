package ipos_pu;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Manages promotional campaigns created by the admin.
 * Campaigns have per-item discount percentages instead of a flat category rate.
 *
 * @author nuhur
 */
public class PromoManager {

    public static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // a campaign the admin created — has its own name, dates, status and per-item discounts
    public static class Campaign {
        public String   id;
        public String   name;
        public LocalDate startDate;
        public LocalDate endDate;
        public String   status;   // "ACTIVE", "ENDED", "TERMINATED"
        // item name -> discount percentage, e.g. "Aspirin" -> 20.0 means 20% off
        public final LinkedHashMap<String, Double> itemDiscounts = new LinkedHashMap<>();
        public int hits      = 0;
        public int purchases = 0;

        public Campaign(String id, String name, LocalDate startDate, LocalDate endDate, String status) {
            this.id        = id;
            this.name      = name;
            this.startDate = startDate;
            this.endDate   = endDate;
            this.status    = status;
        }
    }

    private static final List<Campaign> campaigns = new ArrayList<>();
    private static boolean loadedOnce = false;

    // which campaign the current storefront user has applied (cart reads this on checkout)
    private static String userActiveCampaignId = null;

    public static void refreshFromBackend() {
        try {
            campaigns.clear();
            campaigns.addAll(PromotionApiClient.listAllCampaigns());
            // hydrate item discounts for richer UI display
            for (Campaign c : campaigns) {
                try {
                    c.itemDiscounts.clear();
                    c.itemDiscounts.putAll(PromotionApiClient.getCampaignItemDiscountsByName(c.id));
                } catch (Exception ignored) {}
            }
            loadedOnce = true;
        } catch (Exception ignored) {
            // if backend down, keep whatever is already in memory
        }
    }

    // returns a snapshot of all campaigns (active and inactive)
    public static List<Campaign> getCampaigns() {
        if (!loadedOnce) refreshFromBackend();
        return new ArrayList<>(campaigns);
    }

    // returns only campaigns with status ACTIVE
    public static List<Campaign> getActiveCampaigns() {
        try {
            List<Campaign> active = PromotionApiClient.listActiveCampaigns();
            // hydrate item discounts for cards/banner
            for (Campaign c : active) {
                try {
                    c.itemDiscounts.clear();
                    c.itemDiscounts.putAll(PromotionApiClient.getCampaignItemDiscountsByName(c.id));
                } catch (Exception ignored) {}
            }
            return active;
        } catch (Exception ignored) {
            // fallback to cached list
            List<Campaign> active = new ArrayList<>();
            for (Campaign c : getCampaigns())
                if ("ACTIVE".equals(c.status)) active.add(c);
            return active;
        }
    }

    // finds a campaign by id, returns null if not found
    public static Campaign getCampaign(String id) {
        if (id == null) return null;
        for (Campaign c : getCampaigns()) {
            if (c.id.equals(id)) {
                if (c.itemDiscounts.isEmpty()) {
                    try { c.itemDiscounts.putAll(PromotionApiClient.getCampaignItemDiscountsByName(c.id)); }
                    catch (Exception ignored) {}
                }
                return c;
            }
        }
        return null;
    }

    // AdminPage previously used in-memory add/remove. Now these call backend.
    public static void addCampaign(Campaign c) {
        try {
            String id = PromotionApiClient.createCampaign(c.name, c.startDate, c.endDate, "admin");
            if (id != null) c.id = id;
            // Note: campaign items are managed by AdminPage directly using productIds.
            refreshFromBackend();
        } catch (Exception ignored) {}
    }
    public static void removeCampaign(String id) {
        try {
            PromotionApiClient.deleteCampaign(id);
            refreshFromBackend();
        } catch (Exception ignored) {}
    }
    public static void terminateCampaign(String id) {
        try {
            PromotionApiClient.terminateCampaign(id);
            refreshFromBackend();
        } catch (Exception ignored) {}
    }
    public static void updateCampaign(Campaign c) {
        try {
            PromotionApiClient.updateCampaign(c.id, c.name, c.startDate, c.endDate);
            refreshFromBackend();
        } catch (Exception ignored) {}
    }

    // bump the hit counter when user opens this campaign on the storefront
    public static void recordHit(String id) {
        Campaign c = getCampaign(id);
        if (c != null) c.hits++;
    }

    // add to the purchase counter when an order is placed while this campaign is active
    public static void recordPurchase(String id, int qty) {
        Campaign c = getCampaign(id);
        if (c != null) c.purchases += qty;
    }

    // store which campaign the storefront user has applied (cart reads this at checkout)
    public static void setUserActivePromo(String id)  { userActiveCampaignId = id; }
    public static String getUserActivePromo()          { return userActiveCampaignId; }

    // returns the discount multiplier (0.0-1.0) for a specific item under a campaign
    // returns 1.0 (no discount) if the campaign doesnt have a discount for this item
    public static double getItemDiscountRate(String campaignId, String itemName) {
        Campaign c = getCampaign(campaignId);
        if (c == null || !"ACTIVE".equals(c.status)) return 1.0;
        Double pct = c.itemDiscounts.get(itemName);
        return pct != null ? 1.0 - pct / 100.0 : 1.0;
    }
}
