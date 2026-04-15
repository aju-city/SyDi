package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.CartDAO;
import db.DatabaseConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class ValidateCartStockHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            String body;
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                body = sb.toString();
            }

            String guestToken  = extract(body, "guestToken");
            String memberEmail = extract(body, "memberEmail");

            List<CartDAO.CartItemView> items =
                    (memberEmail != null && !memberEmail.isEmpty())
                            ? CartDAO.getCartItemsForMember(memberEmail)
                            : CartDAO.getCartItems(guestToken);

            String failureMessage = checkStockForItems(items);

            String json;
            if (failureMessage == null) {
                json = "{ \"ok\": true }";
            } else {
                json = "{ \"ok\": false, \"error\": \"" + failureMessage.replace("\"", "\\\"") + "\" }";
            }

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.getBytes(StandardCharsets.UTF_8).length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

        } catch (Exception e) {
            e.printStackTrace();
            String err = "{ \"ok\": false, \"error\": \"Failed to validate stock\" }";
            exchange.sendResponseHeaders(500, err.getBytes(StandardCharsets.UTF_8).length);
            exchange.getResponseBody().write(err.getBytes(StandardCharsets.UTF_8));
        }
    }

    private String extract(String json, String key) {
        String search = "\"" + key + "\"";
        int start = json.indexOf(search);
        if (start == -1) return null;

        int colon = json.indexOf(":", start);
        if (colon == -1) return null;

        int valueStart = colon + 1;
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }

        if (valueStart >= json.length()) return null;

        if (json.charAt(valueStart) == '"') {
            int endQuote = json.indexOf("\"", valueStart + 1);
            if (endQuote == -1) return null;
            return json.substring(valueStart + 1, endQuote);
        }

        int end = valueStart;
        while (end < json.length() &&
                (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-' || json.charAt(end) == '.')) {
            end++;
        }
        return json.substring(valueStart, end);
    }

    private String checkStockForItems(List<CartDAO.CartItemView> items) throws Exception {
        String sql = "SELECT quantity FROM ipos_ca.stock_items WHERE item_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (CartDAO.CartItemView it : items) {
                stmt.setString(1, it.itemId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        return "Item not found in stock: " + it.name;
                    }
                    int available = rs.getInt("quantity");
                    if (available < it.qty) {
                        return "Insufficient stock for " + it.name + " (requested " + it.qty + ", available " + available + ")";
                    }
                }
            }
        }

        return null; // all good
    }
}
