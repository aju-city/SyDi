package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.IPUService;
import service.PUServiceFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class RegisterNonCommercialMemberHandler implements HttpHandler {

    private final Gson gson = new Gson();

    static class EmailRequest {
        String email;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            String body = readBody(exchange.getRequestBody());

            EmailRequest req = gson.fromJson(body, EmailRequest.class);
            String email = req.email;

            IPUService service = PUServiceFactory.getService();
            String generatedPassword = service.registerNonCommercialMember(email);

            String response;

            if (generatedPassword == null) {
                response = "{ \"success\": false, \"message\": \"Email already registered\" }";
            } else {
                response = "{ \"success\": true, \"generatedPassword\": \"" + generatedPassword + "\" }";
            }

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }

        } catch (Exception e) {
            e.printStackTrace(); // <-- will show the real error in backend console
            String error = "{ \"success\": false, \"message\": \"Server error\" }";
            exchange.sendResponseHeaders(500, error.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(error.getBytes());
            }
        }
    }

    private String readBody(InputStream input) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;

        while ((length = input.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }

        return result.toString("UTF-8");
    }
}