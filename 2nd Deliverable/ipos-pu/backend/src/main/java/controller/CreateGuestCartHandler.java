package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.CartDAO;


import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CreateGuestCartHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        try {
            // 1. Generate token
            String guestToken = generateGuestToken();

            // 2. Insert into DB
            CartDAO.createGuestCart(guestToken);

            // 3. Build JSON response
            String json = "{ \"guestToken\": \"" + guestToken + "\" }";

            byte[] responseBytes = json.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, responseBytes.length);

            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();

        } catch (Exception e) {
            e.printStackTrace();
            String error = "{ \"error\": \"Failed to create guest cart.\" }";
            byte[] responseBytes = error.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, responseBytes.length);
            exchange.getResponseBody().write(responseBytes);
            exchange.getResponseBody().close();
        }
    }

    private String generateGuestToken() {
        int num = (int)(Math.random() * 90000) + 10000; // 10000–99999
        return "GT-" + num;
    }
}