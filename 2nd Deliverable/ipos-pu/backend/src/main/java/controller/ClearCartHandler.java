package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.CartDAO;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class ClearCartHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

            String guestToken = extract(body, "guestToken");
            String memberEmail = extract(body, "memberEmail");

            if (memberEmail != null && !memberEmail.isEmpty()) {
                CartDAO.clearCartForMember(memberEmail);
            } else {
                CartDAO.clearCartForGuest(guestToken);
            }

            sendJson(exchange, 200, "{ \"status\": \"OK\" }");

        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{ \"error\": \"Failed to clear cart\" }");
        }
    }

    private String extract(String json, String key) {
        String search = "\"" + key + "\"";
        int start = json.indexOf(search);
        if (start == -1) return "";

        int colon = json.indexOf(":", start);
        if (colon == -1) return "";

        int valueStart = colon + 1;

        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }

        if (json.charAt(valueStart) == '"') {
            int endQuote = json.indexOf("\"", valueStart + 1);
            return json.substring(valueStart + 1, endQuote);
        }

        if (json.startsWith("true", valueStart)) return "true";
        if (json.startsWith("false", valueStart)) return "false";

        int end = valueStart;
        while (end < json.length() &&
                (Character.isDigit(json.charAt(end)) ||
                        json.charAt(end) == '-' ||
                        json.charAt(end) == '.')) {
            end++;
        }

        return json.substring(valueStart, end);
    }


    private void sendJson(HttpExchange ex, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}
