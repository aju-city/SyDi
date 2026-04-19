package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dao.CartDAO;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Handles requests to create a new guest shopping cart.
 */
public class CreateGuestCartHandler implements HttpHandler {

    /**
     * Processes POST requests to create a guest cart and return its token.
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
            String guestToken = generateGuestToken();

            CartDAO.createGuestCart(guestToken);

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

    /**
     * Generates a simple guest token in the format GT-xxxxx.
     *
     * @return a generated guest token
     */
    private String generateGuestToken() {
        int num = (int) (Math.random() * 90000) + 10000;
        return "GT-" + num;
    }
}