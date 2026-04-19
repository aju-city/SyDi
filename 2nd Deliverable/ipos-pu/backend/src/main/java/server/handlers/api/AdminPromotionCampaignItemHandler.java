package server.handlers.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.PromotionCampaignDAO;
import dao.PromotionCampaignItemsDAO;
import db.DatabaseConnection;
import model.PromotionCampaign;
import model.PromotionCampaignItems;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static server.handlers.api.PromotionApi.Codes;

/**
 * Admin single campaign-item endpoint.
 *
 * Uses query param campaignItemId (see PromotionApi.Routes.ADMIN_CAMPAIGN_ITEM).
 *
 * Supported:
 * - PATCH: update discountRate
 * - DELETE: delete item
 */
public class AdminPromotionCampaignItemHandler implements HttpHandler {

    /**
     * Routes the request based on the HTTP method.
     *
     * @param exchange the HTTP exchange
     * @throws IOException if an I/O error occurs while handling the request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method == null) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        if (method.equalsIgnoreCase("PATCH")) {
            handlePatch(exchange);
            return;
        }
        if (method.equalsIgnoreCase("DELETE")) {
            handleDelete(exchange);
            return;
        }

        exchange.sendResponseHeaders(405, -1);
    }

    /**
     * Handles PATCH requests for updating a campaign item's discount rate.
     *
     * @param exchange the HTTP exchange
     * @throws IOException if an I/O error occurs while sending the response
     */
    private void handlePatch(HttpExchange exchange) throws IOException {
        Map<String, String> qp = RequestUtil.parseQueryParams(exchange);
        Integer campaignItemIdQ = RequestUtil.getIntParam(qp, "campaignItemId");

        PromotionApi.UpdateCampaignItemRequest req = JsonUtil.readJson(exchange, PromotionApi.UpdateCampaignItemRequest.class);
        Integer campaignItemId = campaignItemIdQ != null ? campaignItemIdQ : (req == null ? null : req.campaignItemId);
        if (campaignItemId == null || req == null || req.discountRate == null) {
            JsonUtil.sendJson(exchange, 400, ApiResponse.error(Codes.VALIDATION_ERROR, "Missing campaignItemId or discountRate."));
            return;
        }
        if (req.discountRate < 0 || req.discountRate > 100) {
            JsonUtil.sendJson(exchange, 400, ApiResponse.error(Codes.INVALID_DISCOUNT_RATE, "discountRate must be between 0 and 100."));
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.DB_CONNECTION_FAILED, "Database connection failed."));
                return;
            }

            PromotionCampaignItemsDAO itemsDAO = new PromotionCampaignItemsDAO(conn);
            PromotionCampaignItems existing = itemsDAO.getCampaignItemById(campaignItemId);
            if (existing == null) {
                JsonUtil.sendJson(exchange, 404, ApiResponse.error(Codes.CAMPAIGN_ITEM_NOT_FOUND, "Campaign item not found."));
                return;
            }

            PromotionCampaignDAO campaignDAO = new PromotionCampaignDAO(conn);
            PromotionCampaign campaign = campaignDAO.getCampaignById(existing.getCampaignId());
            if (campaign == null) {
                JsonUtil.sendJson(exchange, 404, ApiResponse.error(Codes.CAMPAIGN_NOT_FOUND, "Campaign not found."));
                return;
            }

            // Same product in overlapping campaigns cannot have a different discount rate.
            Timestamp start = campaign.getStartDatetime();
            Timestamp end = campaign.getEndDatetime();
            List<PromotionCampaignItemsDAO.ProductDiscountRow> overlaps =
                    itemsDAO.findOverlappingDiscountsForProduct(existing.getProductId(), start, end, campaign.getCampaignId());
            for (PromotionCampaignItemsDAO.ProductDiscountRow row : overlaps) {
                double existingRate = row.getDiscountRate();
                if (Double.compare(existingRate, req.discountRate) != 0) {
                    JsonUtil.sendJson(exchange, 409, ApiResponse.error(
                            Codes.PROMOTION_CONFLICT,
                            "Promotion conflict: product already discounted at a different rate in an overlapping campaign (campaignId=" + row.getCampaignId() + ")."
                    ));
                    return;
                }
            }

            itemsDAO.updateDiscountRate(campaignItemId, req.discountRate);
            JsonUtil.sendJson(exchange, 200, ApiResponse.ok(Codes.CAMPAIGN_ITEM_UPDATED, "Campaign item updated.", null));

        } catch (SQLException e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.DB_ERROR, "Database error: " + e.getMessage()));
        } catch (Exception e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.SERVER_ERROR, "Server error: " + e.getMessage()));
        }
    }

    /**
     * Handles DELETE requests for deleting a campaign item.
     *
     * @param exchange the HTTP exchange
     * @throws IOException if an I/O error occurs while sending the response
     */
    private void handleDelete(HttpExchange exchange) throws IOException {
        Map<String, String> qp = RequestUtil.parseQueryParams(exchange);
        Integer campaignItemId = RequestUtil.getIntParam(qp, "campaignItemId");
        if (campaignItemId == null) {
            JsonUtil.sendJson(exchange, 400, ApiResponse.error(Codes.VALIDATION_ERROR, "Missing or invalid campaignItemId."));
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.DB_CONNECTION_FAILED, "Database connection failed."));
                return;
            }

            PromotionCampaignItemsDAO itemsDAO = new PromotionCampaignItemsDAO(conn);
            PromotionCampaignItems existing = itemsDAO.getCampaignItemById(campaignItemId);
            if (existing == null) {
                JsonUtil.sendJson(exchange, 404, ApiResponse.error(Codes.CAMPAIGN_ITEM_NOT_FOUND, "Campaign item not found."));
                return;
            }

            itemsDAO.deleteCampaignItem(campaignItemId);
            JsonUtil.sendJson(exchange, 200, ApiResponse.ok(Codes.CAMPAIGN_ITEM_DELETED, "Campaign item deleted.", null));

        } catch (SQLException e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.DB_ERROR, "Database error: " + e.getMessage()));
        } catch (Exception e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.SERVER_ERROR, "Server error: " + e.getMessage()));
        }
    }
}