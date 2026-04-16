package ipos_pu;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal promotions API client using existing HttpClient.
 * Keeps parsing very lightweight (project-style, no external JSON libs).
 */
public final class PromotionApiClient {
    private PromotionApiClient() {}

    private static final String BASE = "http://localhost:8080";

    public static String createCampaign(String name, LocalDate startDate, LocalDate endDate, String createdBy) throws Exception {
        String start = toDateTimeStart(startDate);
        String end = toDateTimeEnd(endDate);
        if (createdBy == null || createdBy.trim().isEmpty()) createdBy = "admin";

        String body = "{"
                + "\"campaignName\":\"" + esc(name) + "\","
                + "\"start\":\"" + esc(start) + "\","
                + "\"end\":\"" + esc(end) + "\","
                + "\"createdBy\":\"" + esc(createdBy) + "\""
                + "}";

        String resp = HttpClient.postJson(BASE + "/api/promotions/campaign/create", body);
        String id = extractNumberField(resp, "campaignId");
        return id;
    }

    public static void updateCampaign(String campaignId, String name, LocalDate startDate, LocalDate endDate) throws Exception {
        String start = toDateTimeStart(startDate);
        String end = toDateTimeEnd(endDate);
        String body = "{"
                + "\"campaignId\":" + campaignId + ","
                + "\"campaignName\":\"" + esc(name) + "\","
                + "\"start\":\"" + esc(start) + "\","
                + "\"end\":\"" + esc(end) + "\""
                + "}";
        HttpClient.postJson(BASE + "/api/promotions/campaign/update", body);
    }

    public static void deleteCampaign(String campaignId) throws Exception {
        String body = "{ \"campaignId\": " + campaignId + " }";
        HttpClient.postJson(BASE + "/api/promotions/campaign/delete", body);
    }

    public static void terminateCampaign(String campaignId) throws Exception {
        String body = "{ \"campaignId\": " + campaignId + " }";
        HttpClient.postJson(BASE + "/api/promotions/campaign/terminate", body);
    }

    public static void upsertCampaignItem(String campaignId, String productId, double discountRate) throws Exception {
        String body = "{"
                + "\"campaignId\":" + campaignId + ","
                + "\"productId\":\"" + esc(productId) + "\","
                + "\"discountRate\":" + discountRate
                + "}";
        String resp = HttpClient.postJson(BASE + "/api/promotions/campaign/add-item", body);
        String err = extractStringField(resp, "error");
        if ("PROMO_CONFLICT".equals(err)) {
            String msg = extractStringField(resp, "message");
            throw new Exception(msg != null && !msg.isEmpty() ? msg : "Promotion conflict.");
        }
        if (err != null && !err.isEmpty()) {
            String msg = extractStringField(resp, "message");
            if (msg == null || msg.isEmpty()) msg = extractStringField(resp, "error");
            throw new Exception(msg != null && !msg.isEmpty() ? msg : "Failed to add campaign item.");
        }
    }

    public static List<PromoManager.Campaign> listAllCampaigns() throws Exception {
        String resp = HttpClient.get(BASE + "/api/promotions/campaign/all");
        return parseCampaigns(resp);
    }

    public static List<PromoManager.Campaign> listActiveCampaigns() throws Exception {
        String resp = HttpClient.get(BASE + "/api/promotions/active");
        return parseCampaigns(resp);
    }

    public static Map<String, Double> getCampaignItemDiscountsByName(String campaignId) throws Exception {
        // Uses public products endpoint which includes (name, discountRate)
        String resp = HttpClient.get(BASE + "/api/promotions/products?campaignId=" + campaignId);
        List<Map<String, String>> products = parseObjectArray(resp, "products");
        LinkedHashMap<String, Double> out = new LinkedHashMap<>();
        for (Map<String, String> p : products) {
            String name = p.get("name");
            String dr = p.get("discountRate");
            if (name == null || dr == null) continue;
            try { out.put(name, Double.parseDouble(dr)); } catch (NumberFormatException ignored) {}
        }
        return out;
    }

    public static Map<String, String> loadNameToProductId() throws Exception {
        // AdminPage already has a DB connection to ipos_ca, so prefer that.
        // This method exists for future use; currently unused.
        return new LinkedHashMap<>();
    }

    private static String toDateTimeStart(LocalDate d) {
        if (d == null) d = LocalDate.now();
        return d.toString() + " 00:00";
    }

    private static String toDateTimeEnd(LocalDate d) {
        if (d == null) d = LocalDate.now().plusDays(7);
        return d.toString() + " 23:59";
    }

