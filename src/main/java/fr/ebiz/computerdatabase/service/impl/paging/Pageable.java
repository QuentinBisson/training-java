package fr.ebiz.computerdatabase.service.impl.paging;

import fr.ebiz.computerdatabase.model.Builder;

public class Pageable {
    private int page;
    private int elements;

    public static <U> Pageable.PageableBuilder builder() {
        return new Pageable.PageableBuilder();
    }

    public int getPage() {
        return page;
    }

    public int getElements() {
        return elements;
    }

    public static class PageableBuilder implements Builder<Pageable> {
        private Pageable pageable;

        public PageableBuilder() {
            pageable = new Pageable();
        }

        public PageableBuilder elements(int elements) {
            pageable.elements = elements;
            return this;
        }

        public PageableBuilder page(int page) {
            pageable.page = page;
            return this;
        }

        @Override
        public Pageable build() {
            return pageable;
        }
    }
}
