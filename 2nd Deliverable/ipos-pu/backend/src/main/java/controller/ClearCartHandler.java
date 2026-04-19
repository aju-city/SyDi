package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.CartDAO;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Handles requests to clear a shopping cart for either a member or guest user.
 */
public class ClearCartHandler implements HttpHandler {

    /**
     * Processes POST requests to clear the current cart.
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
            String body = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8
            );

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

    /**
     * Extracts a string value from a simple JSON request body.
     *
     * @param json the JSON body
     * @param key the key to search for
     * @return the extracted value, or null if not found
     */
    private String extract(String json, String key) {
        String search = "\"" + key + "\"";
        int start = json.indexOf(search);

        if (start == -1) {
            return null;
        }

        int colon = json.indexOf(":", start);

        if (colon == -1) {
            return null;
        }

        int valueStart = colon + 1;

        while (valueStart < json.length()
                && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }

        if (json.charAt(valueStart) == '"') {
            int endQuote = json.indexOf("\"", valueStart + 1);
            return json.substring(valueStart + 1, endQuote);
        }

        return null;
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

        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}