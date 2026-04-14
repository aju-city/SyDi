package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.NonCommercialMemberDAO;
import db.DatabaseConnection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

public class ChangePasswordHandler implements HttpHandler {

    private final Gson gson = new Gson();

    static class ChangePasswordRequest {
        String email;
        String newPassword;
    }

    static class ChangePasswordResponse {
        boolean success;
        String message;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {

            String body = readBody(exchange.getRequestBody());
            ChangePasswordRequest req = gson.fromJson(body, ChangePasswordRequest.class);

            NonCommercialMemberDAO memberDAO = new NonCommercialMemberDAO(conn);

            boolean updated = memberDAO.updatePasswordAndClearFlag(req.email, req.newPassword);

            ChangePasswordResponse response = new ChangePasswordResponse();

            if (updated) {
                response.success = true;
                response.message = "Password updated successfully.";
            } else {
                response.success = false;
                response.message = "Failed to update password.";
            }

            sendJson(exchange, response);

        } catch (Exception e) {
            e.printStackTrace();
            String error = "{ \"success\": false, \"message\": \"Server error\" }";
            exchange.sendResponseHeaders(500, error.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(error.getBytes());
            }
        }
    }

    private String readBody(InputStream input) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = input.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        return result.toString("UTF-8");
    }

    private void sendJson(HttpExchange exchange, Object obj) throws IOException {
        String json = gson.toJson(obj);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
    }
}