package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.CartDAO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GetCartHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            String query = exchange.getRequestURI().getQuery();
            if (query == null) {
                sendJson(exchange, 400, "{ \"error\": \"Missing query parameters\" }");
                return;
            }

            // Extract guestToken or memberEmail
            String guestToken = null;
            String memberEmail = null;

            if (query.contains("guestToken=")) {
                guestToken = query.split("guestToken=")[1];
            }
            if (query.contains("memberEmail=")) {
                memberEmail = query.split("memberEmail=")[1];
            }

            if (guestToken == null && memberEmail == null) {
                sendJson(exchange, 400, "{ \"error\": \"Missing guestToken or memberEmail\" }");
                return;
            }

            // Fetch items
            List<CartDAO.CartItemView> items;

            if (memberEmail != null) {
                items = CartDAO.getCartItemsForMember(memberEmail);
            } else {
                items = CartDAO.getCartItems(guestToken);
            }

            // Build JSON
            StringBuilder json = new StringBuilder();
            json.append("{ \"items\": [");

            for (int i = 0; i < items.size(); i++) {
                CartDAO.CartItemView it = items.get(i);

                json.append("{")
                        .append("\"itemId\":\"").append(it.itemId).append("\",")
                        .append("\"name\":\"").append(it.name).append("\",")
                        .append("\"price\":").append(it.price).append(",")
                        .append("\"qty\":").append(it.qty)
                        .append("}");

                if (i < items.size() - 1) json.append(",");
            }

            json.append("] }");

            sendJson(exchange, 200, json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{ \"error\": \"Failed to load cart\" }");
        }
    }

    private void sendJson(HttpExchange ex, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json");
        ex.sendResponseHeaders(status, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.getResponseBody().close();
    }
}
