package server.handlers.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.PromotionCampaignDAO;
import db.DatabaseConnection;
import model.PromotionCampaign;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import static server.handlers.api.PromotionApi.Codes;

/**
 * Admin campaigns collection endpoint.
 *
 * Routes:
 * - POST PromotionApi.Routes.ADMIN_CAMPAIGNS
 * - GET  PromotionApi.Routes.ADMIN_CAMPAIGNS
 */
public class AdminPromotionCampaignsHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if (method == null) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        if (method.equalsIgnoreCase("POST")) {
            handleCreate(exchange);
            return;
        }
        if (method.equalsIgnoreCase("GET")) {
            handleList(exchange);
            return;
        }

        exchange.sendResponseHeaders(405, -1);
    }

    private void handleCreate(HttpExchange exchange) throws IOException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.DB_CONNECTION_FAILED, "Database connection failed."));
                return;
            }

            PromotionApi.CreateCampaignRequest req = JsonUtil.readJson(exchange, PromotionApi.CreateCampaignRequest.class);
            if (req == null) {
                JsonUtil.sendJson(exchange, 400, ApiResponse.error(Codes.VALIDATION_ERROR, "Missing request body."));
                return;
            }

            String name = req.campaignName == null ? null : req.campaignName.trim();
            String createdBy = req.createdBy == null ? null : req.createdBy.trim();
            Timestamp start = DateTimeUtil.parseTimestamp(req.startDatetime);
            Timestamp end = DateTimeUtil.parseTimestamp(req.endDatetime);
            String status = (req.status == null || req.status.trim().isEmpty()) ? "SCHEDULED" : req.status.trim();

            if (name == null || name.isEmpty() || createdBy == null || createdBy.isEmpty()) {
                JsonUtil.sendJson(exchange, 400, ApiResponse.error(Codes.VALIDATION_ERROR, "Missing campaignName or createdBy."));
                return;
            }
            if (start == null || end == null) {
                JsonUtil.sendJson(exchange, 400, ApiResponse.error(Codes.INVALID_CAMPAIGN_DATES, "Invalid startDatetime or endDatetime (use ISO-8601)."));
                return;
            }
            if (!start.before(end)) {
                JsonUtil.sendJson(exchange, 400, ApiResponse.error(Codes.INVALID_CAMPAIGN_DATES, "startDatetime must be before endDatetime."));
                return;
            }

            PromotionCampaign campaign = new PromotionCampaign();
            campaign.setCampaignName(name);
            campaign.setStartDatetime(start);
            campaign.setEndDatetime(end);
            campaign.setStatus(status);
            campaign.setCreatedBy(createdBy);

            PromotionCampaignDAO dao = new PromotionCampaignDAO(conn);
            int id = dao.createCampaignAndReturnId(campaign);

            PromotionApi.CreateCampaignResponse data = new PromotionApi.CreateCampaignResponse();
            data.campaignId = id;
            JsonUtil.sendJson(exchange, 200, ApiResponse.ok(Codes.CAMPAIGN_CREATED, "Campaign created.", data));

        } catch (SQLException e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.DB_ERROR, "Database error: " + e.getMessage()));
        } catch (Exception e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.SERVER_ERROR, "Server error: " + e.getMessage()));
        }
    }

    private void handleList(HttpExchange exchange) throws IOException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.DB_CONNECTION_FAILED, "Database connection failed."));
                return;
            }

            PromotionCampaignDAO dao = new PromotionCampaignDAO(conn);
            List<PromotionCampaign> campaigns = dao.getAllCampaigns();

            PromotionApi.ListCampaignsResponse data = new PromotionApi.ListCampaignsResponse();
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

    static PromotionApi.CampaignData toCampaignData(PromotionCampaign c) {
        PromotionApi.CampaignData d = new PromotionApi.CampaignData();
        d.campaignId = c.getCampaignId();
        d.campaignName = c.getCampaignName();
        d.startDatetime = c.getStartDatetime() == null ? null : c.getStartDatetime().toString();
        d.endDatetime = c.getEndDatetime() == null ? null : c.getEndDatetime().toString();
        d.status = c.getStatus();
        d.createdBy = c.getCreatedBy();
        d.createdAt = c.getCreatedAt() == null ? null : c.getCreatedAt().toString();
        d.updatedAt = c.getUpdatedAt() == null ? null : c.getUpdatedAt().toString();
        return d;
    }
}

