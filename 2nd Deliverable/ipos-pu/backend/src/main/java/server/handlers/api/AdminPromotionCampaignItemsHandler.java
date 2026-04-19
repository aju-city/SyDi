package server.handlers.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.PromotionCampaignDAO;
import dao.PromotionCampaignItemsDAO;
import dao.StockItemDAO;
import db.DatabaseConnection;
import model.PromotionCampaign;
import model.PromotionCampaignItems;
import model.StockItem;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static server.handlers.api.PromotionApi.Codes;

/**
 * Admin campaign items collection endpoint.
 *
 * Routes:
 * - POST PromotionApi.Routes.ADMIN_CAMPAIGN_ITEMS?campaignId=...
 * - GET  PromotionApi.Routes.ADMIN_CAMPAIGN_ITEMS?campaignId=...
 */
public class AdminPromotionCampaignItemsHandler implements HttpHandler {

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

        if (method.equalsIgnoreCase("POST")) {
            handleAdd(exchange);
            return;
        }
        if (method.equalsIgnoreCase("GET")) {
            handleList(exchange);
            return;
        }

        exchange.sendResponseHeaders(405, -1);
    }

    /**
     * Handles POST requests for adding a product to a campaign.
     *
     * @param exchange the HTTP exchange
     * @throws IOException if an I/O error occurs while sending the response
     */
    private void handleAdd(HttpExchange exchange) throws IOException {
        Map<String, String> qp = RequestUtil.parseQueryParams(exchange);
        Integer campaignIdQ = RequestUtil.getIntParam(qp, "campaignId");

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.DB_CONNECTION_FAILED, "Database connection failed."));
                return;
            }

            PromotionApi.AddCampaignItemRequest req = JsonUtil.readJson(exchange, PromotionApi.AddCampaignItemRequest.class);
            if (req == null) {
                JsonUtil.sendJson(exchange, 400, ApiResponse.error(Codes.VALIDATION_ERROR, "Missing request body."));
                return;
            }

            Integer campaignId = campaignIdQ != null ? campaignIdQ : req.campaignId;
            String productId = req.productId == null ? null : req.productId.trim();
            Double discountRate = req.discountRate;

            if (campaignId == null || productId == null || productId.isEmpty() || discountRate == null) {
                JsonUtil.sendJson(exchange, 400, ApiResponse.error(Codes.VALIDATION_ERROR, "Missing campaignId, productId, or discountRate."));
                return;
            }
            if (discountRate < 0 || discountRate > 100) {
                JsonUtil.sendJson(exchange, 400, ApiResponse.error(Codes.INVALID_DISCOUNT_RATE, "discountRate must be between 0 and 100."));
                return;
            }

            PromotionCampaignDAO campaignDAO = new PromotionCampaignDAO(conn);
            PromotionCampaign campaign = campaignDAO.getCampaignById(campaignId);
            if (campaign == null) {
                JsonUtil.sendJson(exchange, 404, ApiResponse.error(Codes.CAMPAIGN_NOT_FOUND, "Campaign not found."));
                return;
            }

            // Validate product exists in CA.
            StockItemDAO stockDAO = new StockItemDAO(conn);
            StockItem stock = stockDAO.getStockItemById(productId);
            if (stock == null) {
                JsonUtil.sendJson(exchange, 400, ApiResponse.error(Codes.INVALID_PRODUCT_ID, "Unknown productId (not found in ipos_ca.stock_items)."));
                return;
            }

            // Same product in overlapping campaigns cannot have a different discount rate.
            Timestamp start = campaign.getStartDatetime();
            Timestamp end = campaign.getEndDatetime();
            PromotionCampaignItemsDAO itemsDAO = new PromotionCampaignItemsDAO(conn);
            List<PromotionCampaignItemsDAO.ProductDiscountRow> overlaps =
                    itemsDAO.findOverlappingDiscountsForProduct(productId, start, end, campaignId);
            for (PromotionCampaignItemsDAO.ProductDiscountRow row : overlaps) {
                double existingRate = row.getDiscountRate();
                if (Double.compare(existingRate, discountRate) != 0) {
                    JsonUtil.sendJson(exchange, 409, ApiResponse.error(
                            Codes.PROMOTION_CONFLICT,
                            "Promotion conflict: product already discounted at a different rate in an overlapping campaign (campaignId=" + row.getCampaignId() + ")."
                    ));
                    return;
                }
            }

            PromotionCampaignItems item = new PromotionCampaignItems();
            item.setCampaignId(campaignId);
            item.setProductId(productId);
            item.setDiscountRate(discountRate);

            int id = itemsDAO.createCampaignItemAndReturnId(item);
            PromotionApi.AddCampaignItemResponse data = new PromotionApi.AddCampaignItemResponse();
            data.campaignItemId = id;
            JsonUtil.sendJson(exchange, 200, ApiResponse.ok(Codes.CAMPAIGN_ITEM_ADDED, "Campaign item added.", data));

        } catch (SQLException e) {
            String msg = e.getMessage() == null ? "" : e.getMessage();
            if (msg.contains("uq_campaign_product")) {
                JsonUtil.sendJson(exchange, 409, ApiResponse.error(Codes.DUPLICATE_CAMPAIGN_PRODUCT, "Product already exists in this campaign."));
                return;
            }
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.DB_ERROR, "Database error: " + e.getMessage()));
        } catch (Exception e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.SERVER_ERROR, "Server error: " + e.getMessage()));
        }
    }

    /**
     * Handles GET requests for listing all items in a campaign.
     *
     * @param exchange the HTTP exchange
     * @throws IOException if an I/O error occurs while sending the response
     */
    private void handleList(HttpExchange exchange) throws IOException {
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
            List<PromotionCampaignItems> items = itemsDAO.getItemsByCampaignId(campaignId);

            PromotionApi.ListCampaignItemsResponse data = new PromotionApi.ListCampaignItemsResponse();
            data.items = items.stream()
                    .map(AdminPromotionCampaignItemsHandler::toItemData)
                    .collect(Collectors.toList());
            JsonUtil.sendJson(exchange, 200, ApiResponse.ok(Codes.OK, "OK", data));

        } catch (SQLException e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.DB_ERROR, "Database error: " + e.getMessage()));
        } catch (Exception e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.SERVER_ERROR, "Server error: " + e.getMessage()));
        }
    }

    /**
     * Maps a PromotionCampaignItems model to API response data.
     *
     * @param i the campaign item model
     * @return the mapped campaign item data
     */
    static PromotionApi.CampaignItemData toItemData(PromotionCampaignItems i) {
        PromotionApi.CampaignItemData d = new PromotionApi.CampaignItemData();
        d.campaignItemId = i.getCampaignItemId();
        d.campaignId = i.getCampaignId();
        d.productId = i.getProductId();
        d.discountRate = i.getDiscountRate();
        return d;
    }
}