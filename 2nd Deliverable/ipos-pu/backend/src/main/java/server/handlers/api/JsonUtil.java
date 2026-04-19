package server.handlers.api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Utility methods for reading and sending JSON in HTTP handlers.
 */
final class JsonUtil {
    private static final Gson gson = new Gson();

    private JsonUtil() {
    }

    /**
     * Reads and deserializes a JSON request body.
     *
     * @param exchange the HTTP exchange
     * @param cls the target class
     * @param <T> the target type
     * @return the deserialized object
     * @throws IOException if an I/O error occurs while reading the request body
     */
    static <T> T readJson(HttpExchange exchange, Class<T> cls) throws IOException {
        String body = readBody(exchange.getRequestBody());
        return gson.fromJson(body, cls);
    }

    /**
     * Serializes and sends a JSON response.
     *
     * @param exchange the HTTP exchange
     * @param statusCode the HTTP status code
     * @param payload the response object
     * @throws IOException if an I/O error occurs while sending the response
     */
    static void sendJson(HttpExchange exchange, int statusCode, Object payload) throws IOException {
        String json = gson.toJson(payload);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    /**
     * Reads the full request body from an input stream.
     *
     * @param input the request input stream
     * @return the request body as a string
     * @throws IOException if an I/O error occurs while reading the stream
     */
    private static String readBody(InputStream input) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = input.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return new String(result.toByteArray(), StandardCharsets.UTF_8);
    }
}