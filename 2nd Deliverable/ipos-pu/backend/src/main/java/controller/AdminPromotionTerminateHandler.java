package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.PromotionCampaignDAO;
import db.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;

/**
 * POST /api/promotions/campaign/terminate
 * Body: { "campaignId": 123 }
 */
public class AdminPromotionTerminateHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String body = JsonBodyUtil.readBody(exchange.getRequestBody());
        Integer campaignId = JsonBodyUtil.extractInt(body, "campaignId");
        if (campaignId == null) {
            JsonResponseUtil.sendJson(exchange, 400, "{ \"error\": \"Missing campaignId\" }");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) throw new IllegalStateException("No DB connection");
            PromotionCampaignDAO dao = new PromotionCampaignDAO(conn);
            if (dao.getCampaignById(campaignId) == null) {
                JsonResponseUtil.sendJson(exchange, 404, "{ \"error\": \"Campaign not found\" }");
                return;
            }
            dao.updateCampaignStatus(campaignId, "TERMINATED");
            JsonResponseUtil.sendJson(exchange, 200, "{ \"status\": \"OK\" }");
        } catch (Exception e) {
            e.printStackTrace();
            JsonResponseUtil.sendJson(exchange, 500, "{ \"error\": \"Failed to terminate campaign\" }");
        }
    }
}

