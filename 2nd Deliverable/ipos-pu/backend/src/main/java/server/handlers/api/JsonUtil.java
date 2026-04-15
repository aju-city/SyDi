package server.handlers.api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

final class JsonUtil {
    private static final Gson gson = new Gson();

    private JsonUtil() {}

    static <T> T readJson(HttpExchange exchange, Class<T> cls) throws IOException {
        String body = readBody(exchange.getRequestBody());
        return gson.fromJson(body, cls);
    }

    static void sendJson(HttpExchange exchange, int statusCode, Object payload) throws IOException {
        String json = gson.toJson(payload);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

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
