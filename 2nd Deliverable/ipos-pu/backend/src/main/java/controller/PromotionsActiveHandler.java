package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.PromotionCampaignDAO;
import db.DatabaseConnection;
import model.PromotionCampaign;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.List;

/**
 * GET /api/promotions/active
 */
public class PromotionsActiveHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) throw new IllegalStateException("No DB connection");
            PromotionCampaignDAO dao = new PromotionCampaignDAO(conn);
            List<PromotionCampaign> active = dao.getActiveCampaigns(new Timestamp(System.currentTimeMillis()));
            JsonResponseUtil.sendJson(exchange, 200, toJson(active));
        } catch (Exception e) {
            e.printStackTrace();
            JsonResponseUtil.sendJson(exchange, 500, "{ \"error\": \"Failed to load active campaigns\" }");
        }
    }

    private String toJson(List<PromotionCampaign> campaigns) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"campaigns\": [");
        for (int i = 0; i < campaigns.size(); i++) {
            PromotionCampaign c = campaigns.get(i);
            sb.append("{")
                    .append("\"campaignId\":").append(c.getCampaignId()).append(",")
                    .append("\"campaignName\":\"").append(escape(c.getCampaignName())).append("\",")
                    .append("\"start\":\"").append(c.getStartDatetime()).append("\",")
                    .append("\"end\":\"").append(c.getEndDatetime()).append("\",")
                    .append("\"status\":\"").append(escape(c.getStatus())).append("\"")
                    .append("}");
            if (i < campaigns.size() - 1) sb.append(",");
        }
        sb.append("] }");
        return sb.toString();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}

