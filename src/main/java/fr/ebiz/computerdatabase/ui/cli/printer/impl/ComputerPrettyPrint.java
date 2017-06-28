package fr.ebiz.computerdatabase.ui.cli.printer.impl;

import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.ui.cli.printer.PrettyPrint;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ComputerPrettyPrint implements PrettyPrint<Computer> {

    private static final String DOT = ".";
    private static final String SPACE = " ";
    private static final String COMPUTER = "Computer : ";
    private static final String INTRODUCED_AT = "Introduced at" + SPACE;
    private static final String DISCONTINUED_AT = "Discontinued at" + SPACE;
    private static final String CREATED_BY = "It was created by" + SPACE;

    @Override
    public String printList(List<Computer> listToPrint) {
        return listToPrint.stream().map(computer -> {
            StringBuilder sb = new StringBuilder("" + computer.getId()).append(" - ")
                    .append(computer.getName().trim());
            if (computer.getIntroduced() != null) {
                sb.append(DOT).append(SPACE)
                        .append(INTRODUCED_AT)
                        .append(formatDate(computer.getIntroduced()));
                if (computer.getDiscontinued() != null) {
                    sb.append(DOT).append(SPACE)
                            .append(DISCONTINUED_AT)
                            .append(formatDate(computer.getDiscontinued()));
                }
            }
            sb.append(DOT);
            if (computer.getCompanyId() != null) {
                sb.append(SPACE)
                        .append(CREATED_BY)
                        .append(computer.getCompanyId()).append(DOT);
            }
            return sb.toString();
        }).collect(Collectors.joining(System.lineSeparator()));
    }

    @Override
    public String printDetail(Computer objectToPrint) {
        StringBuilder sb = new StringBuilder(COMPUTER)
                .append(objectToPrint.getId()).append(" - ")
                .append(objectToPrint.getName().trim());

        if (objectToPrint.getIntroduced() != null) {
            sb.append(System.lineSeparator())
                    .append(INTRODUCED_AT).append(formatDate(objectToPrint.getIntroduced()));
            if (objectToPrint.getDiscontinued() != null) {
                sb.append(System.lineSeparator()).append(DISCONTINUED_AT)
                        .append(formatDate(objectToPrint.getDiscontinued()));
            }
        }
        if (objectToPrint.getCompanyId() != null) {
            sb.append(System.lineSeparator())
                    .append(SPACE).append(CREATED_BY)
                    .append(objectToPrint.getCompanyId());
        }
        return sb.toString();
    }


    private String formatDate(OffsetDateTime date) {
        return date != null ? date.format(DateTimeFormatter.ISO_DATE) : "";
    }

}
