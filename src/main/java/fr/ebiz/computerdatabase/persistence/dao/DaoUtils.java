package fr.ebiz.computerdatabase.persistence.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class DaoUtils {

    public static final OffsetDateTime toDate(Timestamp timestamp) throws SQLException {
        return timestamp != null ? OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC) : null;
    }

    public static final Timestamp toTimestamp(OffsetDateTime date) {
        return date != null ? Timestamp.from(date.toInstant()) : null;
    }

}
