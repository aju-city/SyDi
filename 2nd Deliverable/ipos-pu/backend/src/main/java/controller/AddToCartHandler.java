package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.CartDAO;
import dao.StockItemDAO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Handles requests to add an item to a member or guest shopping cart.
 */
public class AddToCartHandler implements HttpHandler {

    /**
     * Processes POST requests for adding items to the cart.
     *
     * @param exchange the HTTP exchange containing the request and response
     * @throws IOException if an I/O error occurs while handling the request
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("NEW ADD TO CART HANDLER RUNNING");

        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            InputStream is = exchange.getRequestBody();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;

            while ((length = is.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            String body = result.toString("UTF-8");

            System.out.println("DEBUG raw body = [" + body + "]");

            String guestToken = extract(body, "guestToken");
            String memberEmail = extract(body, "memberEmail");
            String itemId = extract(body, "itemId");
            String qtyStr = extract(body, "qty");

            System.out.println("DEBUG extracted guestToken  = [" + guestToken + "]");
            System.out.println("DEBUG extracted memberEmail = [" + memberEmail + "]");
            System.out.println("DEBUG extracted itemId      = [" + itemId + "]");
            System.out.println("DEBUG extracted qtyStr      = [" + qtyStr + "]");

            if (itemId == null || qtyStr == null) {
                sendJson(exchange, 400, "{ \"error\": \"Missing itemId or qty\" }");
                return;
            }

            int qty = Integer.parseInt(qtyStr);

            if (!StockItemDAO.itemExists(itemId)) {
                sendJson(exchange, 400, "{ \"error\": \"Invalid itemId\" }");
                return;
            }

            if (memberEmail != null && !memberEmail.trim().isEmpty()) {
                CartDAO.addOrUpdateCartItemForMember(memberEmail, itemId, qty);
            } else {
                if (guestToken == null || guestToken.trim().isEmpty()) {
                    sendJson(exchange, 400, "{ \"error\": \"Missing guestToken\" }");
                    return;
                }

                CartDAO.addOrUpdateCartItem(guestToken, itemId, qty);
            }

            ActivityLogger.log(
                    memberEmail,
                    guestToken,
                    itemId,
                    null,
                    null,
                    "ADD_TO_CART"
            );

            sendJson(exchange, 200, "{ \"status\": \"OK\" }");

        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 500, "{ \"error\": \"Failed to add item\" }");
        }
    }

    /**
     * Extracts a value from a simple JSON string using the given key.
     * Supports quoted string values and basic numeric values.
     *
     * @param json the JSON request body
     * @param key the key to search for
     * @return the extracted value, or null if the key is not found
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

        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) {
            valueStart++;
        }

        if (valueStart >= json.length()) {
            return null;
        }

        if (json.charAt(valueStart) == '"') {
            int endQuote = json.indexOf("\"", valueStart + 1);
            if (endQuote == -1) {
                return null;
            }
            return json.substring(valueStart + 1, endQuote);
        }

        int end = valueStart;
        while (end < json.length()
                && (Character.isDigit(json.charAt(end))
                || json.charAt(end) == '-'
                || json.charAt(end) == '.')) {
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