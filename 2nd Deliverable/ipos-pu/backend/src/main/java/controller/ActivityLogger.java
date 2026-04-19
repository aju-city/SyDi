package controller;

import dao.ActivityLogDAO;
import db.DatabaseConnection;
import model.ActivityLog;

import java.sql.Connection;
import java.sql.Timestamp;

/**
 * Utility class for recording customer activity events in the database.
 */
public class ActivityLogger {

    /**
     * Logs a customer activity event.
     *
     * @param memberEmail the member's email if the user is logged in, otherwise null or blank
     * @param guestToken the guest token if the user is a guest, otherwise null
     * @param productId the related product ID, if applicable
     * @param campaignId the related campaign ID, if applicable
     * @param orderId the related order ID, if applicable
     * @param eventType the type of activity being recorded
     */
    public static void log(String memberEmail,
                           String guestToken,
                           String productId,
                           Integer campaignId,
                           Integer orderId,
                           String eventType) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            ActivityLogDAO dao = new ActivityLogDAO(conn);

            ActivityLog activity = new ActivityLog();

            activity.setCustomerType(
                    memberEmail != null && !memberEmail.trim().isBlank() ? "MEMBER" : "GUEST"
            );
            activity.setMemberEmail(
                    memberEmail != null && !memberEmail.trim().isBlank() ? memberEmail : null
            );
            activity.setGuestToken(
                    (memberEmail == null || memberEmail.trim().isBlank()) ? guestToken : null
            );
            activity.setProductId(productId);
            activity.setCampaignId(campaignId);
            activity.setOrderId(orderId);
            activity.setEventType(eventType);
            activity.setEventDatetime(new Timestamp(System.currentTimeMillis()));

            dao.createActivity(activity);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to log activity: " + eventType);
        }
    }
}