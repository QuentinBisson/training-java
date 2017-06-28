package fr.ebiz.computerdatabase.service.impl.paging;

import fr.ebiz.computerdatabase.model.Builder;

import java.util.ArrayList;
import java.util.List;

public class Page<T> {

    private List<T> elements;
    private int totalPages;
    private int currentPage;

    public Page() {
        elements = new ArrayList<>();
    }

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

    public static class PageBuilder<U> implements Builder<Page<U>> {
        private Page<U> page;

        public PageBuilder() {
            page = new Page<>();
        }

        public PageBuilder elements(List<U> elements) {
            page.elements.addAll(elements);
            return this;
        }

        public PageBuilder totalPages(int totalPages) {
            page.totalPages = totalPages;
            return this;
        }

        public PageBuilder currentPage(int currentPage) {
            page.currentPage = currentPage;
            return this;
        }

        @Override
        public Page<U> build() {
            return page;
        }
    }
}
