package server.handlers.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.PromotionCampaignDAO;
import db.DatabaseConnection;
import model.PromotionCampaign;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static server.handlers.api.PromotionApi.Codes;

/**
 * Early termination endpoint.
 *
 * POST PromotionApi.Routes.ADMIN_CAMPAIGN_TERMINATE?campaignId=...
 */
public class AdminPromotionTerminateHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        Map<String, String> qp = RequestUtil.parseQueryParams(exchange);
        Integer campaignId = RequestUtil.getIntParam(qp, "campaignId");
        if (campaignId == null) {
            PromotionApi.TerminateCampaignRequest req = JsonUtil.readJson(exchange, PromotionApi.TerminateCampaignRequest.class);
            campaignId = req == null ? null : req.campaignId;
        }
        if (campaignId == null) {
            JsonUtil.sendJson(exchange, 400, ApiResponse.error(Codes.VALIDATION_ERROR, "Missing or invalid campaignId."));
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.DB_CONNECTION_FAILED, "Database connection failed."));
                return;
            }

            PromotionCampaignDAO dao = new PromotionCampaignDAO(conn);
            PromotionCampaign existing = dao.getCampaignById(campaignId);
            if (existing == null) {
                JsonUtil.sendJson(exchange, 404, ApiResponse.error(Codes.CAMPAIGN_NOT_FOUND, "Campaign not found."));
                return;
            }

            dao.updateCampaignStatus(campaignId, "TERMINATED");
            JsonUtil.sendJson(exchange, 200, ApiResponse.ok(Codes.CAMPAIGN_TERMINATED, "Campaign terminated.", null));

        } catch (SQLException e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.DB_ERROR, "Database error: " + e.getMessage()));
        } catch (Exception e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.SERVER_ERROR, "Server error: " + e.getMessage()));
        }
    }
}

