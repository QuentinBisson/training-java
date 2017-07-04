package fr.ebiz.computerdatabase.ui.cli.printer;

import java.util.List;

public interface PrettyPrint<T> {

    /**
     * Print a list of elements.
     *
     * @param listToPrint The list of elements to print
     * @return The printable string
     */
    String printList(List<T> listToPrint);

    /**
     * Print the detail of an element.
     *
     * @param objectToPrint The element to print
     * @return The print
     */
    String printDetail(T objectToPrint);

}
