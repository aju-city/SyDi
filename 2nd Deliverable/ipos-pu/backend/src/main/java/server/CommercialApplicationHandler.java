package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.CommercialApplication;
import model.CommercialApplicationRequest;
import service.DirectorFormatter;
import service.IPUService;
import service.PUServiceFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class CommercialApplicationHandler implements HttpHandler {

    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            // Read JSON body (Java 8 compatible)
            String json = readBody(exchange.getRequestBody());

            // Convert JSON → CommercialApplication
            // Parse JSON into request model
            CommercialApplicationRequest req =
                    gson.fromJson(json, CommercialApplicationRequest.class);

            // Format directors into one string
            String formattedDirectors = DirectorFormatter.format(req.directors);

            // Build the real CommercialApplication object
            CommercialApplication app = new CommercialApplication();
            app.setCompanyName(req.companyName);
            app.setRegNumber(req.regNumber);
            app.setBusinessType(req.businessType);
            app.setAddress(req.address);
            app.setEmail(req.email);
            app.setPhone(req.phone);
            app.setDirectorDetails(formattedDirectors);

            // Get service
            IPUService service = PUServiceFactory.getService();

            // Submit application
            int id = service.submitCommercialApplication(app);

            // Build response
            String response = "{\"success\": true, \"application_id\": " + id + "}";

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes(StandardCharsets.UTF_8).length);

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes(StandardCharsets.UTF_8));
            os.close();

        } catch (Exception e) {
            String error = "{\"success\": false, \"error\": \"" + e.getMessage() + "\"}";
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(400, error.getBytes(StandardCharsets.UTF_8).length);
            exchange.getResponseBody().write(error.getBytes(StandardCharsets.UTF_8));
            exchange.getResponseBody().close();
        }
    }

    // Java 8 compatible request-body reader
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