package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.CartDAO;
import dao.StockItemDAO;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class AddToCartHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            // Read full JSON body
            InputStream is = exchange.getRequestBody();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String body = result.toString("UTF-8");

            System.out.println("DEBUG raw body = [" + body + "]");

            // Extract fields
            String guestToken = extract(body, "guestToken");
            String memberEmail = extract(body, "memberEmail");
            String itemId = extract(body, "itemId");
            String qtyStr = extract(body, "qty");

            System.out.println("DEBUG extracted guestToken  = [" + guestToken + "]");
            System.out.println("DEBUG extracted memberEmail = [" + memberEmail + "]");
            System.out.println("DEBUG extracted itemId      = [" + itemId + "]");
            System.out.println("DEBUG extracted qtyStr      = [" + qtyStr + "]");

            int qty = Integer.parseInt(qtyStr);

            // Validate stock item exists
            if (!StockItemDAO.itemExists(itemId)) {
                sendJson(exchange, 400, "{ \"error\": \"Invalid itemId\" }");
                return;
            }

            // Route to correct DAO method
            if (memberEmail != null && !memberEmail.isEmpty()) {
                // MEMBER CART
                CartDAO.addOrUpdateCartItemForMember(memberEmail, itemId, qty);
            } else {
                // GUEST CART
                CartDAO.addOrUpdateCartItem(guestToken, itemId, qty);
            }

            sendJson(exchange, 200, "{ \"status\": \"OK\" }");

        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{ \"error\": \"Failed to add item\" }");
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

        if (json.charAt(valueStart) == '"') {
            int endQuote = json.indexOf("\"", valueStart + 1);
            return json.substring(valueStart + 1, endQuote);
        }

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
        ex.getResponseBody().write(bytes);
        ex.getResponseBody().close();
    }
}
