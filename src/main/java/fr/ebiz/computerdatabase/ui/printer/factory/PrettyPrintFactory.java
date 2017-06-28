package fr.ebiz.computerdatabase.ui.printer.factory;

import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.ui.printer.PrettyPrint;
import fr.ebiz.computerdatabase.ui.printer.impl.CompanyPrettyPrint;
import fr.ebiz.computerdatabase.ui.printer.impl.ComputerPrettyPrint;

public class PrettyPrintFactory {

    private static PrettyPrintFactory instance = new PrettyPrintFactory();

    public static PrettyPrintFactory getInstance() {
        return instance;
    }

    public final <T> PrettyPrint<T> make(Class<T> clazz) {
        if (Computer.class.equals(clazz)) {
            return (PrettyPrint<T>) new ComputerPrettyPrint();
        } else if(Company.class.equals(clazz)) {
            return (PrettyPrint<T>) new CompanyPrettyPrint();
        }
        throw new IllegalArgumentException("No pretty printer exists for this class");
    }

}
