package server.handlers.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.NonCommercialMemberDAO;
import db.DatabaseConnection;
import model.NonCommercialMember;

import java.io.IOException;
import java.sql.Connection;

public class NonCommercialRegisterHandler implements HttpHandler {

    static class RegisterRequest {
        String email;
        String password;
    }

    static class RegisterData {
        Integer memberId;
        String memberAccountNo;
    }

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

            RegisterRequest req = JsonUtil.readJson(exchange, RegisterRequest.class);
            if (req == null || req.email == null || req.email.trim().isEmpty() || req.password == null || req.password.trim().isEmpty()) {
                JsonUtil.sendJson(exchange, 400, ApiResponse.error("VALIDATION_ERROR", "Missing email or password."));
                return;
            }

            NonCommercialMemberDAO dao = new NonCommercialMemberDAO(conn);
            if (dao.getMemberByEmail(req.email) != null) {
                JsonUtil.sendJson(exchange, 409, ApiResponse.error("DUPLICATE_REGISTRATION", "Email already registered."));
                return;
            }

            NonCommercialMember member = new NonCommercialMember();
            member.setEmail(req.email);
            member.setPassword(req.password);
            member.setMustChangePassword(true);
            member.setTotalOrders(0);

            int memberId = dao.register(member);
            NonCommercialMember created = dao.getMemberById(memberId);

            RegisterData data = new RegisterData();
            data.memberId = memberId;
            data.memberAccountNo = created == null ? null : created.getMemberAccountNo();
            JsonUtil.sendJson(exchange, 200, ApiResponse.ok("REGISTRATION_OK", "Registration successful.", data));

        } catch (Exception e) {
            JsonUtil.sendJson(exchange, 500, ApiResponse.error("SERVER_ERROR", "Server error."));
        }
    }
}
