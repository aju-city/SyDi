/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ipos_pu;

/**
 *
 * @author nuhur
 */
public class PromoManager {

    // holds all the info for a single promo like its name discount and what category it applies to
    public static class PromoConfig {
        public final String code, tag, name, desc;
        public final String discountCategory; // "All", "OTC", "Supplements", "Skincare"
        public final double discountRate;     // e.g. 0.80 = 20% off

        public PromoConfig(String code, String tag, String name, String desc,
                           String discountCategory, double discountRate) {
            this.code             = code;
            this.tag              = tag;
            this.name             = name;
            this.desc             = desc;
            this.discountCategory = discountCategory;
            this.discountRate     = discountRate;
        }
    }

    // max number of promos that can be active at once
    public static final int MAX_ENABLED = 3;

    // all the promos available in the system
    public static final PromoConfig[] ALL_PROMOS = {
        new PromoConfig("WINTER_SALE",     "WINTER SALE",
            "Winter Wellness Sale",
            "20% off all OTC medicines \u2014 stock up on health essentials this winter.",
            "OTC",         0.80),
        new PromoConfig("EASTER_SALE",     "EASTER",
            "Easter Health Sale",
            "15% off all supplements \u2014 treat yourself this Easter season.",
            "Supplements", 0.85),
        new PromoConfig("SUMMER_SALE",     "SUMMER SALE",
            "Summer Skincare Event",
            "20% off the full skincare range \u2014 get summer-ready.",
            "Skincare",    0.80),
        new PromoConfig("BLACK_FRIDAY",    "BLACK FRIDAY",
            "Black Friday Deals",
            "25% off everything in the store \u2014 our biggest sale of the year.",
            "All",         0.75),
        new PromoConfig("NEW_YEAR",        "NEW YEAR",
            "New Year New You",
            "10% off supplements \u2014 kick off the new year with a health boost.",
            "Supplements", 0.90),
        new PromoConfig("VALENTINES",      "VALENTINE\u2019S",
            "Valentine\u2019s Beauty Sale",
            "15% off all skincare \u2014 the perfect gift for someone special.",
            "Skincare",    0.85),
        new PromoConfig("BACK_TO_SCHOOL",  "BACK TO SCHOOL",
            "Back to School Health",
            "10% off OTC medicines \u2014 keep the family healthy this term.",
            "OTC",         0.90),
        new PromoConfig("SPRING_SALE",     "SPRING SALE",
            "Spring Refresh Sale",
            "15% off skincare \u2014 refresh your routine for the new season.",
            "Skincare",    0.85),
        new PromoConfig("CHRISTMAS",       "CHRISTMAS",
            "Christmas Wellness Sale",
            "20% off supplements \u2014 stock up on vitamins for the festive season.",
            "Supplements", 0.80),
        new PromoConfig("CLEARANCE",       "CLEARANCE",
            "End of Season Clearance",
            "25% off OTC products \u2014 end of season clearance on selected items.",
            "OTC",         0.75),
    };

    // tracks which promos are currently turned on
    private static final boolean[] enabled = new boolean[ALL_PROMOS.length];

    // first 3 are on by default when the app starts
    static {
        enabled[0] = true;
        enabled[1] = true;
        enabled[2] = true;
    }

    // finds a promo config by its code string
    public static PromoConfig getConfig(String code) {
        for (PromoConfig cfg : ALL_PROMOS)
            if (cfg.code.equals(code)) return cfg;
        return null;
    }

    // helper to get the array index for a given code
    private static int indexOf(String code) {
        for (int i = 0; i < ALL_PROMOS.length; i++)
            if (ALL_PROMOS[i].code.equals(code)) return i;
        return -1;
    }

    // checks if a specific promo is currently enabled
    public static boolean isEnabled(String code) {
        int i = indexOf(code);
        return i >= 0 && enabled[i];
    }

    // toggles a promo on or off, returns false if we already hit the max
    public static boolean setEnabled(String code, boolean on) {
        int i = indexOf(code);
        if (i < 0) return false;
        if (on && getEnabledCount() >= MAX_ENABLED) return false;
        enabled[i] = on;
        return true;
    }

    // counts how many promos are currently active
    public static int getEnabledCount() {
        int c = 0;
        for (boolean b : enabled) if (b) c++;
        return c;
    }
}
