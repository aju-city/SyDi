package server.handlers.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.CommercialApplicationDAO;
import db.DatabaseConnection;
import model.CommercialApplication;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Connection;

public class CommercialApplicationHandler implements HttpHandler {
    static class CommercialApplicationData {
        Integer applicationId;
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

            CommercialApplication app = JsonUtil.readJson(exchange, CommercialApplication.class);
            if (app == null || app.getEmail() == null || app.getEmail().trim().isEmpty() || app.getCompanyName() == null || app.getCompanyName().trim().isEmpty()) {
                JsonUtil.sendJson(exchange, 400, ApiResponse.error("VALIDATION_ERROR", "Missing required application fields."));
                return;
            }

            if (app.getStatus() == null || app.getStatus().trim().isEmpty()) {
                app.setStatus("submitted");
            }
            app.setReviewedBy(null);
            app.setNotes(null);

            CommercialApplicationDAO dao = new CommercialApplicationDAO(conn);
            if (dao.emailExists(app.getEmail())) {
                JsonUtil.sendJson(exchange, 409, ApiResponse.error("DUPLICATE_APPLICATION", "An application with this email already exists."));
                return;
            }

            int id = dao.createApplicationAndReturnId(app);
            CommercialApplicationData data = new CommercialApplicationData();
            data.applicationId = id;
            JsonUtil.sendJson(exchange, 200, ApiResponse.ok("APPLICATION_SUBMITTED", "Application submitted.", data));

        } catch (SQLException e) {
            e.printStackTrace();
            JsonUtil.sendJson(exchange, 500, ApiResponse.error("DB_ERROR", "Database error: " + e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            JsonUtil.sendJson(exchange, 500, ApiResponse.error("SERVER_ERROR", "Server error: " + e.getMessage()));
        }
    }
}
