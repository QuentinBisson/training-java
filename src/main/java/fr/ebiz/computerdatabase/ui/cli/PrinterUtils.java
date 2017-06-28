package fr.ebiz.computerdatabase.ui.cli;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class PrinterUtils {

    public static final String MANDATORY_INPUT = "Input is mandatory. It can't be empty";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    public static String readString(Scanner scanner, String label, boolean mandatory) {
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

    public static OffsetDateTime readDate(Scanner scanner, String label, boolean mandatory) {
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
                    // We try to parse the input
                    return OffsetDateTime.parse(input, FORMATTER);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date");
                }
            }
        } while (true);
    }

}
