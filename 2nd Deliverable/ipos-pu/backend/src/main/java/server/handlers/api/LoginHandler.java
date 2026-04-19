package server.handlers.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.AdminUsersDAO;
import dao.CommercialApplicationDAO;
import dao.NonCommercialMemberDAO;
import db.DatabaseConnection;
import model.NonCommercialMember;

import java.io.IOException;
import java.sql.Connection;

/**
 * API handler for login requests.
 */
public class LoginHandler implements HttpHandler {

    /**
     * Request body for login.
     */
    static class LoginRequest {
        String emailOrUsername;
        String password;
    }

    /**
     * Response payload for successful login.
     */
    static class LoginData {
        String role; // admin | member
        boolean mustChangePassword;
    }

    /**
     * Handles POST login requests.
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

            LoginRequest req = JsonUtil.readJson(exchange, LoginRequest.class);
            String identifier = req == null ? null : req.emailOrUsername;
            String password = req == null ? null : req.password;

            if (identifier == null || identifier.trim().isEmpty() || password == null) {
                JsonUtil.sendJson(exchange, 400, ApiResponse.error("VALIDATION_ERROR", "Missing email/username or password."));
                return;
            }

            AdminUsersDAO adminDAO = new AdminUsersDAO(conn);
            CommercialApplicationDAO commercialDAO = new CommercialApplicationDAO(conn);
            NonCommercialMemberDAO memberDAO = new NonCommercialMemberDAO(conn);

            // 1) Admin login
            if (adminDAO.authenticate(identifier, password)) {
                LoginData data = new LoginData();
                data.role = "admin";
                data.mustChangePassword = false;
                JsonUtil.sendJson(exchange, 200, ApiResponse.ok("LOGIN_OK", "Admin login successful.", data));
                return;
            }

            // 2) Commercial applicants cannot log in here
            if (commercialDAO.emailExists(identifier)) {
                JsonUtil.sendJson(exchange, 403, ApiResponse.error("COMMERCIAL_CANNOT_LOGIN", "Commercial applicants cannot log in here."));
                return;
            }

            // 3) Member login
            NonCommercialMember member = memberDAO.getMemberByEmail(identifier);
            if (member != null && password.equals(member.getPassword())) {
                LoginData data = new LoginData();
                data.role = "member";
                data.mustChangePassword = member.isMustChangePassword();
                String code = data.mustChangePassword ? "PASSWORD_CHANGE_REQUIRED" : "LOGIN_OK";
                String message = data.mustChangePassword ? "Password change required." : "Login successful.";
                JsonUtil.sendJson(exchange, 200, ApiResponse.ok(code, message, data));
                return;
            }

            // 4) Invalid login
            JsonUtil.sendJson(exchange, 401, ApiResponse.error("INVALID_LOGIN", "Invalid email/username or password."));

        } catch (Exception e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error("SERVER_ERROR", "Server error."));
        }
    }
}