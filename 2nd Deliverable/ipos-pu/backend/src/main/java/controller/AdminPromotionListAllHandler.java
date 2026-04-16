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
 * GET /api/promotions/campaign/all
 */
public class AdminPromotionListAllHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) throw new IllegalStateException("No DB connection");
            PromotionCampaignDAO dao = new PromotionCampaignDAO(conn);
            List<PromotionCampaign> campaigns = dao.getAllCampaigns();

            // best-effort: compute derived status for UI (ACTIVE/ENDED/SCHEDULED) if not terminated
            Timestamp now = new Timestamp(System.currentTimeMillis());
            for (PromotionCampaign c : campaigns) {
                if ("TERMINATED".equals(c.getStatus())) continue;
                if (c.getStartDatetime() != null && c.getEndDatetime() != null) {
                    if (now.before(c.getStartDatetime())) c.setStatus("SCHEDULED");
                    else if (now.after(c.getEndDatetime())) c.setStatus("ENDED");
                    else c.setStatus("ACTIVE");
                }
            }

            JsonResponseUtil.sendJson(exchange, 200, toJson(campaigns));
        } catch (Exception e) {
            e.printStackTrace();
            JsonResponseUtil.sendJson(exchange, 500, "{ \"error\": \"Failed to list campaigns\" }");
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

