package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.CartDAO;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Handles requests to retrieve cart contents for either a guest
 * or logged-in member.
 */
public class GetCartHandler implements HttpHandler {

    /**
     * Processes GET requests to load cart items.
     *
     * @param exchange the HTTP exchange containing request and response data
     * @throws IOException if an I/O error occurs while handling the request
     */
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

            List<CartDAO.CartItemView> items;

            if (memberEmail != null) {
                items = CartDAO.getCartItemsForMember(memberEmail);
            } else {
                items = CartDAO.getCartItems(guestToken);
            }

            StringBuilder json = new StringBuilder();
            json.append("{ \"items\": [");

            for (int i = 0; i < items.size(); i++) {
                CartDAO.CartItemView item = items.get(i);

                json.append("{")
                        .append("\"itemId\":\"").append(item.itemId).append("\",")
                        .append("\"name\":\"").append(item.name).append("\",")
                        .append("\"price\":").append(item.price).append(",")
                        .append("\"qty\":").append(item.qty)
                        .append("}");

                if (i < items.size() - 1) {
                    json.append(",");
                }
            }

            json.append("] }");

            sendJson(exchange, 200, json.toString());

        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{ \"error\": \"Failed to load cart\" }");
        }
    }

    /**
     * Sends a JSON response with the given HTTP status code.
     *
     * @param ex the HTTP exchange
     * @param status the HTTP status code
     * @param json the JSON response body
     * @throws IOException if an I/O error occurs while sending the response
     */
    private void sendJson(HttpExchange ex, int status, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        ex.getResponseHeaders().add("Content-Type", "application/json");
        ex.sendResponseHeaders(status, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.getResponseBody().close();
    }
}