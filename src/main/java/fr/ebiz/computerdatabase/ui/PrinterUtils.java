package fr.ebiz.computerdatabase.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Scanner;

public class PrinterUtils {

    public static final String MANDATORY_INPUT = "Input is mandatory. It can't be empty";

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

    public static LocalDate readLocalDate(Scanner scanner, String label, boolean mandatory) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy").withLocale(Locale.getDefault());
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
                    return LocalDate.parse(input, formatter);
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date");
                }
            }
        } while (true);
    }

}
