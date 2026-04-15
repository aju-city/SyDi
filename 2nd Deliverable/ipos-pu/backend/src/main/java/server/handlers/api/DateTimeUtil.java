package server.handlers.api;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

final class DateTimeUtil {
    private DateTimeUtil() {}

    /**
     * Accepts either:
     * - OffsetDateTime ISO string (e.g. 2026-04-15T10:15:30+01:00)
     * - LocalDateTime ISO string (e.g. 2026-04-15T10:15:30)
     */
    static Timestamp parseTimestamp(String value) {
        if (value == null) return null;
        String v = value.trim();
        if (v.isEmpty()) return null;
        try {
            OffsetDateTime odt = OffsetDateTime.parse(v);
            return Timestamp.from(odt.toInstant());
        } catch (DateTimeParseException ignored) {
            // fall through
        }
        try {
            LocalDateTime ldt = LocalDateTime.parse(v);
            return Timestamp.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        } catch (DateTimeParseException ignored) {
            return null;
        }
    }
}

