package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.PromotionCampaignDAO;
import dao.PromotionCampaignItemsDAO;
import dao.StockItemDAO;
import db.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Timestamp;

/**
 * POST /api/promotions/campaign/add-item
 * Body: { "campaignId": 123, "productId": "10000001", "discountRate": 15.0 }
 */
public class AdminPromotionAddItemHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String body = JsonBodyUtil.readBody(exchange.getRequestBody());
        Integer campaignId = JsonBodyUtil.extractInt(body, "campaignId");
        String productId = JsonBodyUtil.extractString(body, "productId");
        Double discountRate = JsonBodyUtil.extractDouble(body, "discountRate");

        if (campaignId == null) {
            JsonResponseUtil.sendJson(exchange, 400, "{ \"error\": \"Missing campaignId\" }");
            return;
        }
        if (productId == null || productId.trim().isEmpty()) {
            JsonResponseUtil.sendJson(exchange, 400, "{ \"error\": \"Missing productId\" }");
            return;
        }
        if (discountRate == null) {
            JsonResponseUtil.sendJson(exchange, 400, "{ \"error\": \"Missing discountRate\" }");
            return;
        }
        if (discountRate < 0 || discountRate > 100) {
            JsonResponseUtil.sendJson(exchange, 400, "{ \"error\": \"discountRate must be between 0 and 100\" }");
            return;
        }

        try {
            if (!StockItemDAO.itemExists(productId.trim())) {
                JsonResponseUtil.sendJson(exchange, 400, "{ \"error\": \"Unknown productId\" }");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsonResponseUtil.sendJson(exchange, 500, "{ \"error\": \"Failed to validate product\" }");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) throw new IllegalStateException("No DB connection");

            PromotionCampaignDAO campDao = new PromotionCampaignDAO(conn);
            if (campDao.getCampaignById(campaignId) == null) {
                JsonResponseUtil.sendJson(exchange, 404, "{ \"error\": \"Campaign not found\" }");
                return;
            }

            // If campaign is currently in-window, mark it ACTIVE (best effort).
            Timestamp now = new Timestamp(System.currentTimeMillis());
            var camp = campDao.getCampaignById(campaignId);
            if (camp != null && camp.getStartDatetime() != null && camp.getEndDatetime() != null) {
                if (now.after(camp.getStartDatetime()) && now.before(camp.getEndDatetime())
                        && !"TERMINATED".equals(camp.getStatus())) {
                    campDao.updateCampaignStatus(campaignId, "ACTIVE");
                }
            }

            PromotionCampaignItemsDAO itemsDao = new PromotionCampaignItemsDAO(conn);
            itemsDao.upsertCampaignItem(campaignId, productId.trim(), discountRate);

            JsonResponseUtil.sendJson(exchange, 200, "{ \"status\": \"OK\" }");
        } catch (Exception e) {
            e.printStackTrace();
            JsonResponseUtil.sendJson(exchange, 500, "{ \"error\": \"Failed to add campaign item\" }");
        }
    }
}

