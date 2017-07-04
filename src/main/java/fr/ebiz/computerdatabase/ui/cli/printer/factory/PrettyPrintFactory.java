package fr.ebiz.computerdatabase.ui.cli.printer.factory;

import fr.ebiz.computerdatabase.model.Company;
import fr.ebiz.computerdatabase.model.Computer;
import fr.ebiz.computerdatabase.ui.cli.printer.PrettyPrint;
import fr.ebiz.computerdatabase.ui.cli.printer.impl.CompanyPrettyPrint;
import fr.ebiz.computerdatabase.ui.cli.printer.impl.ComputerPrettyPrint;

public class PrettyPrintFactory {

    private static PrettyPrintFactory instance;

    /**
     * Get the instance.
     * Creates it thread-safe if it does not exist.
     *
     * @return the singleton instance
     */
    public static synchronized PrettyPrintFactory getInstance() {
        if (instance == null) {
            instance = new PrettyPrintFactory();
        }
        return instance;
    }

    /**
     * Make a {@link PrettyPrint} instance.
     *
     * @param clazz The class to use to determine which pretty print to create
     * @param <T>   The type of the class
     * @return The pretty print to use
     */
    @SuppressWarnings("unchecked")
    public final <T> PrettyPrint<T> make(Class<T> clazz) {
        if (Computer.class.equals(clazz)) {
            return (PrettyPrint<T>) new ComputerPrettyPrint();
        } else if (Company.class.equals(clazz)) {
            return (PrettyPrint<T>) new CompanyPrettyPrint();
        }
        throw new IllegalArgumentException("No pretty printer exists for this class");
    }

}
