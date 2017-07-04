package fr.ebiz.computerdatabase.ui.cli;

import fr.ebiz.computerdatabase.utils.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

class PrinterUtils {

    private static final String MANDATORY_INPUT = "Input is mandatory. It can't be empty";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Read a string from the scanner.
     *
     * @param scanner   The scanner to read from
     * @param label     The label to display till the input is valid
     * @param mandatory Is the value mandatory ?
     * @return The read string or null if not mandatory
     */
    static String readString(Scanner scanner, String label, boolean mandatory) {
        do {
            System.out.print(label);
            String input = scanner.nextLine();
            if (mandatory && StringUtils.isBlank(input)) {
                System.out.println(MANDATORY_INPUT);
            } else {
                return input;
            }
        } while (true);
    }

    /**
     * Read a date from a scanner.
     *
     * @param scanner   The scanner to read from
     * @param label     The label to display till the input is valid
     * @param mandatory Is the value mandatory ?
     * @return the read date or null if not mandatory
     */
    static LocalDate readDate(Scanner scanner, String label, boolean mandatory) {
        do {
            System.out.print(label);
            String input = scanner.nextLine();

            if (mandatory && StringUtils.isBlank(input)) {
                System.out.println(MANDATORY_INPUT);
            } else if (!mandatory && StringUtils.isBlank(input)) {
                // We accept no date so we return null
                return null;
            } else {
                try {
                    // TODO change this when possible
                    // We try to parse the input
                    return LocalDate.parse(input, FORMATTER);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date");
                }
            }
        } while (true);
    }

}
