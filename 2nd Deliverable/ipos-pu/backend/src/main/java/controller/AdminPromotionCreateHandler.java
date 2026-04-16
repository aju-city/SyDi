package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.PromotionCampaignDAO;
import db.DatabaseConnection;
import model.PromotionCampaign;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Timestamp;

/**
 * POST /api/promotions/campaign/create
 * Body: { "campaignName": "...", "start": "yyyy-MM-dd HH:mm", "end": "yyyy-MM-dd HH:mm", "createdBy": "admin" }
 */
public class AdminPromotionCreateHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String body = JsonBodyUtil.readBody(exchange.getRequestBody());
        String name = JsonBodyUtil.extractString(body, "campaignName");
        String startStr = JsonBodyUtil.extractString(body, "start");
        String endStr = JsonBodyUtil.extractString(body, "end");
        String createdBy = JsonBodyUtil.extractString(body, "createdBy");

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
        if (createdBy == null || createdBy.trim().isEmpty()) createdBy = "admin";

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) throw new IllegalStateException("No DB connection");
            PromotionCampaignDAO dao = new PromotionCampaignDAO(conn);

            PromotionCampaign c = new PromotionCampaign();
            c.setCampaignName(name.trim());
            c.setStartDatetime(start);
            c.setEndDatetime(end);
            c.setStatus("SCHEDULED");
            c.setCreatedBy(createdBy.trim());

            dao.createCampaign(c);

            JsonResponseUtil.sendJson(exchange, 201, "{ \"campaignId\": " + c.getCampaignId() + " }");
        } catch (Exception e) {
            e.printStackTrace();
            JsonResponseUtil.sendJson(exchange, 500, "{ \"error\": \"Failed to create campaign\" }");
        }
    }
}

