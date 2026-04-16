package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.PromotionCampaignItemsDAO;
import db.DatabaseConnection;
import model.PromotionCampaignItems;

import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.util.List;

/**
 * GET /api/promotions/campaign/items?campaignId=123
 */
public class AdminPromotionListItemsHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        Integer campaignId = getIntQueryParam(exchange.getRequestURI(), "campaignId");
        if (campaignId == null) {
            JsonResponseUtil.sendJson(exchange, 400, "{ \"error\": \"Missing campaignId\" }");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) throw new IllegalStateException("No DB connection");
            PromotionCampaignItemsDAO dao = new PromotionCampaignItemsDAO(conn);
            List<PromotionCampaignItems> items = dao.getItemsByCampaignId(campaignId);
            JsonResponseUtil.sendJson(exchange, 200, toJson(items));
        } catch (Exception e) {
            e.printStackTrace();
            JsonResponseUtil.sendJson(exchange, 500, "{ \"error\": \"Failed to list campaign items\" }");
        }
    }

    private Integer getIntQueryParam(URI uri, String key) {
        String q = uri.getQuery();
        if (q == null) return null;
        for (String part : q.split("&")) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2 && key.equals(kv[0])) {
                try { return Integer.parseInt(kv[1]); } catch (Exception ignored) { return null; }
            }
        }
        return null;
    }

    private String toJson(List<PromotionCampaignItems> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"items\": [");
        for (int i = 0; i < items.size(); i++) {
            PromotionCampaignItems it = items.get(i);
            sb.append("{")
                    .append("\"campaignItemId\":").append(it.getCampaignItemId()).append(",")
                    .append("\"campaignId\":").append(it.getCampaignId()).append(",")
                    .append("\"productId\":\"").append(escape(it.getProductId())).append("\",")
                    .append("\"discountRate\":").append(it.getDiscountRate())
                    .append("}");
            if (i < items.size() - 1) sb.append(",");
        }
        sb.append("] }");
        return sb.toString();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

