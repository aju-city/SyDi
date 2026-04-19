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

/**
 * Handles HTTP requests related to catalogue products.
 * Supports retrieving all products, searching products,
 * and retrieving a single product by ID.
 */
public class CatalogueController implements HttpHandler {

    /**
     * Processes incoming GET requests for catalogue endpoints.
     *
     * @param exchange the HTTP exchange containing request and response data
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();

        if (!method.equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        URI uri = exchange.getRequestURI();
        String path = uri.getPath();
        String query = uri.getQuery();

        try (Connection conn = DatabaseConnection.getConnection()) {

            CatalogueService service = new CatalogueService(conn);

            // /api/catalogue/all
            if (path.endsWith("/all")) {
                List<Product> list = service.getAllProducts();
                sendJson(exchange, buildProductListJson(list));
                return;
            }

            // /api/catalogue/search?query=value
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

                Product product = service.getProductById(id);

                if (product == null) {
                    sendJson(exchange, "{ \"error\": \"Item not found\" }");
                    return;
                }

                sendJson(exchange, buildProductJson(product));
                return;
            }

            sendJson(exchange, "{ \"error\": \"Invalid catalogue endpoint\" }");

        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, "{ \"error\": \"Server error\" }");
        }
    }

    /**
     * Builds a JSON response containing a list of products.
     *
     * @param products the product list
     * @return JSON string containing products
     */
    private String buildProductListJson(List<Product> products) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"products\": [");

        for (int i = 0; i < products.size(); i++) {
            sb.append(buildProductJson(products.get(i)));

            if (i < products.size() - 1) {
                sb.append(",");
            }
        }

        sb.append("] }");
        return sb.toString();
    }

    /**
     * Builds a JSON representation of a single product.
     *
     * @param product the product to convert
     * @return JSON string for the product
     */
    private String buildProductJson(Product product) {
        return "{"
                + "\"itemId\":\"" + product.getItemId() + "\","
                + "\"name\":\"" + product.getName() + "\","
                + "\"description\":\"" + product.getDescription() + "\","
                + "\"price\":" + product.getPrice() + ","
                + "\"quantity\":" + product.getQuantity() + ","
                + "\"stockLimit\":" + product.getStockLimit()
                + "}";
    }

    /**
     * Sends a JSON response with HTTP 200 status.
     *
     * @param exchange the HTTP exchange
     * @param json the JSON body
     * @throws IOException if an I/O error occurs
     */
    private void sendJson(HttpExchange exchange, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}