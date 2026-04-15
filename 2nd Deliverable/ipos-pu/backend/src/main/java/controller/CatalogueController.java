package controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import db.DatabaseConnection;
import model.Product;
import service.CatalogueService;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;

public class CatalogueController implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();
        if (!method.equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        URI uri = exchange.getRequestURI();
        String path = uri.getPath();          // /api/catalogue/all
        String query = uri.getQuery();        // query=paracetamol

        try (Connection conn = DatabaseConnection.getConnection()) {

            CatalogueService service = new CatalogueService(conn);

            // /api/catalogue/all
            if (path.endsWith("/all")) {
                List<Product> list = service.getAllProducts();
                sendJson(exchange, buildProductListJson(list));
                return;
            }

            // /api/catalogue/search?query=xxx
            if (path.endsWith("/search")) {
                if (query == null || !query.contains("query=")) {
                    sendJson(exchange, "{ \"error\": \"Missing query parameter\" }");
                    return;
                }

                String q = query.split("query=")[1];
                List<Product> list = service.searchProducts(q);
                sendJson(exchange, buildProductListJson(list));
                return;
            }

            // /api/catalogue/item/{id}
            if (path.startsWith("/api/catalogue/item/")) {
                String id = path.substring("/api/catalogue/item/".length());
                Product p = service.getProductById(id);

                if (p == null) {
                    sendJson(exchange, "{ \"error\": \"Item not found\" }");
                    return;
                }

                sendJson(exchange, buildProductJson(p));
                return;
            }

            sendJson(exchange, "{ \"error\": \"Invalid catalogue endpoint\" }");

        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, "{ \"error\": \"Server error\" }");
        }
    }

    private String buildProductListJson(List<Product> list) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"products\": [");

        for (int i = 0; i < list.size(); i++) {
            sb.append(buildProductJson(list.get(i)));
            if (i < list.size() - 1) sb.append(",");
        }

        sb.append("] }");
        return sb.toString();
    }

    private String buildProductJson(Product p) {
        return "{"
                + "\"itemId\":\"" + p.getItemId() + "\","
                + "\"name\":\"" + p.getName() + "\","
                + "\"description\":\"" + p.getDescription() + "\","
                + "\"price\":" + p.getPrice() + ","
                + "\"quantity\":" + p.getQuantity() + ","
                + "\"stockLimit\":" + p.getStockLimit()
                + "}";
    }


    private void sendJson(HttpExchange ex, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json");
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}
