package fr.ebiz.computerdatabase.cli.utils;

public final class StringUtils {

    /**
     * Check if a string is numeric.
     *
     * @param s The string to check
     * @return true if the string is not null and is an integer
     */
    public static boolean isNumeric(String s) {
        return !isBlank(s) && s.matches("\\d+");
    }

    /**
     * Clean a string by removing spaces.
     *
     * @param s The string to clean
     * @return The cleaned string
     */
    public static String cleanString(String s) {
        return s != null ? s.toLowerCase().trim() : "";
    }

    /**
     * Verify if a string is blank.
     *
     * @param s The string to check
     * @return true if the string is blank
     */
    public static boolean isBlank(String s) {
        return s == null || "".equals(s.trim());
    }
}
