package fr.ebiz.computerdatabase.cli.printer.impl;

import fr.ebiz.computerdatabase.cli.printer.PrettyPrint;
import fr.ebiz.computerdatabase.dto.ComputerDto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class ComputerDtoPrettyPrint implements PrettyPrint<ComputerDto> {

    private static final String DOT = ".";
    private static final String SPACE = " ";
    private static final String COMPUTER = "Computer : ";
    private static final String INTRODUCED_AT = "Introduced at" + SPACE;
    private static final String DISCONTINUED_AT = "Discontinued at" + SPACE;
    private static final String CREATED_BY = "It was created by" + SPACE;

    /**
     * {@inheritDoc}
     */
    @Override
    public String printList(List<ComputerDto> listToPrint) {
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
            if (computer.getCompanyName() != null) {
                sb.append(SPACE)
                        .append(CREATED_BY)
                        .append(computer.getCompanyName()).append(DOT);
            }
            return sb.toString();
        }).collect(Collectors.joining(System.lineSeparator()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String printDetail(ComputerDto objectToPrint) {
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
        if (objectToPrint.getCompanyName() != null) {
            sb.append(System.lineSeparator())
                    .append(SPACE).append(CREATED_BY)
                    .append(objectToPrint.getCompanyName());
        }
        return sb.toString();
    }

    /**
     * Format an {@link OffsetDateTime} to a String using the ISO format.
     *
     * @param date The date to format
     * @return The date formatted in ISO format
     */
    private String formatDate(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ISO_DATE) : "";
    }

}
