package server.handlers.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.PromotionCampaignDAO;
import db.DatabaseConnection;
import model.PromotionCampaign;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static server.handlers.api.PromotionApi.Codes;

/**
 * Public endpoint to list active campaigns.
 *
 * GET PromotionApi.Routes.PROMOTIONS_ACTIVE
 */
public class PromotionsActiveHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.DB_CONNECTION_FAILED, "Database connection failed."));
                return;
            }

            PromotionCampaignDAO dao = new PromotionCampaignDAO(conn);
            List<PromotionCampaign> campaigns = dao.getActiveCampaignsNow();

            PromotionApi.ActiveCampaignsResponse data = new PromotionApi.ActiveCampaignsResponse();
            data.campaigns = campaigns.stream()
                    .map(AdminPromotionCampaignsHandler::toCampaignData)
                    .collect(Collectors.toList());
            JsonUtil.sendJson(exchange, 200, ApiResponse.ok(Codes.OK, "OK", data));

        } catch (SQLException e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.DB_ERROR, "Database error: " + e.getMessage()));
        } catch (Exception e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.SERVER_ERROR, "Server error: " + e.getMessage()));
        }
    }
}

