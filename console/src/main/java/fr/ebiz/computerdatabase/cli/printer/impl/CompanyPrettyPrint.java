package fr.ebiz.computerdatabase.cli.printer.impl;

import fr.ebiz.computerdatabase.cli.printer.PrettyPrint;
import fr.ebiz.computerdatabase.core.Company;

import java.util.List;

import static java.util.stream.Collectors.joining;

public class CompanyPrettyPrint implements PrettyPrint<Company> {
    private static final String DOT = ".";
    private static final String COMPANY = "Company : ";

    /**
     * {@inheritDoc}
     */
    @Override
    public String printList(List<Company> listToPrint) {
        return listToPrint.stream()
                .map(company -> "" + company.getId() +
                        " - " +
                        company.getName().trim() +
                        DOT)
                .collect(joining(System.lineSeparator()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String printDetail(Company objectToPrint) {
        return COMPANY +
                objectToPrint.getId() + " - " +
                objectToPrint.getName().trim();
    }
}
