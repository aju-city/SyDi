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

/**
 * Handles commercial application submissions.
 */
public class CommercialApplicationHandler implements HttpHandler {

    private final Gson gson = new Gson();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            String json = readBody(exchange.getRequestBody());

            CommercialApplicationRequest req =
                    gson.fromJson(json, CommercialApplicationRequest.class);

            String formattedDirectors = DirectorFormatter.format(req.directors);

            CommercialApplication app = new CommercialApplication();
            app.setCompanyName(req.companyName);
            app.setRegNumber(req.regNumber);
            app.setBusinessType(req.businessType);
            app.setAddress(req.address);
            app.setEmail(req.email);
            app.setPhone(req.phone);
            app.setDirectorDetails(formattedDirectors);

            IPUService service = PUServiceFactory.getService();
            int id = service.submitCommercialApplication(app);

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

    /**
     * Reads the full request body from the input stream.
     */
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