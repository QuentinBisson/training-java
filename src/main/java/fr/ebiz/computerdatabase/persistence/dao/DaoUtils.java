package fr.ebiz.computerdatabase.persistence.dao;

import fr.ebiz.computerdatabase.persistence.exception.DaoException;
import org.slf4j.Logger;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

public final class DaoUtils {

    private static final String TOO_MANY_RESULTS_WERE_FOUND_EXCEPTION = "Too many results were found !";

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

    /**
     * Check the list contains only one element and throws an exception otherwise.
     *
     * @param list   The list of elements
     * @param logger The logger
     * @param <T>    Type of elements found
     * @return The only object if it exists
     */
    public static <T> Optional<T> checkOnlyOne(List<T> list, Logger logger) {
        if (list.isEmpty()) {
            return Optional.empty();
        } else if (list.size() > 1) {
            String message = TOO_MANY_RESULTS_WERE_FOUND_EXCEPTION;
            logger.error(message);
            throw new DaoException(message);
        }
        return Optional.of(list.get(0));
    }
}
