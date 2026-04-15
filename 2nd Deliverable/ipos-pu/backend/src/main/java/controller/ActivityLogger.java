package controller;

import dao.ActivityLogDAO;
import db.DatabaseConnection;
import model.ActivityLog;

import java.sql.Connection;
import java.sql.Timestamp;

public class ActivityLogger {

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
                    memberEmail != null && !memberEmail.isBlank() ? "MEMBER" : "GUEST"
            );
            activity.setMemberEmail(
                    memberEmail != null && !memberEmail.isBlank() ? memberEmail : null
            );
            activity.setGuestToken(
                    (memberEmail == null || memberEmail.isBlank()) ? guestToken : null
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