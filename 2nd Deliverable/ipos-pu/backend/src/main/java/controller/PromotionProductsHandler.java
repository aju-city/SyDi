package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.PromotionCampaignDAO;
import dao.PromotionCampaignItemsDAO;
import db.DatabaseConnection;

import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

/**
 * GET /api/promotions/products?campaignId=123
 * Returns campaign products with base price + per-item discountRate.
 */
public class PromotionProductsHandler implements HttpHandler {
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

            PromotionCampaignDAO campDao = new PromotionCampaignDAO(conn);
            var c = campDao.getCampaignById(campaignId);
            if (c == null) {
                JsonResponseUtil.sendJson(exchange, 404, "{ \"error\": \"Campaign not found\" }");
                return;
            }

            PromotionCampaignItemsDAO itemsDao = new PromotionCampaignItemsDAO(conn);
            List<Map<String, Object>> products = itemsDao.getCampaignProductsWithDiscount(campaignId);
            JsonResponseUtil.sendJson(exchange, 200, toJson(products));
        } catch (Exception e) {
            e.printStackTrace();
            JsonResponseUtil.sendJson(exchange, 500, "{ \"error\": \"Failed to load campaign products\" }");
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

    private String toJson(List<Map<String, Object>> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"products\": [");
        for (int i = 0; i < products.size(); i++) {
            Map<String, Object> p = products.get(i);
            sb.append("{")
                    .append("\"productId\":\"").append(escape(String.valueOf(p.get("productId")))).append("\",")
                    .append("\"name\":\"").append(escape(String.valueOf(p.get("name")))).append("\",")
                    .append("\"description\":\"").append(escape(String.valueOf(p.get("description")))).append("\",")
                    .append("\"price\":").append(String.valueOf(p.get("price"))).append(",")
                    .append("\"discountRate\":").append(String.valueOf(p.get("discountRate")))
                    .append("}");
            if (i < products.size() - 1) sb.append(",");
        }
        sb.append("] }");
        return sb.toString();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

