package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.CartDAO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Handles requests to remove an item from a member or guest cart.
 */
public class RemoveCartHandler implements HttpHandler {

    /**
     * Processes POST requests to remove a cart item.
     *
     * @param exchange the HTTP exchange containing request and response data
     * @throws IOException if an I/O error occurs while handling the request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            // Read request body
            InputStream is = exchange.getRequestBody();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            String body = result.toString("UTF-8");

            // Extract fields
            String guestToken = extract(body, "guestToken");
            String memberEmail = extract(body, "memberEmail");
            String itemId = extract(body, "itemId");

            System.out.println("DEBUG RemoveCartHandler guestToken  = [" + guestToken + "]");
            System.out.println("DEBUG RemoveCartHandler memberEmail = [" + memberEmail + "]");
            System.out.println("DEBUG RemoveCartHandler itemId      = [" + itemId + "]");

            // Route request based on user type
            if (memberEmail != null && !memberEmail.isEmpty()) {
                CartDAO.removeItemForMember(memberEmail, itemId);
            } else {
                CartDAO.removeItem(guestToken, itemId);
            }

            sendJson(exchange, 200, "{ \"status\": \"OK\" }");

        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{ \"error\": \"Failed to remove item\" }");
        }
    }

    /**
     * Extracts a value from a simple JSON string using the given key.
     *
     * @param json the JSON request body
     * @param key the key to search for
     * @return the extracted value, or null if not found
     */
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
        while (end < json.length() && Character.isLetterOrDigit(json.charAt(end))) {
            end++;
        }

        return json.substring(valueStart, end);
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