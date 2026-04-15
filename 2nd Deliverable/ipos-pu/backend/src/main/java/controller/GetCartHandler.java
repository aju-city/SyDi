package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.CartDAO;
import dao.NonCommercialMemberDAO;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class GetCartHandler implements HttpHandler {

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

            List<CartDAO.CartItemView> items =
                    (memberEmail != null && !memberEmail.isEmpty())
                            ? CartDAO.getCartItemsForMember(memberEmail)
                            : CartDAO.getCartItems(guestToken);

            double subtotal = 0;
            for (CartDAO.CartItemView it : items) {
                subtotal += it.price * it.qty;
            }

            boolean discountApplied = false;
            double discountAmount = 0;
            double finalTotal = subtotal;

            // FUNCTIONALITY 9: 10th-order discount
            if (memberEmail != null && !memberEmail.isEmpty()) {
                int totalOrders = NonCommercialMemberDAO.getTotalOrders(memberEmail);

                if ((totalOrders + 1) % 10 == 0) {
                    discountApplied = true;
                    discountAmount = subtotal * 0.10;
                    finalTotal = subtotal - discountAmount;
                }
            }

            String json = buildJson(items, subtotal, discountApplied, discountAmount, finalTotal);

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, json.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(json.getBytes(StandardCharsets.UTF_8));
            }

        } catch (Exception e) {
            e.printStackTrace();
            String error = "{ \"error\": \"Failed to retrieve cart\" }";
            exchange.sendResponseHeaders(500, error.getBytes().length);
            exchange.getResponseBody().write(error.getBytes());
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

        return null;
    }

    private String buildJson(List<CartDAO.CartItemView> items, double subtotal,
                             boolean discountApplied, double discountAmount, double finalTotal) {

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"items\":[");

        for (int i = 0; i < items.size(); i++) {
            CartDAO.CartItemView it = items.get(i);
            sb.append("{")
                    .append("\"itemId\":\"").append(it.itemId).append("\",")
                    .append("\"name\":\"").append(it.name).append("\",")
                    .append("\"unitPrice\":").append(it.price).append(",")
                    .append("\"qty\":").append(it.qty)
                    .append("}");
            if (i < items.size() - 1) sb.append(",");
        }

        sb.append("],");
        sb.append("\"subtotal\":").append(subtotal).append(",");
        sb.append("\"discountApplied\":").append(discountApplied).append(",");
        sb.append("\"discountAmount\":").append(discountAmount).append(",");
        sb.append("\"finalTotal\":").append(finalTotal);
        sb.append("}");

        return sb.toString();
    }
}
