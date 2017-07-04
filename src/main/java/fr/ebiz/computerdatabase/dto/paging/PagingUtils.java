package fr.ebiz.computerdatabase.dto.paging;

public final class PagingUtils {

    /**
     * Utility method used to count the number of pages.
     *
     * @param elementsPerPage  The number of elements per page
     * @param numberOfElements The total number of elements
     * @return the total number of pages
     */
    public static int countPages(int elementsPerPage, int numberOfElements) {
        return (numberOfElements / elementsPerPage)
                + ((numberOfElements % elementsPerPage) > 0 ? 1 : 0);
    }
}
