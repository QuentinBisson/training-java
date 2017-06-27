package fr.ebiz.computerdatabase.ui.printer;

import java.util.List;

public interface PrettyPrint<T> {

    String printList(List<T> listToPrint);

    String printDetail(T objectToPrint);

}
