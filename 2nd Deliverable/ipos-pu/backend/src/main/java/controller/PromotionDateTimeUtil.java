package controller;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class PromotionDateTimeUtil {
    private PromotionDateTimeUtil() {}

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static Timestamp parseTimestamp(String input) {
        if (input == null) return null;
        try {
            LocalDateTime dt = LocalDateTime.parse(input, FMT);
            return Timestamp.valueOf(dt);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
