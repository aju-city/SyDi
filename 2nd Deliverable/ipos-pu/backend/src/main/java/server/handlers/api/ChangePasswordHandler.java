package server.handlers.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.NonCommercialMemberDAO;
import db.DatabaseConnection;
import model.NonCommercialMember;

import java.io.IOException;
import java.sql.Connection;

/**
 * API handler for changing a member password.
 */
public class ChangePasswordHandler implements HttpHandler {

    /**
     * Request body for password change.
     */
    static class ChangePasswordRequest {
        String email;
        String newPassword;
    }

    /**
     * Handles POST requests to update a member password.
     *
     * @param exchange the HTTP exchange
     * @throws IOException if an I/O error occurs while handling the request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                JsonUtil.sendJson(exchange, 500, ApiResponse.error("DB_CONNECTION_FAILED", "Database connection failed."));
                return;
            }

            ChangePasswordRequest req = JsonUtil.readJson(exchange, ChangePasswordRequest.class);
            if (req == null || req.email == null || req.email.trim().isEmpty() || req.newPassword == null || req.newPassword.trim().isEmpty()) {
                JsonUtil.sendJson(exchange, 400, ApiResponse.error("VALIDATION_ERROR", "Missing email or newPassword."));
                return;
            }

            NonCommercialMemberDAO dao = new NonCommercialMemberDAO(conn);
            NonCommercialMember member = dao.getMemberByEmail(req.email);
            if (member == null) {
                JsonUtil.sendJson(exchange, 404, ApiResponse.error("NOT_FOUND", "Member not found."));
                return;
            }

            dao.updatePassword(member.getMemberID(), req.newPassword);
            JsonUtil.sendJson(exchange, 200, ApiResponse.ok("PASSWORD_UPDATED", "Password updated successfully.", null));

        } catch (Exception e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error("SERVER_ERROR", "Server error."));
        }
    }
}