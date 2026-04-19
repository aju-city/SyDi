package ipos_pu;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Sends activity log events to the backend API.
 */
public class ActivityLoggerClient {

    public static void log(String eventType, String productId, Integer campaignId, Integer orderId) {
        try {
            URL url = new URL("http://localhost:8080/api/activity/log");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            boolean isGuest = (CartManager.memberEmail == null || CartManager.memberEmail.trim().isBlank());

            StringBuilder json = new StringBuilder("{");
            if (isGuest) {
                json.append("\"guestToken\":\"").append(CartManager.guestToken).append("\"");
            } else {
                json.append("\"memberEmail\":\"").append(CartManager.memberEmail).append("\"");
            }

            json.append(",\"eventType\":\"").append(eventType).append("\"");

            if (productId != null) {
                json.append(",\"productId\":\"").append(productId).append("\"");
            }
            if (campaignId != null) {
                json.append(",\"campaignId\":").append(campaignId);
            }
            if (orderId != null) {
                json.append(",\"orderId\":").append(orderId);
            }

            json.append("}");

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.toString().getBytes());
            }

            int status = conn.getResponseCode();
            if (status != 200) {
                System.out.println("Activity log failed with status: " + status);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}