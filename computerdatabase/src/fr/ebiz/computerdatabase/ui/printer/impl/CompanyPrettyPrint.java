package fr.ebiz.computerdatabase.ui.printer.impl;

import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.ui.printer.PrettyPrint;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

public class CompanyPrettyPrint implements PrettyPrint<Company> {
    private static final String DOT = ".";
    private static final String COMPANY = "Company : ";
    @Override
    public String printList(List<Company> listToPrint) {
        return listToPrint.stream()
                .map(company -> new StringBuilder("" + company.getId())
                        .append(" - ")
                        .append(company.getName().trim())
                        .append(DOT)
                        .toString())
                .collect(joining(System.lineSeparator()));
    }

    @Override
    public String printDetail(Company objectToPrint) {
        return new StringBuilder(COMPANY)
                .append(objectToPrint.getId()).append(" - ")
                .append(objectToPrint.getName().trim())
                .toString();
    }
}
