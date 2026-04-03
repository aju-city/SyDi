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

    // which campaign the current storefront user has applied (cart reads this on checkout)
    private static String userActiveCampaignId = null;

    // returns a snapshot of all campaigns (active and inactive)
    public static List<Campaign> getCampaigns() {
        return new ArrayList<>(campaigns);
    }

    // returns only campaigns with status ACTIVE
    public static List<Campaign> getActiveCampaigns() {
        List<Campaign> active = new ArrayList<>();
        for (Campaign c : campaigns)
            if ("ACTIVE".equals(c.status)) active.add(c);
        return active;
    }

    // finds a campaign by id, returns null if not found
    public static Campaign getCampaign(String id) {
        if (id == null) return null;
        for (Campaign c : campaigns)
            if (c.id.equals(id)) return c;
        return null;
    }

    public static void addCampaign(Campaign c)    { campaigns.add(c); }
    public static void removeCampaign(String id)  { campaigns.removeIf(c -> c.id.equals(id)); }

    // generates a simple unique id using the current timestamp
    public static String generateId() { return "C_" + System.currentTimeMillis(); }

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
