package fr.ebiz.computerdatabase.dto.paging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page<T> implements Serializable {

    private final List<T> elements;
    private int totalPages;
    private int totalElements;
    private int currentPage;

    /**
     * Constructor.
     */
    private Page() {
        elements = new ArrayList<>();
    }

    /**
     * Create a Page builder instance.
     *
     * @param <T> The type of elements returned by the Page object to build
     * @return a new Page builder
     */
    public static <T> PageBuilder<T> builder() {
        return new PageBuilder<>();
    }

    public List<T> getElements() {
        return elements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public static class PageBuilder<U> {
        private final Page<U> page;

        /**
         * Constructor.
         */
        PageBuilder() {
            page = new Page<>();
        }

        /**
         * Set the page elements.
         *
         * @param elements The elements to add
         * @return The page builder instance
         */
        public PageBuilder elements(List<U> elements) {
            page.elements.addAll(elements);
            return this;
        }

        /**
         * Set the total number of pages.
         *
         * @param totalPages The total number of pages
         * @return The page builder instance
         */
        public PageBuilder totalPages(int totalPages) {
            page.totalPages = totalPages;
            return this;
        }

        /**
         * Set the total number of elements.
         *
         * @param totalElements The total number of elements
         * @return The page builder instance
         */
        public PageBuilder totalElements(int totalElements) {
            page.totalElements = totalElements;
            return this;
        }

        /**
         * Set the current page.
         *
         * @param currentPage The current
         * @return The page builder instance
         */
        public PageBuilder currentPage(int currentPage) {
            page.currentPage = currentPage;
            return this;
        }

        /**
         * Build the page instance.
         *
         * @return the page instance
         */
        public Page<U> build() {
            return page;
        }
    }
}
