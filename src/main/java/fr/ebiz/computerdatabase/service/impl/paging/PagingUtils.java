package fr.ebiz.computerdatabase.service.impl.paging;

public class PagingUtils {

    public static int countPages(int elementsPerPage, int numberOfCompanies) {
        return (numberOfCompanies / elementsPerPage)
                + ((numberOfCompanies % elementsPerPage) > 0 ? 1 : 0);
    }
}
