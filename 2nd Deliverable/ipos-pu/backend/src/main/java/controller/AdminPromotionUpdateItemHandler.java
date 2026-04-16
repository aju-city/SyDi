package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.PromotionCampaignItemsDAO;
import db.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;

/**
 * POST /api/promotions/campaign/update-item
 * Body: { "campaignItemId": 456, "discountRate": 20.0 }
 */
public class AdminPromotionUpdateItemHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String body = JsonBodyUtil.readBody(exchange.getRequestBody());
        Integer campaignItemId = JsonBodyUtil.extractInt(body, "campaignItemId");
        Double discountRate = JsonBodyUtil.extractDouble(body, "discountRate");

        if (campaignItemId == null) {
            JsonResponseUtil.sendJson(exchange, 400, "{ \"error\": \"Missing campaignItemId\" }");
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

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) throw new IllegalStateException("No DB connection");
            PromotionCampaignItemsDAO dao = new PromotionCampaignItemsDAO(conn);
            if (dao.getCampaignItemById(campaignItemId) == null) {
                JsonResponseUtil.sendJson(exchange, 404, "{ \"error\": \"Campaign item not found\" }");
                return;
            }
            dao.updateDiscountRate(campaignItemId, discountRate);
            JsonResponseUtil.sendJson(exchange, 200, "{ \"status\": \"OK\" }");
        } catch (Exception e) {
            e.printStackTrace();
            JsonResponseUtil.sendJson(exchange, 500, "{ \"error\": \"Failed to update campaign item\" }");
        }
    }
}

