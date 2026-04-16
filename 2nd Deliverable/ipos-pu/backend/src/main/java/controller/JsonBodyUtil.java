package controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Minimal JSON body helpers aligned with existing controller style.
 * This does not implement full JSON parsing; it only supports simple key extraction.
 */
public final class JsonBodyUtil {
    private JsonBodyUtil() {}

    public static String readBody(InputStream is) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return new String(result.toByteArray(), StandardCharsets.UTF_8);
    }

    public static String extractString(String json, String key) {
        String search = "\"" + key + "\"";
        int start = json.indexOf(search);
        if (start == -1) return null;

        int colon = json.indexOf(":", start);
        if (colon == -1) return null;

        int valueStart = colon + 1;
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) valueStart++;
        if (valueStart >= json.length()) return null;

        if (json.charAt(valueStart) == '"') {
            int endQuote = json.indexOf("\"", valueStart + 1);
            if (endQuote == -1) return null;
            return json.substring(valueStart + 1, endQuote);
        }
        return null;
    }

    public static Integer extractInt(String json, String key) {
        String raw = extractNumberToken(json, key);
        if (raw == null || raw.isEmpty()) return null;
        return Integer.parseInt(raw);
    }

    public static Double extractDouble(String json, String key) {
        String raw = extractNumberToken(json, key);
        if (raw == null || raw.isEmpty()) return null;
        return Double.parseDouble(raw);
    }

    private static String extractNumberToken(String json, String key) {
        String search = "\"" + key + "\"";
        int start = json.indexOf(search);
        if (start == -1) return null;

        int colon = json.indexOf(":", start);
        if (colon == -1) return null;

        int valueStart = colon + 1;
        while (valueStart < json.length() && Character.isWhitespace(json.charAt(valueStart))) valueStart++;
        if (valueStart >= json.length()) return null;

        if (json.charAt(valueStart) == '"') {
            int endQuote = json.indexOf("\"", valueStart + 1);
            if (endQuote == -1) return null;
            return json.substring(valueStart + 1, endQuote);
        }

        int end = valueStart;
        while (end < json.length()) {
            char c = json.charAt(end);
            if (Character.isDigit(c) || c == '-' || c == '.') {
                end++;
            } else {
                break;
            }
        }
        return json.substring(valueStart, end).trim();
    }
}
