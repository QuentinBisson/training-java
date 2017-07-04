package fr.ebiz.computerdatabase.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class FormUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Validate a date is a local date.
     *
     * @param parameter The parameter to check
     * @return true if the parameter is a local date
     */
    public static LocalDate getLocalDate(String parameter) {
        try {
            return LocalDate.parse(parameter, FORMATTER);
        } catch (DateTimeParseException dtpe) {
            return null;
        }
    }
}
