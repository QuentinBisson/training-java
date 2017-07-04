package fr.ebiz.computerdatabase.persistence.dao;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class DaoUtils {

    /**
     * Utility method used to convert a {@link Timestamp} to an {@link OffsetDateTime}.
     *
     * @param timestamp The timestamp to convert
     * @return the converted value if not null, null otherwise
     */
    public static OffsetDateTime toDate(Timestamp timestamp) {
        return timestamp != null ? OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC) : null;
    }

    /**
     * Utility method used to convert an {@link OffsetDateTime} to a {@link Timestamp}.
     *
     * @param date The date to convert
     * @return the converted value if not null, null otherwise
     */
    public static Timestamp toTimestamp(OffsetDateTime date) {
        return date != null ? Timestamp.from(date.toInstant()) : null;
    }

}