    private static List<PromoManager.Campaign> parseCampaigns(String json) {
        List<Map<String, String>> objs = parseObjectArray(json, "campaigns");
        List<PromoManager.Campaign> out = new ArrayList<>();
        for (Map<String, String> m : objs) {
            String id = m.get("campaignId");
            String name = m.get("campaignName");
            String start = m.get("start");
            String end = m.get("end");
            String status = m.get("status");
            PromoManager.Campaign c = new PromoManager.Campaign(
                    id != null ? id : "",
                    name != null ? name : "",
                    parseSqlDate(start),
                    parseSqlDate(end),
                    status != null ? status : "SCHEDULED"
            );
            out.add(c);
        }
        return out;
    }

    private static LocalDate parseSqlDate(String ts) {
        // expected like: 2026-04-16 00:00:00.0
        if (ts == null) return null;
        String s = ts.trim();
        if (s.length() >= 10) {
            try { return LocalDate.parse(s.substring(0, 10)); } catch (Exception ignored) {}
        }
        return null;
    }

    private static List<Map<String, String>> parseObjectArray(String json, String arrayKey) {
        // Very small, tolerant parser for shapes like: { "campaigns": [ { ... }, { ... } ] }
        // Not a full JSON parser.
        List<Map<String, String>> out = new ArrayList<>();
        if (json == null) return out;
        int k = json.indexOf("\"" + arrayKey + "\"");
        if (k < 0) return out;
        int lb = json.indexOf('[', k);
        int rb = json.indexOf(']', lb);
        if (lb < 0 || rb < 0 || rb <= lb) return out;
        String arr = json.substring(lb + 1, rb).trim();
        if (arr.isEmpty()) return out;

        // split top-level objects by tracking braces
        int depth = 0;
        int start = -1;
        for (int i = 0; i < arr.length(); i++) {
            char ch = arr.charAt(i);
            if (ch == '{') {
                if (depth == 0) start = i;
                depth++;
            } else if (ch == '}') {
                depth--;
                if (depth == 0 && start >= 0) {
                    String obj = arr.substring(start, i + 1);
                    out.add(parseFlatObject(obj));
                    start = -1;
                }
            }
        }
        return out;
    }

    private static Map<String, String> parseFlatObject(String objJson) {
        LinkedHashMap<String, String> m = new LinkedHashMap<>();
        if (objJson == null) return m;
        String s = objJson.trim();
        if (s.startsWith("{")) s = s.substring(1);
        if (s.endsWith("}")) s = s.substring(0, s.length() - 1);

        // naive tokenization by commas, but respecting quoted strings
        List<String> parts = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inStr = false;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if (ch == '"' && (i == 0 || s.charAt(i - 1) != '\\')) inStr = !inStr;
            if (ch == ',' && !inStr) {
                parts.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(ch);
            }
        }
        if (cur.length() > 0) parts.add(cur.toString());

        for (String p : parts) {
            String[] kv = p.split(":", 2);
            if (kv.length != 2) continue;
            String key = unq(kv[0].trim());
            String val = kv[1].trim();
            m.put(key, unq(val));
        }
        return m;
    }

    private static String extractNumberField(String json, String key) {
        if (json == null) return null;
        String search = "\"" + key + "\"";
        int i = json.indexOf(search);
        if (i < 0) return null;
        int colon = json.indexOf(':', i);
        if (colon < 0) return null;
        int j = colon + 1;
        while (j < json.length() && Character.isWhitespace(json.charAt(j))) j++;
        int k = j;
        while (k < json.length() && (Character.isDigit(json.charAt(k)) || json.charAt(k) == '-')) k++;
        return json.substring(j, k).trim();
    }

    private static String extractStringField(String json, String key) {
        if (json == null) return null;
        String search = "\"" + key + "\"";
        int i = json.indexOf(search);
        if (i < 0) return null;
        int colon = json.indexOf(':', i);
        if (colon < 0) return null;
        int j = colon + 1;
        while (j < json.length() && Character.isWhitespace(json.charAt(j))) j++;
        if (j >= json.length()) return null;
        if (json.charAt(j) != '"') return null;
        int k = j + 1;
        while (k < json.length()) {
            char c = json.charAt(k);
            if (c == '"' && json.charAt(k - 1) != '\\') break;
            k++;
        }
        if (k >= json.length()) return null;
        return unq(json.substring(j, k + 1));
    }

    private static String unq(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1);
        }
        return s.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

