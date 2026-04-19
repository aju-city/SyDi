package server.handlers.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.PromotionCampaignDAO;
import db.DatabaseConnection;
import model.PromotionCampaign;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import static server.handlers.api.PromotionApi.Codes;

/**
 * Admin single-campaign endpoint.
 *
 * Uses query param campaignId (see PromotionApi.Routes.ADMIN_CAMPAIGN).
 *
 * Supported:
 * - GET: fetch by id
 * - PATCH: update fields (name/dates/status)
 * - DELETE: delete campaign
 */
public class AdminPromotionCampaignHandler implements HttpHandler {

    /**
     * Routes the request to the correct handler method based on the HTTP method.
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

        if (method.equalsIgnoreCase("GET")) {
            handleGet(exchange);
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
     * Handles GET requests for fetching a single campaign by ID.
     *
     * @param exchange the HTTP exchange
     * @throws IOException if an I/O error occurs while sending the response
     */
    private void handleGet(HttpExchange exchange) throws IOException {
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

            PromotionCampaignDAO dao = new PromotionCampaignDAO(conn);
            PromotionCampaign c = dao.getCampaignById(campaignId);
            if (c == null) {
                JsonUtil.sendJson(exchange, 404, ApiResponse.error(Codes.CAMPAIGN_NOT_FOUND, "Campaign not found."));
                return;
            }

            PromotionApi.CampaignData data = AdminPromotionCampaignsHandler.toCampaignData(c);
            JsonUtil.sendJson(exchange, 200, ApiResponse.ok(Codes.OK, "OK", data));

        } catch (SQLException e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.DB_ERROR, "Database error: " + e.getMessage()));
        } catch (Exception e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.SERVER_ERROR, "Server error: " + e.getMessage()));
        }
    }

    /**
     * Handles PATCH requests for updating selected campaign fields.
     *
     * @param exchange the HTTP exchange
     * @throws IOException if an I/O error occurs while sending the response
     */
    private void handlePatch(HttpExchange exchange) throws IOException {
        Map<String, String> qp = RequestUtil.parseQueryParams(exchange);
        Integer campaignId = RequestUtil.getIntParam(qp, "campaignId");

        PromotionApi.UpdateCampaignRequest req = JsonUtil.readJson(exchange, PromotionApi.UpdateCampaignRequest.class);
        if (campaignId == null) {
            campaignId = req == null ? null : req.campaignId;
        }
        if (campaignId == null) {
            JsonUtil.sendJson(exchange, 400, ApiResponse.error(Codes.VALIDATION_ERROR, "Missing or invalid campaignId."));
            return;
        }
        if (req == null) {
            JsonUtil.sendJson(exchange, 400, ApiResponse.error(Codes.VALIDATION_ERROR, "Missing request body."));
            return;
        }

        String newName = req.campaignName == null ? null : req.campaignName.trim();
        Timestamp newStart = req.startDatetime == null ? null : DateTimeUtil.parseTimestamp(req.startDatetime);
        Timestamp newEnd = req.endDatetime == null ? null : DateTimeUtil.parseTimestamp(req.endDatetime);
        String newStatus = req.status == null ? null : req.status.trim();

        if (req.startDatetime != null && newStart == null) {
            JsonUtil.sendJson(exchange, 400, ApiResponse.error(Codes.INVALID_CAMPAIGN_DATES, "Invalid startDatetime (use ISO-8601)."));
            return;
        }
        if (req.endDatetime != null && newEnd == null) {
            JsonUtil.sendJson(exchange, 400, ApiResponse.error(Codes.INVALID_CAMPAIGN_DATES, "Invalid endDatetime (use ISO-8601)."));
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

            // If either date is provided, validate the combined window.
            Timestamp start = newStart != null ? newStart : existing.getStartDatetime();
            Timestamp end = newEnd != null ? newEnd : existing.getEndDatetime();
            if (start != null && end != null && !start.before(end)) {
                JsonUtil.sendJson(exchange, 400, ApiResponse.error(Codes.INVALID_CAMPAIGN_DATES, "startDatetime must be before endDatetime."));
                return;
            }

            // Apply updates directly to avoid changing DAO structure.
            StringBuilder sql = new StringBuilder("UPDATE PromotionCampaign SET ");
            int fields = 0;
            if (newName != null && !newName.isEmpty()) {
                sql.append("campaign_name = ?");
                fields++;
            }
            if (newStart != null) {
                if (fields++ > 0) sql.append(", ");
                sql.append("start_datetime = ?");
            }
            if (newEnd != null) {
                if (fields++ > 0) sql.append(", ");
                sql.append("end_datetime = ?");
            }
            if (newStatus != null && !newStatus.isEmpty()) {
                if (fields++ > 0) sql.append(", ");
                sql.append("status = ?");
            }
            if (fields == 0) {
                JsonUtil.sendJson(exchange, 400, ApiResponse.error(Codes.VALIDATION_ERROR, "No fields to update."));
                return;
            }
            sql.append(" WHERE campaign_id = ?");

            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                int idx = 1;
                if (newName != null && !newName.isEmpty()) stmt.setString(idx++, newName);
                if (newStart != null) stmt.setTimestamp(idx++, newStart);
                if (newEnd != null) stmt.setTimestamp(idx++, newEnd);
                if (newStatus != null && !newStatus.isEmpty()) stmt.setString(idx++, newStatus);
                stmt.setInt(idx, campaignId);
                stmt.executeUpdate();
            }

            JsonUtil.sendJson(exchange, 200, ApiResponse.ok(Codes.CAMPAIGN_UPDATED, "Campaign updated.", null));

        } catch (SQLException e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.DB_ERROR, "Database error: " + e.getMessage()));
        } catch (Exception e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.SERVER_ERROR, "Server error: " + e.getMessage()));
        }
    }

    /**
     * Handles DELETE requests for removing a campaign by ID.
     *
     * @param exchange the HTTP exchange
     * @throws IOException if an I/O error occurs while sending the response
     */
    private void handleDelete(HttpExchange exchange) throws IOException {
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

            PromotionCampaignDAO dao = new PromotionCampaignDAO(conn);
            PromotionCampaign existing = dao.getCampaignById(campaignId);
            if (existing == null) {
                JsonUtil.sendJson(exchange, 404, ApiResponse.error(Codes.CAMPAIGN_NOT_FOUND, "Campaign not found."));
                return;
            }

            dao.deleteCampaign(campaignId);
            JsonUtil.sendJson(exchange, 200, ApiResponse.ok(Codes.CAMPAIGN_DELETED, "Campaign deleted.", null));

        } catch (SQLException e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.DB_ERROR, "Database error: " + e.getMessage()));
        } catch (Exception e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error(Codes.SERVER_ERROR, "Server error: " + e.getMessage()));
        }
    }
}