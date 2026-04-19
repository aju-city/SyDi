package server.handlers.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.PromotionCampaignDAO;
import dao.PromotionCampaignItemsDAO;
import db.DatabaseConnection;
import model.PromotionCampaign;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static server.handlers.api.PromotionApi.Codes;

/**
 * Public endpoint for listing products in a promotion campaign.
 */
public class PromotionProductsHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        Map<String, String> qp = RequestUtil.parseQueryParams(exchange);
        Integer campaignId = RequestUtil.getIntParam(qp, "campaignId");
        if (campaignId == null) {
            JsonUtil.sendJson(exchange, 400, ApiResponse.error(Codes.VALIDATION_ERROR, "Missing or invalid campaignId."));
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.DB_CONNECTION_FAILED, "Database connection failed."));
                return;
            }

            PromotionCampaignDAO campaignDAO = new PromotionCampaignDAO(conn);
            PromotionCampaign campaign = campaignDAO.getCampaignById(campaignId);
            if (campaign == null) {
                JsonUtil.sendJson(exchange, 404, ApiResponse.error(Codes.CAMPAIGN_NOT_FOUND, "Campaign not found."));
                return;
            }

            PromotionCampaignItemsDAO itemsDAO = new PromotionCampaignItemsDAO(conn);
            List<PromotionCampaignItemsDAO.PromotionProductRow> rows = itemsDAO.getPromotionProductsWithBasePrice(campaignId);

            PromotionApi.PromotionProductsResponse data = new PromotionApi.PromotionProductsResponse();
            data.campaignId = campaignId;
            data.products = rows.stream()
                    .map(PromotionProductsHandler::toPriceData)
                    .collect(Collectors.toList());

            JsonUtil.sendJson(exchange, 200, ApiResponse.ok(Codes.OK, "OK", data));

        } catch (SQLException e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.DB_ERROR, "Database error: " + e.getMessage()));
        } catch (Exception e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.SERVER_ERROR, "Server error: " + e.getMessage()));
        }
    }

    private static PromotionApi.PromotionProductPriceData toPriceData(PromotionCampaignItemsDAO.PromotionProductRow r) {
        PromotionApi.PromotionProductPriceData d = new PromotionApi.PromotionProductPriceData();
        d.productId = r.getProductId();
        d.name = r.getName();
        d.description = r.getDescription();
        d.basePrice = r.getBasePrice();
        d.discountRate = r.getDiscountRate();
        d.discountedPrice = r.getBasePrice() * (1.0 - (r.getDiscountRate() / 100.0));
        return d;
    }
}