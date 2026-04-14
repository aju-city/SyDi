package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.AdminUsersDAO;
import dao.CommercialApplicationDAO;
import dao.NonCommercialMemberDAO;
import model.NonCommercialMember;
import db.DatabaseConnection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

public class LoginHandler implements HttpHandler {

    private final Gson gson = new Gson();

    static class LoginRequest {
        String emailOrUsername;
        String password;
    }

    static class LoginResponse {
        boolean success;
        String role;                 // "admin" or "member"
        boolean mustChangePassword;  // only for members
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
            LoginRequest req = gson.fromJson(body, LoginRequest.class);

            String identifier = req.emailOrUsername;
            String password = req.password;

            LoginResponse response = new LoginResponse();

            AdminUsersDAO adminDAO = new AdminUsersDAO(conn);
            CommercialApplicationDAO saDAO = new CommercialApplicationDAO(conn);
            NonCommercialMemberDAO memberDAO = new NonCommercialMemberDAO(conn);

            // 1. Admin login
            if (adminDAO.authenticate(identifier, password)) {
                response.success = true;
                response.role = "admin";
                response.mustChangePassword = false;
                response.message = "Admin login successful.";
                sendJson(exchange, response);
                return;
            }

            // 2. SA commercial applicants cannot log in
            if (saDAO.emailExists(identifier)) {
                response.success = false;
                response.message = "Commercial applicants cannot log in here.";
                sendJson(exchange, response);
                return;
            }

            // 3. Non-commercial member login
            NonCommercialMember member = memberDAO.getMemberByEmail(identifier);

            if (member != null && member.getPassword().equals(password)) {

                response.success = true;
                response.role = "member";
                response.mustChangePassword = member.isMustChangePassword();

                if (member.isMustChangePassword()) {
                    response.message = "Password change required.";
                } else {
                    response.message = "Login successful.";
                }

                sendJson(exchange, response);
                return;
            }

            // 4. Invalid login
            response.success = false;
            response.message = "Invalid email/username or password.";
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