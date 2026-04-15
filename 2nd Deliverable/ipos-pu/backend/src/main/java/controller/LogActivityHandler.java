package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LogActivityHandler implements HttpHandler {

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
            String productId = extract(body, "productId");
            String eventType = extract(body, "eventType");

            String campaignIdStr = extract(body, "campaignId");
            String orderIdStr = extract(body, "orderId");

            Integer campaignId = null;
            Integer orderId = null;

            if (campaignIdStr != null && !campaignIdStr.isBlank()) {
                campaignId = Integer.parseInt(campaignIdStr);
            }

            if (orderIdStr != null && !orderIdStr.isBlank()) {
                orderId = Integer.parseInt(orderIdStr);
            }

            if (eventType == null || eventType.isBlank()) {
                sendJson(exchange, 400, "{ \"error\": \"Missing eventType\" }");
                return;
            }

            ActivityLogger.log(
                    memberEmail,
                    guestToken,
                    productId,
                    campaignId,
                    orderId,
                    eventType
            );

            sendJson(exchange, 200, "{ \"status\": \"OK\" }");

        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{ \"error\": \"Failed to log activity\" }");
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
        while (end < json.length()
                && json.charAt(end) != ','
                && json.charAt(end) != '}'
                && !Character.isWhitespace(json.charAt(end))) {
            end++;
        }

        return json.substring(valueStart, end);
    }

    private void sendJson(HttpExchange ex, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json");
        ex.sendResponseHeaders(status, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.getResponseBody().close();
    }
}