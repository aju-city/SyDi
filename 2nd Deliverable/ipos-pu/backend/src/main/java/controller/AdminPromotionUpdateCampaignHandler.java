package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.PromotionCampaignDAO;
import db.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Timestamp;

/**
 * POST /api/promotions/campaign/update
 * Body: { "campaignId": 123, "campaignName": "...", "start": "yyyy-MM-dd HH:mm", "end": "yyyy-MM-dd HH:mm" }
 */
public class AdminPromotionUpdateCampaignHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String body = JsonBodyUtil.readBody(exchange.getRequestBody());
        Integer campaignId = JsonBodyUtil.extractInt(body, "campaignId");
        String name = JsonBodyUtil.extractString(body, "campaignName");
        String startStr = JsonBodyUtil.extractString(body, "start");
        String endStr = JsonBodyUtil.extractString(body, "end");

        if (campaignId == null) {
            JsonResponseUtil.sendJson(exchange, 400, "{ \"error\": \"Missing campaignId\" }");
            return;
        }
        if (name == null || name.trim().isEmpty()) {
            JsonResponseUtil.sendJson(exchange, 400, "{ \"error\": \"Missing campaignName\" }");
            return;
        }
        Timestamp start = PromotionDateTimeUtil.parseTimestamp(startStr);
        Timestamp end = PromotionDateTimeUtil.parseTimestamp(endStr);
        if (start == null || end == null) {
            JsonResponseUtil.sendJson(exchange, 400, "{ \"error\": \"Invalid start/end format (expected yyyy-MM-dd HH:mm)\" }");
            return;
        }
        if (!start.before(end)) {
            JsonResponseUtil.sendJson(exchange, 400, "{ \"error\": \"start must be before end\" }");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) throw new IllegalStateException("No DB connection");

            PromotionCampaignDAO dao = new PromotionCampaignDAO(conn);
            if (dao.getCampaignById(campaignId) == null) {
                JsonResponseUtil.sendJson(exchange, 404, "{ \"error\": \"Campaign not found\" }");
                return;
            }

            dao.updateCampaign(campaignId, name.trim(), start, end);

            // best-effort status recompute based on new window if not terminated
            Timestamp now = new Timestamp(System.currentTimeMillis());
            var updated = dao.getCampaignById(campaignId);
            if (updated != null && !"TERMINATED".equals(updated.getStatus())) {
                String nextStatus;
                if (now.before(updated.getStartDatetime())) nextStatus = "SCHEDULED";
                else if (now.after(updated.getEndDatetime())) nextStatus = "ENDED";
                else nextStatus = "ACTIVE";
                dao.updateCampaignStatus(campaignId, nextStatus);
            }

            JsonResponseUtil.sendJson(exchange, 200, "{ \"status\": \"OK\" }");
        } catch (Exception e) {
            e.printStackTrace();
            JsonResponseUtil.sendJson(exchange, 500, "{ \"error\": \"Failed to update campaign\" }");
        }
    }
}

