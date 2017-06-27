package fr.ebiz.computerdatabase.ui;

public class StringUtils {

    public static final boolean isNumeric(String s) {
       return s != null && s.matches("\\d+");
    }

    public static String cleanString(String s) {
        return s != null ? s.toLowerCase().trim() : "";
    }

    public static boolean isNotBlank(String s) {
        return s != null && !"".equals(s.trim());
    }

    public static boolean isBlank(String s) {
        return !isNotBlank(s);
    }
}
